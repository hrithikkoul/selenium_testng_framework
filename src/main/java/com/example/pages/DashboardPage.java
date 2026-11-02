package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {

    WebDriver driver;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }
    private By navigationDashboard =By.xpath("//span[text()='Dashboard']");

    private By dashboardHeader = By.xpath("//h6[text()='Dashboard']");
    private By profileDropdown = By.cssSelector(".oxd-userdropdown-name");
 // 1. Add the Admin side menu locator if it isn't there
    private By adminMenuLink = By.xpath("//span[text()='Admin']");
    private By leaveMenu = By.xpath("//a[normalize-space()='Leave'] | //span[text()='Leave']/parent::a");
    public void isDashboardClicked() {
         driver.findElement(navigationDashboard).click();
    }   
    
    public boolean isDashboardDisplayed() {
        return driver.findElement(dashboardHeader).isDisplayed();
    }

    public String getLoggedInUser() {
        return driver.findElement(profileDropdown).getText();
    }
    public AdminPage clickAdminMenu() {
        driver.findElement(adminMenuLink).click();
        return new AdminPage(driver);
    }
    public LeavePage clickLeaveMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(leaveMenu));
        driver.findElement(leaveMenu).click();
        return new LeavePage(driver); // Returns the new page object we just made!
    }
}