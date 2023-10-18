package com.alberthealth.appointmentbooking.service;

import com.alberthealth.appointmentbooking.exception.SlotIntersectionException;
import com.alberthealth.appointmentbooking.model.*;
import com.alberthealth.appointmentbooking.model.requests.BookAppointmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentBookingService {
    @Autowired
    SlotService slotService;

    @Autowired
    UserService userService;

    public void createAvailableSlots(Long userId, List<AvailabilitySlot> availabilityAvailabilitySlots) {
        User user = userService.getUserById(userId);
        List<TimeSlot> existingSLots = user.getAllSlots();
        slotService.checkIntersections(existingSLots, availabilityAvailabilitySlots);
        user.getAvailabilitySlots().addAll(availabilityAvailabilitySlots);
        userService.updateUser(user);
    }

    public List<TimeSlot> getAvailableSlots(Long userId) {
        User user = userService.getUserById(userId);
        List<TimeSlot> existingSlots = user.getAllSlots();
        return slotService.getAvailableTimeIntervals(existingSlots, user.getOwnAppointments());
    }

    public void bookAppointment(Long userId, BookAppointmentRequest bar) {
        User ownerUser = userService.getUserById(userId);
        List<TimeSlot> existingSlots = ownerUser.getAllSlots();
        User bookedUser = userService.getUserById(bar.getBookedUserId());
        List<TimeSlot> availableSlots = slotService.getAvailableTimeIntervals(existingSlots, ownerUser.getOwnAppointments());
        if(slotService.isSlotSubSlotOfList(new TimeSlot(bar.getStartTime(), bar.getEndTime()), availableSlots)) {
            slotService.createAppointment(new Appointment(bar.getStartTime(), bar.getEndTime(), bar.getDescription(), bookedUser, ownerUser));
        }else{
            throw new SlotIntersectionException("There is no available time slot for given interval.");
        }
    }
}
