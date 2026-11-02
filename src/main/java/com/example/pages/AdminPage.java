package com.example.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AdminPage {

    WebDriver driver;

    
    public AdminPage(WebDriver driver) {
        this.driver = driver;
    }

    // --- Locators (By style) ---
    private By addButton = By.xpath("//button[normalize-space()='Add']");
    private By searchUsernameField = By.xpath("//label[text()='Username']/parent::div/following-sibling::div/input");
    private By searchButton = By.xpath("//button[@type='submit']");
    
    
    // Add User Form Locators
    private By userRoleDropdown = By.xpath("//label[text()='User Role']/parent::div/following-sibling::div//div[@class='oxd-select-text-input']");
    private By employeeNameField = By.xpath("//input[@placeholder='Type for hints...']");
    private By statusDropdown = By.xpath("//label[text()='Status']/parent::div/following-sibling::div//div[@class='oxd-select-text-input']");
    private By newUsernameField = By.xpath("//label[text()='Username']/parent::div/following-sibling::div/input");
    private By passwordField = By.xpath("(//input[@type='password'])[1]");
    private By confirmPasswordField = By.xpath("(//input[@type='password'])[2]");
    private By saveButton = By.xpath("//button[@type='submit']");
    private By noRecordsFoundLabel = By.xpath("//span[normalize-space()='No Records Found'] | //div[@class='oxd-table-body']/div[@class='oxd-table-card']/following-sibling::span");
    private By resetButton = By.xpath("//button[normalize-space()='Reset']");
    private By deleteIconInFirstRow = By.xpath("(//div[@class='oxd-table-card'])[1]//button[descendant::i[contains(@class, 'bi-trash')]]");
    // Targets the "Yes, Delete" button inside OrangeHRM's confirmation modal dialog popup
    private By confirmDeleteButton = By.xpath("//button[normalize-space()='Yes, Delete']");
    // Result Table Locator
    private By firstResultUsernameCell = By.xpath("(//div[@class='oxd-table-card'])[1]//div[@role='cell'][2]");

    // --- Action Methods ---

    public void clickAddButton() {
        driver.findElement(addButton).click();
    }

    public void searchUser(String username) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        wait.until(ExpectedConditions.urlContains("/admin/viewSystemUsers"));
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchUsernameField));
        driver.findElement(searchUsernameField).clear();
        driver.findElement(searchUsernameField).sendKeys(username);
        
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(searchButton));
                driver.findElement(searchButton).click();
                break; 
            } catch (Exception e) {
                attempts++;
                Thread.sleep(500); 
            }
        }
        
        Thread.sleep(3000); 
    }

    public String getFirstRecordUsername() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(firstResultUsernameCell));
        return driver.findElement(firstResultUsernameCell).getText();
    }
    
    public void fillAddUserForm(String username, String password) {
        driver.findElement(newUsernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(confirmPasswordField).sendKeys(password);
        driver.findElement(saveButton).click(); 
    }

    public void selectDropdownOption(By dropdownLocator, String optionText) {
        driver.findElement(dropdownLocator).click();
        String optionXpath = String.format("//div[@role='listbox']//span[text()='%s']", optionText);
        By optionElement = By.xpath(optionXpath);
        driver.findElement(optionElement).click();
    }

    // NEW DYNAMIC METHOD: Types a keyword character, waits for popups, picks the first match
    public String selectEmployeeNameByKeyword(String keyword) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        wait.until(ExpectedConditions.elementToBeClickable(employeeNameField));
        driver.findElement(employeeNameField).clear();
        
        // 1. Type the keyword letter
        driver.findElement(employeeNameField).sendKeys(keyword);
        Thread.sleep(500);
        
        // 2. CRITICAL: Send a Space or Arrow Down to trigger the application's input listeners
        driver.findElement(employeeNameField).sendKeys(Keys.SPACE);
        Thread.sleep(200);
        driver.findElement(employeeNameField).sendKeys(Keys.BACK_SPACE);
        Thread.sleep(2000);
        
        // 3. Wait until the dynamic listbox container shows up on screen
        By listboxOptions = By.xpath("//div[@role='listbox']//div[@role='option']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(listboxOptions));
        
        // 4. Extract the matching text data from the first suggestion row
        String selectedName = driver.findElement(listboxOptions).getText();
        System.out.println("--> Dynamically picked employee name: " + selectedName);
        
        // 5. Explicitly wait for the specific option element to be clickable, then hit it
        wait.until(ExpectedConditions.elementToBeClickable(listboxOptions));
        driver.findElement(listboxOptions).click();
        
        Thread.sleep(1000);
        return selectedName;
    }

    public void createNewUser(String role, String nameKeyword, String status, String username, String password) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        selectDropdownOption(userRoleDropdown, role);
        
        // Calls the smart selector using your data keyword char sequence
        selectEmployeeNameByKeyword(nameKeyword);
        
        selectDropdownOption(statusDropdown, status);
        
        driver.findElement(newUsernameField).clear();
        driver.findElement(newUsernameField).sendKeys(username);
        
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
        
        driver.findElement(confirmPasswordField).clear();
        driver.findElement(confirmPasswordField).sendKeys(password);
        
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        driver.findElement(saveButton).click();
        
        wait.until(ExpectedConditions.urlContains("/admin/viewSystemUsers"));
        Thread.sleep(1500); 
    }
 

    // Click Reset to clear out previous filters cleanly
    public void clickResetButton() {
        driver.findElement(resetButton).click();
    }

    // Advanced search method supporting combination filters
    public void searchUserWithFilters(String username, String role, String status) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/admin/viewSystemUsers"));
        
        // Fill Username if provided
        if (username != null && !username.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchUsernameField));
            driver.findElement(searchUsernameField).clear();
            driver.findElement(searchUsernameField).sendKeys(username);
        }
        
        // Select User Role dropdown if provided
        if (role != null && !role.isEmpty()) {
            selectDropdownOption(userRoleDropdown, role);
        }
        
        // Select Status dropdown if provided
        if (status != null && !status.isEmpty()) {
            selectDropdownOption(statusDropdown, status);
        }
        
        // Execute Search with our anti-stale retry logic
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(searchButton));
                driver.findElement(searchButton).click();
                break; 
            } catch (Exception e) {
                attempts++;
                Thread.sleep(500); 
            }
        }
        Thread.sleep(2500); // Wait for grid reload animation
    }

    // Method to verify if the search results grid is completely empty
    public boolean isNoRecordsFoundDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsFoundLabel)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
        public void deleteFirstUserInList() throws InterruptedException {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            
            wait.until(ExpectedConditions.elementToBeClickable(deleteIconInFirstRow));
            driver.findElement(deleteIconInFirstRow).click();
            
           
            wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
            driver.findElement(confirmDeleteButton).click();
            
            
            Thread.sleep(2000);
        }
    }
