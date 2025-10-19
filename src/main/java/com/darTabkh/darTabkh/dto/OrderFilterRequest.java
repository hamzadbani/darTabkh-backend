package com.darTabkh.darTabkh.dto;

import com.darTabkh.darTabkh.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {
    private String reference;
    private Long mealId;
    private Long clientId;
    private Long cookerId;
    private OrderStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String clientCity;
    private String cookerCity;
}
