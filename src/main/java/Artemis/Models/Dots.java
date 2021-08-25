package Artemis.Models;

import com.google.gson.annotations.SerializedName;

public class Dots {

    private int id;
    private String reason;
    @SerializedName(value = "student_id")
    private int studentId;
    @SerializedName(value = "assigning_teacher")
    private Teacher assigningTeacher;

    public Dots(int id, String reason, int studentId, Teacher assigningTeacher) {
        this.id = id;
        this.reason = reason;
        this.studentId = studentId;
        this.assigningTeacher = assigningTeacher;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Teacher getAssigningTeacher() {
        return assigningTeacher;
    }

    public String getAssigningTeacherName(){
        return assigningTeacher.getFullName();
    }

    public void setAssigningTeacherId(Teacher assigningTeacher) {
        this.assigningTeacher = assigningTeacher;
    }

}
