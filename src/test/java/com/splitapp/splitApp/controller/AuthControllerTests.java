package com.splitapp.splitApp.controller;

import com.splitapp.splitApp.dto.request.LoginRequest;
import com.splitapp.splitApp.dto.request.RegisterRequest;
import com.splitapp.splitApp.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser3@test.com");
        request.setPassword("password123");
        request.setDisplayName("New User");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl() + "/api/auth/register", request, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void shouldLoginSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("logintest3@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Login Test");
        restTemplate.postForEntity(baseUrl() + "/api/auth/register", registerRequest, AuthResponse.class);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("logintest3@test.com");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl() + "/api/auth/login", loginRequest, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void shouldReturn400ForInvalidEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setDisplayName("Test");

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.postForEntity(baseUrl() + "/api/auth/register", request, Object.class));
    }
}