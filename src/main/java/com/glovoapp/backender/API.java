package com.glovoapp.backender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
@SpringBootApplication
class API {
    private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    
    @Value("${backender.distance.block.bicycle}")
    private double distanceBlockBicycle;
    
    @Value("#{'${backender.block.wordBox}'.split(',')}") 
    List<String> wordBlockBox;
    
    @Value("#{'${backender.priority}'.split(',')}") 
    List<String> priorityList;
    
    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository, CourierRepository courierRepository) {
        this.welcomeMessage = welcomeMessage;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
    }

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return welcomeMessage;
    }

    @RequestMapping("/orders")
    @ResponseBody
    List<OrderVM> orders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }
    
    @RequestMapping("/orders/{courierId}")
    @ResponseBody
    List<OrderVM> ordersByCourier(@PathVariable String courierId) {    	
    	//get courier for parameter
    	Courier courier = courierRepository.findById(courierId);
    	
    	// getting all orders
    	List<Order> orders = orderRepository.findAll();
    	
    	// create new orders List
    	List<Order> showOrders = new ArrayList<Order>();
    	
    	for (Order order : orders) {
			// config in application.properties >> backender.block.wordBox
			if (order.containWords(wordBlockBox)) {
				if (courier.getBox() == false) {
					continue;
				}
			}
			
			// set the distance for pickup.
			// In my opinion is better put this attribute (distance) in the class base, 
			// for a code more clear and more easy maintenance after
			order.setDistance(DistanceCalculator.calculateDistance(order.getPickup(), courier.getLocation()));
			
			// config in application.properties >> backender.distance.block.bicycle
			if ( order.getDistance() >= this.distanceBlockBicycle) {
				if (courier.getVehicle() == Vehicle.BICYCLE) {
					continue;
				}
			}
			
			// create a new list 
			showOrders.add(order);
					
			// sorted for closest of couriers
			Collections.sort (showOrders, new Comparator<Object>() {
	            public int compare(Object o1, Object o2) {
	            	Order p1 = (Order) o1;
	            	Order p2 = (Order) o2;
	                return p1.getDistance() < p2.getDistance() ? -1 : (p1.getDistance() > p2.getDistance() ? +1 : 0);
	            }
	        });
		}
    	
    	// sorted for vip o food, depend the config
    	Collections.sort (showOrders, new Comparator<Object>() {
    		public int compare(Object o1, Object o2) {
	        	Order p1 = (Order) o1;
	            Order p2 = (Order) o2;
	            
	            // config in backender.priority
	            if (priorityList.get(0) == "vip" ) {
	            	return p1.getVip() && !p2.getVip() ? -1 : (p2.getVip() && !p1.getVip() ? +1 : 0);
	            }	            	
	            return p1.getFood() && !p2.getFood() ? -1 : (p2.getFood() && !p1.getFood() ? +1 : 0);
	            	
	         }
	       });

    	return showOrders
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        SpringApplication.run(API.class);
    }
}
