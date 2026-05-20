package com.tcc.consent_command_service.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${user-service.url}") String userServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public boolean userExists(Long userId) {
        try {
            var response = restClient.get()
                    .uri("/api/v1/users/{id}/exists", userId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {})
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException(
                    "User service is unavailable. Failed to verify if user " + userId + " exists.", e);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(
                    "User service returned unexpected error for user " + userId + ": " + e.getStatusCode(), e);
        }
    }
}
