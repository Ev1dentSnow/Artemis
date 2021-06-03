package Artemis.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    int id;
    @SerializedName(value = "first_name")
    String firstName;
    @SerializedName(value = "last_name")
    String lastName;
    Date dob;
    String house;
    String email;
    String comments;

}


