package portfolio.ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import portfolio.ui.pages.CheckoutPage;
import portfolio.ui.pages.InventoryPage;
import portfolio.ui.pages.LoginPage;

public class CheckoutTests extends BaseTest {

    @Test(description = "A user can add two items to the cart and complete an order")
    public void endToEndPurchase() {
        new LoginPage(driver).open(BASE_URL).login("standard_user", "secret_sauce");

        InventoryPage inventory = new InventoryPage(driver);
        Assert.assertTrue(inventory.isLoaded(), "Product list should load after login");

        inventory.addToCart("sauce-labs-backpack").addToCart("sauce-labs-bike-light");
        Assert.assertEquals(inventory.getCartCount(), 2, "Cart badge should show 2 items");

        inventory.openCart();
        CheckoutPage checkout = new CheckoutPage(driver);
        Assert.assertEquals(checkout.getCartItemCount(), 2, "Cart should contain 2 items");

        checkout.startCheckout();
        checkout.enterShippingInfo("Test", "Tester", "20147");
        checkout.finishOrder();

        Assert.assertEquals(checkout.getConfirmationMessage(), "Thank you for your order!");
    }
}
