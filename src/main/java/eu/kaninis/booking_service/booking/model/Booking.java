package eu.kaninis.booking_service.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private LocalDateTime dateTime;

    @Min(1)
    private int numberOfPeople;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BookingStatus status = BookingStatus.PENDING;

    // Constructors
    public Booking() {
    }

    public Booking(String name, String email, LocalDateTime dateTime,
                   int numberOfPeople, BookingStatus status) {
        this.name = name;
        this.email = email;
        this.dateTime = dateTime;
        this.numberOfPeople = numberOfPeople;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}