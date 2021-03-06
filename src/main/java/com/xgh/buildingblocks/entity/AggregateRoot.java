package com.xgh.buildingblocks.entity;

import com.xgh.buildingblocks.event.EntityEvent;
import com.xgh.buildingblocks.event.EventStream;
import com.xgh.exceptions.DeletedEntityException;
import java.lang.reflect.Method;

public abstract class AggregateRoot<IdT extends EntityId> extends DomainEntity<IdT> {
    /*
     * Eventos que foram gerados durante um ciclo de execução e que faltam ser persistidos
     */
    private final EventStream uncommittedEvents = new EventStream();

    private Boolean deleted = false;

    private EntityVersion version;

    protected AggregateRoot() {
        this.version = new EntityVersion(0);
    }

    /*
     * Grava o novo evento na lista de eventos à serem persistidos (uncommittedEvents)
     * e aplica o evento
     *
     * TODO preecher o version com o nextVersion automagicamente
     */
    protected void recordAndApply(EntityEvent<IdT> event) {
        if (this.isDeleted()) {
            throw new DeletedEntityException();
        }

        this.record(event);
        this.apply(event);
    }

    /*
     * Retorna a próxima versão do agregado
     */
    protected EntityVersion nextVersion() {
        return this.getVersion().next();
    }

    /*
     * Aplica o evento, atualizando os metadados e invocando o handler do evento
     */
    private void apply(EntityEvent<IdT> event) {
        this.updateMetadata(event);
        this.invokeHandlerMethod(event);
    }

    private void updateMetadata(EntityEvent<IdT> event) {
        this.id = event.getEntityId();
        this.version = event.getEntityVersion();
    }

    /*
     * Encontra e executa o handler do evento
     */
    private void invokeHandlerMethod(EntityEvent<?> event) {
        Method handlerMethod = getHandlerMethod(event);
        if (handlerMethod != null) {
            handlerMethod.setAccessible(true);
            try {
                handlerMethod.invoke(this, event);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Não foi possível invocar o método de aplicação do evento: %s", event.getClass().getName()), e);
            }
        }
    }

    /*
     * Encontra o handler do evento (método 'when' com o argumento do mesmo tipo do evento)
     */
    private Method getHandlerMethod(EntityEvent<?> event) {
        try {
            return getClass().getDeclaredMethod("when", event.getClass());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private void record(EntityEvent<?> event) {
        this.uncommittedEvents.add(event);
    }

    public EventStream getUncommittedEvents() {
        return uncommittedEvents;
    }

    /*
     * Aplica todos os eventos do event stream na entidade limpa
     */
    @SuppressWarnings("unchecked")
    protected void reconstitute(EventStream events) {
        if (!this.version.isBlank()) {
            throw new RuntimeException("Só é possível reconstituir à partir de uma entidade sem alterações");
        }

        while (events.hasNext()) {
            this.apply((EntityEvent<IdT>) events.next());
        }
    }

    public final EntityVersion getVersion() {
        return version;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    protected void markDeleted() {
        this.deleted = true;
    }
}
