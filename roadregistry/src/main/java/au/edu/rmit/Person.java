package au.edu.rmit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

public class Person {

    // Personal information and demerit-related data
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<LocalDate, Integer> demeritPoints; // Key: date of offense, Value: demerit points
    private boolean isSuspended;

    // Main method: runs manual test cases for addPerson()
    public static void main(String[] args) {
        // Each of these tests checks one specific validation failure
        testPerson("12a$%AB", "Valid", "15-11-2000", "Too short personID");
        testPerson("01a$%dABX", "Valid", "15-11-2000", "Invalid start digits in personID");
        testPerson("56abcdefAB", "Valid", "15-11-2000", "Missing special characters in personID");
        testPerson("56s_d%&f12", "Valid", "15-11-2000", "Invalid end characters in personID");
        testPerson("56s_d%&fAB", "32|Highland Street|Melbourne|Victoria", "15-11-2000", "Invalid address format");
        testPerson("56s_d%&fAB", "32|Highland Street|Melbourne|NSW|Australia", "15-11-2000", "Invalid state");
        testPerson("56s_d%&fAB", "32|Highland Street|Melbourne|Victoria|Australia", "2000-11-15", "Invalid birthdate format");
        testPerson("56s_d%&fAB", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-2000", "Valid person");

    // Create person with valid info
        Person p = new Person();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setPersonID("56s_d%&fAB");
        p.setAddress("32|Main Street|Melbourne|Victoria|Australia");
        p.setBirthdate("15-11-2000");

        // Add the person to the file
        if (p.addPerson()) {
            System.out.println("Person added.");
        }

        // Test updatePersonalDetails (valid update)
        boolean updated = p.updatePersonalDetails("56s_d%&fAB", "John", "Smith", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        System.out.println(updated ? "Update successful." : "Update failed.");

        // Test addDemeritPoints
        LocalDate offenseDate = LocalDate.parse("15-10-2023", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String result = p.addDemeritPoints(offenseDate, 5);
        System.out.println("Add demerit result: " + result);

    }

    // Helper function to run addPerson() and print results for a test case
    public static void testPerson(String personID, String address, String birthdate, String testName) {
        Person p = new Person();
        p.firstName = "Test";
        p.lastName = "User";
        p.personID = personID;
        p.address = address;
        p.birthdate = birthdate;

        System.out.println("Test: " + testName);
        boolean result = p.addPerson();
        System.out.println("Result: " + (result ? "Passed ✅" : "Failed ❌"));
        System.out.println("--------------------------------------------------");
    }

    // Adds a person to the system (validates then writes to TXT file)
    public boolean addPerson() {
        // Validate personID format
        if (!personID.matches("^[2-9]{2}.{6}[A-Z]{2}$")) {
            System.out.println("Error: Invalid ID format. Must start with digits 2–9, contain 2+ special characters (between chars 3–8), and end with 2 uppercase letters.");
            return false;
        }

        // Count special characters between positions 3 and 8
        int special = 0;
        for (int i = 2; i < 8; i++) {
            if (!Character.isLetterOrDigit(personID.charAt(i))) special++;
        }
        if (special < 2) {
            System.out.println("Error: ID must contain at least 2 special characters between characters 3 and 8.");
            return false;
        }

        // Validate address structure and ensure state is Victoria
        String[] parts = address.split("\\|");
        if (parts.length != 5) {
            System.out.println("Error: Address must follow the format Street Number|Street|City|State|Country.");
            return false;
        }
        if (!parts[3].equalsIgnoreCase("Victoria")) {
            System.out.println("Error: State must be Victoria.");
            return false;
        }

        // Validate birthdate format
        try {
            LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Error: Birthdate must follow format DD-MM-YYYY.");
            return false;
        }

        // If all validations pass, write the person data to a text file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("PersonData.txt", true))) {
            bw.write(firstName + " " + lastName + "," + personID + "," + address + "," + birthdate + ",false,\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error: Failed to write to file.");
            return false;
        }
    }

    // Updates the personal details of a person (under strict validation rules)
    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        try {
            // Parse current and new birthdate
            LocalDate currentBirth = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate newBirth = LocalDate.parse(newBirthdate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // Condition 1: If under 18, address can't be changed
            if (Period.between(currentBirth, LocalDate.now()).getYears() < 18 && !newAddress.equals(address)) {
                System.out.println("Error: Person is under 18; address cannot be changed.");
                return false;
            }

            // Condition 2: If changing birthday, nothing else can change
            if (!newBirthdate.equals(birthdate) &&
                (!newID.equals(personID) || !newFirstName.equals(firstName) || !newLastName.equals(lastName) || !newAddress.equals(address))) {
                System.out.println("Error: If changing birthday, no other fields (ID, name, address) can be modified.");
                return false;
            }

            // Condition 3: If ID starts with even digit, it cannot be changed
            if (Character.getNumericValue(personID.charAt(0)) % 2 == 0 && !newID.equals(personID)) {
                System.out.println("Error: ID starts with an even digit and cannot be changed.");
                return false;
            }

            // Temporarily assign new values for validation
            String tempID = personID;
            String tempAddress = address;
            String tempBirthdate = birthdate;

            personID = newID;
            address = newAddress;
            birthdate = newBirthdate;

            // Reuse addPerson for validation logic
            if (!addPerson()) {
                System.out.println("Error: Updated details failed validation.");
                personID = tempID;
                address = tempAddress;
                birthdate = tempBirthdate;
                return false;
            }

            // Apply new values
            firstName = newFirstName;
            lastName = newLastName;
            System.out.println("Update successful.");
            return true;

        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid birthdate format. Use DD-MM-YYYY.");
            return false;
        } catch (Exception e) {
            System.out.println("Error: Unexpected failure during update.");
            return false;
        }
    }

    // Adds demerit points and updates suspension status if thresholds are exceeded
    public String addDemeritPoints(LocalDate offenseDate, int points) {
        // Validate demerit point range
        if (points < 1 || points > 6) {
            System.out.println("Error: Points must be between 1 and 6.");
            return "Failed";
        }

        if (demeritPoints == null) {
            demeritPoints = new HashMap<>();
        }

        // Add the offense
        demeritPoints.put(offenseDate, points);

        try {
            // Determine age of person
            LocalDate birth = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            int age = Period.between(birth, LocalDate.now()).getYears();

            // Calculate total demerit points in the last 2 years
            LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
            int total = 0;
            for (LocalDate d : demeritPoints.keySet()) {
                if (!d.isBefore(twoYearsAgo)) {
                    total += demeritPoints.get(d);
                }
            }

            // Check suspension thresholds based on age
            if ((age < 21 && total > 6) || (age >= 21 && total > 12)) {
                isSuspended = true;
                System.out.println("Notice: License suspended due to excessive demerit points.");
            }

            return "Success";
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid birthdate format.");
            return "Failed";
        }
    }
    public void setFirstName(String firstName) {
    this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}

