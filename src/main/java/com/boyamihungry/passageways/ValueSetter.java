package com.boyamihungry.passageways;

import fj.data.Either;

import java.lang.reflect.Field;

public class ValueSetter {
    private ValueSetterHelper<?> setterFunction;
    //private String variableName;
    private Object owningObject;
    private Field variableField;

    public ValueSetter(String fieldName, Object fieldOwningObj, ValueSetterHelper setterFunction) {
        this.setterFunction = setterFunction;
        //this.variableName = fieldName;
        this.variableField = setupField(fieldOwningObj, fieldName).right().value();
        this.owningObject = fieldOwningObj;
    }

    private Either<Exception, Field> setupField(Object obj, String fieldName) {
        try {
            Class clazz = obj.getClass();
            return Either.right(clazz.getDeclaredField(fieldName));
        } catch (Exception e) {
            e.printStackTrace();
            return Either.left(e);
        }
    }

    public void setValueForVariable() {
        setterFunction.setValue(owningObject, variableField);
    }


    @FunctionalInterface
    public interface ValueSetterHelper<T> {
        void setValue(Object o, Field field);
    }
}