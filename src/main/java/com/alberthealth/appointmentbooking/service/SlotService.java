package com.alberthealth.appointmentbooking.service;

import com.alberthealth.appointmentbooking.dao.AppointmentRepository;
import com.alberthealth.appointmentbooking.dao.AvailabilitySlotRepository;
import com.alberthealth.appointmentbooking.exception.SlotIntersectionException;
import com.alberthealth.appointmentbooking.model.Appointment;
import com.alberthealth.appointmentbooking.model.AvailabilitySlot;
import com.alberthealth.appointmentbooking.model.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    public void updateAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    public List<Appointment> findUpcomingAppointments(LocalDateTime reminderTime){
        return appointmentRepository.findUpcomingAppointments(reminderTime);
    }

    public AvailabilitySlot createAvailabilitySlot(AvailabilitySlot availabilitySlot) {
        return availabilitySlotRepository.save(availabilitySlot);
    }

    public void deleteAvailabilitySlot(AvailabilitySlot availabilitySlot){
        availabilitySlotRepository.delete(availabilitySlot);
    }

    public void updateAvailabilitySlot(AvailabilitySlot availabilitySlot){
        availabilitySlotRepository.save(availabilitySlot);
    }

    public List<AvailabilitySlot> createAvailabilitySlots(List<AvailabilitySlot> availabilitySlots){
        return availabilitySlotRepository.saveAll(availabilitySlots);
    }

    public void checkIntersections(List<TimeSlot> existingAvailabilitySlots, List<AvailabilitySlot> newAvailabilitySlots){
        checkInnerIntersection(newAvailabilitySlots);
        if(!existingAvailabilitySlots.isEmpty()){
            for (AvailabilitySlot newSlot : newAvailabilitySlots) {
                checkIntersectionBetween(existingAvailabilitySlots, newSlot);
            }
        }
    }

    private boolean doSlotsOverlap(TimeSlot slot1, TimeSlot slot2) {
        return slot1.getEndTime().isAfter(slot2.getStartTime()) && slot1.getStartTime().isBefore(slot2.getEndTime()) ||
                slot2.getEndTime().isAfter(slot1.getStartTime()) && slot2.getStartTime().isBefore(slot1.getEndTime());
    }

    private void checkIntersectionBetween(List<TimeSlot> slotList1, TimeSlot slotList2) {
        for (TimeSlot existingAvailabilitySlot : slotList1) {
            if (doSlotsOverlap(existingAvailabilitySlot, slotList2)) {
                throw new SlotIntersectionException("New slot intersects with an existing slot.");
            }
        }
    }

    private void checkInnerIntersection(List<AvailabilitySlot> newSlots){
        for (int i = 0; i < newSlots.size(); i++) {
            for (int j = i + 1; j < newSlots.size(); j++) {
                if (doSlotsOverlap(newSlots.get(i), newSlots.get(j))) {
                    throw new SlotIntersectionException("New slots intersect with each other.");
                }
            }
        }
    }

    public List<TimeSlot> getAvailableTimeIntervals(List<TimeSlot> allTimeSlots, List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            return allTimeSlots;
        }

        List<TimeSlot> availableIntervals = new ArrayList<>(allTimeSlots);

        for (Appointment appointment : appointments) {
            availableIntervals = processAppointment(availableIntervals, appointment);
        }

        return availableIntervals;
    }

    private List<TimeSlot> processAppointment(List<TimeSlot> intervals, Appointment appointment) {
        LocalDateTime appointmentStartTime = appointment.getStartTime();
        LocalDateTime appointmentEndTime = appointment.getEndTime();

        return intervals.stream()
                .flatMap(timeSlot -> splitTimeSlot(timeSlot, appointmentStartTime, appointmentEndTime).stream())
                .collect(Collectors.toList());
    }

    private List<TimeSlot> splitTimeSlot(TimeSlot timeSlot, LocalDateTime appointmentStartTime, LocalDateTime appointmentEndTime) {
        LocalDateTime timeSlotStartTime = timeSlot.getStartTime();
        LocalDateTime timeSlotEndTime = timeSlot.getEndTime();

        if (appointmentStartTime.isEqual(timeSlotStartTime) && appointmentEndTime.isEqual(timeSlotEndTime)) {
            return Collections.emptyList(); // The appointment fully occupies the availability slot, so filter it out.
        } else if (appointmentStartTime.isEqual(timeSlotStartTime)) {
            return splitTimeSlotOnStart(timeSlot, appointmentEndTime);
        } else if (appointmentEndTime.isEqual(timeSlotEndTime)) {
            return splitTimeSlotOnEnd(timeSlot, appointmentStartTime);
        } else if (appointmentStartTime.isAfter(timeSlotStartTime) && appointmentEndTime.isBefore(timeSlotEndTime)) {
            return splitTimeSlotInMiddle(timeSlot, appointmentStartTime, appointmentEndTime);
        } else {
            return Collections.singletonList(timeSlot); // No changes needed, keep the original time slot.
        }
    }

    private List<TimeSlot> splitTimeSlotOnStart(TimeSlot timeSlot, LocalDateTime splitTime) {
        LocalDateTime timeSlotStartTime = timeSlot.getStartTime();
        LocalDateTime timeSlotEndTime = timeSlot.getEndTime();

        if (splitTime.isAfter(timeSlotStartTime) && splitTime.isBefore(timeSlotEndTime)) {
            TimeSlot availableInterval = new TimeSlot();
            availableInterval.setStartTime(splitTime);
            availableInterval.setEndTime(timeSlotEndTime);
            return Collections.singletonList(availableInterval);
        } else {
            return Collections.singletonList(timeSlot);
        }
    }

    private List<TimeSlot> splitTimeSlotOnEnd(TimeSlot timeSlot, LocalDateTime splitTime) {
        LocalDateTime timeSlotStartTime = timeSlot.getStartTime();
        LocalDateTime timeSlotEndTime = timeSlot.getEndTime();

        if (splitTime.isAfter(timeSlotStartTime) && splitTime.isBefore(timeSlotEndTime)) {
            TimeSlot availableInterval = new TimeSlot();
            availableInterval.setStartTime(timeSlotStartTime);
            availableInterval.setEndTime(splitTime);
            return Collections.singletonList(availableInterval);
        } else {
            return Collections.singletonList(timeSlot);
        }
    }

    private List<TimeSlot> splitTimeSlotInMiddle(TimeSlot timeSlot, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime timeSlotStartTime = timeSlot.getStartTime();
        LocalDateTime timeSlotEndTime = timeSlot.getEndTime();

        if (startTime.isAfter(timeSlotStartTime) && endTime.isBefore(timeSlotEndTime)) {
            TimeSlot firstInterval = new TimeSlot();
            firstInterval.setStartTime(timeSlotStartTime);
            firstInterval.setEndTime(startTime);

            TimeSlot secondInterval = new TimeSlot();
            secondInterval.setStartTime(endTime);
            secondInterval.setEndTime(timeSlotEndTime);

            return Arrays.asList(firstInterval, secondInterval);
        } else {
            return Collections.singletonList(timeSlot);
        }
    }

    public boolean isSlotSubSlotOfList(TimeSlot newSlot, List<TimeSlot> slots) {
        return slots.stream()
                .anyMatch(slot ->
                        (slot.getStartTime().isEqual(newSlot.getStartTime()) || slot.getStartTime().isBefore(newSlot.getStartTime())) &&
                                (slot.getEndTime().isEqual(newSlot.getStartTime()) || slot.getEndTime().isAfter(newSlot.getStartTime())));
    }
}
