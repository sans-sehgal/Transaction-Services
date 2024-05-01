package dev.codescreen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import dev.codescreen.classes.Amount;
import dev.codescreen.utils.RequestValidator;

public class RequestValidatorTests {

    @Test
    public void testIsValidAmount_ValidAmount_ReturnsTrue() {
        Amount validAmount = new Amount("100", "USD", "CREDIT");
        assertTrue(RequestValidator.isValidAmount(validAmount, "LOAD"));
    }

    @Test
    public void testIsValidAmount_NullAmount_ReturnsFalse() {
        assertFalse(RequestValidator.isValidAmount(null, null));
    }

    @Test
    public void testInvalidAmountFormat() {
        assertFalse(RequestValidator.isValidAmount(new Amount("abc", "USD", "CREDIT"), "LOAD"));
        assertFalse(RequestValidator.isValidAmount(new Amount("-50", "USD", "CREDIT"), "LOAD"));
    }

    @Test
    public void testMissingFields() {
        assertFalse(RequestValidator.isValidAmount(new Amount("50", null, "CREDIT"), "LOAD"));
        assertFalse(RequestValidator.isValidAmount(new Amount("50", "USD", null), "LOAD"));
    }

}
