package com.sitemanagement.models;

import com.sitemanagement.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int residentId; // Hangi sakine ait olduğu
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime date;

    public Transaction(int transactionId, int residentId, BigDecimal amount, TransactionType type, String description) {
        this.transactionId = transactionId;
        this.residentId = residentId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = LocalDateTime.now(); // İşlem anının tarih ve saatini otomatik atar
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getResidentId() {
        return residentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }
}