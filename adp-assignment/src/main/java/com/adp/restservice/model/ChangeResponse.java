package com.adp.restservice.model;

import java.util.List;

// Response model class
public class ChangeResponse {
    private List<Double> changeCoins;
    private double totalChange;

    public ChangeResponse(List<Double> changeCoins) {
        this.changeCoins = changeCoins;
        this.totalChange = calculateTotalChange(changeCoins);
    }

    private double calculateTotalChange(List<Double> changeCoins) {
        double total = 0.0;
        for (double coin : changeCoins) {
            total += coin;
        }
        return total;
    }

	public List<Double> getChangeCoins() {
		return changeCoins;
	}

	public void setChangeCoins(List<Double> changeCoins) {
		this.changeCoins = changeCoins;
	}

	public double getTotalChange() {
		return totalChange;
	}

	public void setTotalChange(double totalChange) {
		this.totalChange = totalChange;
	}
    
    
}
