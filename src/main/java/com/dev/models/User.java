package com.dev.models;

import com.dev.services.KeyGenerator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {
    private long userId;
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String email;
    private String userPassword;
    private transient Set<Privilege> privileges = new HashSet<>(); // this stores all the roles and privileges of the user

    public User() {
        setUserId(0);
    }



    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        if (userId == 0)
            this.userId = KeyGenerator.generateKey();
        else this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", privileges=" + privileges +
                '}';
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Boolean hasPrivilege(String privilegeName) {
        for (Privilege privilege : privileges) {
            if (privilege.getPrivilegeName().equals(privilegeName))
                return true;
        }
        return false;
    }

    public Boolean hasRole(String roleName) {
        for (Privilege privilege : privileges) {
            if (Objects.equals(privilege.privilegeName, "ROLE_" + roleName))
                return true;
        }
        return false;
    }

}