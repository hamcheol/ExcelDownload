package com.rp.pilot.excel.repository;

import com.rp.pilot.excel.model.Order;

public interface OrderRepository {

	public void insertOrder(Order order);
	
	/**
	 * CSV 다운로드 
	 * @param pageSize
	 */
	public String csvDownload(int pageSize);

	public int selectTotalOrderCount();

}
