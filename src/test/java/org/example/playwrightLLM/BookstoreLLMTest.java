package org.example.playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AI-Generated Playwright Test using Playwright MCP Agent
 * 
 * Prompt used: "Test search for earbuds on depaul.bncollege.com, filter by brand JBL,
 * color Black, price Over $50, click the JBL Quantum product, verify product details,
 * add to cart, verify cart shows 1 item, proceed to checkout as guest, fill contact info,
 * verify pickup and payment details, go back to cart, remove item, and verify cart is empty."
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreLLMTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        boolean isCI = System.getenv("CI") != null;
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(isCI)
                .setSlowMo(isCI ? 0 : 500));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
        page.setDefaultTimeout(60000);
    }

    @AfterAll
    static void closeBrowser() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    @Order(1)
    @DisplayName("Search and filter earbuds, add JBL Quantum to cart")
    void searchFilterAndAddToCart() {
        // Navigate to bookstore
        page.navigate("https://depaul.bncollege.com/");

        // Search for earbuds
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForTimeout(3000);

        // Apply Brand filter: JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                .filter(new Locator.FilterOptions().setHasText("brand JBL"))
                .getByRole(AriaRole.IMG).click();
        page.waitForTimeout(2000);

        // Apply Color filter: Black
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black"))
                .locator("svg").first().click();
        page.waitForTimeout(2000);

        // Apply Price filter: Over $50
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("#facet-price").locator("label").first().click();
        page.waitForTimeout(2000);

        // Select the JBL Quantum product
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.waitForTimeout(3000);

        // Verify product details
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming");
        assertThat(page.locator("body")).containsText("668972707");
        assertThat(page.locator("body")).containsText("$164.98");

        // Add to cart and verify
        page.getByLabel("Add to cart").click();
        page.waitForTimeout(5000);
        assertThat(page.locator("body")).containsText("1 item");

        // Navigate to cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
        page.waitForTimeout(3000);
    }

    @Test
    @Order(2)
    @DisplayName("Verify cart contents, apply promo code, and checkout")
    void verifyCartAndCheckout() {
        // Verify cart page
        assertThat(page.locator("body")).containsText("Your Shopping Cart");
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("$164.98");

        // Verify order summary
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Test invalid promo code
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(3000);
        assertThat(page.locator("body")).containsText("not valid");

        // Proceed to checkout
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).nth(1).click();
        page.waitForTimeout(5000);
    }

    @Test
    @Order(3)
    @DisplayName("Proceed as guest")
    void proceedAsGuest() {
        assertThat(page.locator("body")).containsText("Create Account");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        page.waitForTimeout(5000);
    }

    @Test
    @Order(4)
    @DisplayName("Fill contact information and continue")
    void fillContactInformation() {
        assertThat(page.locator("body")).containsText("Contact Information");

        // Fill in contact details
        page.getByPlaceholder("Please enter your first name").fill("Avi");
        page.getByPlaceholder("Please enter your last name").fill("Anand");
        page.getByPlaceholder("Please enter a valid email").fill("Ogumon45@gmail.com");
        page.getByPlaceholder("Please enter a valid phone").fill("8475329582");

        // Verify sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(5000);
    }

    @Test
    @Order(5)
    @DisplayName("Verify pickup information and continue")
    void verifyPickupInformation() {
        // Verify contact info displayed correctly
        assertThat(page.locator("body")).containsText("Avi");
        assertThat(page.locator("body")).containsText("Anand");
        assertThat(page.locator("body")).containsText("Ogumon45@gmail.com");

        // Verify pickup location
        assertThat(page.locator("body")).containsText("DePaul University Loop Campus");
        assertThat(page.locator("body")).containsText("I'll pick them up");

        // Verify sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(5000);
    }

    @Test
    @Order(6)
    @DisplayName("Verify payment page totals with tax and return to cart")
    void verifyPaymentAndReturnToCart() {
        // Verify final totals with tax
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("17.22");
        assertThat(page.locator("body")).containsText("185.20");

        // Verify item
        assertThat(page.locator("body")).containsText("JBL Quantum");

        // Go back to cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
        page.waitForTimeout(3000);
    }

    @Test
    @Order(7)
    @DisplayName("Remove item from cart and verify cart is empty")
    void removeItemAndVerifyEmpty() {
        page.getByLabel("Remove product JBL Quantum").click();
        page.waitForTimeout(3000);
        assertThat(page.locator("body")).containsText("empty");
    }
}