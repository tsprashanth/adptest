package com.adp.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.adp.restservice.controller.ChangeController;
import com.adp.restservice.model.ChangeRequest;
import com.adp.restservice.service.ChangeService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ChangeController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@WithMockUser
class ChangeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChangeService changeService;

	@InjectMocks
	private ChangeController changeController;

	String exampleInput = "{\"billAmount\":\"7.1\",\"paidAmount\":\"10\",\"maximizeCoins\":false}";

	@Test
	void testCalculateChangeEndpoint() throws Exception {

		Map<Double, Integer> mockResponse = new HashMap<>();
		mockResponse.put(2.0, 1);
		mockResponse.put(0.25, 3);
		mockResponse.put(0.1, 1);
		mockResponse.put(0.05, 1);
		ChangeRequest request = new ChangeRequest("7.1", "10", Boolean.FALSE);
		Mockito.when(changeService.calculateChange(request)).thenReturn(mockResponse);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/change").accept(MediaType.APPLICATION_JSON)
				.content(exampleInput).contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());

	}

	@Test
	void testCalculateChangeEndpoint_InsufficientFunds_ThrowsException() {
		// Prepare mock service response for insufficient funds
		when(changeService.calculateChange(new ChangeRequest("10.50", "9.00", true)))
				.thenThrow(new IllegalArgumentException("Paid amount is less than bill amount"));

		// Make the API call and verify that an exception is thrown
		assertThrows(IllegalArgumentException.class,
				() -> changeController.change(new ChangeRequest("10.50", "9.00", true)));
	}

	@Test
	void testCalculateChangeEndpoint_NonNumericInput_ThrowsException() {
		// Make the API call with non-numeric inputs and verify that an exception is
		// thrown
		assertThrows(IllegalArgumentException.class,
				() -> changeController.change(new ChangeRequest("abc", "def", true)));
	}

	@Test
	void testCalculateChangeEndpoint_NegativeAmount_ThrowsException() {
		// Make the API call with negative amounts and verify that an exception is
		// thrown
		assertThrows(IllegalArgumentException.class,
				() -> changeController.change(new ChangeRequest("-10.50", "12.15", true)));
	}

	@Test
	void testCalculateChangeEndpoint_InvalidMaximizeCoins_ThrowsException() {
		// Make the API call with invalid maximizeCoins flag and verify that an
		// exception is thrown
		assertThrows(IllegalArgumentException.class,
				() -> changeController.change(new ChangeRequest("10.50", "12test15", false)));
	}
}
