package Artemis.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Assignment {

    private int id;
    @SerializedName(value = "assignment_name")
    private String assignmentName;
    @SerializedName(value = "max_marks")
    private double maxMarks;
    @SerializedName(value = "date_assigned")
    private Date dateAssigned;
    @SerializedName(value = "date_due")
    private Date dateDue;
    @SerializedName(value = "teacher_id")
    private int teacherId;

    public Assignment(int id, String assignmentName, double maxMarks, Date dateAssigned, Date dateDue, int teacherId) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.maxMarks = maxMarks;
        this.dateAssigned = dateAssigned;
        this.dateDue = dateDue;
        this.teacherId = teacherId;
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

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public Date getDateDue() {
        return dateDue;
    }

    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
}