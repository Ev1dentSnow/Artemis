/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Artemis.Models;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Moagi Moja
 */
public class Teacher {

    @SerializedName(value = "user_id")
    private int userId;
    @SerializedName(value = "first_name")
    private String firstName;
    @SerializedName(value = "last_name")
    private String lastName;
    private String fullName;
    private String subject;

    public Teacher(int userId, String firstName, String lastName, String subject) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.fullName = this.firstName + this.lastName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
