package com.example.apple.geektech.Utils;

public class UserObject {
        String  name;
        String token;
        String uid;
    String phone;
    String status;
    int width,height;
    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public UserObject(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public UserObject(String name, String phone, String token, String uid, String lastSeen, int w, int h) {
        this.token = token;
        this.status = lastSeen;
        this.name = name;
        this.uid = uid;
        this.phone = phone;
        this.width = w;
        this.height = h;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {return name;}

    public String getPhone() {return phone;}
    public String getUid() {return uid;}
}
