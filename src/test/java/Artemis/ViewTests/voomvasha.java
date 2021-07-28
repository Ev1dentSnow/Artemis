package Artemis.ViewTests;

import java.util.HashMap;

public class voomvasha {

    private HashMap<String, String> user_details = new HashMap<>();


    public voomvasha(HashMap<String, String> user_details) {
        this.user_details = user_details;
    }

    public HashMap<String, String> getUser_details() {
        return user_details;
    }

    public void setUser_details(HashMap<String, String> user_details) {
        this.user_details = user_details;
    }
}
