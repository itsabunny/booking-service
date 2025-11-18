package eu.kaninis.booking_service.booking.repository;

import eu.kaninis.booking_service.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
