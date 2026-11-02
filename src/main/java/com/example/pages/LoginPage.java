package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    WebDriver driver;

   
    By usernameField = By.xpath("//input[@placeholder='Username']");
    By passwordField = By.xpath("//input[@placeholder='Password']");
    By loginButton = By.xpath("//button[@type='submit']");

    By errorMsg = By.xpath("//div[contains(text(),'incorrect')]");

    
    By profileIcon = By.xpath("//img[@class='oxd-userdropdown-img']");
    public By Logout = By.xpath("//a[normalize-space()='Logout']");
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // ---------- Actions ----------
    public void login(String user, String pass) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(user);  

        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(pass);

        driver.findElement(loginButton).click();
        
    }
    public void logout() {
        driver.findElement(profileIcon).click();
        driver.findElement(Logout).click();
    }

    // ---------- Validations ----------
    public String getErrorMessage() {
        return driver.findElement(By.xpath("//div[@role='alert']")).getText();
    }

    public boolean isLoginSuccessful() {
        return driver.findElements(profileIcon).size() > 0;
    }
    
    public boolean isPasswordMasked() {
        return driver.findElement(passwordField)
                     .getAttribute("type")
                     .equals("password");
    }
    private By requiredError = By.xpath("//span[text()='Required']");

    public String getErrorMessage2() {
        return driver.findElement(requiredError).getText();
    }

	public void enterUsername(String string) {
		// TODO Auto-generated method stub
		
	}
}
