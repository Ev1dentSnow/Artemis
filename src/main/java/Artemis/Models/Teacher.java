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
    private String subject;

    public Teacher(int userId, String subject) {
        this.userId = userId;
        this.subject = subject;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
