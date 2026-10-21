package portfolio.ui;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import portfolio.ui.pages.InventoryPage;
import portfolio.ui.pages.LoginPage;

public class LoginTests extends BaseTest {

    @Test(description = "A valid user can log in and sees the product list")
    public void validLoginShowsProducts() {
        new LoginPage(driver).open(BASE_URL).login("standard_user", "secret_sauce");

        Assert.assertTrue(new InventoryPage(driver).isLoaded(),
                "Product list should be visible after a valid login");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][] {
            {"locked_out_user", "secret_sauce",
                "Epic sadface: Sorry, this user has been locked out."},
            {"standard_user", "wrong_password",
                "Epic sadface: Username and password do not match any user in this service"},
            {"", "secret_sauce",
                "Epic sadface: Username is required"},
            {"standard_user", "",
                "Epic sadface: Password is required"}
        };
    }

    @Test(dataProvider = "invalidLogins",
          description = "Invalid logins are rejected with the correct error message")
    public void invalidLoginShowsError(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(driver).open(BASE_URL);
        loginPage.login(username, password);

        Assert.assertEquals(loginPage.getErrorMessage(), expectedError);
    }
}
