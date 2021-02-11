/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artemis;

import artemis.Controllers.LoginController;
import artemis.Models.Admin;
import artemis.Models.Student;
import artemis.Models.Teacher;
import artemis.Views.LoginView;

/**
 *
 * @author Moagi Moja
 */
public class ArtemisDemo {

    /**
     * @param args the command line arguments
     */

    private static Student s;
    private static Teacher t;
    private static Admin a;

    public static void main(String[] args) {


        LoginController lc = new LoginController(s,t,a);
        lc.initLoginView();

    }
    
}
