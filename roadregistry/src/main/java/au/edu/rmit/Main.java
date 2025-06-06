package au.edu.rmit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
public static void main(String[] args) {
    // Test cases 
    
    // ==== ADD PERSON ====
    System.out.println("Adding person (invalid ID):");
    Person personFailAdd = new Person();
    personFailAdd.setFirstName("Alice");
    personFailAdd.setLastName("Smith");
    personFailAdd.setPersonID("12ABcdefXY"); // ❌ Invalid (starts with 1)
    personFailAdd.setAddress("123|Lygon Street|Carlton|Victoria|Australia");
    personFailAdd.setBirthdate("01-01-2000");
    boolean addedFail = personFailAdd.addPerson();
    System.out.println(addedFail ? "✅ Person successfully added." : "❌ Failed to add person.");

    System.out.println("\nAdding person (valid ID):");
    Person personSuccessAdd = new Person();
    personSuccessAdd.setFirstName("John");
    personSuccessAdd.setLastName("Doe");
    // personSuccessAdd.setPersonID("56$#a&*AB");
    personSuccessAdd.setPersonID("56$#a1*&AB");    
    personSuccessAdd.setAddress("32|Main Street|Melbourne|Victoria|Australia");
    personSuccessAdd.setBirthdate("15-11-2000");
    boolean addedSuccess = personSuccessAdd.addPerson();
    System.out.println(addedSuccess ? "✅ Person successfully added." : "❌ Failed to add person.");


    // ==== UPDATE PERSONAL DETAILS ====
    System.out.println("\nUpdating personal details (invalid):");
    boolean updateFail = personSuccessAdd.updatePersonalDetails("77ABcdefXY", "John", "Smith", "999|Fake Street|Nowhere|NSW|Australia", "15-11-2000");
    System.out.println(updateFail ? "✅ Update successful." : "❌ Update failed."); // ❌ Invalid state

    System.out.println("\nUpdating personal details (valid):");
    boolean updateSuccess = personSuccessAdd.updatePersonalDetails("56$#a1*&AB", "John", "Smith", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
    System.out.println(updateSuccess ? "✅ Update successful." : "❌ Update failed.");


    // ==== ADD DEMERIT POINTS ====
    System.out.println("\nAdding demerit points (invalid):");
    LocalDate badOffenseDate = LocalDate.parse("01-06-2023", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String demeritFail = personSuccessAdd.addDemeritPoints(badOffenseDate, 10); // ❌ Invalid (10 > 6)
    System.out.println("Demerit update result: " + demeritFail);

    System.out.println("\nAdding demerit points (valid):");
    LocalDate goodOffenseDate = LocalDate.parse("02-06-2023", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String demeritSuccess = personSuccessAdd.addDemeritPoints(goodOffenseDate, 5); // ✅ Valid
    System.out.println("Demerit update result: " + demeritSuccess);
    
}
} // workflow showcase

// Trigger workflow
