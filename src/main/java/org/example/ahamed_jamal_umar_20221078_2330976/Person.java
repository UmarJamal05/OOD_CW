package org.example.ahamed_jamal_umar_20221078_2330976;

public abstract class Person {
    protected String username;
    protected String password;

    public Person(String username, String password) {
        this.username = username;
        this.password = password;
    }

    abstract String getUsername();
    abstract String getPassword();
}
