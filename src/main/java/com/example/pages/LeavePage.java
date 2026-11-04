package com.example.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LeavePage {

    WebDriver driver;

    public LeavePage(WebDriver driver) {
        this.driver = driver;
    }

    // ==========================================
    //                 LOCATORS
    // ==========================================
    
    // Global Navigation Menus
    private By leaveListMenu = By.xpath("//a[normalize-space()='Leave List']");
    private By applyLeaveSubMenu = By.xpath("//a[normalize-space()='Apply']");
    private By pimModuleMenu = By.xpath("//span[text()='PIM'] | //a[contains(@href, 'pim/viewPimModule')]");
    private By userDropdown = By.xpath("//span[@class='oxd-userdropdown-tab']");
    private By logoutLink = By.xpath("//a[normalize-space()='Logout']");
    
    // PIM Employee List Grid Locators
    private By pimTableRows = By.xpath("//div[@class='oxd-table-body']/div[@class='oxd-table-card']");
    private By pimFirstNameCell = By.xpath(".//div[@role='cell'][3]"); // Standard First Name column index

    // Form Dropdowns & Inputs
    private By employeeNameField = By.xpath("//input[@placeholder='Type for hints...']");
    private By leaveTypeDropdown = By.xpath("//label[text()='Leave Type']/parent::div/following-sibling::div//div[@class='oxd-select-text-input']");
    private By assignButton = By.xpath("//button[@type='submit']");
    
    // Calendar Pickers
    private By fromDateField = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[1] | (//label[text()='From Date']/parent::div/following-sibling::div//input)[1]");
    private By toDateField = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[2] | (//label[text()='To Date']/parent::div/following-sibling::div//input)[1]");
    
    // Phase 1: Entitlements Allocation
    private By entitlementsMenu = By.xpath("//span[contains(normalize-space(),'Entitlements')]");
    private By addEntitlementsOption = By.xpath("//ul[@class='oxd-dropdown-menu']/li/a[contains(normalize-space(),'Add Entitlements')]");
    private By entitlementInputBox = By.xpath("//label[text()='Entitlement']/parent::div/following-sibling::div/input");
    private By saveButton = By.xpath("//button[@type='submit'][normalize-space()='Save']");
    private By confirmDialogButton = By.xpath("//button[contains(@class,'oxd-button--secondary')][normalize-space()='Confirm']");

    // Results Tables & Actions
    private By searchButton = By.xpath("//button[@type='submit']");
    private By statusDropdown = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div[2]/div[2]/div/div[1]/div/div[7]");
    private By approveButton = By.xpath("(//div[@class='oxd-table-body']/div[@class='oxd-table-card'])[1]//button[normalize-space()='Approve']");
    private By firstRowStatusCell = By.xpath("(//div[@class='oxd-table-body']/div[@class='oxd-table-card'])[1]//div[@role='cell'][count(//div[@class='oxd-table-header']//div[@role='columnheader'][normalize-space()='Status']/preceding-sibling::div) + 1]");
    private By firstRowBalanceCell = By.xpath("(//div[@class='oxd-table-body']/div[@class='oxd-table-card'])[1]//div[@role='cell'][count(//div[@class='oxd-table-header']//div[@role='columnheader'][contains(normalize-space(),'Leave Balance')]/preceding-sibling::div) + 1]");

    
    //              ACTION METHODS
   

   
    public String captureEmployeeNameFromPIMGrid() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("Clicking on PIM Menu...");
        wait.until(ExpectedConditions.elementToBeClickable(pimModuleMenu)).click();
        Thread.sleep(2500); // Wait for PIM database grid to load fully
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(pimTableRows));
        List<WebElement> rows = driver.findElements(pimTableRows);
        
        if (rows.isEmpty()) {
            throw new RuntimeException("PIM Grid is completely empty! No employees found.");
        }
        
        
        WebElement firstRow = rows.get(0);
        String firstName = firstRow.findElement(pimFirstNameCell).getText().trim();
        
        System.out.println("🎯 Scraped target First Name from PIM successfully: " + firstName);
        return firstName;
    }

    public void addEmployeeEntitlement(String employeeName, String leaveType, String quotaAmount) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(entitlementsMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(addEntitlementsOption)).click();
        Thread.sleep(1500);
        
        selectEmployeeByKeyword(employeeName);
        selectLeaveType(leaveType);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(entitlementInputBox));
        driver.findElement(entitlementInputBox).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        driver.findElement(entitlementInputBox).sendKeys(quotaAmount);
        driver.findElement(saveButton).click();
        
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmDialogButton));
            driver.findElement(confirmDialogButton).click();
        } catch (Exception e) {
            System.out.println("No duplicate overwrite popup appeared.");
        }
        Thread.sleep(3000); 
    }

    
    public void selectEmployeeByKeyword(String nameToType) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(employeeNameField));
        
        
        driver.findElement(employeeNameField).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        Thread.sleep(300);
        
        
        driver.findElement(employeeNameField).sendKeys(nameToType);
        System.out.println("Typed Name Keyword: " + nameToType);
        
        
        By listboxContainer = By.xpath("//div[@role='listbox']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(listboxContainer));
        Thread.sleep(800); 
        
        
        By realOption = By.xpath("//div[@role='listbox']//div[@role='option']");
        wait.until(ExpectedConditions.elementToBeClickable(realOption));
        
        driver.findElement(realOption).click();
        System.out.println("Dropdown element linked and clicked cleanly.");
        Thread.sleep(1500); 
    }

    public void selectLeaveType(String leaveType) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(leaveTypeDropdown));
        driver.findElement(leaveTypeDropdown).click();
        String optionXpath = String.format("//div[@role='listbox']//span[text()='%s']", leaveType);
        driver.findElement(By.xpath(optionXpath)).click();
    }

    public void assignLeaveDates(String fromDate, String toDate) throws InterruptedException {
        driver.findElement(fromDateField).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        driver.findElement(fromDateField).sendKeys(fromDate);
        driver.findElement(toDateField).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        driver.findElement(toDateField).sendKeys(toDate);
        Thread.sleep(500);
    }

    public void clickAssignButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(assignButton));
        driver.findElement(assignButton).click();
    }

    public void clickApplyLeaveSubMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(applyLeaveSubMenu)).click();
    }

    public void navigateToLeaveList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(leaveListMenu));
        driver.findElement(leaveListMenu).click();
    }

    public void searchLeaveRecordsForEmployee(String employeeName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameField));
        
        try {
            By clearStatusChipButton = By.xpath("//span[contains(@class, 'oxd-chip')]//i[contains(@class, 'oxd-chip-close')]");
            while (!driver.findElements(clearStatusChipButton).isEmpty()) {
                driver.findElement(clearStatusChipButton).click();
                Thread.sleep(300);
            }
        } catch (Exception e) {}

        selectStatusFilter("Pending Approval");
        selectEmployeeByKeyword(employeeName);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        driver.findElement(searchButton).click();
        Thread.sleep(3000); 
    }

    public String getFirstRowLeaveStatus() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowStatusCell));
        return driver.findElement(firstRowStatusCell).getText();
    }

    public double getFirstRowLeaveBalance() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowBalanceCell));
        return Double.parseDouble(driver.findElement(firstRowBalanceCell).getText().trim());
    }  

    public void approveFirstRowLeaveRequest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(approveButton));
        driver.findElement(approveButton).click();
        Thread.sleep(3000); 
    }

    public void logoutUserRole() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(userDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        Thread.sleep(2000); 
    }
    public void selectStatusFilter(String status) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown));
        driver.findElement(statusDropdown).click();
        String optionXpath = String.format("//div[@role='listbox']//span[text()='%s']", status);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(optionXpath)));
        driver.findElement(By.xpath(optionXpath)).click();
}
}