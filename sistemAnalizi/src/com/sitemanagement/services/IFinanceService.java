package com.sitemanagement.services;

import com.sitemanagement.enums.TransactionType;
import com.sitemanagement.models.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface IFinanceService {
    boolean addDebtToResident(int residentId, BigDecimal amount, TransactionType type, String description);

    boolean logManualPayment(int residentId, BigDecimal amount, String note);

    BigDecimal calculateTotalDebt(int residentId);

    List<Transaction> getResidentLedger(int residentId);

    List<Transaction> getSiteGeneralReport();
}