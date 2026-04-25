package ru.practicum.shareit.item.mapper;

import MyAnnotations.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusValue;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public Item toData(ItemDtoRequest itemDtoRequest) {
        Item item = new Item();
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());
        return item;
    }

    @Loggable(value = "toResponse",level = LogLevel.DEBUG)
    public ItemDtoResponse toResponse(Item item) {
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse();
        LocalDateTime now = LocalDateTime.now();
        itemDtoResponse.setId(item.getId());
        itemDtoResponse.setName(item.getName());
        itemDtoResponse.setDescription(item.getDescription());
        itemDtoResponse.setAvailable(item.getAvailable());
        itemDtoResponse.setUserId(item.getUser().getId());

        List<CommentResponse> commentResponseList = commentRepository.findByItem_IdOrderByCreatedAsc(item.getId())
                .stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());

        Booking lastBooking = null;
        Booking nextBooking = null;
        lastBooking = bookingRepository
                .findFirstByItem_IdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(
                        item.getId(),
                        StatusValue.APPROVED,
                        now
                )
                .orElse(null);

        nextBooking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                        item.getId(),
                        StatusValue.APPROVED,
                        now
                )
                .orElse(null);

        itemDtoResponse.setComments(commentResponseList);
        return itemDtoResponse;
    }
}
