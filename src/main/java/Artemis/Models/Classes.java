package Artemis.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Classes {

    private int id;
    private String name;
    @SerializedName(value = "graduation_date")
    private Date graduationDate;

    public Classes(int id, String name, Date graduationDate) {
        this.id = id;
        this.name = name;
        this.graduationDate = graduationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(Date graduationDate) {
        this.graduationDate = graduationDate;
    }
}
