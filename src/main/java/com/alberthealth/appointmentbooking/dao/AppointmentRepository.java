package com.alberthealth.appointmentbooking.dao;

import com.alberthealth.appointmentbooking.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.startTime >= :reminderTime")
    List<Appointment> findUpcomingAppointments(@Param("reminderTime")LocalDateTime reminderTime);
}
