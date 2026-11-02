package com.example;
import baseclass.BaseTest;
import org.testng.Assert;
import org.testng.annotations.*;


import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;

public class DashboardTest extends BaseTest {

    @Test(priority =11) 
    public void verifyDashboardDisplayed() {

        LoginPage lp = new LoginPage(driver);
        lp.login("Admin", "admin123");
        
        DashboardPage dp = new DashboardPage(driver);
        dp.isDashboardClicked();

        Assert.assertTrue(dp.isDashboardDisplayed());
    }
    @Test(priority = 12)
    public void verifyLoggedInUser() {

        LoginPage lp = new LoginPage(driver);
        lp.login("Admin", "admin123");

        DashboardPage dp = new DashboardPage(driver);

        Assert.assertFalse(dp.getLoggedInUser().isEmpty());
    }
    
}         