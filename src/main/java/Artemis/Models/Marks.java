package Artemis.Models;

import com.google.gson.annotations.SerializedName;
import java.sql.Date;

public class Marks {

    @SerializedName(value = "studentid", alternate = {"studentId"})
    private int studentId;
    @SerializedName(value = "classid", alternate = {"classId"})
    private int classId;
    private String name;
    @SerializedName(value = "marksawarded", alternate = {"marksAwarded"})
    private double marksawarded;
    @SerializedName(value = "maxmarks", alternate = {"maxMarks"})
    private int maxMarks;
    @SerializedName(value = "duedate", alternate = {"dueDate"})
    private Date dueDate;


    public Marks(int studentId, int classId, String name, double marksawarded, int maxMarks, Date dueDate) {
        this.studentId = studentId;
        this.classId = classId;
        this.name = name;
        this.marksawarded = marksawarded;
        this.maxMarks = maxMarks;
        this.dueDate = dueDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMarksawarded() {
        return marksawarded;
    }

    public void setMarksawarded(double marksawarded) {
        this.marksawarded = marksawarded;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
