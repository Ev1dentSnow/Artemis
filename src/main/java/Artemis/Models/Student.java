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
public class Student extends User {


    private int form;
    @SerializedName(value = "primary_contact_name")
    String primaryContactName;
    @SerializedName(value = "primary_contact_email")
    String primaryContactEmail;
    @SerializedName(value = "secondary_contact_name")
    String secondaryContactName;
    @SerializedName(value = "secondary_contact_email")
    String secondaryContactEmail;

    public Student(int id, String firstName, String lastName, String username, Date dob, String house, int form,
                   String email, String comments, String pcn, String pce, String scn, String sce){

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.dob = dob;
        this.house = house;
        this.form = form;
        this.email = email;
        this.comments = comments;
        this.primaryContactName = pcn;
        this.primaryContactEmail = pce;
        this.secondaryContactName = scn;
        this.secondaryContactEmail = sce;

    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
         this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public Date getDob(){
        return (Date) dob;
    }

    public void setDob(Date dob){
        this.dob = dob;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getHouse(){
        return house;
    }

    public void setHouse(String house){
        this.house = house;
    }

    public int getForm(){
        return form;
    }

    public void setForm(int form){
        this.form = form;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getSecondaryContactName() {
        return secondaryContactName;
    }

    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }

    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }

    public void setSecondaryContactEmail(String secondaryContactEmail) {
        this.secondaryContactEmail = secondaryContactEmail;
    }

    public String getComments(){
        return comments;
    }

    public void setComments(String comments){
        this.comments = comments;
    }
}
