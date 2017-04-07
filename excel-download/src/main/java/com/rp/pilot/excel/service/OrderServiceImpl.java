package com.rp.pilot.excel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rp.pilot.excel.model.Order;
import com.rp.pilot.excel.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public void saveOrder(Order order) {
		orderRepository.insertOrder(order);
	}

	@Override
	public void csvDownload(int pageSize) {
		orderRepository.csvDownload(pageSize);
	}

}
