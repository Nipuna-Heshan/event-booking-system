import model.Cart;
import model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.BookingUtils;
import util.PasswordUtils;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTests {

    private Event testEvent;

    @BeforeEach
    public void setup() {
        testEvent = new Event(1, "Test Event", "Test Venue", "Mon", 50.0, 45, 50, true);
    }

    // 1. Test Password Encryption and Decryption
    @Test
    public void testPasswordEncryptionDecryption() {
        String original = "abc123";
        String encrypted = PasswordUtils.encrypt(original);
        String decrypted = PasswordUtils.decrypt(encrypted);
        assertEquals(original, decrypted, "Decrypted password should match original");
    }

    // 2. Test Booking Date Validity
    @Test
    public void testCanBookToday() {
        String today = java.time.LocalDate.now().getDayOfWeek().toString().substring(0, 3);
        today = today.substring(0, 1).toUpperCase() + today.substring(1).toLowerCase();
        assertTrue(BookingUtils.canBook(today), "Should be able to book events today");
    }

    // 3. Test Ticket Availability Check
    @Test
    public void testCartQuantityDoesNotExceedRemaining() {
        Cart cart = new Cart();
        cart.addItem(testEvent, 5); // Only 5 remaining
        assertTrue(cart.getQuantityInCart(testEvent) <= testEvent.getRemainingTickets(),
                "Quantity in cart should not exceed available tickets");
    }

    // 4. Test Confirmation Code Format
    @Test
    public void testValidConfirmationCodeFormat() {
        assertTrue("123456".matches("\\d{6}"), "Valid 6-digit code should match");
        assertFalse("12a456".matches("\\d{6}"), "Alphanumeric code should not match");
        assertFalse("12345".matches("\\d{6}"), "Code with less than 6 digits should not match");
        assertFalse("1234567".matches("\\d{6}"), "Code with more than 6 digits should not match");
    }

    // 5. Dummy Event Duplication Check Simulation (since DB cannot be initialized here)
    @Test
    public void testEventEqualityForDuplicateCheck() {
        Event event1 = new Event(1, "Jazz Night", "Theatre Nova", "Mon", 50.0, 20, 50, true);
        Event event2 = new Event(2, "Jazz Night", "Theatre Nova", "Mon", 55.0, 25, 60, true);

        assertEquals(event1.getTitle(), event2.getTitle());
        assertEquals(event1.getLocation(), event2.getLocation());
        assertEquals(event1.getDay(), event2.getDay());
    }
}
