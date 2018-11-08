package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@RunWith(JUnitPlatform.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class APITest {
	
	@Autowired API api;
	
	@Test
	@DisplayName("Test Glovo Box required")
	void blockWordBoxTest() {
		List<OrderVM> orders = api.ordersByCourier("courier-2");
		assertEquals(orders.size(), 0);
	}
	
	
	@Test
	@DisplayName("Test BICYCLE block plus 5Km")
	void blockDistanceTest() {
		List<OrderVM> orders = api.ordersByCourier("courier-3");
		assertEquals(orders.size(), 2);
	}
	
	
	@Test
	@DisplayName("Test the priority is VIP order and the near courier")
	void vipFirstanNearCourierTest() {
		List<OrderVM> orders = api.ordersByCourier("courier-1");
		
		OrderVM orderExpected = new OrderVM("order-3", "I want a cake");
		OrderVM orderVM = orders.get(0);
		
		assertEquals(orderVM.getId(), orderExpected.getId());
	}			
}
