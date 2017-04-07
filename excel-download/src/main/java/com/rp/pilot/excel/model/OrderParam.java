package com.rp.pilot.excel.model;

public class OrderParam {
	private int pageNo;
	private int pageSize;
	private int totalCount;

	public OrderParam(int pageSize, int totalCount) {
		this.pageSize = pageSize;
		this.totalCount = totalCount;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return (pageNo - 1) * pageSize;
	}
	
	public int getLastPageNo() {
		return (totalCount / pageSize) + 1;
	}
}
