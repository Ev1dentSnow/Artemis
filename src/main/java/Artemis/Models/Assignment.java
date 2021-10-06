package Artemis.Models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Date;

public class Assignment {

    private int id;
    @SerializedName(value = "assignment_name")
    private String assignmentName;
    @SerializedName(value = "max_marks")
    private double maxMarks;
    @SerializedName(value = "date_assigned")
    private LocalDate dateAssigned;
    @SerializedName(value = "date_due")
    private LocalDate dateDue;
    private Teacher teacher;

    public Assignment(int id, String assignmentName, double maxMarks, LocalDate dateAssigned, LocalDate dateDue, Teacher teacher) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.maxMarks = maxMarks;
        this.dateAssigned = dateAssigned;
        this.dateDue = dateDue;
        this.teacher = teacher;
    }

    public Assignment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public LocalDate getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDate dateDue) {
        this.dateDue = dateDue;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacherId) {
        this.teacher = teacherId;
    }
}
