package com.sitemanagement.managers;

import java.math.BigDecimal;
import java.util.List;

import com.sitemanagement.models.Transaction;
import com.sitemanagement.services.IFinanceService;

public class FinanceManager implements IFinanceService{

	@Override
	public boolean addDebtToResident(int residentId, BigDecimal amount, String description) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logManualPayment(int residentId, BigDecimal amount, String note) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BigDecimal calculateTotalDebt(int residentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> getResidentLedger(int residentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
