package org.example.ahamed_jamal_umar_20221078_2330976;

public class SystemAdministrator extends Person{
    private String fullName;
    public SystemAdministrator(String fullName, String username, String password) {
        super(username, password);
        this.fullName = fullName;
    }
    @Override
    public String getUsername() {
        return super.username;
    }

    @Override
    public String getPassword() {
        return super.password;
    }
}
