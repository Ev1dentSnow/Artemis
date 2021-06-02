/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Artemis.Models;

import java.util.Date;

/**
 *
 * @author Moagi Moja
 */
public class Student extends User {


    private int form;

    public Student(String f, String l, Date dob, String h, int fo, String em){
        
        firstName = f;
        lastName = l;
        this.dob = dob;
        house = h;
        form = fo;
        email = em;

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



}
