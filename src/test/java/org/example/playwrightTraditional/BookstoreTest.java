package org.example.playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    // Test data
    private static final String FIRST_NAME = "Avi";
    private static final String LAST_NAME = "Anand";
    private static final String EMAIL = "Ogumon45@gmail.com";
    private static final String PHONE = "8475329582";

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(500));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
        page.setDefaultTimeout(60000);
    }

    @AfterAll
    static void teardown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    // ===================== TestCase 1: Bookstore =====================
    @Test
    @Order(1)
    @DisplayName("TestCase Bookstore - Search, filter, and add JBL earbuds to cart")
    void testBookstore() {
        // Navigate to DePaul bookstore
        page.navigate("https://depaul.bncollege.com/");
        page.waitForLoadState();

        // Enter "earbuds" in the search box and press Enter
        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForTimeout(3000);

        // Click on "Brand" filter and select "JBL"
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.waitForTimeout(1000);
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
                .filter(new Locator.FilterOptions().setHasText("brand JBL"))
                .getByRole(AriaRole.IMG).click();
        page.waitForTimeout(2000);

        // Click on "Color" filter and select "Black"
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.waitForTimeout(1000);
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black"))
                .locator("svg").first().click();
        page.waitForTimeout(2000);

        // Click on "Price" filter and select "Over $50"
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.waitForTimeout(1000);
        page.locator("#facet-price").locator("label").first().click();
        page.waitForTimeout(2000);

        // Click on the JBL Quantum product link
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.waitForLoadState();
        page.waitForTimeout(3000);

        // Close any popup that might appear
        try {
            page.locator("iframe[title=\"Close message\"]").contentFrame()
                    .getByLabel("Close message from company").click(new Locator.ClickOptions().setTimeout(5000));
            page.waitForTimeout(1000);
        } catch (Exception e) {
            // Popup may not appear, continue
        }

        // Assert product name
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming");

        // Assert SKU number
        assertThat(page.locator("body")).containsText("668972707");

        // Assert price
        assertThat(page.locator("body")).containsText("$164.98");

        // Assert product description is present
        assertThat(page.locator("body")).containsText("JBL Quantum");

        // Add 1 to cart
        page.getByLabel("Add to cart").click();
        page.waitForTimeout(5000);

        // Assert "1 Items" in cart
        assertThat(page.locator("body")).containsText("1 item");

        // Click Cart icon
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
        page.waitForLoadState();
        page.waitForTimeout(3000);
    }

    // ===================== TestCase 2: Your Shopping Cart Page =====================
    @Test
    @Order(2)
    @DisplayName("TestCase Your Shopping Cart Page - Verify cart details and proceed to checkout")
    void testShoppingCartPage() {
        // Assert at cart page
        assertThat(page.locator("body")).containsText("Your Shopping Cart");

        // Assert product name
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        // Assert quantity is 1
        assertThat(page.locator("body")).containsText("1");

        // Assert price
        assertThat(page.locator("body")).containsText("$164.98");

        // Select "FAST In-Store Pickup"
        try {
            page.locator("label").filter(new Locator.FilterOptions().setHasText("FAST In-Store PickupDePaul"))
                    .locator("span").nth(1).click(new Locator.ClickOptions().setTimeout(10000));
            page.waitForTimeout(3000);
        } catch (Exception e) {
            // May already be selected
        }

        // Assert sidebar subtotal
        assertThat(page.locator("body")).containsText("164.98");

        // Assert handling
        assertThat(page.locator("body")).containsText("3.00");

        // Assert taxes TBD
        assertThat(page.locator("body")).containsText("TBD");

        // Assert estimated total
        assertThat(page.locator("body")).containsText("167.98");

        // Enter promo code TEST
        page.getByLabel("Enter Promo Code").click();
        page.getByLabel("Enter Promo Code").fill("TEST");

        // Click APPLY
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(3000);

        // Assert promo code reject message is displayed
        assertThat(page.locator("body")).containsText("not valid");

        // Click PROCEED TO CHECKOUT (use nth(1) as there are two buttons)
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).nth(1).click();
        page.waitForLoadState();
        page.waitForTimeout(5000);
    }

    // ===================== TestCase 3: Create Account Page =====================
    @Test
    @Order(3)
    @DisplayName("TestCase Create Account Page - Verify create account and proceed as guest")
    void testCreateAccountPage() {
        // Assert "Create Account" label is present
        assertThat(page.locator("body")).containsText("Create Account");

        // Select "Proceed as Guest"
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        page.waitForLoadState();
        page.waitForTimeout(5000);
    }

    // ===================== TestCase 4: Contact Information Page =====================
    @Test
    @Order(4)
    @DisplayName("TestCase Contact Information Page - Fill contact info and continue")
    void testContactInformationPage() {
        // Assert at Contact Information page
        assertThat(page.locator("body")).containsText("Contact Information");

        // Enter first name
        page.getByPlaceholder("Please enter your first name").click();
        page.getByPlaceholder("Please enter your first name").fill(FIRST_NAME);

        // Enter last name
        page.getByPlaceholder("Please enter your last name").click();
        page.getByPlaceholder("Please enter your last name").fill(LAST_NAME);

        // Enter email
        page.getByPlaceholder("Please enter a valid email").click();
        page.getByPlaceholder("Please enter a valid email").fill(EMAIL);

        // Enter phone number
        page.getByPlaceholder("Please enter a valid phone").click();
        page.getByPlaceholder("Please enter a valid phone").fill(PHONE);

        // Assert sidebar subtotal
        assertThat(page.locator("body")).containsText("164.98");

        // Assert handling
        assertThat(page.locator("body")).containsText("3.00");

        // Assert taxes TBD
        assertThat(page.locator("body")).containsText("TBD");

        // Assert estimated total
        assertThat(page.locator("body")).containsText("167.98");

        // Click CONTINUE
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForLoadState();
        page.waitForTimeout(5000);
    }

    // ===================== TestCase 5: Pickup Information =====================
    @Test
    @Order(5)
    @DisplayName("TestCase Pickup Information - Verify pickup details and continue")
    void testPickupInformation() {
        // Assert Contact Information: name, email, phone are correct
        assertThat(page.locator("body")).containsText(FIRST_NAME);
        assertThat(page.locator("body")).containsText(LAST_NAME);
        assertThat(page.locator("body")).containsText(EMAIL);

        // Assert Pick Up location
        assertThat(page.locator("body")).containsText("DePaul University Loop Campus");

        // Assert selected Pickup Person ("I'll pick them up")
        assertThat(page.locator("body")).containsText("I'll pick them up");

        // Assert sidebar order subtotal
        assertThat(page.locator("body")).containsText("164.98");

        // Assert handling
        assertThat(page.locator("body")).containsText("3.00");

        // Assert taxes TBD
        assertThat(page.locator("body")).containsText("TBD");

        // Assert estimated total
        assertThat(page.locator("body")).containsText("167.98");

        // Assert pickup item and price are correct
        assertThat(page.locator("body")).containsText("JBL Quantum");
        assertThat(page.locator("body")).containsText("164.98");

        // Click CONTINUE
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForLoadState();
        page.waitForTimeout(5000);
    }

    // ===================== TestCase 6: Payment Information =====================
    @Test
    @Order(6)
    @DisplayName("TestCase Payment Information - Verify totals with tax and go back to cart")
    void testPaymentInformation() {
        // Assert sidebar order subtotal
        assertThat(page.locator("body")).containsText("164.98");

        // Assert handling
        assertThat(page.locator("body")).containsText("3.00");

        // Assert taxes (now calculated)
        assertThat(page.locator("body")).containsText("17.22");

        // Assert total
        assertThat(page.locator("body")).containsText("185.20");

        // Assert pickup item and price
        assertThat(page.locator("body")).containsText("JBL Quantum");
        assertThat(page.locator("body")).containsText("164.98");

        // Click "< BACK TO CART"
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
        page.waitForLoadState();
        page.waitForTimeout(3000);
    }

    // ===================== TestCase 7: Your Shopping Cart (Delete) =====================
    @Test
    @Order(7)
    @DisplayName("TestCase Your Shopping Cart - Delete product and verify empty cart")
    void testDeleteFromCart() {
        // Delete product from cart
        page.getByLabel("Remove product JBL Quantum").click();
        page.waitForTimeout(3000);

        // Assert cart is empty
        assertThat(page.locator("body")).containsText("empty");
    }
}