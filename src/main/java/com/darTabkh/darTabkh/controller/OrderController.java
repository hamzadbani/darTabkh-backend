package com.darTabkh.darTabkh.controller;

import com.darTabkh.darTabkh.dto.*;
import com.darTabkh.darTabkh.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @Operation(summary = "Create a new order (Client only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Only clients can create orders")
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping
    @Operation(summary = "Get all orders with pagination (Admin only)")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Only admins can access all orders")
    })
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<OrderResponse> orders = orderService.getAllOrders(pageRequest);
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping("/search/filters")
    @Operation(summary = "Search orders with filters and pagination")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<PageResponse<OrderResponse>> searchOrdersWithFilters(
            @RequestBody OrderFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<OrderResponse> orders = orderService.getAllOrdersWithFilters(filterRequest, pageRequest);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/client")
    @Operation(summary = "Get current client's orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only clients can access their orders")
    })
    public ResponseEntity<PageResponse<OrderResponse>> getClientOrders(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<OrderResponse> orders = orderService.getClientOrders(pageRequest);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/cooker")
    @Operation(summary = "Get orders for current cooker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only cooks can access their orders")
    })
    public ResponseEntity<PageResponse<OrderResponse>> getCookerOrders(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PageResponse<OrderResponse> orders = orderService.getCookerOrders(pageRequest);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get order by reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<OrderResponse> getOrderByReference(
            @PathVariable @NotBlank(message = "Order reference is required") String reference) {
        OrderResponse order = orderService.getOrderByReference(reference);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}")
    @Operation(summary = "Update an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        OrderResponse order = orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order (Client only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "Only clients can cancel orders"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel order")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        OrderResponse order = orderService.cancelOrder(orderId, reason);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm an order (Cooker only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order confirmed successfully"),
            @ApiResponse(responseCode = "403", description = "Only cooks can confirm orders"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Cannot confirm order")
    })
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }
}
