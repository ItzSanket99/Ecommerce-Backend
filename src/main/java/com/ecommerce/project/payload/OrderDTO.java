package com.ecommerce.project.payload;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO>  orderItems;
    private LocalDate date;
    private PaymentDTO paymentDTO;
    private Double totalAmount;
    private String orderStatus;
    private Long AddressId;
}
