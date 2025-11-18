package eu.kaninis.booking_service.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.kaninis.booking_service.booking.model.Booking;
import eu.kaninis.booking_service.booking.model.BookingStatus;
import eu.kaninis.booking_service.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Booking createSampleBooking(Long id) {
        Booking booking = new Booking(
                "Test User",
                "test@example.com",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                2,
                BookingStatus.PENDING
        );
        if (id != null) {
            try {
                var idField = Booking.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(booking, id);
            } catch (Exception ignored) {}
        }
        return booking;
    }

    @Test
    void getAllBookings_returnsOkAndList() throws Exception {
        // given
        given(bookingService.findAll()).willReturn(List.of(createSampleBooking(1L)));

        // when + then
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));

        then(bookingService).should().findAll();
    }

    @Test
    void getBookingById_existing_returnsOk() throws Exception {
        // given
        given(bookingService.findById(1L)).willReturn(Optional.of(createSampleBooking(1L)));

        // when + then
        mockMvc.perform(get("/bookings/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")));
    }

    @Test
    void getBookingById_nonExisting_returnsNotFound() throws Exception {
        // given
        given(bookingService.findById(99L)).willReturn(Optional.empty());

        // when + then
        mockMvc.perform(get("/bookings/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_valid_returnsOk() throws Exception {
        // given
        Booking toCreate = createSampleBooking(null);
        Booking created = createSampleBooking(1L);
        given(bookingService.create(any(Booking.class))).willReturn(created);

        // when + then
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")));

        then(bookingService).should().create(any(Booking.class));
    }

    @Test
    void createBooking_invalid_returnsBadRequest() throws Exception {
        // Booking utan namn -> @NotBlank på name ska trigga 400
        Booking invalid = new Booking(
                "",
                "invalid-email", // felaktig email
                null,
                0,
                null
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_existing_returnsOk() throws Exception {
        // given
        Booking updateRequest = createSampleBooking(null);
        updateRequest.setName("Updated Name");
        Booking updated = createSampleBooking(1L);
        updated.setName("Updated Name");

        given(bookingService.update(eq(1L), any(Booking.class))).willReturn(Optional.of(updated));

        // when + then
        mockMvc.perform(put("/bookings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void updateBooking_nonExisting_returnsNotFound() throws Exception {
        // given
        Booking updateRequest = createSampleBooking(null);
        given(bookingService.update(eq(99L), any(Booking.class))).willReturn(Optional.empty());

        // when + then
        mockMvc.perform(put("/bookings/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBooking_returnsNoContent() throws Exception {
        // when + then
        mockMvc.perform(delete("/bookings/{id}", 1L))
                .andExpect(status().isNoContent());

        then(bookingService).should().delete(1L);
    }
}