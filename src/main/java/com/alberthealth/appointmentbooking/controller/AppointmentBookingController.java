package com.alberthealth.appointmentbooking.controller;

import com.alberthealth.appointmentbooking.exception.SlotIntersectionException;
import com.alberthealth.appointmentbooking.exception.UserNotFoundException;
import com.alberthealth.appointmentbooking.model.requests.BookAppointmentRequest;
import com.alberthealth.appointmentbooking.model.requests.CreateAvailableSlotsRequest;
import com.alberthealth.appointmentbooking.model.TimeSlot;
import com.alberthealth.appointmentbooking.service.AppointmentBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
public class AppointmentBookingController {
    @Autowired
    AppointmentBookingService appointmentBookingService;

    @ExceptionHandler(value = SlotIntersectionException.class)
    public ResponseEntity<String> handleSlotIntersectionException(SlotIntersectionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<String> handleSlotIntersectionException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/available-slots")
    public ResponseEntity<String> createAvailableSlots(@PathVariable Long userId, @RequestBody CreateAvailableSlotsRequest createAvailableSlots) {
        appointmentBookingService.createAvailableSlots(userId, createAvailableSlots.getSlots());
        return new ResponseEntity<>("Slot created successfully.", HttpStatus.OK);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(@PathVariable Long userId) {
        return new ResponseEntity<>(appointmentBookingService.getAvailableSlots(userId), HttpStatus.OK);
    }

    @PostMapping("/appointments")
    public ResponseEntity<String> bookAppointment(@PathVariable Long userId, @RequestBody BookAppointmentRequest bookAppointmentRequest) {
        appointmentBookingService.bookAppointment(userId, bookAppointmentRequest);
        return new ResponseEntity<>(String.format("%d, booked successfully.", userId), HttpStatus.OK);
    }
}
