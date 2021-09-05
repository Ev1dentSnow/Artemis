package Artemis.Models.JSON.Serializers;

import java.util.HashMap;

public class TeacherJSON {

    private HashMap<String, String> user_details;
    private String subject;

    public TeacherJSON(HashMap<String, String> user_details, String subject) {
        this.user_details = user_details;
        this.subject = subject;
    }

    public HashMap<String, String> getUser_details() {
        return user_details;
    }

    public void setUser_details(HashMap<String, String> user_details) {
        this.user_details = user_details;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
