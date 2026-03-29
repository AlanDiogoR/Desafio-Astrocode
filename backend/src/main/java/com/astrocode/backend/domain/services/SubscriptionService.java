package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.subscription.PaymentRequest;
import com.astrocode.backend.api.dto.subscription.PaymentResponse;
import com.astrocode.backend.api.dto.subscription.PlanInfo;
import com.astrocode.backend.api.dto.subscription.SubscriptionResponse;
import com.astrocode.backend.api.dto.subscription.SubscriptionStatusResponse;
import com.astrocode.backend.domain.entities.Subscription;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.MpCheckoutPlanId;
import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private static final BigDecimal PRICE_MONTHLY = new BigDecimal("19.90");
    private static final BigDecimal PRICE_SEMIANNUAL = new BigDecimal("49.90");
    private static final BigDecimal PRICE_ANNUAL = new BigDecimal("179.90");
    private static final BigDecimal MIN_ANNUAL_THRESHOLD = new BigDecimal("150");
    private static final BigDecimal MIN_SEMIANNUAL_THRESHOLD = new BigDecimal("40");

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

    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getStatus(UUID userId) {
        Subscription sub = getSubscriptionOrThrow(userId);
        String plan;
        if (sub.getStatus() == SubscriptionStatus.CANCELLED) {
            plan = "FREE";
        } else if (sub.getPlanType() != PlanType.FREE && sub.getStatus() == SubscriptionStatus.ACTIVE) {
            plan = "PRO";
        } else {
            plan = "FREE";
        }
        boolean isActive = "PRO".equals(plan)
                && sub.getExpiresAt() != null
                && sub.getExpiresAt().isAfter(OffsetDateTime.now());
        LocalDateTime expires = sub.getExpiresAt() != null ? sub.getExpiresAt().toLocalDateTime() : null;
        return new SubscriptionStatusResponse(isActive ? "PRO" : "FREE", isActive, expires);
    }

    /**
     * Checkout transparente: token do cartão gerado no browser (MP.js).
     */
    @Transactional
    public PaymentResponse processPayment(UUID userId, PaymentRequest request) {
        PlanType planType = MpCheckoutPlanId.toPlanType(request.planId());
        if (planType == PlanType.FREE) {
            throw new IllegalStateException("Plano FREE não requer pagamento");
        }

        BigDecimal expectedAmount = getPrice(planType).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sentAmount = request.transactionAmount().setScale(2, RoundingMode.HALF_UP);
        if (expectedAmount.compareTo(sentAmount) != 0) {
            throw new IllegalArgumentException("Valor não confere com o plano selecionado.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Subscription subscription = getOrCreateFree(user);
        String externalRef = "sub:" + subscription.getId() + ":" + planType.name();

        var payerBuilder = PaymentPayerRequest.builder()
                .email(request.payer().email());
        if (request.payer().firstName() != null && !request.payer().firstName().isBlank()) {
            payerBuilder.firstName(request.payer().firstName());
        }
        if (request.payer().lastName() != null && !request.payer().lastName().isBlank()) {
            payerBuilder.lastName(request.payer().lastName());
        }
        if (request.payer().identification() != null
                && request.payer().identification().number() != null
                && !request.payer().identification().number().isBlank()) {
            payerBuilder.identification(
                    com.mercadopago.client.common.IdentificationRequest.builder()
                            .type(request.payer().identification().type())
                            .number(request.payer().identification().number())
                            .build()
            );
        }

        var paymentRequestBuilder = PaymentCreateRequest.builder()
                .token(request.token())
                .transactionAmount(request.transactionAmount())
                .installments(request.installments())
                .paymentMethodId(request.paymentMethodId())
                .description("Grivy - Plano " + planType.name())
                .externalReference(externalRef)
                .payer(payerBuilder.build())
                .statementDescriptor("GRIVY");
        if (request.issuerId() != null && !request.issuerId().isBlank()) {
            paymentRequestBuilder.issuerId(request.issuerId());
        }

        Payment payment;
        try {
            payment = paymentClient.create(paymentRequestBuilder.build());
        } catch (MPApiException e) {
            log.error("[MP API ERROR] status={} message={}", e.getStatusCode(), e.getMessage());
            throw new IllegalStateException("Erro na API do Mercado Pago: " + e.getMessage());
        } catch (MPException e) {
            log.error("[MP ERROR] {}", e.getMessage());
            throw new IllegalStateException("Erro ao processar pagamento: " + e.getMessage());
        }

        String status = payment.getStatus();
        log.info("[MP] Pagamento criado: id={} status={} detail={}",
                payment.getId(), status, payment.getStatusDetail());

        subscription.setMpPaymentId(payment.getId() != null ? String.valueOf(payment.getId()) : null);
        subscription.setMpExternalReference(externalRef);

        if ("approved".equals(status)) {
            activatePaidPlan(subscription, planType, request.transactionAmount(), payment.getId());
            subscription = subscriptionRepository.save(subscription);
            log.info("AUDITORIA pagamento checkout: userId={} subscriptionId={} planType={} mpPaymentId={} status={} amount={}",
                    userId, subscription.getId(), planType, payment.getId(), status, request.transactionAmount());
            return new PaymentResponse(
                    payment.getId(),
                    status,
                    payment.getStatusDetail(),
                    "Pagamento aprovado! Bem-vindo ao Grivy Pro."
            );
        }

        subscription = subscriptionRepository.save(subscription);

        return new PaymentResponse(
                payment.getId(),
                status,
                payment.getStatusDetail(),
                translateStatusDetail(payment.getStatusDetail())
        );
    }

    @Transactional
    public void handlePaymentApproved(Long mpPaymentId) {
        Payment payment;
        try {
            payment = paymentClient.get(mpPaymentId);
        } catch (Exception e) {
            log.error("Erro ao obter pagamento do Mercado Pago: {}", e.getMessage());
            return;
        }

        if (payment == null || !"approved".equals(payment.getStatus())) {
            return;
        }

        String externalRef = payment.getExternalReference();
        if (externalRef == null || !externalRef.startsWith("sub:")) {
            return;
        }

        String[] parts = externalRef.split(":");
        if (parts.length < 2) {
            log.warn("externalRef malformado: {}", externalRef);
            return;
        }
        UUID subId = UUID.fromString(parts[1]);
        Subscription subscription = subscriptionRepository.findById(subId).orElse(null);
        if (subscription == null) {
            return;
        }

        PlanType planType;
        if (parts.length >= 3) {
            try {
                planType = PlanType.valueOf(parts[2]);
            } catch (IllegalArgumentException e) {
                planType = inferPlanFromAmount(payment.getTransactionAmount());
            }
        } else {
            planType = inferPlanFromAmount(payment.getTransactionAmount());
        }

        activatePaidPlan(subscription, planType,
                payment.getTransactionAmount() != null ? payment.getTransactionAmount() : BigDecimal.ZERO,
                payment.getId());
        subscription.setMpPaymentId(String.valueOf(payment.getId()));
        subscription.setMpExternalReference(externalRef);
        subscriptionRepository.save(subscription);

        log.info("AUDITORIA pagamento webhook MP: subscriptionId={} userId={} planType={} mpPaymentId={} externalRef={}",
                subscription.getId(), subscription.getUser().getId(), planType, payment.getId(), externalRef);
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
        if (amount == null) {
            return PlanType.MONTHLY;
        }
        if (amount.compareTo(PRICE_ANNUAL) <= 0 && amount.compareTo(MIN_ANNUAL_THRESHOLD) >= 0) {
            return PlanType.ANNUAL;
        }
        if (amount.compareTo(PRICE_SEMIANNUAL) <= 0 && amount.compareTo(MIN_SEMIANNUAL_THRESHOLD) >= 0) {
            return PlanType.SEMIANNUAL;
        }
        return PlanType.MONTHLY;
    }

    @Transactional
    public void cancelSubscription(UUID userId) {
        Subscription subscription = getSubscriptionOrThrow(userId);
        if (subscription.getPlanType() == PlanType.FREE) {
            throw new IllegalStateException("Assinatura gratuita não pode ser cancelada");
        }
        var previousPlan = subscription.getPlanType();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
        log.info("AUDITORIA alteração de plano: userId={} subscriptionId={} ação=cancelamento planoAnterior={}",
                userId, subscription.getId(), previousPlan);
    }

    @Transactional
    public void expireSubscriptions() {
        OffsetDateTime now = OffsetDateTime.now();
        var expired = subscriptionRepository.findByStatusAndExpiresAtBefore(SubscriptionStatus.ACTIVE, now);
        var toSave = expired.stream()
                .filter(sub -> sub.getPlanType() != PlanType.FREE)
                .peek(sub -> {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    sub.setPlanType(PlanType.FREE);
                })
                .toList();
        if (!toSave.isEmpty()) {
            subscriptionRepository.saveAll(toSave);
            log.info("AUDITORIA expiração de planos: assinaturasRebaixadas={} ids={}",
                    toSave.size(),
                    toSave.stream().map(s -> s.getId().toString()).toList());
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
                new PlanInfo(PlanType.MONTHLY, "Pro Mensal", PRICE_MONTHLY, 1, "Tudo ilimitado + cartões de crédito"),
                new PlanInfo(PlanType.SEMIANNUAL, "Pro Semestral", PRICE_SEMIANNUAL, 6, "Tudo ilimitado + cartões de crédito (16% OFF)"),
                new PlanInfo(PlanType.ANNUAL, "Elite Anual", PRICE_ANNUAL, 12, "Tudo ilimitado + cartões + Open Finance (24% OFF)")
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

    private String translateStatusDetail(String detail) {
        if (detail == null) {
            return "Pagamento não aprovado. Tente novamente.";
        }
        return switch (detail) {
            case "cc_rejected_insufficient_amount" -> "Saldo insuficiente no cartão.";
            case "cc_rejected_bad_filled_card_number" -> "Número do cartão inválido.";
            case "cc_rejected_bad_filled_date" -> "Data de validade inválida.";
            case "cc_rejected_bad_filled_security_code" -> "Código de segurança inválido.";
            case "cc_rejected_blacklist" -> "Cartão não autorizado. Contate seu banco.";
            case "cc_rejected_call_for_authorize" -> "Ligue para seu banco para autorizar o pagamento.";
            case "cc_rejected_card_disabled" -> "Cartão desabilitado. Ative-o com seu banco.";
            case "cc_rejected_duplicated_payment" -> "Pagamento duplicado. Aguarde antes de tentar novamente.";
            case "cc_rejected_high_risk" -> "Pagamento recusado por segurança. Tente outro cartão.";
            case "pending_contingency" -> "Pagamento em análise. Aguarde a confirmação.";
            case "pending_review_manual" -> "Pagamento em revisão. Confirmaremos em breve por e-mail.";
            default -> "Pagamento não aprovado (" + detail + "). Tente com outro cartão.";
        };
    }
}
