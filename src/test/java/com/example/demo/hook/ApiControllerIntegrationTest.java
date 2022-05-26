package com.example.demo.hook;

import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.exception.TimePeriodException;
import com.example.demo.model.*;
import com.example.demo.service.CallbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WebHookController.class)
class ApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CallbackService service;

    private ObjectMapper objectMapper;
    private Register register;
    private Deregister deregister;
    private Change change;
    private Callback callback;
    private ServiceTime serviceTime;

    @Nested
    class CallbackStub {

        @BeforeEach
        void setup() throws Exception {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            serviceTime = new ServiceTime();
        }

        @Test
        void shouldStubCallbackWithTimeAndReturnOk() throws Exception {
            mockMvc.perform(post("/callback")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(serviceTime)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class CallbackRegister {

        @BeforeEach
        void setup() throws Exception {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

            URL callbackUrl = new URL("http://localhost:8080/callback");
            callback = new Callback();
            callback.setUrl(callbackUrl);
            callback.setPeriod(5000L);

            register = new Register();
            register.setCallback(callback);
        }

        @Test
        void shouldAcceptCallbackRegisterAndReturnJsonOk() throws Exception {

            mockMvc.perform(post("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldRejectSecondCallbackRegisterAndReturnJsonConflict() throws Exception {

            mockMvc.perform(post("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isOk());

            doThrow(DuplicateCallbackException.class)
                    .when(service)
                    .register(any(Register.class));

            mockMvc.perform(post("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isConflict())
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof DuplicateCallbackException));

            verify(service, times(2)).register(any(Register.class));
        }
    }

    @Nested
    class CallbackDeRegister {

        @BeforeEach
        void setup() throws Exception {
            objectMapper = new ObjectMapper();

            URL callbackUrl = new URL("http://localhost:8080/callback");
            callback = new Callback();
            callback.setUrl(callbackUrl);
            callback.setPeriod(5000L);

            deregister = new Deregister();
            deregister.setUrl(callbackUrl);
        }

        @Test
        void shouldAcceptCallbackDeRegisterAndReturnJsonOk() throws Exception {
            mockMvc.perform(delete("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(deregister)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class CallbackChange {

        @BeforeEach
        void setup() throws Exception {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

            URL callbackUrl = new URL("http://localhost:8080/callback");
            callback = new Callback();
            callback.setUrl(callbackUrl);
            callback.setPeriod(5000L);

            change = new Change();
            change.setCallback(callback);
        }

        @Test
        void shouldAcceptCallbackChangeAndReturnJsonOk() throws Exception {
            mockMvc.perform(put("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(change)))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldRejectCallbackChangeAndReturnJsonBadRequest() throws Exception {
            doThrow(TimePeriodException.class)
                    .when(service)
                    .change(any(Change.class));

            mockMvc.perform(put("/webhook")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(change)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof TimePeriodException));
        }
    }
}
