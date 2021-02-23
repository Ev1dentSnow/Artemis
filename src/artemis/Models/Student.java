/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artemis.Models;

import java.util.Date;

/**
 *
 * @author Moagi Moja
 */
public class Student extends Person{

    private String house;
    private int form;
    private boolean prefectStatus;
    
    public Student(String f, String l, Date dob, String h, int fo, boolean ps){
        
        fName = f;
        lName = l;
        DOB = dob;
        house = h;
        form = fo;
        prefectStatus = ps;
        
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

    public boolean isPrefectStatus(){
        return prefectStatus;
    }

    public void setPrefectStatus(boolean prefectStatus){
        this.prefectStatus = prefectStatus;
    }
}
