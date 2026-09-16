package com.ashutosh.week7_jpa.controller;

import com.ashutosh.week7_jpa.entity.Payment;
import com.ashutosh.week7_jpa.entity.PaymentStatus;
import com.ashutosh.week7_jpa.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(
            @PathVariable Long id
    ) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrderId(
            @PathVariable Long orderId
    ) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @PostMapping("/order/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(
            @PathVariable Long orderId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String transactionId
    ) {
        return paymentService.createPayment(
                orderId,
                paymentMethod,
                transactionId
        );
    }

    @PutMapping("/{paymentId}/status")
    public Payment updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status
    ) {
        return paymentService.updatePaymentStatus(
                paymentId,
                status
        );
    }
}