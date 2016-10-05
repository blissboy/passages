package com.boyamihungry.passageways;

import fj.data.Either;

import java.lang.reflect.Field;

public class ValueSetter {
    private ValueSetterHelper<?> setterFunction;
    private String variableName;
    private Field variableField;

    public ValueSetter(String fieldName, Object fieldOwningObj, ValueSetterHelper setterFunction) {
        this.setterFunction = setterFunction;
        this.variableName = fieldName;
        this.variableField = setupField(fieldOwningObj, fieldName).right().value();
    }

    private Either<Exception, Field> setupField(Object obj, String fieldName) {
        try {
            Class clazz = obj.getClass();
            return Either.right(clazz.getField(fieldName));
        } catch (Exception e) {
            return Either.left(e);
        }
    }

    public void setValueForVariable() {
        setterFunction.setValue(variableField);
    }


    @FunctionalInterface
    public interface ValueSetterHelper<T> {
        void setValue(Field field);
    }
}