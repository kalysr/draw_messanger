package com.example.apple.geektech.Utils;

public class UserObject {
        String  name;
        String ref_key;
    String phone;
    String status;

    public String getStatus() {
        return status;
    }

    public String getRef_key() {
        return ref_key;
    }

    public UserObject(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public UserObject(String name, String phone,String ref_key, String lastSeen) {
        this.ref_key = ref_key;
        this.status = lastSeen;
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public String getPhone() {return phone;}
}
