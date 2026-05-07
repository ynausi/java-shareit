package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusValue;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Integer> {

    //Всё бронирования пользователя
    Collection<Booking> findByBooker_IdOrderByStartTimeDesc(int bookerId);

    //Заверщенные
    Collection<Booking> findByBooker_IdAndEndTimeBeforeOrderByStartTimeDesc(int bookerId, Instant now);

    //Будущие
    Collection<Booking> findByBooker_IdAndStartTimeAfterOrderByStartTimeDesc(int bookerId,Instant now);

    //Текущие
    Collection<Booking> findByBooker_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(int bookerId,Instant now1,Instant now2);

    //В зависимости от значения status
    Collection<Booking> findByBooker_IdAndStatusOrderByStartTimeDesc(int bookerId, StatusValue status);

    Collection<Booking> findByItem_User_IdOrderByStartTimeDesc(int bookerId);

    Collection<Booking> findByItem_User_IdAndEndTimeBeforeOrderByStartTimeDesc(int bookerId,Instant now);

    Collection<Booking> findByItem_User_IdAndStartTimeAfterOrderByStartTimeDesc(int bookerId,Instant now);

    Collection<Booking>  findByItem_User_IdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(int bookerId,Instant now1,Instant now2);

    Collection<Booking> findByItem_User_IdAndStatusOrderByStartTimeDesc(int bookerId,StatusValue status);

    Boolean existsByItem_IdAndBooker_Id_AndStatusAndEndTimeBefore(Integer itemId, Integer userId, StatusValue status, LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndTimeBeforeOrderByEndTimeDesc(Integer itemId, StatusValue statusValue, LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartTimeAfterOrderByStartTimeAsc(Integer itemId,StatusValue statusValue,LocalDateTime now);

}
