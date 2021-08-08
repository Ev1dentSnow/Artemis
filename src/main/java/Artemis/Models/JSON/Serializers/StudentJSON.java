package Artemis.Models.JSON.Serializers;

import java.util.HashMap;

/**
 * "Helper" Class / "Interface" (idk which term to use), which is used to make deserializing Student JSON data muuuuch
 *  much easier. This is one of very few instances in this project where a "helper" class like this is required for
 *  deserialization
 */

public class StudentJSON {



    private HashMap<String, String> user_details = new HashMap<>();
    private int form;
    private int enrollment_year;
    private String primary_contact_name;
    private String primary_contact_email;
    private String secondary_contact_name;
    private String secondary_contact_email;

    public StudentJSON(HashMap<String, String> user_details, int form, int enrollment_year, String primary_contact_name, String primary_contact_email, String secondary_contact_name, String secondary_contact_email) {
        this.user_details = user_details;
        this.form = form;
        this.enrollment_year = enrollment_year;
        this.primary_contact_name = primary_contact_name;
        this.primary_contact_email = primary_contact_email;
        this.secondary_contact_name = secondary_contact_name;
        this.secondary_contact_email = secondary_contact_email;
    }

    public HashMap<String, String> getUser_details() {
        return user_details;
    }

    public void setUser_details(HashMap<String, String> user_details) {
        this.user_details = user_details;
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public int getEnrollment_year() {
        return enrollment_year;
    }

    public void setEnrollment_year(int enrollment_year) {
        this.enrollment_year = enrollment_year;
    }

    public String getPrimary_contact_name() {
        return primary_contact_name;
    }

    public void setPrimary_contact_name(String primary_contact_name) {
        this.primary_contact_name = primary_contact_name;
    }

    public String getPrimary_contact_email() {
        return primary_contact_email;
    }

    public void setPrimary_contact_email(String primary_contact_email) {
        this.primary_contact_email = primary_contact_email;
    }

    public String getSecondary_contact_name() {
        return secondary_contact_name;
    }

    public void setSecondary_contact_name(String secondary_contact_name) {
        this.secondary_contact_name = secondary_contact_name;
    }

    public String getSecondary_contact_email() {
        return secondary_contact_email;
    }

    public void setSecondary_contact_email(String secondary_contact_email) {
        this.secondary_contact_email = secondary_contact_email;
    }
}
