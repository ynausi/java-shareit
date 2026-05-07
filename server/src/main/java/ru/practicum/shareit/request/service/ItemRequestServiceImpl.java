package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestReq;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Collection<ItemRequestResponse> findUserRequests(int userId) {
        return itemRequestRepository.findAllByUser_Id(userId)
                .stream()
                .map(itemRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse save(ItemRequestReq itemRequestReq, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("No user with id:" + userId)
        );
        ItemRequest itemRequest = itemRequestMapper.toData(itemRequestReq);
        itemRequest.setUser(user);
        ItemRequest created = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toResponse(created);
    }

    @Override
    public ItemRequestResponse findByRequestId(int requestId, int userId) {
        return itemRequestMapper.toResponse(itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("No such request with id:" + requestId))
        );
    }


}
