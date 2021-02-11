/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artemis.Controllers;


import artemis.Models.Admin;
import artemis.Models.Student;
import artemis.Models.Teacher;
import artemis.Views.LoginView;

/**
 *
 * @author Moagi Moja
 */
public class LoginController {

    private Student s;
    private Teacher t;
    private Admin a;
    private LoginView lv;
    private String userName;
    private String passWord;

    public LoginController(Student s, Teacher t, Admin a){

        this.s = s;
        this.t = t;
        this.a = a;
    }

    public void initLoginView(){
    LoginView.main(null);
    }
    

    
}
