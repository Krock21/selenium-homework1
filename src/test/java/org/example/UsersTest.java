package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {
    ChromeDriver driver = new ChromeDriver();

    @BeforeEach
    void init() {
        driver.get("http://localhost:8080/login");
        Users.loginIntoRoot(driver, "a");
    }

    @AfterEach
    void finish() {
        driver.close();
    }

    String getNewUserName(WebDriver driver) {
        List<String> users = Users.loadUsers(driver);
        String newUserName;
        int newName = new Random().nextInt();
        while (true) {
            if (!users.contains(Integer.toString(newName))) {
                newUserName = Integer.toString(newName);
                break;
            }
        }
        return newUserName;
    }

    void createAndDeleteAndCheckUsers(String username, String password) {
        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(username));

        Users.open(driver);
        assertTrue(Users.createUser(driver, username, password, password));

        Users.open(driver);
        assertTrue(Users.loadUsers(driver).contains(username));

        Users.open(driver);
        assertTrue(Users.deleteUser(driver, username));

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(username));
    }

    void createAndDeleteAndCheckUsers(String username) {
        createAndDeleteAndCheckUsers(username, username);
    }

    void checkBadSymbolLogin(String username, Character symbol) {
        Users.open(driver);
        IllegalArgumentException badCharacter = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, username + symbol + username, "abc", "abc")
        );
        assertEquals("Restricted character '" + symbol + "' in the name", badCharacter.getMessage());
    }

    @Test
    void checkLoginsAndPasswordsAndCreationsAndDeletions() {
        Users.open(driver);
        String newUserName = getNewUserName(driver);
        // password is same as login, so check it too
        createAndDeleteAndCheckUsers(newUserName);
        createAndDeleteAndCheckUsers(newUserName + '\'' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '\"' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '\\' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + "\\\\" + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '-' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '_' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + ',' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '.' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '(' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + ')' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '{' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '}' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '`' + newUserName);
        createAndDeleteAndCheckUsers(newUserName + '~' + newUserName);
        checkBadSymbolLogin(newUserName, ' ');
        // \n can't be sent

        // login shouldn't contain characters "<", "/", ">": login
        Users.open(driver);
        IllegalArgumentException badCharacterL = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName + "<" + newUserName, newUserName, newUserName)
        );
        assertEquals("login shouldn't contain characters \"<\", \"/\", \">\": login", badCharacterL.getMessage());
        Users.open(driver);
        IllegalArgumentException badCharacterR = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName + ">" + newUserName, newUserName, newUserName)
        );
        assertEquals("login shouldn't contain characters \"<\", \"/\", \">\": login", badCharacterR.getMessage());
        Users.open(driver);
        IllegalArgumentException badCharacterSlash = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName + "/" + newUserName, newUserName, newUserName)
        );
        assertEquals("login shouldn't contain characters \"<\", \"/\", \">\": login", badCharacterSlash.getMessage());

        //check ' ', '<', '/', '>' in password
        createAndDeleteAndCheckUsers(newUserName, newUserName + " " + newUserName);
        createAndDeleteAndCheckUsers(newUserName, newUserName + "<" + newUserName);
        createAndDeleteAndCheckUsers(newUserName, newUserName + "/" + newUserName);
        createAndDeleteAndCheckUsers(newUserName, newUserName + ">" + newUserName);
    }


    @Test
    void checkPasswordErrors() {
        Users.open(driver);
        String newUserName = getNewUserName(driver);

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        IllegalArgumentException badConfirmation = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName, newUserName, newUserName + "a")
        );
        assertEquals("Password doesn't match!", badConfirmation.getMessage());

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        IllegalArgumentException emptyPasswords = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName, "", "")
        );
        assertEquals("Password is required!", emptyPasswords.getMessage());

        Users.open(driver);
        IllegalArgumentException emptyPasswordAndBadConfirmation = assertThrows(IllegalArgumentException.class,
                () -> Users.createUser(driver, newUserName, "", "abc")
        );
        assertEquals("Password doesn't match!", emptyPasswordAndBadConfirmation.getMessage()); // youtrack errors order

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));
    }

    @Test
    void checkLoginsErrors() {
        Users.open(driver);
        assertEquals("Login is required!",
                assertThrows(IllegalArgumentException.class,
                        () -> Users.createUser(driver, "", "abc", "abc")
                ).getMessage());

        Users.open(driver);
        List<String> users = Users.loadUsers(driver);

        for (String user : users) {
            Users.open(driver);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Users.createUser(driver, user, "abc", "abc")
            );
            if (user.equals("guest") || user.equals("root")) {
                assertEquals("Removing null is prohibited", exception.getMessage());
            } else {
                assertEquals("Value should be unique: login", exception.getMessage());
            }
        }
    }
}