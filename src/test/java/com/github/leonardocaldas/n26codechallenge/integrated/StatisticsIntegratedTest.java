package com.github.leonardocaldas.n26codechallenge.integrated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.util.IntegratedTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsIntegratedTest {

    private static final String TRANSACTION_RESOURCE = "/transactions";
    private static final String STATISTICS_RESOURCE = "/statistics";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionAggregateRepository repository;

    @Autowired
    private IntegratedTestUtil util;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void should_get_empty_statistics() throws Exception {
        mockMvc.perform(get(STATISTICS_RESOURCE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.max", is(0.0)))
                .andExpect(jsonPath("$.min", is(0.0)))
                .andExpect(jsonPath("$.avg", is(0.0)))
                .andExpect(jsonPath("$.sum", is(0.0)))
                .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void should_get_valid_statistics() throws Exception {
        createTransaction(100.0);
        createTransaction(50.0);
        createTransaction(25.0);
        createTransaction(150.0);

        mockMvc.perform(get(STATISTICS_RESOURCE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.max", is(150.0)))
                .andExpect(jsonPath("$.min", is(25.0)))
                .andExpect(jsonPath("$.avg", is(81.25)))
                .andExpect(jsonPath("$.sum", is(325.00)))
                .andExpect(jsonPath("$.count", is(4)));
    }

    @Test
    public void should_get_statistics_without_old_ones() throws Exception {
        createTransaction(100.0);
        createTransaction(30.0);
        createOldTransaction(50.0);
        createOldTransaction(25.0);

        mockMvc.perform(get(STATISTICS_RESOURCE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.max", is(100.0)))
                .andExpect(jsonPath("$.min", is(30.0)))
                .andExpect(jsonPath("$.avg", is(65.0)))
                .andExpect(jsonPath("$.sum", is(130.00)))
                .andExpect(jsonPath("$.count", is(2)));
    }

    private void createOldTransaction(Double amount) throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .timestamp(util.getOutOfRangeTimestamp())
                .build();

        mockMvc.perform(post(TRANSACTION_RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private void createTransaction(Double amount) throws Exception {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .timestamp(util.getCurrentTimestamp())
                .build();

        mockMvc.perform(post(TRANSACTION_RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


}