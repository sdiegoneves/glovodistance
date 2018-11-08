package com.glovoapp.backender;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class APITest extends ApplicationTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private API api;
	
	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(api).build();
	}
	
	@Test
	void testOrdersByCourier() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/orders/courier-7e1552836a04")).andExpect(MockMvcResultMatchers.status().isOk());
	}
}
