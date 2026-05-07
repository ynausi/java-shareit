package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import java.util.Collection;

public interface ItemRequestService {

    Collection<ItemRequestResponse> findUserRequests(int userId);

    ItemRequestResponse save(ItemRequestReq itemRequestReq, int userId);

    ItemRequestResponse findByRequestId(int requestId,int userId);
}
