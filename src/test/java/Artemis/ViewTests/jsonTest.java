package Artemis.ViewTests;

import Artemis.Models.JSON.Deserializers.StudentJSON;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


class jsonTest {

        public static void main(String[] args) {
            String jsonString = """
        
[
    {
        "user_details": {
            "id": 8,
            "username": "tsexwale21",
            "password": "pbkdf2_sha256$260000$1w2qonEvDWXX2uTKmzzGj9$P69qcRwc5ZvZ6BjZe/UYqH55cnYCsmpAWbEYKPPcbi4=",
            "email": "tsexwale21@school.com",
            "dob": "2003-07-07",
            "first_name": "Tokyo",
            "last_name": "Sexwale",
            "comments": "Really cool kid"
        },
        "form": 4,
        "enrollment_year": 2018,
        "primary_contact_name": null,
        "primary_contact_email": null,
        "secondary_contact_name": null,
        "secondary_contact_email": null
    },
    {
        "user_details": {
            "id": 10,
            "username": "mmoshimane21",
            "password": "pbkdf2_sha256$260000$FPhIiY4s4dgZ8FyW59qthg$iYF18GF2lHNYlaX4xsSPxdKTxCCN/tBLCpkeJYQuuvg=",
            "email": "mmoshimane21@school.com",
            "dob": "2003-07-07",
            "first_name": "Mokyo",
            "last_name": "Moshimane",
            "comments": "Nice guy"
        },
        "form": 4,
        "enrollment_year": 2018,
        "primary_contact_name": "vooonda",
        "primary_contact_email": "vooonda@wa.com",
        "secondary_contact_name": "coolda",
        "secondary_contact_email": "coolda@wa.com"
    }
]

""";



            Gson gson = new Gson();
            StudentJSON[] stu = gson.fromJson(jsonString, StudentJSON[].class);
            System.out.println(stu);
        }


    }

    class User {

        int id;
        String username;
        @SerializedName(value = "first_name")
        String firstName;
        @SerializedName(value = "last_name")
        String lastName;
        Date dob;
        String house;
        String email;
        String comments;

    }

    class Student extends User {


        private int form;
        @SerializedName(value = "primary_contact_name")
        String primaryContactName;
        @SerializedName(value = "primary_contact_email")
        String primaryContactEmail;
        @SerializedName(value = "secondary_contact_name")
        String secondaryContactName;
        @SerializedName(value = "secondary_contact_email")
        String secondaryContactEmail;

        public Student(int i, String f, String l, Date dob, String h, int fo, String em, String c, String pcn, String pce, String scn, String sce){

            id = i;
            firstName = f;
            lastName = l;
            this.dob = dob;
            house = h;
            form = fo;
            email = em;
            comments = c;
            primaryContactName = pcn;
            primaryContactEmail = pce;
            secondaryContactName = scn;
            secondaryContactEmail = sce;

        }

        public int getId(){
            return this.id;
        }

        public void setId(int id){
            this.id = id;
        }

        public String getUsername(){
            return this.username;
        }

        public String getFirstName(){
            return firstName;
        }

        public void setFirstName(String firstName){
            this.firstName = firstName;
        }

        public String getLastName(){
            return lastName;
        }

        public void setLastName(String lastName){
            this.lastName = lastName;
        }

        public Date getDob(){
            return (Date) dob;
        }

        public void setDob(Date dob){
            this.dob = dob;
        }

        public String getEmail(){
            return email;
        }

        public void setEmail(String email){
            this.email = email;
        }

        public String getHouse(){
            return house;
        }

        public void setHouse(String house){
            this.house = house;
        }

        public int getForm(){
            return form;
        }

        public void setForm(int form){
            this.form = form;
        }

        public String getPrimaryContactName() {
            return primaryContactName;
        }

        public void setPrimaryContactName(String primaryContactName) {
            this.primaryContactName = primaryContactName;
        }

        public String getPrimaryContactEmail() {
            return primaryContactEmail;
        }

        public void setPrimaryContactEmail(String primaryContactEmail) {
            this.primaryContactEmail = primaryContactEmail;
        }

        public String getSecondaryContactName() {
            return secondaryContactName;
        }

        public void setSecondaryContactName(String secondaryContactName) {
            this.secondaryContactName = secondaryContactName;
        }

        public String getSecondaryContactEmail() {
            return secondaryContactEmail;
        }

        public void setSecondaryContactEmail(String secondaryContactEmail) {
            this.secondaryContactEmail = secondaryContactEmail;
        }

        public String getComments(){
            return comments;
        }

        public void setComments(String comments){
            this.comments = comments;
        }
    }

