package io.viana.payment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.viana.payment_service.dto.CreatePaymentRequest;
import io.viana.payment_service.entity.Payment;
import io.viana.payment_service.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "API de gerenciamento de pagamentos")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar novo pagamento")
    public Payment create(@RequestBody CreatePaymentRequest dto) {

        Payment payment = new Payment();
        payment.setCustomerName(dto.getCustomerName());
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());

        return service.create(payment);
    }

    @GetMapping
    @Operation(summary = "Listar todos os pagamentos")
    public List<Payment> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID")
    public Payment findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmar pagamento")
    public Payment confirm(@PathVariable UUID id) {
        return service.confirm(id);
    }
}