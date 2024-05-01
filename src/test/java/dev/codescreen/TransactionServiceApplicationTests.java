package dev.codescreen;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codescreen.classes.AuthorizationRequest;
import dev.codescreen.classes.Amount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authorizeTransactionTest() throws Exception {
        AuthorizationRequest request1 = new AuthorizationRequest("user123", new Amount("50", "USD", "DEBIT"));
        AuthorizationRequest request2 = new AuthorizationRequest("user123", new Amount("50", "USD", "CREDIT"));

        mockMvc.perform(MockMvcRequestBuilders.put("/load/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.balance.amount").value("50.0"))
                .andExpect(jsonPath("$.balance.currency").value("USD"));

        mockMvc.perform(MockMvcRequestBuilders.put("/authorization/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.responseCode").value("APPROVED"))
                .andExpect(jsonPath("$.balance.amount").value("0.0"))
                .andExpect(jsonPath("$.balance.currency").value("USD"));
    }

    @Test
    public void testLoadRequestMissingUserId() throws Exception {
        AuthorizationRequest request = new AuthorizationRequest(null, new Amount("50", "USD", "CREDIT"));
        mockMvc.perform(MockMvcRequestBuilders.put("/load/123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testLoadRequestInvalidAmountFormat() throws Exception {
        AuthorizationRequest request = new AuthorizationRequest("user123", new Amount("abc", "USD", "CREDIT"));
        mockMvc.perform(MockMvcRequestBuilders.put("/load/123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testLoadRequestUnsupportedCurrency() throws Exception {
        AuthorizationRequest request = new AuthorizationRequest("user123", new Amount("50", "EUR", "CREDIT"));
        mockMvc.perform(MockMvcRequestBuilders.put("/load/123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testLoadRequestMissingDebitOrCreditField() throws Exception {
        AuthorizationRequest request = new AuthorizationRequest("user123", new Amount("50", "USD", null));
        mockMvc.perform(MockMvcRequestBuilders.put("/load/123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
