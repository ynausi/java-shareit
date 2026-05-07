package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.Collection;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(RestTemplate restTemplate,
                             @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Collection<ItemRequestResponse>> findUserRequests(Integer userId) {
        return get("/requests", userId, new ParameterizedTypeReference<Collection<ItemRequestResponse>>() {});
    }

    public ResponseEntity<ItemRequestResponse> findItemRequestById(Integer userId,Integer requestId) {
        return get("/requests/" + requestId,userId,ItemRequestResponse.class);
    }

    public ResponseEntity<ItemRequestResponse> createItemRequest(ItemRequestReq itemRequestReq,Integer userId) {
        return post("/requests",userId,itemRequestReq,ItemRequestResponse.class);
    }
}
