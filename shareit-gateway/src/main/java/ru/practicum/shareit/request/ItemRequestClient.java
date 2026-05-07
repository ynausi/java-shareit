package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestReq;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(RestTemplate restTemplate,
                             @Value("${shareit-server.url}") String serverUrl
    ) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> findUserRequests(Integer userId) {
        return get("/requests",userId);
    }

    public ResponseEntity<Object> findItemRequestById(Integer userId,Integer requestId) {
        return get("/requests/" + requestId,userId);
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestReq itemRequestReq,Integer userId) {
        return post("/requests",userId,itemRequestReq);
    }
}
