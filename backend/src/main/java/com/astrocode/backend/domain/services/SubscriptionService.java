package com.astrocode.backend.domain.services;

import com.astrocode.backend.api.dto.subscription.CheckoutRequest;
import com.astrocode.backend.api.dto.subscription.CheckoutResponse;
import com.astrocode.backend.api.dto.subscription.PlanInfo;
import com.astrocode.backend.api.dto.subscription.PreferenceCheckoutResponse;
import com.astrocode.backend.api.dto.subscription.SubscriptionResponse;
import com.astrocode.backend.api.dto.subscription.SubscriptionStatusApiResponse;
import com.astrocode.backend.domain.entities.Subscription;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.PaymentRejectedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import com.astrocode.backend.domain.model.MpCheckoutPlanId;
import com.astrocode.backend.domain.model.enums.PlanType;
import com.astrocode.backend.domain.model.enums.SubscriptionStatus;
import com.astrocode.backend.domain.repositories.SubscriptionRepository;
import com.astrocode.backend.domain.repositories.UserRepository;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private static final BigDecimal PRICE_MONTHLY = new BigDecimal("9.90");
    private static final BigDecimal PRICE_SEMIANNUAL = new BigDecimal("49.90");
    private static final BigDecimal PRICE_ANNUAL = new BigDecimal("89.90");
    private static final BigDecimal MIN_ANNUAL_THRESHOLD = new BigDecimal("80");
    private static final BigDecimal MIN_SEMIANNUAL_THRESHOLD = new BigDecimal("40");

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;
    private final PreferenceClient preferenceClient;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.webhook.base-url:}")
    private String webhookBaseUrl;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                              UserRepository userRepository,
                              PaymentClient paymentClient,
                              PreferenceClient preferenceClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
        this.preferenceClient = preferenceClient;
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
    public SubscriptionStatusApiResponse getSubscriptionStatusForApi(UUID userId) {
        Subscription sub = getSubscriptionOrThrow(userId);
        String status;
        if (sub.getStatus() == SubscriptionStatus.CANCELLED) {
            status = "CANCELLED";
        } else if (sub.getPlanType() != PlanType.FREE && sub.getStatus() == SubscriptionStatus.ACTIVE) {
            status = "PRO";
        } else {
            status = "FREE";
        }
        boolean isActive = "PRO".equals(status)
                && sub.getExpiresAt() != null
                && sub.getExpiresAt().isAfter(OffsetDateTime.now());
        LocalDateTime expires = sub.getExpiresAt() != null ? sub.getExpiresAt().toLocalDateTime() : null;
        return new SubscriptionStatusApiResponse(status, expires, isActive);
    }

    /**
     * Checkout por Preferência Mercado Pago (redirect). O webhook usa o mesmo {@code external_reference}
     * {@code sub:{subscriptionId}:{PlanType}} que o fluxo transparente.
     */
    @Transactional
    public PreferenceCheckoutResponse createPreferenceCheckout(UUID userId, String planId) {
        PlanType planType = MpCheckoutPlanId.toPlanType(planId);
        if (planType == PlanType.FREE) {
            throw new IllegalStateException("Plano FREE não requer checkout");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Subscription subscription = getOrCreateFree(user);
        BigDecimal unitPrice = getPrice(planType);
        String externalRef = "sub:" + subscription.getId() + ":" + planType.name();

        String base = frontendUrl != null ? frontendUrl.replaceAll("/$", "") : "http://localhost:3000";
        if (webhookBaseUrl == null || webhookBaseUrl.isBlank()) {
            throw new IllegalStateException(
                    "APP_WEBHOOK_BASE_URL não configurado. Defina a URL pública do backend para notificações do Mercado Pago.");
        }
        String notify = webhookBaseUrl.replaceAll("/$", "") + "/api/subscriptions/webhook";

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Grivy Pro — " + planType.name())
                .quantity(1)
                .unitPrice(unitPrice)
                .currencyId("BRL")
                .build();

        PreferenceRequest prefReq = PreferenceRequest.builder()
                .items(List.of(item))
                .payer(PreferencePayerRequest.builder().email(user.getEmail()).build())
                .externalReference(externalRef)
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success(base + "/subscription/success")
                        .failure(base + "/subscription/cancelled")
                        .pending(base + "/subscription/pending")
                        .build())
                .autoReturn("approved")
                .notificationUrl(notify)
                .build();

        try {
            Preference pref = preferenceClient.create(prefReq);
            String checkoutUrl = pref.getSandboxInitPoint() != null && !pref.getSandboxInitPoint().isBlank()
                    ? pref.getSandboxInitPoint()
                    : pref.getInitPoint();
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                throw new PaymentRejectedException("Mercado Pago não retornou URL de checkout.");
            }
            return new PreferenceCheckoutResponse(checkoutUrl, pref.getId());
        } catch (MPException | MPApiException e) {
            throw new PaymentRejectedException("Erro ao criar preferência no Mercado Pago: " + e.getMessage(), e);
        }
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
        String externalRef = "sub:" + subscription.getId() + ":" + request.planType().name();

        var payerBuilder = PaymentPayerRequest.builder()
                .email(request.payerEmail());

        if (request.payerIdentificationType() != null && !request.payerIdentificationType().isBlank()
                && request.payerIdentificationNumber() != null && !request.payerIdentificationNumber().isBlank()) {
            payerBuilder.identification(IdentificationRequest.builder()
                    .type(request.payerIdentificationType())
                    .number(request.payerIdentificationNumber())
                    .build());
        }

        var paymentRequestBuilder = PaymentCreateRequest.builder()
                .transactionAmount(amount)
                .token(request.token())
                .paymentMethodId(request.paymentMethodId())
                .installments(request.installments())
                .description("Grivy - Plano " + request.planType().name())
                .externalReference(externalRef)
                .payer(payerBuilder.build())
                .statementDescriptor("GRIVY");
        if (request.issuerId() != null && !request.issuerId().isBlank()) {
            paymentRequestBuilder.issuerId(request.issuerId());
        }
        var paymentRequest = paymentRequestBuilder.build();

        Payment payment;
        try {
            payment = paymentClient.create(paymentRequest);
        } catch (MPException | MPApiException e) {
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

        log.info("AUDITORIA pagamento checkout: userId={} subscriptionId={} planType={} mpPaymentId={} status={} amount={}",
                userId, subscription.getId(), request.planType(), payment.getId(), status, amount);

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
        if (amount == null) return PlanType.MONTHLY;
        if (amount.compareTo(PRICE_ANNUAL) <= 0 && amount.compareTo(MIN_ANNUAL_THRESHOLD) >= 0) return PlanType.ANNUAL;
        if (amount.compareTo(PRICE_SEMIANNUAL) <= 0 && amount.compareTo(MIN_SEMIANNUAL_THRESHOLD) >= 0) return PlanType.SEMIANNUAL;
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
}
