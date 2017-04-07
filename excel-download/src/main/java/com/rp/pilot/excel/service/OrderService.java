package com.rp.pilot.excel.service;

import com.rp.pilot.excel.model.Order;

public interface OrderService {
	public void saveOrder(Order order);
	
	public String csvDownload(int pageSize);

}
