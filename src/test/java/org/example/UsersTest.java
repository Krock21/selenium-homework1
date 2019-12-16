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
        driver.get("http:/localhost:8080/users");
        String newUserName = getNewUserName(driver);

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.createUser(driver, newUserName, newUserName, newUserName));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.deleteUser(driver, newUserName));

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.createUser(driver, newUserName, "a", "a"));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertTrue(Users.deleteUser(driver, newUserName));

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));
    }


    @Test
    void checkPassword() {
        driver.get("http:/localhost:8080/users");
        String newUserName = getNewUserName(driver);

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, newUserName, newUserName, newUserName + "a"));

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));

        driver.get("http://localhost:8080/users");
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, newUserName, "", ""));

        driver.get("http://localhost:8080/users");
        assertFalse(Users.loadUsers(driver).contains(newUserName));
    }

    @Test
    void checkEmptyLogin() {
        driver.get("http://localhost:8080/users");
        assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, "", "abc", "abc"));
    }

    @Test
    void checkLogins() {
        driver.get("http://localhost:8080/users");
        List<String> users = Users.loadUsers(driver);

        for(String user : users) {
            driver.get("http://localhost:8080/users");
            assertThrows(IllegalArgumentException.class, () -> Users.createUser(driver, user, "abc", "abc"));
        }
    }
}