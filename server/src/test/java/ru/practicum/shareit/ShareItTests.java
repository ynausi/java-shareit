package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShareItTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

	@Test
	void shouldCreateBookingWhenRequestIsValid() {

        User booker = new User();
        booker.setEmail("trot@gmail.com");
        booker.setName("Ynausi");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@gmail.com");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setAvailable(true);
        item.setUser(savedOwner);
        item.setName("Дрель");
        item.setDescription("Обычная дрель");
        Item savedItem = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequest bookingRequest = new BookingRequest(savedItem.getId(), start,end);
        BookingResponse actualResponse = bookingService.save(bookingRequest,savedBooker.getId());

        List<Booking> bookings = bookingRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Item> items = itemRepository.findAll();

        assertThat(bookings, hasSize(1));
        assertThat(items, hasSize(1));
        assertThat(users, hasSize(2));
        assertThat(actualResponse.getId(), notNullValue());
        assertThat(actualResponse.getBooker().getId(),equalTo(savedBooker.getId()));
        assertThat(actualResponse.getItem().getId(),equalTo(savedItem.getId()));
        assertThat(actualResponse.getItem().getName(),equalTo("Дрель"));
        assertThat(actualResponse.getStart(),equalTo(start));
        assertThat(actualResponse.getEnd(),equalTo(end));
	}

    @Test
    void shouldFindByName() {

        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().minusMinutes(4);

        User user1 = new User();
        user1.setName("John");
        user1.setEmail("John@gmail.com");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("Robert");
        user2.setEmail("Rober@gmail.com");
        User savedUser2 = userRepository.save(user2);

        Item item1 = new Item();
        item1.setName("Перфоратор");
        item1.setDescription("Отличный перфоратор");
        item1.setUser(savedUser1);
        item1.setAvailable(true);
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Дрель");
        item2.setDescription("Хорошая дрель");
        item2.setUser(savedUser1);
        item2.setAvailable(true);
        Item savedItem2 = itemRepository.save(item2);

        ItemDtoRequest itemDtoRequest1 = new ItemDtoRequest();
        itemDtoRequest1.setAvailable(true);
        itemDtoRequest1.setName("перфоратор");
        itemDtoRequest1.setDescription("неплохой перфоратор");

        ItemDtoRequest itemDtoRequest2 = new ItemDtoRequest();
        itemDtoRequest2.setAvailable(true);
        itemDtoRequest2.setName("Штора");
        itemDtoRequest2.setDescription("Темная штора");

        List<Item> items = itemRepository.findAll();
        Collection<ItemDtoResponse> findItem1 = itemService.findByName("Перфоратор");
        Collection<ItemDtoResponse> findItem2 = itemService.findByName("Штора");

        assertThat(items, hasSize(2));
        assertThat(findItem1.size(),equalTo(1));
        assertThat(findItem1.stream().findFirst().orElseThrow().getName(),equalTo("Перфоратор"));
        assertThat(findItem2,empty());
    }

    @Test
    void shouldFindUserById() {

        User user1 = new User();
        user1.setName("John");
        user1.setEmail("John@gmail.com");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("Robert");
        user2.setEmail("Rober@gmail.com");
        User savedUser2 = userRepository.save(user2);

        assertThat(user1.getId(),equalTo(userService.findById(savedUser1.getId()).getId()));
        assertThat(user1.getName(),equalTo(userService.findById(savedUser1.getId()).getName()));
        assertThat(user1.getEmail(),equalTo(userService.findById(savedUser1.getId()).getEmail()));
        assertThat(user2.getId(),equalTo(userService.findById(savedUser2.getId()).getId()));
        assertThat(user2.getName(),equalTo(userService.findById(savedUser2.getId()).getName()));
        assertThat(user2.getEmail(),equalTo(userService.findById(savedUser2.getId()).getEmail()));
    }
}
