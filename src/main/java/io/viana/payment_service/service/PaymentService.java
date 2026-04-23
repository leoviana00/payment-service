package io.viana.payment_service.service;

import io.viana.payment_service.entity.Payment;
import io.viana.payment_service.enums.PaymentStatus;
import io.viana.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment create(Payment payment) {
        return repository.save(payment);
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public Payment findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment confirm(UUID id) {

        Payment payment = findById(id);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be confirmed");
        }

        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setConfirmedAt(LocalDateTime.now());

        return repository.save(payment);
    }
}