package Artemis.Models;

import com.google.gson.annotations.SerializedName;

public class Marks {

   private int id;
   private Assignment assignment;
   @SerializedName(value = "mark_awarded")
   private double markAwarded;
   @SerializedName(value = "class_id")
   private int classId;
   private String assignmentName;
   private double assignmentMaxMarks;
   private String markAwardedStringRepresentation;
   private String percentage;

   public Marks(int id, Assignment assignment, double markAwarded, int classId) {
      this.id = id;
      this.assignment = assignment;
      this.markAwarded = markAwarded;
      this.classId = classId;
      this.assignmentName = assignment.getAssignmentName();
      this.assignmentMaxMarks = assignment.getMaxMarks();
      this.markAwardedStringRepresentation = Double.toString(markAwarded) + "/" + Double.toString(assignmentMaxMarks);
      this.percentage = String.valueOf(markAwarded / assignmentMaxMarks) + "%";
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Assignment getAssignment() {
      return assignment;
   }

   public String getAssignmentName() {
      return assignmentName;
   }

   public double getAssignmentMaxMarks() {
      return assignmentMaxMarks;
   }

   public void setAssignmentMaxMarks(double assignmentMaxMarks) {
      this.assignmentMaxMarks = assignmentMaxMarks;
   }

   public String getMarkAwardedStringRepresentation() {
      return markAwardedStringRepresentation;
   }

   public void setMarkAwardedStringRepresentation(String markAwardedStringRepresentation) {
      this.markAwardedStringRepresentation = markAwardedStringRepresentation;
   }

   public String getPercentage() {
      return percentage;
   }

   public void setPercentage(String percentage) {
      this.percentage = percentage;
   }

   public void setAssignmentName(String assignmentName) {
      this.assignmentName = assignmentName;
   }

   public void setAssignment(Assignment assignment) {
      this.assignment = assignment;
   }

   public double getMarkAwarded() {
      return markAwarded;
   }

   public void setMarkAwarded(double markAwarded) {
      this.markAwarded = markAwarded;
   }

   public int getClassId() {
      return classId;
   }

   public void setClassId(int classId) {
      this.classId = classId;
   }
}
