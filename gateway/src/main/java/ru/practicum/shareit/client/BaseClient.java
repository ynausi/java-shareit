package ru.practicum.shareit.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected <T> ResponseEntity<T> get(String path, Integer userId, Class<T> responseType) {
        return sendRequest(HttpMethod.GET, path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> get(
            String path,
            Integer userId,
            ParameterizedTypeReference<T> responseType) {
        return sendRequest(HttpMethod.GET, path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> post(String path, Integer userId, Object body, Class<T> responseType) {
        return sendRequest(HttpMethod.POST, path, userId, body, responseType);
    }

    protected <T> ResponseEntity<T> patch(String path, Integer userId, Object body, Class<T> responseType) {
        return sendRequest(HttpMethod.PATCH, path, userId, body, responseType);
    }

    protected <T> ResponseEntity<T> delete(String path, Integer userId, Class<T> responseType) {
        return sendRequest(HttpMethod.DELETE, path, userId, null, responseType);
    }

    private <T> ResponseEntity<T> sendRequest(
            HttpMethod method,
            String path,
            Integer userId,
            Object body,
            Class<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, makeHeaders(userId));
        try {
            return restTemplate.exchange(
                    serverUrl + path,
                    method,
                    requestEntity,
                    responseType
            );
        } catch (HttpStatusCodeException e) {
            return makeErrorResponse(e);
        }
    }

    private <T> ResponseEntity<T> sendRequest(
            HttpMethod method,
            String path,
            Integer userId,
            Object body,
            ParameterizedTypeReference<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, makeHeaders(userId));
        try {
            return restTemplate.exchange(
                    serverUrl + path,
                    method,
                    requestEntity,
                    responseType
            );
        } catch (HttpStatusCodeException e) {
            return makeErrorResponse(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> makeErrorResponse(HttpStatusCodeException e) {
        return (ResponseEntity<T>) ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getResponseBodyAsString());
    }

    private HttpHeaders makeHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}