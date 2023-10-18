package com.alberthealth.appointmentbooking.service;

import com.alberthealth.appointmentbooking.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentReminderService {

    @Autowired
    private SlotService slotService;

    @Scheduled(fixedRate = 60000) // Run every minute
    public void sendAppointmentReminders() {
        LocalDateTime reminderTime = LocalDateTime.now().plusMinutes(15);
        List<Appointment> upcomingAppointments = slotService.findUpcomingAppointments(reminderTime);

        for (Appointment appointment : upcomingAppointments) {
            if (!appointment.isReminderSent()) {
                sendReminder(appointment);
                appointment.setReminderSent(true);
                slotService.updateAppointment(appointment);
            }
        }
    }

    private void sendReminder(Appointment appointment) {
        System.out.println(appointment);
        System.out.println("Last 15 minutes to appointment.");
    }
}
