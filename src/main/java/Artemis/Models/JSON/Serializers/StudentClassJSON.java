package Artemis.Models.JSON.Serializers;

import Artemis.Models.Classes;
import Artemis.Models.Student;

public class StudentClassJSON {

    private Classes classs;
    private StudentJSON student;
    private static int toStringNumber;

    public StudentClassJSON(Classes classs, StudentJSON student) {
        this.classs = classs;
        this.student = student;

    }

    public Classes getClasss() {
        return classs;
    }

    public void setClasss(Classes classs) {
        this.classs = classs;
    }

    public StudentJSON getStudent() {
        return student;
    }

    public void setStudent(StudentJSON student) {
        this.student = student;
    }

    public static void setToStringNumber(int toStringNumber) {
        StudentClassJSON.toStringNumber = toStringNumber;
    }

    @Override
    public String toString() {
        if (toStringNumber == 0) {
            return classs.getName();
        }
        else if (toStringNumber == 1) {
            return student.getUser_details().get("first_name") + " " + student.getUser_details().get("last_name");
        }

        return "";
    }
}
