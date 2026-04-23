package com.sitemanagement.services;

import com.sitemanagement.models.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface IFinanceService {
    // Yöneticinin aidat, tamirat, fatura vb. giderleri daireye yansıtması
    boolean addDebtToResident(int residentId, BigDecimal amount, String description); 
    
    // Yapilmis odemeyi gir, belki daha sonra uygulama uzerinden odeme mantigi getiririz.
    boolean logManualPayment(int residentId, BigDecimal amount, String note); 
    
    BigDecimal calculateTotalDebt(int residentId);
    List<Transaction> getResidentLedger(int residentId); // Sakinin göreceği hesap dökümü
}