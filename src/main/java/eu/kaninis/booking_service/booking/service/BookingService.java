package eu.kaninis.booking_service.booking.service;

import eu.kaninis.booking_service.booking.model.Booking;
import eu.kaninis.booking_service.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> update(Long id, Booking updated) {
        return bookingRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setEmail(updated.getEmail());
                    existing.setDateTime(updated.getDateTime());
                    existing.setNumberOfPeople(updated.getNumberOfPeople());
                    existing.setStatus(updated.getStatus());
                    return bookingRepository.save(existing);
                });
    }

    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }
}