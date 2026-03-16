package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.subscription.CheckoutRequest;
import com.astrocode.backend.api.dto.subscription.CheckoutResponse;
import com.astrocode.backend.api.dto.subscription.PlanInfo;
import com.astrocode.backend.api.dto.subscription.SubscriptionResponse;
import com.astrocode.backend.domain.entities.Subscription;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.PaymentRejectedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SubscriptionService {

    private static final BigDecimal PRICE_MONTHLY = new BigDecimal("9.90");
    private static final BigDecimal PRICE_SEMIANNUAL = new BigDecimal("49.90");
    private static final BigDecimal PRICE_ANNUAL = new BigDecimal("89.90");

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                              UserRepository userRepository,
                              PaymentClient paymentClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
    }

    public Optional<Subscription> findByUserId(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional
    public Subscription getOrCreateFree(User user) {
        return subscriptionRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    var sub = Subscription.builder()
                            .user(user)
                            .planType(PlanType.FREE)
                            .status(SubscriptionStatus.ACTIVE)
                            .build();
                    return subscriptionRepository.save(sub);
                });
    }

    public boolean isPro(UUID userId) {
        return userRepository.findByIdWithSubscription(userId)
                .map(User::isPro)
                .orElse(false);
    }

    public Subscription getSubscriptionOrThrow(UUID userId) {
        return findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada"));
    }

    @Transactional
    public CheckoutResponse processPayment(UUID userId, CheckoutRequest request) {
        if (request.planType() == PlanType.FREE) {
            throw new IllegalStateException("Plano FREE não requer pagamento");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Subscription subscription = getOrCreateFree(user);
        BigDecimal amount = getPrice(request.planType());
        String externalRef = "sub:" + subscription.getId();

        var payerBuilder = PaymentPayerRequest.builder()
                .email(request.payerEmail());

        if (request.payerIdentificationType() != null && !request.payerIdentificationType().isBlank()
                && request.payerIdentificationNumber() != null && !request.payerIdentificationNumber().isBlank()) {
            payerBuilder.identification(IdentificationRequest.builder()
                    .type(request.payerIdentificationType())
                    .number(request.payerIdentificationNumber())
                    .build());
        }

        var paymentRequest = PaymentCreateRequest.builder()
                .transactionAmount(amount)
                .token(request.token())
                .installments(request.installments())
                .description("Grivy - Plano " + request.planType().name())
                .externalReference(externalRef)
                .payer(payerBuilder.build())
                .statementDescriptor("GRIVY")
                .build();

        Payment payment;
        try {
            payment = paymentClient.create(paymentRequest);
        } catch (MPException e) {
            throw new PaymentRejectedException("Erro ao processar pagamento: " + e.getMessage(), e);
        }

        String status = payment.getStatus();
        if ("rejected".equals(status) || "cancelled".equals(status)) {
            String detail = payment.getStatusDetail() != null ? payment.getStatusDetail() : status;
            throw new PaymentRejectedException("Pagamento recusado: " + detail);
        }

        subscription.setMpPaymentId(payment.getId() != null ? String.valueOf(payment.getId()) : null);
        subscription.setMpExternalReference(externalRef);

        if ("approved".equals(status)) {
            activatePaidPlan(subscription, request.planType(), amount, payment.getId());
        }
        subscription = subscriptionRepository.save(subscription);

        return new CheckoutResponse(
                subscription.getId(),
                subscription.getPlanType(),
                subscription.getStatus(),
                subscription.getAmountPaid(),
                subscription.getMpPaymentId(),
                subscription.getExpiresAt()
        );
    }

    @Transactional
    public void handlePaymentApproved(Long mpPaymentId) {
        Payment payment;
        try {
            payment = paymentClient.get(mpPaymentId);
        } catch (Exception e) {
            throw new PaymentRejectedException("Erro ao obter pagamento do Mercado Pago", e);
        }

        if (payment == null || !"approved".equals(payment.getStatus())) {
            return;
        }

        String externalRef = payment.getExternalReference();
        if (externalRef == null || !externalRef.startsWith("sub:")) {
            return;
        }

        UUID subId = UUID.fromString(externalRef.substring(4));
        Subscription subscription = subscriptionRepository.findById(subId).orElse(null);
        if (subscription == null) {
            return;
        }

        PlanType planType = inferPlanFromAmount(payment.getTransactionAmount());
        activatePaidPlan(subscription, planType,
                payment.getTransactionAmount() != null ? payment.getTransactionAmount() : BigDecimal.ZERO,
                payment.getId());
        subscription.setMpPaymentId(String.valueOf(payment.getId()));
        subscription.setMpExternalReference(externalRef);
        subscriptionRepository.save(subscription);
    }

    private void activatePaidPlan(Subscription subscription, PlanType planType, BigDecimal amount, Long mpPaymentId) {
        OffsetDateTime now = OffsetDateTime.now();
        int months = getPlanMonths(planType);

        subscription.setPlanType(planType);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartsAt(now);
        subscription.setExpiresAt(now.plusMonths(months));
        subscription.setAmountPaid(amount);
        subscription.setMpPaymentId(mpPaymentId != null ? String.valueOf(mpPaymentId) : subscription.getMpPaymentId());
    }

    private PlanType inferPlanFromAmount(BigDecimal amount) {
        if (amount == null) return PlanType.MONTHLY;
        if (amount.compareTo(PRICE_ANNUAL) <= 0 && amount.compareTo(new BigDecimal("80")) >= 0) return PlanType.ANNUAL;
        if (amount.compareTo(PRICE_SEMIANNUAL) <= 0 && amount.compareTo(new BigDecimal("40")) >= 0) return PlanType.SEMIANNUAL;
        return PlanType.MONTHLY;
    }

    @Transactional
    public void cancelSubscription(UUID userId) {
        Subscription subscription = getSubscriptionOrThrow(userId);
        if (subscription.getPlanType() == PlanType.FREE) {
            throw new IllegalStateException("Assinatura gratuita não pode ser cancelada");
        }
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void expireSubscriptions() {
        OffsetDateTime now = OffsetDateTime.now();
        var expired = subscriptionRepository.findByStatusAndExpiresAtBefore(SubscriptionStatus.ACTIVE, now);
        for (Subscription sub : expired) {
            if (sub.getPlanType() != PlanType.FREE) {
                sub.setStatus(SubscriptionStatus.EXPIRED);
                sub.setPlanType(PlanType.FREE);
                subscriptionRepository.save(sub);
            }
        }
    }

    public BigDecimal getPrice(PlanType planType) {
        return switch (planType) {
            case FREE -> BigDecimal.ZERO;
            case MONTHLY -> PRICE_MONTHLY;
            case SEMIANNUAL -> PRICE_SEMIANNUAL;
            case ANNUAL -> PRICE_ANNUAL;
        };
    }

    public int getPlanMonths(PlanType planType) {
        return switch (planType) {
            case FREE -> 0;
            case MONTHLY -> 1;
            case SEMIANNUAL -> 6;
            case ANNUAL -> 12;
        };
    }

    public List<PlanInfo> listPlans() {
        return List.of(
                new PlanInfo(PlanType.FREE, "Grátis", BigDecimal.ZERO, 0, "2 contas, 30 transações/mês, 2 metas"),
                new PlanInfo(PlanType.MONTHLY, "Pro Mensal", PRICE_MONTHLY, 1, "Ilimitado + cartão de crédito"),
                new PlanInfo(PlanType.SEMIANNUAL, "Pro Semestral", PRICE_SEMIANNUAL, 6, "Ilimitado + cartão de crédito (16% OFF)"),
                new PlanInfo(PlanType.ANNUAL, "Elite Anual", PRICE_ANNUAL, 12, "Ilimitado + cartão + Open Finance (24% OFF)")
        );
    }

    public SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlanType(),
                subscription.getStatus(),
                subscription.getStartsAt(),
                subscription.getExpiresAt(),
                subscription.getAmountPaid(),
                subscription.getCreatedAt()
        );
    }
}
