package com.adp.restservice.service;

import java.util.Map;

import com.adp.restservice.model.ChangeRequest;

// Service interface
public interface ChangeService {
	Map<Double, Integer> calculateChange(ChangeRequest request);
}
