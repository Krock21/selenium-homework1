package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class Users {

    final String defaultBaseURL = "http://localhost:8080";

    private Users() {
    }

    public static void open(WebDriver driver, String baseURL) {
        driver.get(baseURL + "/users");
        (new WebDriverWait(driver, 2)).until(ExpectedConditions.elementToBeClickable(By.className("ring-menu__logo__img")));
    }

    public static void open(WebDriver driver) {
        open(driver, "http://localhost:8080");
    }

    public static void loginIntoRoot(WebDriver driver, String rootPassword) {
        driver.findElement(By.name("l.L.login")).sendKeys("root");
        driver.findElement(By.name("l.L.password")).sendKeys(rootPassword);
        driver.findElement(By.id("id_l.L.loginButton")).click();
    }

    static List<String> loadUsers(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[starts-with(@id,'id_l.U.usersList.UserLogin.editUser')]")));
        return driver.findElements(By.xpath("//*[starts-with(@id,'id_l.U.usersList.UserLogin.editUser')]"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
    }


    public static boolean createUser(WebDriver driver, String login, String password, String confirmPassword) {
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("id_l.U.createNewUser"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("id_l.U.cr.login"))).sendKeys(login);
        driver.findElement(By.id("id_l.U.cr.password")).sendKeys(password);
        driver.findElement(By.id("id_l.U.cr.confirmPassword")).sendKeys(confirmPassword);
        driver.findElement(By.id("id_l.U.cr.createUserOk")).click();
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(By.id("id_l.E.AdminBreadcrumb.AdminBreadcrumb"))) != null;
        } catch (TimeoutException exception) {
            WebDriverWait errorsWait = new WebDriverWait(driver, 1);
            try {
                errorsWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.className("error-bulb2")
                        )
                ).click();
                throw new IllegalArgumentException(
                        errorsWait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                        By.className("error-tooltip")
                                )
                        ).getText());
            } catch (TimeoutException bulb2NotFound) {
                try {
                    throw new IllegalArgumentException(
                            errorsWait.until(
                                    ExpectedConditions.presenceOfElementLocated(
                                            By.className("errorSeverity")
                                    )
                            ).getText());
                } catch (TimeoutException errorSeverityNotFound) {
                    throw new IllegalArgumentException("unexpected error");
                }
            }
        }
    }

    public static boolean deleteUser(WebDriver driver, String login) {
        List<WebElement> users = driver.findElements(By.xpath("//*[starts-with(@id,'id_l.U.usersList.UserLogin.editUser')]"))
                .stream().filter(webElement -> webElement.getText().equals(login)).collect(Collectors.toList());
        if (users.size() != 1) {
            return false;
        } else {
            WebElement user = users.get(0);
            String userId = user.getAttribute("id").substring("id_l.U.usersList.UserLogin.editUser".length()); // _aa_bb
            String deleteID = "id_l.U.usersList.deleteUser" + userId + "_" + login.replace('-', '_');
            WebDriverWait wait = new WebDriverWait(driver, 2);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.id(deleteID)));
                WebElement deleteA = driver.findElement(By.id(deleteID));
                deleteA.click();
                driver.switchTo().alert().accept();
                return wait.until(ExpectedConditions.elementToBeClickable(By.className("message")))
                        .findElement(By.xpath("//table/tbody/tr/td[2]/ul/li")).getText().equals("User "+ login +" deleted.");
            } catch (Exception e) {
                System.out.println("Cannot find \"delete\" button");
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
}
