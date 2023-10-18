package com.alberthealth.appointmentbooking.dao;

import com.alberthealth.appointmentbooking.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
}
