package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    /** Row lock: verify endpoint aur webhook ek saath aaye to Pro do baar extend nahi hoga. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.razorpayOrderId = :orderId")
    Optional<Payment> lockByOrderId(@Param("orderId") String orderId);
}