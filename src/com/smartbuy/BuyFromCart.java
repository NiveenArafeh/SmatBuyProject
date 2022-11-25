package com.smartbuy;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BuyFromCart {
	public WebDriver driver;
	public int numberOfTry = 10;
	public int numberOfItemsInventory;
	SoftAssert softassertProcess = new SoftAssert();

	@BeforeTest
	public void LoginToWebSite() {

		driver = runBrowser("chrome", driver);
		driver.manage().window().maximize();
		driver.get("https://smartbuy-me.com/smartbuystore/");
		driver.findElement(By.xpath("/html/body/main/header/div[2]/div/div[2]/a")).click();

	}

	@Test(priority = 1)
	public void verifyAddManyItemsToCartForSamsung_TV() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	
		String msg = "";
		for (int i = 0; i < numberOfTry; i++)

		{
			driver.findElement(By.xpath(
					"//*[@id=\"newtab-Featured\"]/div/div[1]/div/div/div/div[3]/div/div[3]/div[1]/div/div/form[1]/div[1]/button"))
					.click();
			driver.findElement(By.xpath("//a[normalize-space()='Continue']")).click();

			msg = driver.findElement(By.xpath("//*[@id=\"addToCartLayer\"]/div[1]")).getText().toString();

			if (msg.contains("Sorry")) {
				driver.findElement(By.xpath("//*[@id=\"addToCartLayer\"]/a[1]")).click();
				numberOfItemsInventory = i;
				break;

			}
		}
		System.out.println("The number of Items inside inventory is " + numberOfItemsInventory);

		Double priceInDouble = reformatNumber(driver.findElement(By.xpath("//div[@class='item__price']")),"JOD");

		Double expectedPrice = reformatNumber(
				driver.findElement(By.xpath("//div[@class='item__total js-item-total hidden-xs hidden-sm']")),"JOD");
		System.out.println("The expected price is :" + expectedPrice);
		softassertProcess.assertEquals(expectedPrice, priceInDouble * numberOfItemsInventory, "It was Failed becuase ");
		softassertProcess.assertAll();
		System.out.println("====================================");
	}

	@Test(priority = 2)
	public void verifyPriceAfterDiscountForSamsung_TV() {
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.navigate().back();
		Double percentage=reformatNumber(driver.findElement(By.xpath("//span[contains(text(),'27.1')]")), "%");
		Double priceBeforeDiscount=reformatNumber(driver.findElement(By.xpath("//span[normalize-space()='369 JOD']")), "JOD");
		
		Double actualPrice = priceBeforeDiscount-(priceBeforeDiscount*(percentage/100));
		System.out.println("The price after discount :"+actualPrice);
		Double expectedPrice=reformatNumber(driver.findElement(By.xpath("//span[normalize-space()='269 JOD']")), "JOD");
		System.out.println("The Expected price is "+expectedPrice);
		
	  //hint: rounding the actual price to decimal digit by this formula :Math.round(actualPrice*10)/10.0
		softassertProcess.assertEquals(Math.round(actualPrice*10)/10.0, expectedPrice,"It was Failed becuase ");
		softassertProcess.assertAll();
		
	
	}
	//Method to convert any string to double number by parsing regex
	public double reformatNumber(WebElement elemnet, String regix) {
		
		String priceOfItem = elemnet.getText().toString();
		String[] priceOfItemWithoutJOD = priceOfItem.split(regix);
		String priceOfItemWithoutJODSP = priceOfItemWithoutJOD[0].trim();
		String updatedPrice = priceOfItemWithoutJODSP.replace(",", "");
		Double priceInDouble = Double.parseDouble(updatedPrice);
		// System.out.println(priceInDouble);
		return priceInDouble;
	}



	public WebDriver runBrowser(String browser, WebDriver driver) {
		if (browser.equalsIgnoreCase("firefox")) {

			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("chrome")) {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();

		} else if (browser.equalsIgnoreCase("edge")) {
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
		}
		return driver;

	}
}
