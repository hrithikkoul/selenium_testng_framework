package com.example;

import org.testng.Assert;
import org.testng.annotations.Test;
import baseclass.BaseTest;
import com.example.pages.LoginPage;
import com.example.pages.DashboardPage;
import com.example.pages.LeavePage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LeaveTest extends BaseTest {

    @Test
    public void verifyCompleteLeaveLifecycleMultiUserRole() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        
        // --- Static Login Configurations ---
        String masterAdmin = "Admin";
        String masterAdminPass = "admin123";
        String targetedLeaveType = "CAN - Matternity";
        
        // Dynamic Date Calculator
        LocalDate today = LocalDate.now();
        long offset = (System.currentTimeMillis() % 20) + 1; 
        LocalDate futureFromDate = today.plusYears(1).plusDays(offset);
        LocalDate futureToDate = futureFromDate.plusDays(2); 
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dynamicFromDate = futureFromDate.format(formatter);
        String dynamicToDate = futureToDate.format(formatter);

        try {
            // =========================================================================
            // [STEP 1: PIM DATA CAPTURE] - Login and fetch the Name from PIM Column 1
            // =========================================================================
            System.out.println("\n--- [PHASE 1] Logging in as Admin to access PIM Module ---");
            loginPage.login(masterAdmin, masterAdminPass);
            Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Master Admin Login Failed!");
            
            LeavePage leavePage = new LeavePage(driver);
            
            
            String targetEmployeeName = leavePage.captureEmployeeNameFromPIMGrid();
            System.out.println("Will now process Leave flow for Employee: " + targetEmployeeName);
            System.out.println("\n--- [PHASE 2] Moving to Leave Menu to Add Entitlement ---");
            dashboardPage.clickLeaveMenu(); // Standard side menu interaction
            
            System.out.println("Adding 15.00 days quota balance...");
            leavePage.addEmployeeEntitlement(targetEmployeeName, targetedLeaveType, "15.00");
            
            // Navigation reset to clean form context
            leavePage.navigateToLeaveList();
            dashboardPage.clickLeaveMenu();

            // =========================================================================
            // [STEP 3: LEAVE ASSIGNMENT/REQUEST] - Input the fetched name into Leave Form
            // =========================================================================
            System.out.println("\n--- [PHASE 3] Assigning Leave via form input for: " + targetEmployeeName + " ---");
            
            leavePage.selectEmployeeByKeyword(targetEmployeeName);
            leavePage.selectLeaveType(targetedLeaveType);
            leavePage.assignLeaveDates(dynamicFromDate, dynamicToDate);
            leavePage.clickAssignButton();
            System.out.println("Leave assigned cleanly without any 'Searching...' dropdown delays.");

            // =========================================================================
            // [STEP 4: APPROVAL & REVIEWS] - Filter Roster and Check Balance Deductions
            // =========================================================================
            System.out.println("\n--- [PHASE 4] Moving to Leave List grid for final verification ---");
            leavePage.navigateToLeaveList();
            leavePage.searchLeaveRecordsForEmployee(targetEmployeeName);
            
            double balanceBeforeApproval = leavePage.getFirstRowLeaveBalance();
            System.out.println("Verified Balance BEFORE Approval: " + balanceBeforeApproval);
            
            System.out.println("Clicking Approve button element row action...");
            leavePage.approveFirstRowLeaveRequest();
            
            // Table reload trigger
            leavePage.searchLeaveRecordsForEmployee(targetEmployeeName);
            
            String postActionStatus = leavePage.getFirstRowLeaveStatus();
            double balanceAfterApproval = leavePage.getFirstRowLeaveBalance();
            
            System.out.println("[FINAL STATE] Status: " + postActionStatus);
            System.out.println("[FINAL STATE] Remaining Quota Balance: " + balanceAfterApproval);
            
            // Core Logic Asserts
            Assert.assertTrue(postActionStatus.contains("Scheduled"), "Leave row did not transition to Scheduled status!");
            Assert.assertTrue(balanceAfterApproval < balanceBeforeApproval, "Database logic error: Days were not subtracted!");
            
            System.out.println("\n🎉 COMPLETED! PIM-driven dynamic data extraction test run successful!");

        } catch (Exception e) {
            Assert.fail("E2E Test workflow threw an unexpected element error exception: " + e.getMessage());
        }
    }
}