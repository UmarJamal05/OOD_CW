package org.example.ahamed_jamal_umar_20221078_2330976;

public class SystemAdministrator extends Person{
    private String name;
    public SystemAdministrator() {
        super("u.a.j.005");
        this.name = "Umar Jamal";
    }

    @Override
    public String getPassword() {
        return super.password;
    }
    public String getName(){
        return name;
    }
}
