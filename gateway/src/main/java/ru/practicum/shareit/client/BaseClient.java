package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;


public class BaseClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BaseClient(RestTemplate restTemplate,String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Integer userId) {
        return sendRequest(HttpMethod.GET, path, userId, null);
    }

    protected ResponseEntity<Object> post(String path, Integer userId, Object body) {
        return sendRequest(HttpMethod.POST, path, userId, body);
    }

    protected ResponseEntity<Object> patch(String path, Integer userId, Object body) {
        return sendRequest(HttpMethod.PATCH, path, userId, body);
    }

    protected ResponseEntity<Object> delete(String path, Integer userId) {
        return sendRequest(HttpMethod.DELETE, path, userId, null);
    }

    private ResponseEntity<Object> sendRequest(
            HttpMethod method,
            String path,
            Integer userId,
            Object body
    ) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, makeHeaders(userId));

        try {
            return restTemplate.exchange(
                    serverUrl + path,
                    method,
                    requestEntity,
                    Object.class
            );
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
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
