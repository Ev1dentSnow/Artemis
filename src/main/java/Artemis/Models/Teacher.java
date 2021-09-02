/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Artemis.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 *
 * @author Moagi Moja
 */
public class Teacher extends User {

    private String subject;

    public Teacher(int id, String firstName, String lastName, String username, Date dob, String house,
                   String email, String comments, String subject) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.dob = dob;
        this.house = house;
        this.email = email;
        this.comments = comments;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
