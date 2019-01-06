package com.example.apple.geektech;

public class UserObject {
        String  name;

    String phone;

    public UserObject(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public String getPhone() {return phone;}
}
