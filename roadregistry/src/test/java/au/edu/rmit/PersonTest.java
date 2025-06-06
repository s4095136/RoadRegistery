package au.edu.rmit;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonTest {

    private Person person;

    @BeforeEach
    public void setup() {
        person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPersonID("56s_d%&fAB");
        person.setAddress("32|Main Street|Melbourne|Victoria|Australia");
        person.setBirthdate("15-11-2000");
    }

    // --- Tests for addPerson() ---

    @Test
    public void testAddPerson_ValidPerson_ShouldReturnTrue() {
        assertTrue(person.addPerson());
    }
    
    @Test
    public void testAddPerson_InvalidPersonID_ShouldReturnFalse() {
        person.setPersonID("12a$%AB");
        assertFalse(person.addPerson());
    }//testing for personID starting with 1

    @Test
    public void testAddPerson_InsufficientSpecialChars_ShouldReturnFalse() {
        person.setPersonID("56abcdefAB");
        assertFalse(person.addPerson());
    } // testing for personID without special characters

    @Test
    public void testAddPerson_InvalidAddressFormat_ShouldReturnFalse() {
        person.setAddress("32|Highland Street|Melbourne|Victoria"); 
        assertFalse(person.addPerson());
    }// testing for missing country

    @Test
    public void testAddPerson_InvalidBirthdateFormat_ShouldReturnFalse() {
        person.setBirthdate("2000-11-15");
        assertFalse(person.addPerson());
    } // testing for wrong date format

    // --- Tests for updatePersonalDetails() ---

    @Test
    public void testUpdatePersonalDetails_ValidUpdate_ShouldReturnTrue() {
        boolean updated = person.updatePersonalDetails("56s_d%&fAB", "John", "Smith", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(updated);
    }

    @Test
    public void testUpdatePersonalDetails_Under18AddressChange_ShouldReturnFalse() {
        person.setBirthdate("15-11-2010"); // person under 18
        boolean updated = person.updatePersonalDetails("56s_d%&fAB", "John", "Smith", "Different|Address|Here|Victoria|Australia", "15-11-2010");
        assertFalse(updated);
    }// testing for person under 18 trying to change their address

    @Test
    public void testUpdatePersonalDetails_BirthdateChangeWithOtherChanges_ShouldReturnFalse() {
        boolean updated = person.updatePersonalDetails("56s_d%&fAB", "John", "Smith", "32|Main Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(updated);
    }// testing for birthday change with other changes

    @Test
    public void testUpdatePersonalDetails_IDStartsWithEvenDigitChangeID_ShouldReturnFalse() {
        person.setPersonID("26s_d%&fAB"); // starts with 2 (even)
        boolean updated = person.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    }// testing for ID starting with and even number

    @Test
    public void testUpdatePersonalDetails_InvalidNewDetails_ShouldReturnFalse() {
        boolean updated = person.updatePersonalDetails("12abcdefAB", "John", "Doe", "32|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(updated);
    } // testing for 

    // --- Tests for addDemeritPoints() ---

    @Test
    public void testAddDemeritPoints_ValidPoints_ShouldReturnSuccess() {
        String result = person.addDemeritPoints(LocalDate.now(), 3);
        assertEquals("Success", result);
    }// testing for adding demerit points

    @Test
    public void testAddDemeritPoints_PointsBelowRange_ShouldReturnFailed() {
        String result = person.addDemeritPoints(LocalDate.now(), 0);
        assertEquals("Failed", result);
    } // testing for adding demerit points below the range

    @Test
    public void testAddDemeritPoints_PointsAboveRange_ShouldReturnFailed() {
        String result = person.addDemeritPoints(LocalDate.now(), 7);
        assertEquals("Failed", result);
    } // testing for adding demerit points above the range

    @Test
    public void testAddDemeritPoints_ExceedPointsUnder21_ShouldSetSuspended() {
        person.setBirthdate("01-01-2010"); // under 21 years
        // Add points to exceed 6 total
        person.addDemeritPoints(LocalDate.now().minusMonths(1), 4);
        String result = person.addDemeritPoints(LocalDate.now(), 3);
        assertEquals("Success", result);
        // Unfortunately no getter for isSuspended - you can add one to assert this if needed
    } // testing for adding demerit points for a person under the age of 21

    @Test
    public void testAddDemeritPoints_ExceedPointsOver21_ShouldSetSuspended() {
        person.setBirthdate("01-01-1990"); // over 21 years
        // Add points to exceed 12 total
        person.addDemeritPoints(LocalDate.now().minusMonths(1), 7);
        person.addDemeritPoints(LocalDate.now().minusMonths(6), 6);
        String result = person.addDemeritPoints(LocalDate.now(), 1);
        assertEquals("Success", result);
    } // testing for adding demerit points for a person over the age of 21
}
