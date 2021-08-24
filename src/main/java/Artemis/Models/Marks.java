package Artemis.Models;

import com.google.gson.annotations.SerializedName;
import java.sql.Date;

public class Marks {

    private int assignment_id;
    private String assignment_name;
    private double max_marks;
    private double marks_awarded;
    private String date_assigned;
    private String date_due;
    private int teacher_id;

    public Marks(int assignment_id, String assignment_name, double max_marks, double marks_awarded, String date_assigned, String date_due, int teacher_id) {
        this.assignment_id = assignment_id;
        this.assignment_name = assignment_name;
        this.max_marks = max_marks;
        this.marks_awarded = marks_awarded;
        this.date_assigned = date_assigned;
        this.date_due = date_due;
        this.teacher_id = teacher_id;
    }

    public int getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(int assignment_id) {
        this.assignment_id = assignment_id;
    }

    public String getAssignment_name() {
        return assignment_name;
    }

    public void setAssignment_name(String assignment_name) {
        this.assignment_name = assignment_name;
    }

    public double getMax_marks() {
        return max_marks;
    }

    public void setMax_marks(double max_marks) {
        this.max_marks = max_marks;
    }

    public double getMarks_awarded() {
        return marks_awarded;
    }

    public void setMarks_awarded(double marks_awarded) {
        this.marks_awarded = marks_awarded;
    }

    public String getDate_assigned() {
        return date_assigned;
    }

    public void setDate_assigned(String date_assigned) {
        this.date_assigned = date_assigned;
    }

    public String getDate_due() {
        return date_due;
    }

    public void setDate_due(String date_due) {
        this.date_due = date_due;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }
}
