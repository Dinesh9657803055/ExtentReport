package com.bhavindholakiya;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class ExtentReportDemo {
    WebDriver driver;
    WebDriverWait wait;
    ExtentSparkReporter reporter;
    ExtentReports extent;
    String browserName = "firefox";
    String baseURL = "https://app.skilldb.com/login";
    String reportPath = System.getProperty("user.dir")+"\\report\\index.html";
    @BeforeTest
    public void DriverSetup(){
        if (browserName.equalsIgnoreCase("chrome")){
            driver = new ChromeDriver();
        } else if (browserName.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else if (browserName.equalsIgnoreCase("edge")){
            driver = new EdgeDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(baseURL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        reporter = new ExtentSparkReporter(reportPath);
        extent = new ExtentReports();

        reporter.config().setReportName("SkillDB - Smoke Test Report");
        reporter.config().setDocumentTitle("SkillDB - Test Result");

        extent.attachReporter(reporter);
        extent.setSystemInfo("QA Person", "Bhavin Dholakiya");
        extent.setSystemInfo("Environment", "Production");
    }
    @Test (priority = 5)
    public void doLogin(){
        ExtentTest test = extent.createTest("Login with valid credential");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("bhavin.dholakiya@openxcell.com");
        test.log(Status.INFO, "Email entered");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("QAwsedrf@23");
        test.log(Status.INFO, "Password entered");
        driver.findElement(By.cssSelector("button[type='submit'] span[class='Polaris-Button__Text']")).click();
        WebElement userProfile =  driver.findElement(By.cssSelector(".Polaris-TopBar-Menu__ActivatorWrapper"));
        wait.until(ExpectedConditions.visibilityOf(userProfile));
        if (userProfile.isDisplayed()){
          userProfile.click();
          wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".Polaris-ActionList__Text"))).click();
        } else {
            Assert.assertFalse(userProfile.isDisplayed());
            test.fail("Result do not match!");
        }
    }
    @Test (priority = 3)
    public void doLoginWithInvalidEmail(){
        ExtentTest test = extent.createTest("Login with Invalid Email");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("bhavin.dholakiya@openxcell");
        test.log(Status.INFO, "Email entered");
        String errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message.SFPro-Medium.plp-1"))).getText();
        Assert.assertEquals(errorMessage, "Invalid Email Format");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("QAwsedrf@23");
        test.log(Status.INFO, "Password entered");
        test.fail("Result do not match!");
    }
    @Test (priority = 2)
    public void doLoginWithInvalidPassword(){
        ExtentTest test = extent.createTest("Login with Invalid Password");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("bhavin.dholakiya@openxcell.com");
        test.log(Status.INFO, "Email entered");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("QAwse32drf@23");
        test.log(Status.INFO, "Password entered");
        driver.findElement(By.cssSelector("button[type='submit'] span[class='Polaris-Button__Text']")).click();
        String errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='alert']"))).getText();
        Assert.assertEquals(errorMessage, "Incorrect password!");
    }
    @Test (priority = 4)
    public void doLoginWithInvalidDetails(){
        ExtentTest test = extent.createTest("Login with invalid credential");
        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("bhavin@openxcell.asa");
        test.log(Status.INFO, "Email entered");
        String errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message.SFPro-Medium.plp-1"))).getText();
        Assert.assertEquals(errorMessage, "Invalid Email Format");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("QAwse3");
        test.log(Status.INFO, "Password entered");
    }
    @AfterTest
    public void tearDown(){
        extent.flush();
        driver.quit();
    }
}