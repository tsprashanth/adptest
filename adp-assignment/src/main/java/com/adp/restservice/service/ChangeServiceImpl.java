package com.adp.restservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.adp.restservice.model.ChangeRequest;

import jakarta.annotation.PostConstruct;

// Service implementation class
@Service
@Component
public class ChangeServiceImpl implements ChangeService {

	private final Map<Integer, Integer> billCounts = new HashMap<>();
	private final Map<Double, Integer> coinCounts = new HashMap<>();

	@Value("${coins.available:false}")
	private boolean coinsAvailable;

	@Value("${coin.count.0.01:100}")
	private int count0_01;

	@Value("${coin.count.0.05:100}")
	private int count0_05;

	@Value("${coin.count.0.10:100}")
	private int count0_10;

	@Value("${coin.count.0.25:100}")
	private int count0_25;

	@PostConstruct
	public void initializeCounts() {
		// Initialize bill counts
		for (int bill : new int[] { 1, 2, 5, 10, 20, 50, 100 }) {
			billCounts.put(bill, 100);
		}

		// Initialize coin counts based on application.properties or default values
		if (coinsAvailable) {
			coinCounts.put(0.01, count0_01);
			coinCounts.put(0.05, count0_05);
			coinCounts.put(0.10, count0_10);
			coinCounts.put(0.25, count0_25);
		} else {
			// Initialize with default values
			coinCounts.put(0.01, 100);
			coinCounts.put(0.05, 100);
			coinCounts.put(0.10, 100);
			coinCounts.put(0.25, 100);
		}
	}

	@Override
	public Map<Double, Integer> calculateChange(ChangeRequest request) {

		double billAmount = parseAmount(request.getBillAmount());
		double paidAmount = parseAmount(request.getPaidAmount());
		if (paidAmount < billAmount) {
			throw new IllegalArgumentException("Paid amount cannot be less than bill amount");
		}
		validateInput(billAmount, paidAmount);

		// Calculate change with maximum coins
		Map<Double, Integer> change = calculateChange(billAmount, paidAmount, coinCounts, billCounts,
				request.isMaximizeCoins());

		// Create and return the response
		return change;
	}

	private double parseAmount(String amount) {
		try {
			return Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid bill or paid amount format");
		}
	}

	private void validateInput(double billAmount, double paidAmount) {
		if (billAmount <= 0 || paidAmount <= 0) {
			throw new IllegalArgumentException("Bill amount and paid amount must be positive numbers");
		}
	}

	private Map<Double, Integer> calculateChange(double billAmount, double paidAmount, Map<Double, Integer> coinCounts,
			Map<Integer, Integer> billCounts, boolean maximizeCoins) {
		// Round the change amount to the nearest whole decimal
		// Calculate change amount in cents

		int billAmountCents = (int) Math.round(billAmount * 100);
		int paidAmountCents = (int) Math.round(paidAmount * 100);
		int changeAmountCents = paidAmountCents - billAmountCents;

		Map<Double, Integer> change = new HashMap<>();

		// Calculate change using both coins and bills
		List<Double> availableDenominations = new ArrayList<>(coinCounts.keySet());
		if (!maximizeCoins) {
			for (Integer bill : billCounts.keySet()) {
				availableDenominations.add(bill.doubleValue());
			}
		}
		Collections.sort(availableDenominations, Collections.reverseOrder()); // Sort denominations in descending order

		for (double denomination : availableDenominations) {
			int denominationCents = (int) Math.round(denomination * 100);
			int denominationCount = coinCounts.getOrDefault(denomination, billCounts.get((int) denomination));
			if (changeAmountCents >= denominationCents && denominationCount > 0) {
				// Calculate the number of denominations to use
				int usedDenomination = Math.min(changeAmountCents / denominationCents, denominationCount);
				// Calculate the total value of the used denominations
				int changeValueCents = usedDenomination * denominationCents;

				// Update the change map
				change.put(denomination, usedDenomination);
				// Update the remaining change amount
				changeAmountCents -= changeValueCents;
				// Update the denomination count
				if (coinCounts.containsKey(denomination)) {
					coinCounts.put(denomination, coinCounts.get(denomination) - usedDenomination);
				} else {
					billCounts.put((int) denomination, billCounts.get((int) denomination) - usedDenomination);
				}
			}
		}

		// Check if change could be made
		if (changeAmountCents > 0) {
			throw new IllegalArgumentException("Not enough coins to make change");
		}

		return change;
	}

}
