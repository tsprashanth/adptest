package com.adp.restservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.adp.restservice.model.ChangeRequest;
import com.adp.restservice.service.ChangeService;

@RestController
public class ChangeController {
	@Autowired
	private ChangeService changeService;

	@PostMapping(value = "/change", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> change(@RequestBody ChangeRequest request) {
		try {
			Map<Double, Integer> change = changeService.calculateChange(request);
			return ResponseEntity.ok(change);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
}
