package com.xgh.model.command.operational.product.events;

import com.xgh.buildingblocks.entity.EntityVersion;
import com.xgh.buildingblocks.event.EntityEvent;
import com.xgh.model.command.operational.product.ProductId;
import com.xgh.model.command.operational.supplier.SupplierId;
import com.xgh.model.command.operational.valueobjects.Name;

public class ProductWasRegistered extends EntityEvent<ProductId> {
    private final Name name;
    private final Float price;
    private final Name brand;
    private final Float amount;
    private final SupplierId supplierId;

    protected ProductWasRegistered() {
        name = null;
        price = null;
        brand = null;
        amount = null;
        supplierId = null;
    }

    public ProductWasRegistered(ProductId id, Name name, Float price, Name brand, Float amount, SupplierId supplierId, EntityVersion entityVersion) {
        super(id, entityVersion);
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.amount = amount;
        this.supplierId = supplierId;
    }

    public Name getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public Name getBrand() {
        return brand;
    }

    public Float getAmount() {
        return amount;
    }

    public SupplierId getSupplierId() {
        return supplierId;
    }
}
