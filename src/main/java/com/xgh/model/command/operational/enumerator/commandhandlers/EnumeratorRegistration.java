package com.xgh.model.command.operational.enumerator.commandhandlers;

import com.xgh.buildingblocks.EventStore;
import com.xgh.buildingblocks.command.CommandHandler;
import com.xgh.model.command.operational.enumerator.Enumerator;
import com.xgh.model.command.operational.enumerator.commands.RegisterEnumerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnumeratorRegistration implements CommandHandler<RegisterEnumerator> {
    private final EventStore eventStore;

    @Autowired
    public EnumeratorRegistration(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void execute(RegisterEnumerator command) {
        Enumerator enumerator = new Enumerator();
        enumerator.register(
                command.getId(),
                command.getKind(),
                command.getName());
        eventStore.push(enumerator);
    }
}
