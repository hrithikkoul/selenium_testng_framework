package com.example;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.example.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumTest {

    WebDriver driver;
    ExtentReports extent = ExtentManager.getInstance();
    ExtentTest test;

    @BeforeMethod
    
    public void setup() {
        WebDriverManager.chromedriver().setup();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        if (!headless) {
            driver.manage().window().maximize();
        }
        driver.get("https://opensource-demo.orangehrmlive.com/");
    }

   // 1️⃣ Positive Test
    @Test (priority =1)
    public void loginWithValidCredentials() {
        test = extent.createTest("Login with Valid Credentials");
        LoginPage lp = new LoginPage(driver);

        lp.login("Admin", "admin123");
        Assert.assertTrue(lp.isLoginSuccessful());

        test.pass("Login successful with valid credentials");
        lp.logout();
        test.pass("Login successful and logout successful");
    }
   
//
    // 2️⃣ Invalid Password
    @Test(priority =3)
    public void loginWithInvalidPassword() {
        test = extent.createTest("Login with Invalid Password");
        LoginPage lp = new LoginPage(driver);

        lp.login("valid.user", "wrongPass");
        Assert.assertFalse(lp.getErrorMessage().isEmpty());

        test.pass("Error shown for invalid password");
    }
 
    // 3️⃣ Invalid Username
    @Test(priority =4)
    public void loginWithInvalidUsername() {
        test = extent.createTest("Login with Invalid Username");
        LoginPage lp = new LoginPage(driver);

        lp.login("wrongUser", "Sky@0566");
        Assert.assertFalse(lp.getErrorMessage().isEmpty());

        test.pass("Error shown for invalid username");
    }

    // 4️⃣ Invalid Username & Password
    @Test(priority =5)
    public void loginWithInvalidCredentials() {
        test = extent.createTest("Login with Invalid Credentials");
        LoginPage lp = new LoginPage(driver);

        lp.login("wrongUser", "wrongPass");
        Assert.assertFalse(lp.getErrorMessage().isEmpty());

        test.pass("Error shown for invalid credentials");
    }

    // 5️⃣ Empty Username & Password
    @Test(priority =6)
    public void loginWithEmptyFields() {

        test = extent.createTest("Login with Empty Fields");

        LoginPage lp = new LoginPage(driver);

        lp.login("", "");

        Assert.assertEquals(lp.getErrorMessage2(), "Required");

        test.pass("Validation message shown for empty fields");
    }
    

    // 6️⃣ Empty Username
    @Test(priority =7)
    public void loginWithEmptyUsername() {
        test = extent.createTest("Login with Empty Username");
        LoginPage lp = new LoginPage(driver);

        lp.login("", "validPassword");
        Assert.assertFalse(lp.getErrorMessage2().isEmpty());

        test.pass("Validation shown for empty username");
    }

    // 7 Empty Password
    @Test(priority =8)
    public void loginWithEmptyPassword() {
        test = extent.createTest("Login with Empty Password");
        LoginPage lp = new LoginPage(driver);

        lp.login("valid.user", "");
        Assert.assertFalse(lp.getErrorMessage2().isEmpty());

        test.pass("Validation shown for empty password");
    }
    
    // 8️Invalid Email Format
    @Test(priority =9)
    public void loginWithInvalidEmailFormat() {
        test = extent.createTest("Login with Invalid Email Format");
        LoginPage lp = new LoginPage(driver);

        lp.login("abc123", "password");
        Assert.assertFalse(lp.getErrorMessage().isEmpty());

        test.pass("Validation shown for invalid email format");
    }
    
    // 9️ Password Masking
    @Test(priority =10)
    public void verifyPasswordIsMasked() {
        test = extent.createTest("Verify Password Masking");
        LoginPage lp = new LoginPage(driver);

        Assert.assertTrue(lp.isPasswordMasked());

        test.pass("Password field is masked");
    }
    
    // 🔟 Multiple Failed Attempts
    @Test
    public void loginMultipleFailedAttempts() {
        test = extent.createTest("Login with Multiple Failed Attempts");
        LoginPage lp = new LoginPage(driver);

        for (int i = 0; i < 3; i++) {
            lp.login("valid.user", "wrongPass");
        }

        Assert.assertFalse(lp.getErrorMessage().isEmpty());

        test.pass("Error shown after multiple failed attempts");
    }
    
    @AfterMethod
    public void tearDown() throws InterruptedException {
    	Thread.sleep(5000);
        driver.quit();
        extent.flush();
    }
}
