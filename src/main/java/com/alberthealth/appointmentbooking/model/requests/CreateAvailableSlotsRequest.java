package com.alberthealth.appointmentbooking.model.requests;

import com.alberthealth.appointmentbooking.model.AvailabilitySlot;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class CreateAvailableSlotsRequest {
    @NotNull
    private List<AvailabilitySlot> slots;
}
