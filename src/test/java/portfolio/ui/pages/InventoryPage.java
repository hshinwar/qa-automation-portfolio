package portfolio.ui.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object for the product list shown after login. */
public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By inventoryList = By.cssSelector(".inventory_list");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** productSlug example: "sauce-labs-backpack" */
    public InventoryPage addToCart(String productSlug) {
        By addButton = By.cssSelector("[data-test='add-to-cart-" + productSlug + "']");
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        return this;
    }

    public int getCartCount() {
        List<WebElement> badges = driver.findElements(cartBadge);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }

    public void openCart() {
        driver.findElement(cartLink).click();
    }
}
