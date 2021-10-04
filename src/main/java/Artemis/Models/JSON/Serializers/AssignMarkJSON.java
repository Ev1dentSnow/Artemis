package Artemis.Models.JSON.Serializers;

public class AssignMarkJSON {

    private int assignment_id;
    private int class_id;
    private int student_id;
    private double mark_awarded;

    public AssignMarkJSON() {

    }

    public AssignMarkJSON(int assignment_id, int class_id, int student_id, double mark_awarded) {
        this.assignment_id = assignment_id;
        this.class_id = class_id;
        this.student_id = student_id;
        this.mark_awarded = mark_awarded;
    }

    public int getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(int assignment_id) {
        this.assignment_id = assignment_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public double getMark_awarded() {
        return mark_awarded;
    }

    public void setMark_awarded(double mark_awarded) {
        this.mark_awarded = mark_awarded;
    }
}
