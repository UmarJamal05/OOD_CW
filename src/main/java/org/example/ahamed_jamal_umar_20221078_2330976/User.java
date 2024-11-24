package org.example.ahamed_jamal_umar_20221078_2330976;

public class User extends Person{
    private String firstName;
    private String lastName;
    private String email;

    public User(String firstName, String lastName, String email, String username, String password) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public String getUsername() {
        return super.username;
    }

    @Override
    public String getPassword() {
        return super.password;
    }
    public void setPassword(String password) { this.password = password; }
    @Override
    public String toString() {
        return username + "," + password + "," + firstName + "," + lastName + "," + email;
    }
}
