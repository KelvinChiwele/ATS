package com.techart.ats.models;

/**
 * Created by Kelvin on 05/06/2017.
 */

public class Directory {
    private String town;
    private String address;
    private String email;
    private String phone;
    private String location;
    private String representative;

    public Directory()  {

    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }
    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
