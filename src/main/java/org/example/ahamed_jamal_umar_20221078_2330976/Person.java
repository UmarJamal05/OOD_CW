package org.example.ahamed_jamal_umar_20221078_2330976;

public abstract class Person {
    protected String password;// Protected field to store the person's password

    // Constructor to initialize the password
    public Person(String password) {
        this.password = password;
    }
    // Abstract method to get the password
    public abstract String getPassword();
}
