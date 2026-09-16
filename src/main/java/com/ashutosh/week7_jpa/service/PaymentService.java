package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Order;
import com.ashutosh.week7_jpa.entity.Payment;
import com.ashutosh.week7_jpa.entity.PaymentStatus;
import com.ashutosh.week7_jpa.exception.ResourceNotFoundException;
import com.ashutosh.week7_jpa.repository.OrderRepository;
import com.ashutosh.week7_jpa.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for order id: "
                                        + orderId
                        )
                );
    }

    public Payment createPayment(
            Long orderId,
            String paymentMethod,
            String transactionId
    ) {

        Order order = orderRepository
                .findByIdWithDetails(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalArgumentException(
                    "Payment already exists for order id: " + orderId
            );
        }

        if (transactionId != null
                && !transactionId.isBlank()
                && paymentRepository
                .findByTransactionId(transactionId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Transaction ID already exists: " + transactionId
            );
        }

        BigDecimal amount = order.getTotalAmount();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);

        Payment savedPayment = paymentRepository.save(payment);

        order.setPayment(savedPayment);

        return savedPayment;
    }

    public Payment updatePaymentStatus(
            Long paymentId,
            PaymentStatus status
    ) {

        Payment payment = getPaymentById(paymentId);
        payment.setStatus(status);

        return paymentRepository.save(payment);
    }
}