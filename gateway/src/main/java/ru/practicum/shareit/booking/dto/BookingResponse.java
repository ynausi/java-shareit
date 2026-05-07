package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Integer id;
    private BookerResponse booker;
    private ItemShortResponse item;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusValue status;
}
