package Artemis.Models;

import com.google.gson.annotations.SerializedName;

public class Marks {

   private int id;
   private Assignment assignment;
   @SerializedName(value = "mark_awarded")
   private double markAwarded;
   @SerializedName(value = "class_id")
   private int classId;

   public Marks(int id, Assignment assignment, double markAwarded, int classId) {
      this.id = id;
      this.assignment = assignment;
      this.markAwarded = markAwarded;
      this.classId = classId;
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
