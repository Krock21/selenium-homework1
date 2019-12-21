package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

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
        int newName = 1;
        while (true) {
            if (!users.contains(Integer.toString(newName))) {
                newUserName = Integer.toString(newName);
                break;
            }
        }
        return newUserName;
    }

    @Test
    void createAndDeleteNewUser() {
        Users.open(driver);
        String newUserName = getNewUserName(driver);

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertTrue(Users.createUser(driver, newUserName, newUserName, newUserName));

        Users.open(driver);
        assertTrue(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertTrue(Users.deleteUser(driver, newUserName));

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertTrue(Users.createUser(driver, newUserName, "a", "a"));

        Users.open(driver);
        assertTrue(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertTrue(Users.deleteUser(driver, newUserName));

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));
    }


    @Test
    void checkPassword() {
        Users.open(driver);
        String newUserName = getNewUserName(driver);

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, newUserName, newUserName, newUserName + "a"));

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        Users.open(driver);
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, newUserName, "", ""));

        Users.open(driver);
        assertFalse(Users.loadUsers(driver).contains(newUserName));
    }

    @Test
    void checkEmptyLogin() {
        Users.open(driver);
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, "", "abc", "abc"));
    }

    @Test
    void checkLogins() {
        Users.open(driver);
        List<String> users = Users.loadUsers(driver);

        for(String user : users) {
            Users.open(driver);
            assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, user, "abc", "abc"));
        }
    }
}