package com.xgh.model.command.operational.valueobjects;

import com.xgh.buildingblocks.valueobject.SingleValueObject;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

public class Cpf extends SingleValueObject<String> implements Document {
    public Cpf(String cpf) {
        super(cpf);
        CPFValidator validator = new CPFValidator();
        validator.initialize(null);
        if (!validator.isValid(cpf, null)) {
            throw new IllegalArgumentException("CPF inválido!");
        }
    }
}
