package com.xgh.model.command.operational.internment.commandhandlers;

import com.xgh.buildingblocks.EventStore;
import com.xgh.buildingblocks.command.CommandHandler;
import com.xgh.exceptions.EntityNotFoundException;
import com.xgh.model.command.operational.animal.Animal;
import com.xgh.model.command.operational.internment.Internment;
import com.xgh.model.command.operational.internment.commands.RegisterInternment;
import com.xgh.model.query.operational.enumerator.Enumerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternmentRegistration implements CommandHandler<RegisterInternment> {

    private final EventStore eventStore;

    @Autowired
    public InternmentRegistration(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void execute(RegisterInternment command) {

        if (!eventStore.entityExists(Animal.class, command.getAnimalId())) {
            throw new EntityNotFoundException(Animal.class.getSimpleName(), command.getAnimalId().getValue());
        }

        Internment internment = new Internment();
        internment.register(command.getId(), command.getBedId(), command.getAnimalId(), command.getBusyAt(),
                command.getBusyUntil());
        eventStore.push(internment);
    }

}
