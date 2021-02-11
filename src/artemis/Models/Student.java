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
    private boolean isMatric;
    
    public Student(String f, String l, Date dob, String h, int fo, boolean im){
        
        fName = f;
        lName = l;
        DOB = dob;
        house = h;
        form = fo;
        isMatric = im;
        
    }
}
