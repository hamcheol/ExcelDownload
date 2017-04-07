package com.rp.pilot.excel.service;

import java.text.Format;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.rp.pilot.excel.model.Order;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderService orderService;
	
	@Test
	public void testSelectOrderTotal() {
		int pageSize = 200;
		orderService.csvDownload(pageSize);
	}

	@Test
	public void testDummyOrders() throws InterruptedException {
		for (int i = 0; i < 2000; i++) {
			createOrder();
			Thread.sleep(10);
		}
	}

	public void createOrder() {
		String ordNo = generateOrdNo();
		String mbrId = RandomStringUtils.random(10, true, true);
		String data = RandomStringUtils.random(1500, true, true);
		orderService.saveOrder(new Order(ordNo, mbrId, data));
	}

	public static String date(String pattern) {
		Date today = new Date();
		Format fdf = FastDateFormat.getInstance(pattern, Locale.getDefault());
		return fdf.format(today);
	}

	public static String generateOrdNo() {
		String curTime = date("yyyyMMddHHmmss");
		int postfix = RandomUtils.nextInt(100000, 999999);
		return curTime + postfix;
	}

}
