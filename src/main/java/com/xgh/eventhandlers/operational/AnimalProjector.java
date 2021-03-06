package com.xgh.eventhandlers.operational;

import com.xgh.buildingblocks.event.EntityEvent;
import com.xgh.buildingblocks.event.Event;
import com.xgh.buildingblocks.event.EventHandler;
import com.xgh.exceptions.ProjectionFailedException;
import com.xgh.infra.repository.PostgresEventStore;
import com.xgh.model.command.operational.animal.Animal;
import com.xgh.model.command.operational.animal.events.AnimalWasDeleted;
import com.xgh.model.command.operational.animal.events.AnimalWasRegistered;
import com.xgh.model.command.operational.animal.events.AnimalWasUpdated;
import com.xgh.model.query.operational.animal.AnimalRepository;
import com.xgh.model.query.operational.enumerator.Enumerator;
import com.xgh.model.query.operational.enumerator.EnumeratorRepository;
import com.xgh.model.query.operational.owner.Owner;
import com.xgh.model.query.operational.owner.OwnerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnimalProjector implements EventHandler {
    private final PostgresEventStore eventStore;
    private final AnimalRepository repository;
    private final EnumeratorRepository breedRepository,specieRepository;
    private final OwnerRepository ownerRepository;

    @Autowired
    public AnimalProjector(PostgresEventStore eventStore, AnimalRepository repository, EnumeratorRepository breedRepository, EnumeratorRepository specieRepository, OwnerRepository ownerRepository) {
        this.eventStore = eventStore;
        this.repository = repository;
        this.breedRepository = breedRepository;
        this.specieRepository = specieRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public boolean isSubscribedTo(Event event) {
        return event instanceof AnimalWasDeleted
                || event instanceof AnimalWasRegistered
                || event instanceof AnimalWasUpdated;
    }

    @Override
    public void execute(Event event) {
        Animal entity = eventStore.pull(Animal.class, ((EntityEvent<?>) event).getEntityId());

        Optional<Owner> owner = ownerRepository.findById(entity.getOwner().getValue());
        if (!owner.isPresent()) {
            throw new ProjectionFailedException(Owner.class.getSimpleName());
        }
        
        Optional<Enumerator> breed = breedRepository.findById(entity.getBreed().getValue());
        if(!breed.isPresent()) {
        	throw new ProjectionFailedException(Enumerator.class.getSimpleName());
        }
        
        Optional<Enumerator> specie = specieRepository.findById(entity.getSpecie().getValue());
        if(!specie.isPresent()) {
        	throw new ProjectionFailedException(Enumerator.class.getSimpleName());
        }
        
        com.xgh.model.query.operational.animal.Animal projection = new com.xgh.model.query.operational.animal.Animal(
                entity.getId().getValue(),
                entity.getName().getValue(),
                owner.get(),
                breed.get(),
                specie.get(),
                entity.getSex().toString(),
                entity.getBirthDate(),
                entity.isCastrated(),
                entity.getWeight(),
                entity.isDeleted()
        );

        repository.save(projection);
    }
}
