package com.darTabkh.darTabkh.dto;

import com.darTabkh.darTabkh.entity.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {
    
    private Integer quantity;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String deliveryPhoneNumber;
    
    private OrderStatus status;
    
    @Size(max = 500, message = "Cancel reason must not exceed 500 characters")
    private String cancelReason;
}
