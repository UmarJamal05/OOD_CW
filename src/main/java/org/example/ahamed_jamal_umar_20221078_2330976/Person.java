package org.example.ahamed_jamal_umar_20221078_2330976;

public abstract class Person {
    protected String password;

    public Person(String password) {
        this.password = password;
    }
    abstract String getPassword();
}
