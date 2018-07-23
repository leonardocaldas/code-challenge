package com.github.leonardocaldas.n26codechallenge.integrated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.util.IntegratedTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode.PAYLOAD_INVALID;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegratedTest {

    private static final String RESOURCE = "/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IntegratedTestUtil util;

    @Test
    public void should_create_valid_transaction() throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(100.0)
                .timestamp(util.getCurrentTimestamp())
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_create_invalid_transaction_without_amount() throws Exception {
        Transaction transaction = Transaction.builder()
                .timestamp(util.getCurrentTimestamp())
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(PAYLOAD_INVALID.getCode())));
    }

    @Test
    public void should_create_invalid_transaction_with_amount_null() throws Exception {
        Transaction transaction = Transaction.builder()
                .timestamp(util.getCurrentTimestamp())
                .amount(null)
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(PAYLOAD_INVALID.getCode())));
    }

    @Test
    public void should_create_invalid_transaction_without_timestamp() throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(10.0)
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(PAYLOAD_INVALID.getCode())));
    }

    @Test
    public void should_create_invalid_transaction_with_timestamp_null() throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(10.0)
                .timestamp(null)
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(PAYLOAD_INVALID.getCode())));
    }

    @Test
    public void should_not_create_transaction_too_old() throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(100.0)
                .timestamp(util.getOutOfRangeTimestamp())
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}