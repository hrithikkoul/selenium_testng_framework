package com.example;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import baseclass.BaseTest;
import com.example.pages.LoginPage;
import com.example.pages.DashboardPage;
import com.example.pages.AdminPage;

public class AdminTest extends BaseTest {

    // ==========================================
    // TEST 1: Data-Driven User Creation Loop
    // ==========================================
    @Test(dataProvider = "employeeNameProvider")
    public void verifyAddAndSearchNewUser(String nameKeyword) throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");
        
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard page didn't display!");

        AdminPage adminPage = dashboardPage.clickAdminMenu();
        adminPage.clickAddButton();
        
        String rawTimestamp = String.valueOf(System.currentTimeMillis()); 
        String shortTimestamp = rawTimestamp.substring(rawTimestamp.length() - 11); 
        String uniqueUsername = "QA_" + shortTimestamp; 
        
        adminPage.createNewUser("ESS", nameKeyword, "Enabled", uniqueUsername, "Password@123");

        adminPage.searchUser(uniqueUsername);
        
        String actualUsername = adminPage.getFirstRecordUsername();
        Assert.assertEquals(actualUsername, uniqueUsername, "The created user was not found in the search results!");
    }

    // ==========================================
    // TEST 2: Combination & Negative Search Filters
    // ==========================================
    @Test
    public void verifyCombinationAndNegativeSearchFilters() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");
        
        DashboardPage dashboardPage = new DashboardPage(driver);
        AdminPage adminPage = dashboardPage.clickAdminMenu();
        
        adminPage.clickAddButton();
        String rawTimestamp = String.valueOf(System.currentTimeMillis()); 
        String uniqueUsername = "QA_" + rawTimestamp.substring(rawTimestamp.length() - 11); 
        
        adminPage.createNewUser("ESS", "a", "Enabled", uniqueUsername, "Password@123");
        
        System.out.println("Executing matching combination filter test...");
        adminPage.searchUserWithFilters(uniqueUsername, "ESS", "Enabled");
        
        String actualUsername = adminPage.getFirstRecordUsername();
        Assert.assertEquals(actualUsername, uniqueUsername, "The filter criteria failed to return the matching user profile!");
        
        adminPage.clickResetButton();
        Thread.sleep(1000);
        
        System.out.println("Executing conflicting negative filter test...");
        adminPage.searchUserWithFilters(uniqueUsername, "ESS", "Disabled"); 
        
        Assert.assertTrue(adminPage.isNoRecordsFoundDisplayed(), "The application grid dynamically populated elements despite search criteria conflicts!");
    }

    // ==========================================
    // TEST 3: User Deletion Cleanup Verification
    // ==========================================
    @Test
    public void verifyDeleteUserCleanup() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");
        
        DashboardPage dashboardPage = new DashboardPage(driver);
        AdminPage adminPage = dashboardPage.clickAdminMenu();
        
        adminPage.clickAddButton();
        String rawTimestamp = String.valueOf(System.currentTimeMillis()); 
        String uniqueUsername = "DEL_" + rawTimestamp.substring(rawTimestamp.length() - 11); 
        adminPage.createNewUser("ESS", "a", "Enabled", uniqueUsername, "Password@123");
        
        adminPage.searchUser(uniqueUsername);
        
        System.out.println("Deleting user profile: " + uniqueUsername);
        adminPage.deleteFirstUserInList();
        
        adminPage.clickResetButton();
        adminPage.searchUser(uniqueUsername);
        Assert.assertTrue(adminPage.isNoRecordsFoundDisplayed(), "The user was not successfully deleted from the system!");
    }

   
    @DataProvider(name = "employeeNameProvider")
    public Object[][] getEmployeeData() {
        return new Object[][] {
            { "a" }  
        };
    }
}