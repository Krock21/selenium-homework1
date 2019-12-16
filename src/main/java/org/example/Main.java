package org.example;

import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        Users.loginIntoRoot(driver, "a");
        driver.get("http:/localhost:8080/users");
        List<String> users = Users.loadUsers(driver);
        for (String user : users) {
            System.out.println(user);
        }

        driver.get("http://localhost:8080/users");
        boolean ans = Users.createUser(driver, "zzy", "zz", "zz");
        System.out.println(ans);
        driver.get("http://localhost:8080/users");
        ans = Users.deleteUser(driver, "zzy");
        System.out.println(ans);
        driver.close();
    }
}
