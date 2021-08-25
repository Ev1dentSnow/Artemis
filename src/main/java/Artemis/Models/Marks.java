package Artemis.Models;

import com.google.gson.annotations.SerializedName;
import java.sql.Date;

public class Marks {

   private int id;
   private Assignment assignment;
   @SerializedName(value = "marks_awarded")
   private double marksAwarded;
   @SerializedName(value = "class_id")
   private int classId;

   public Marks(int id, Assignment assignment, double marksAwarded, int classId) {
      this.id = id;
      this.assignment = assignment;
      this.marksAwarded = marksAwarded;
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

   public double getMarksAwarded() {
      return marksAwarded;
   }

   public void setMarksAwarded(double marksAwarded) {
      this.marksAwarded = marksAwarded;
   }

   public int getClassId() {
      return classId;
   }

   public void setClassId(int classId) {
      this.classId = classId;
   }
}
