package com.xgh.test.model.command.operational.supplier;

import com.xgh.buildingblocks.EventStore;
import com.xgh.model.command.operational.enumerator.Enumerator;
import com.xgh.model.command.operational.supplier.Supplier;
import com.xgh.model.command.operational.supplier.SupplierId;
import com.xgh.model.command.operational.valueobjects.Name;
import com.xgh.model.command.operational.valueobjects.Phone;
import com.xgh.test.model.command.operational.enumerator.EnumeratorSampleData;
import com.xgh.test.model.command.operational.valueobjects.AddressSampleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("CommandSupplierSampleData")
public class SupplierSampleData {
    @Autowired
    private EventStore eventStore;

    @Autowired
    private AddressSampleData address;

    @Autowired
    private EnumeratorSampleData enumSampleData;

    public Supplier getSample() {
        Supplier supplier = new Supplier();

        Enumerator dissType = enumSampleData.getSample();
        supplier.register(
                new SupplierId(),
                new Name("Nestle"),
                new Phone("44999999999"),
                "00000000191",
                address.getSample(),
                dissType.getId());
        eventStore.push(supplier);
        return supplier;
    }
}
