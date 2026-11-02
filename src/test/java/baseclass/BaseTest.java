package baseclass;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {

        WebDriverManager.chromedriver().setup();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless","false"));
        ChromeOptions Options = new ChromeOptions();
        if (headless) {
        	Options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        Options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        driver = new ChromeDriver(Options );

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        if (!headless) {
        	driver.manage().window().maximize();
        }
        driver.get("https://opensource-demo.orangehrmlive.com/");
    } 

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}