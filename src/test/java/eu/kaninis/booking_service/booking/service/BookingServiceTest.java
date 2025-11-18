package eu.kaninis.booking_service.booking.service;

import eu.kaninis.booking_service.booking.model.Booking;
import eu.kaninis.booking_service.booking.model.BookingStatus;
import eu.kaninis.booking_service.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking(
                "Test User",
                "test@example.com",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                2,
                BookingStatus.PENDING
        );
    }

    @Test
    void findAll_returnsAllBookings() {
        // given
        given(bookingRepository.findAll()).willReturn(List.of(booking));

        // when
        List<Booking> result = bookingService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
        then(bookingRepository).should(times(1)).findAll();
    }

    @Test
    void findById_existingId_returnsBooking() {
        // given
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));

        // when
        Optional<Booking> result = bookingService.findById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        then(bookingRepository).should(times(1)).findById(1L);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // given
        given(bookingRepository.findById(99L)).willReturn(Optional.empty());

        // when
        Optional<Booking> result = bookingService.findById(99L);

        // then
        assertThat(result).isEmpty();
        then(bookingRepository).should(times(1)).findById(99L);
    }

    @Test
    void create_savesBooking() {
        // given
        given(bookingRepository.save(any(Booking.class))).willAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            // simulera genererat id
            try {
                var idField = Booking.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(saved, 1L);
            } catch (Exception ignored) {}
            return saved;
        });

        // when
        Booking result = bookingService.create(booking);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
        then(bookingRepository).should(times(1)).save(any(Booking.class));
    }

    @Test
    void update_existingId_updatesAndReturnsBooking() {
        // given
        Booking existing = booking;
        given(bookingRepository.findById(1L)).willReturn(Optional.of(existing));

        Booking updated = new Booking(
                "Updated User",
                "updated@example.com",
                LocalDateTime.of(2025, 1, 2, 12, 0),
                3,
                BookingStatus.CONFIRMED
        );
        given(bookingRepository.save(any(Booking.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<Booking> result = bookingService.update(1L, updated);

        // then
        assertThat(result).isPresent();
        Booking updatedResult = result.get();
        assertThat(updatedResult.getName()).isEqualTo("Updated User");
        assertThat(updatedResult.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        then(bookingRepository).should(times(1)).findById(1L);
        then(bookingRepository).should(times(1)).save(any(Booking.class));
    }

    @Test
    void update_nonExistingId_returnsEmpty() {
        // given
        given(bookingRepository.findById(42L)).willReturn(Optional.empty());

        // when
        Optional<Booking> result = bookingService.update(42L, booking);

        // then
        assertThat(result).isEmpty();
        then(bookingRepository).should(times(1)).findById(42L);
        then(bookingRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        // when
        bookingService.delete(10L);

        // then
        then(bookingRepository).should(times(1)).deleteById(eq(10L));
    }
}