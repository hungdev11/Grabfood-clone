package com.order;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

public class OrderTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "E://build_tools//chromedriver-win64//chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://localhost:3000");

        WebElement cartButton = driver.findElement(By.id("cart-button"));
        cartButton.click();

        List<WebElement> cartItems = driver.findElements(By.cssSelector("[data-testid='cart-item']"));
        if (!cartItems.isEmpty()) {
            System.out.println("✅ Có sản phẩm trong giỏ hàng: " + cartItems.size());
        } else {
            System.out.println("❌ Không có sản phẩm trong giỏ hàng!");
            driver.quit();
            return;
        }

        sleep(3000);

        WebElement checkButton = driver.findElement(By.id("checkout-button"));
        checkButton.click();

        sleep(3000);

        WebElement orderNote = driver.findElement(By.id(("note")));
        orderNote.sendKeys("TEST NOTE INPUT");

        sleep(2000);

        WebElement orderButton = driver.findElement(By.id("order-button"));
        orderButton.click();
        System.out.println("✅ Đặt hàng thành công");
        sleep(3000);
        driver.quit();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
