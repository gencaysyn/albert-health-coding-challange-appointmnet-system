package com.alberthealth.appointmentbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlot extends TimeSlot{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public AvailabilitySlot(LocalDateTime startTime, LocalDateTime endTime) {
        super(startTime, endTime);
    }
}
