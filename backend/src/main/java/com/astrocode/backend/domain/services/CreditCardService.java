package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.PlanAccess.RequiresPro;
import com.astrocode.backend.api.dto.creditcard.CreditCardRequest;
import com.astrocode.backend.api.dto.creditcard.PayBillRequest;
import com.astrocode.backend.domain.entities.*;
import com.astrocode.backend.domain.exceptions.*;
import com.astrocode.backend.domain.model.enums.BillStatus;
import com.astrocode.backend.domain.model.enums.TransactionType;
import com.astrocode.backend.domain.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CreditCardService {

    private static final String PAGAMENTO_FATURA_CATEGORY = "Pagamento de Fatura";

    private final CreditCardRepository creditCardRepository;
    private final CreditCardBillRepository creditCardBillRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final PlanLimitService planLimitService;

    public CreditCardService(
            CreditCardRepository creditCardRepository,
            CreditCardBillRepository creditCardBillRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            BankAccountRepository bankAccountRepository,
            TransactionRepository transactionRepository,
            PlanLimitService planLimitService
    ) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardBillRepository = creditCardBillRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.planLimitService = planLimitService;
    }

    @Transactional
    @RequiresPro(message = "Cartão de crédito está disponível no plano PRO. Faça upgrade para continuar.")
    public CreditCard create(CreditCardRequest request, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        creditCardRepository.findByUserIdAndNameIgnoreCase(userId, request.name().trim())
                .ifPresent(_c -> {
                    throw new DuplicateCreditCardNameException(request.name().trim());
                });

        var creditCard = CreditCard.builder()
                .user(user)
                .name(request.name().trim())
                .creditLimit(request.creditLimit())
                .closingDay(request.closingDay())
                .dueDay(request.dueDay())
                .color(request.color())
                .currentBillAmount(BigDecimal.ZERO)
                .build();

        creditCard = creditCardRepository.save(creditCard);
        createInitialBill(creditCard);
        return creditCard;
    }

    private void createInitialBill(CreditCard creditCard) {
        var today = LocalDate.now();
        var billMonth = today.getMonthValue();
        var billYear = today.getYear();

        if (today.getDayOfMonth() >= creditCard.getClosingDay()) {
            if (billMonth == 12) {
                billMonth = 1;
                billYear++;
            } else {
                billMonth++;
            }
        }

        var yearMonth = YearMonth.of(billYear, billMonth);
        int safeClosingDay = Math.min(creditCard.getClosingDay(), yearMonth.lengthOfMonth());
        var closingDate = LocalDate.of(billYear, billMonth, safeClosingDay);

        var dueYearMonth = yearMonth.plusMonths(1);
        int safeDueDay = Math.min(creditCard.getDueDay(), dueYearMonth.lengthOfMonth());
        var dueDate = LocalDate.of(dueYearMonth.getYear(), dueYearMonth.getMonthValue(), safeDueDay);

        var bill = CreditCardBill.builder()
                .creditCard(creditCard)
                .month(billMonth)
                .year(billYear)
                .totalAmount(BigDecimal.ZERO)
                .status(BillStatus.OPEN)
                .closingDate(closingDate)
                .dueDate(dueDate)
                .build();

        creditCardBillRepository.save(bill);
    }

    public List<CreditCard> findAllByUserId(UUID userId) {
        return creditCardRepository.findByUserId(userId);
    }

    public CreditCard findById(UUID creditCardId, UUID userId) {
        return creditCardRepository.findByUserIdAndId(userId, creditCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão de crédito não encontrado"));
    }

    @Transactional
    public CreditCard update(UUID creditCardId, CreditCardRequest request, UUID userId) {
        var creditCard = findById(creditCardId, userId);

        creditCardRepository.findByUserIdAndNameIgnoreCase(userId, request.name().trim())
                .filter(c -> !c.getId().equals(creditCardId))
                .ifPresent(_c -> {
                    throw new DuplicateCreditCardNameException(request.name().trim());
                });

        creditCard.setName(request.name().trim());
        creditCard.setCreditLimit(request.creditLimit());
        creditCard.setClosingDay(request.closingDay());
        creditCard.setDueDay(request.dueDay());
        creditCard.setColor(request.color());

        return creditCardRepository.save(creditCard);
    }

    @Transactional
    public void delete(UUID creditCardId, UUID userId) {
        var creditCard = findById(creditCardId, userId);

        var openBill = creditCardBillRepository.findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
                creditCardId, BillStatus.OPEN);
        if (openBill.isPresent() && openBill.get().getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Não é possível excluir o cartão pois há despesas na fatura em aberto");
        }

        creditCardRepository.delete(creditCard);
    }

    public CreditCardBill getCurrentBill(UUID creditCardId, UUID userId) {
        var creditCard = findById(creditCardId, userId);
        return creditCardBillRepository.findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
                        creditCardId, BillStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura em aberto não encontrada"));
    }

    /**
     * Obtém a fatura OPEN atual do cartão, criando uma nova se não existir
     * (ex.: novo ciclo após fechamento da fatura anterior).
     */
    @Transactional
    public CreditCardBill getOrCreateCurrentBill(UUID creditCardId, UUID userId) {
        var creditCard = findById(creditCardId, userId);
        var existing = creditCardBillRepository.findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
                creditCardId, BillStatus.OPEN);
        if (existing.isPresent()) {
            return existing.get();
        }
        try {
            createInitialBill(creditCard);
        } catch (DataIntegrityViolationException e) {
            return creditCardBillRepository.findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
                            creditCardId, BillStatus.OPEN)
                    .orElseThrow(() -> new ResourceNotFoundException("Fatura ativa não encontrada para o cartão"));
        }
        return creditCardBillRepository.findFirstByCreditCardIdAndStatusOrderByYearDescMonthDesc(
                        creditCardId, BillStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura ativa não encontrada para o cartão"));
    }

    public List<CreditCardBill> getBillHistory(UUID creditCardId, UUID userId) {
        findById(creditCardId, userId);
        return creditCardBillRepository.findByCreditCardId(creditCardId);
    }

    @Transactional
    public CreditCardBill payBill(UUID billId, PayBillRequest request, UUID userId) {
        var bill = findBillById(billId, userId);
        var creditCard = bill.getCreditCard();

        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalStateException("Esta fatura já foi paga");
        }

        var bankAccount = bankAccountRepository.findById(request.bankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada"));

        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new AccountNotOwnedException("Você não tem permissão para acessar esta conta");
        }

        var accountBalance = bankAccount.getCurrentBalance();
        if (accountBalance.compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente na conta para realizar o pagamento");
        }

        planLimitService.checkFreePlanTransactionLimit(userId);

        var category = getOrCreatePagamentoFaturaCategory(userId);

        var transaction = Transaction.builder()
                .user(bankAccount.getUser())
                .bankAccount(bankAccount)
                .creditCardBill(bill)
                .category(category)
                .name("Pagamento fatura " + creditCard.getName() + " - " + bill.getMonth() + "/" + bill.getYear())
                .amount(request.amount())
                .date(request.payDate())
                .type(TransactionType.EXPENSE)
                .build();

        transactionRepository.save(transaction);

        bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().subtract(request.amount()));
        bankAccountRepository.save(bankAccount);

        var paidAmount = request.amount();
        var billTotal = bill.getTotalAmount();
        var wasOpen = bill.getStatus() == BillStatus.OPEN;

        if (paidAmount.compareTo(billTotal) >= 0) {
            bill.setStatus(BillStatus.PAID);
            bill.setPaidDate(request.payDate());
            bill.setPaidFromAccount(bankAccount);
            if (wasOpen) {
                creditCard.setCurrentBillAmount(BigDecimal.ZERO);
            }
            getOrCreateCurrentBill(creditCard.getId(), userId);
        } else {
            bill.setTotalAmount(billTotal.subtract(paidAmount));
            if (wasOpen) {
                creditCard.setCurrentBillAmount(creditCard.getCurrentBillAmount().subtract(paidAmount).max(BigDecimal.ZERO));
            }
        }

        creditCardBillRepository.save(bill);
        creditCardRepository.save(creditCard);

        return bill;
    }

    private Category getOrCreatePagamentoFaturaCategory(UUID userId) {
        return categoryRepository.findByUserIdAndTypeAndNameIgnoreCase(userId, TransactionType.EXPENSE, PAGAMENTO_FATURA_CATEGORY)
                .orElseGet(() -> {
                    var user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                    var category = Category.builder()
                            .user(user)
                            .name(PAGAMENTO_FATURA_CATEGORY)
                            .icon("credit_card")
                            .type(TransactionType.EXPENSE)
                            .build();
                    return categoryRepository.save(category);
                });
    }

    public CreditCardBill findBillById(UUID billId, UUID userId) {
        var bill = creditCardBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        if (!bill.getCreditCard().getUser().getId().equals(userId)) {
            throw new ResourceAccessDeniedException("Você não tem permissão para acessar esta fatura");
        }

        return bill;
    }
}
