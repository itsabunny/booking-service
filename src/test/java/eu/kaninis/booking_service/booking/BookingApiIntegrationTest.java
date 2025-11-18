package eu.kaninis.booking_service.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.kaninis.booking_service.booking.model.Booking;
import eu.kaninis.booking_service.booking.model.BookingStatus;
import eu.kaninis.booking_service.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
    }

    private Booking createSampleBooking() {
        return new Booking(
                "Integration User",
                "integration@example.com",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                4,
                BookingStatus.PENDING
        );
    }

    @Test
    void createAndGetBooking_flowWorks() throws Exception {
        Booking booking = createSampleBooking();

        // CREATE (POST /bookings)
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name", is("Integration User")));

        // GET ALL (GET /bookings)
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("integration@example.com")));
    }

    @Test
    void updateBooking_changesPersistedData() throws Exception {
        // Först spara en booking direkt via repository
        Booking booking = bookingRepository.save(createSampleBooking());
        Long id = booking.getId();

        Booking updateRequest = new Booking(
                "Updated Integration User",
                "updated.integration@example.com",
                LocalDateTime.of(2025, 1, 2, 12, 0),
                2,
                BookingStatus.CONFIRMED
        );

        // UPDATE (PUT /bookings/{id})
        mockMvc.perform(put("/bookings/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Integration User")))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        // Kolla via GET /bookings/{id}
        mockMvc.perform(get("/bookings/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updated.integration@example.com")));
    }

    @Test
    void deleteBooking_removesEntity() throws Exception {
        Booking booking = bookingRepository.save(createSampleBooking());
        Long id = booking.getId();

        // DELETE
        mockMvc.perform(delete("/bookings/{id}", id))
                .andExpect(status().isNoContent());

        // GET ska nu ge 404
        mockMvc.perform(get("/bookings/{id}", id))
                .andExpect(status().isNotFound());
    }
}