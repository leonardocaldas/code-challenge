package com.github.leonardocaldas.n26codechallenge.integrated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leonardocaldas.n26codechallenge.representation.Transaction;
import com.github.leonardocaldas.n26codechallenge.service.TransactionService;
import com.github.leonardocaldas.n26codechallenge.util.IntegratedTestUtil;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.leonardocaldas.n26codechallenge.representation.error.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    private static final String RESOURCE = "/transactions";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IntegratedTestUtil util;

    @Test
    public void should_not_accept_put_http_method() throws Exception {
        mockMvc.perform(put(RESOURCE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", Matchers.is(HTTP_METHOD_NOT_SUPPORTED.getCode())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_not_accept_invalid_json_content() throws Exception {
        mockMvc.perform(post(RESOURCE)
                .content("{")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", Matchers.is(PAYLOAD_INVALID.getCode())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_not_accept_content_type_different_from_json() throws Exception {
        mockMvc.perform(post(RESOURCE)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(jsonPath("$.errorCode", Matchers.is(MEDIA_TYPE_NOT_SUPPORTED.getCode())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_unknown_server_error() throws Exception {
        doThrow(new RuntimeException()).when(transactionService).save(any());

        Transaction transaction = Transaction.builder()
                .amount(100.0)
                .timestamp(util.getCurrentTimestamp())
                .build();

        mockMvc.perform(post(RESOURCE)
                .content(objectMapper.writeValueAsString(transaction))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode", Matchers.is(INTERNAL_SERVER_ERROR.getCode())));
    }
}
