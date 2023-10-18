package com.alberthealth.appointmentbooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String email;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="fk_user_id", referencedColumnName="user_id")
    private List<AvailabilitySlot> availabilitySlots;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_user_id", referencedColumnName="user_id")
    private List<Appointment> ownAppointments;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="booked_user_id", referencedColumnName="user_id")
    private List<Appointment> bookedAppointments;

    public List<TimeSlot> getAllSlots(){
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.addAll(getAvailabilitySlots());
        timeSlots.addAll(getOwnAppointments());
        return timeSlots;
    }

}
