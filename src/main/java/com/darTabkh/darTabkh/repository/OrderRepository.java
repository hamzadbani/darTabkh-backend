package com.darTabkh.darTabkh.repository;

import com.darTabkh.darTabkh.entity.Meal;
import com.darTabkh.darTabkh.entity.Order;
import com.darTabkh.darTabkh.entity.OrderStatus;
import com.darTabkh.darTabkh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByReference(String reference);
    boolean existsByReference(String reference);
}
