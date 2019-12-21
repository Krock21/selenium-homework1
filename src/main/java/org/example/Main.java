package org.example;

import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        Users.loginIntoRoot(driver, "a");
        Users.open(driver);
        List<String> users = Users.loadUsers(driver);
        for (String user : users) {
            System.out.println(user);
        }

        Users.open(driver);
        boolean ans = Users.createUser(driver, "zzy", "zz", "zz");
        System.out.println(ans);
        Users.open(driver);
        ans = Users.deleteUser(driver, "zzy");
        System.out.println(ans);
        driver.close();
    }
}
