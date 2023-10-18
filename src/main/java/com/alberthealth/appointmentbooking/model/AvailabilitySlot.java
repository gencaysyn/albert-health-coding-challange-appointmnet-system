package com.alberthealth.appointmentbooking.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlot extends TimeSlot{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
