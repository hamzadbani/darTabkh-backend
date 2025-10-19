package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.dto.*;
import com.darTabkh.darTabkh.entity.*;
import com.darTabkh.darTabkh.exception.ResourceNotFoundException;
import com.darTabkh.darTabkh.mapper.OrderMapper;
import com.darTabkh.darTabkh.repository.MealRepository;
import com.darTabkh.darTabkh.repository.OrderRepository;
import com.darTabkh.darTabkh.repository.UserRepository;
import com.darTabkh.darTabkh.specification.OrderSpecifications;
import com.darTabkh.darTabkh.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    
    public OrderResponse createOrder(OrderRequest request) {
        // Get current client
        User client = getCurrentUser();
        
        // Get meal and validate
        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new ResourceNotFoundException("Meal", "id", request.getMealId().toString()));
        
        // Validate client is not the cooker
        if (meal.getCooker().getId().equals(client.getId())) {
            throw new RuntimeException("You cannot order your own meals");
        }
        
        // Create order
        Order order = orderMapper.toEntity(request);
        order.setMeal(meal);
        order.setClient(client);
        order.setCooker(meal.getCooker());
        order.setUnitPrice(meal.getPrice());
        order.setTotalPrice(meal.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
    
    public PageResponse<OrderResponse> getAllOrders(PageRequest pageRequest) {
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(orderPage, orderResponses);
    }
    
    public PageResponse<OrderResponse> getAllOrdersWithFilters(OrderFilterRequest filterRequest, PageRequest pageRequest) {
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        
        // Build specifications based on filter request
        List<Specification<Order>> specifications = new ArrayList<>();
        
        if (filterRequest.getReference() != null && !filterRequest.getReference().trim().isEmpty()) {
            specifications.add(OrderSpecifications.hasReference(filterRequest.getReference()));
        }
        
        if (filterRequest.getMealId() != null) {
            specifications.add(OrderSpecifications.hasMealId(filterRequest.getMealId()));
        }
        
        if (filterRequest.getClientId() != null) {
            specifications.add(OrderSpecifications.hasClientId(filterRequest.getClientId()));
        }
        
        if (filterRequest.getCookerId() != null) {
            specifications.add(OrderSpecifications.hasCookerId(filterRequest.getCookerId()));
        }
        
        if (filterRequest.getStatus() != null) {
            specifications.add(OrderSpecifications.hasStatus(filterRequest.getStatus()));
        }
        
        if (filterRequest.getFromDate() != null || filterRequest.getToDate() != null) {
            specifications.add(OrderSpecifications.hasDateRange(filterRequest.getFromDate(), filterRequest.getToDate()));
        }
        
        if (filterRequest.getClientCity() != null && !filterRequest.getClientCity().trim().isEmpty()) {
            specifications.add(OrderSpecifications.hasClientCity(filterRequest.getClientCity()));
        }
        
        if (filterRequest.getCookerCity() != null && !filterRequest.getCookerCity().trim().isEmpty()) {
            specifications.add(OrderSpecifications.hasCookerCity(filterRequest.getCookerCity()));
        }
        
        Specification<Order> combinedSpec = OrderSpecifications.combineSpecifications(specifications);
        
        Page<Order> orderPage = orderRepository.findAll(combinedSpec, pageable);
        
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(orderPage, orderResponses);
    }
    
    public PageResponse<OrderResponse> getClientOrders(PageRequest pageRequest) {
        User client = getCurrentUser();
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Specification<Order> spec = OrderSpecifications.hasClientId(client.getId());
        
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(orderPage, orderResponses);
    }
    
    public PageResponse<OrderResponse> getCookerOrders(PageRequest pageRequest) {
        User cooker = getCurrentCooker();
        Pageable pageable = PaginationUtil.toPageable(pageRequest);
        Specification<Order> spec = OrderSpecifications.hasCookerId(cooker.getId());
        
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(orderPage, orderResponses);
    }
    
    public OrderResponse getOrderByReference(String reference) {
        Order order = orderRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "reference", reference));
        
        // Check if user has access to this order
        User currentUser = getCurrentUser();
        if (!order.getClient().getId().equals(currentUser.getId()) && 
            !order.getCooker().getId().equals(currentUser.getId()) &&
            currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You don't have access to this order");
        }
        
        return orderMapper.toResponse(order);
    }
    
    public OrderResponse updateOrder(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId.toString()));
        
        User currentUser = getCurrentUser();
        boolean isClient = order.getClient().getId().equals(currentUser.getId());
        boolean isCooker = order.getCooker().getId().equals(currentUser.getId());
        
        // Only allow updates if order is still pending
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot update order that is no longer pending");
        }
        
        // Client can only update certain fields
        if (isClient) {
            if (request.getQuantity() != null) {
                order.setQuantity(request.getQuantity());
                order.setTotalPrice(order.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            }
            if (request.getNotes() != null) {
                order.setNotes(request.getNotes());
            }
            if (request.getDeliveryAddress() != null) {
                order.setDeliveryAddress(request.getDeliveryAddress());
            }
            if (request.getDeliveryPhoneNumber() != null) {
                order.setDeliveryPhoneNumber(request.getDeliveryPhoneNumber());
            }
        }
        
        // Cooker can only update status and notes
        if (isCooker) {
            if (request.getStatus() != null && request.getStatus() != OrderStatus.PENDING) {
                updateOrderStatus(order, request.getStatus());
            }
            if (request.getNotes() != null) {
                order.setNotes(request.getNotes());
            }
        }
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
    
    public OrderResponse cancelOrder(Long orderId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId.toString()));
        
        User currentUser = getCurrentUser();
        
        // Only client can cancel, and only if order is pending
        if (!order.getClient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the client can cancel this order");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot cancel order that is no longer pending");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(cancelReason);
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
    
    public OrderResponse confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId.toString()));
        
        User cooker = getCurrentCooker();
        
        // Only the cooker can confirm
        if (!order.getCooker().getId().equals(cooker.getId())) {
            throw new RuntimeException("Only the cooker can confirm this order");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in pending status");
        }
        
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
    
    private void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        
        switch (newStatus) {
            case CONFIRMED:
                order.setConfirmedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                break;
        }
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    private User getCurrentCooker() {
        User user = getCurrentUser();
        if (user.getRole() != Role.COOKER) {
            throw new RuntimeException("Only cooks can perform this action");
        }
        return user;
    }
    
    private PageResponse<OrderResponse> buildPageResponse(Page<Order> orderPage, List<OrderResponse> orderResponses) {
        return PageResponse.<OrderResponse>builder()
                .content(orderResponses)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }
}
