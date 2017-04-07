package com.rp.pilot.excel.repository;

import com.rp.pilot.excel.model.Order;

public interface OrderRepository {

	public void insertOrder(Order order);
	
	public void csvDownload(int pageSize);

	public int selectTotalOrderCount();

}
