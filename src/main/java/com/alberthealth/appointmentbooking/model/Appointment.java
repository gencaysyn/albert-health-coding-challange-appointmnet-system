package com.alberthealth.appointmentbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment extends TimeSlot{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booked_user_id")
    private User bookedUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    private boolean reminderSent = false;

    public Appointment(LocalDateTime startTime, LocalDateTime endTime, String description, User bookedUser, User ownerUser) {
        super(startTime, endTime);
        this.description = description;
        this.bookedUser = bookedUser;
        this.ownerUser = ownerUser;
    }
}