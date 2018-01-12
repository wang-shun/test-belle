package com.wonhigh.bs.sso.server.controller.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.wonhigh.bs.sso.server.common.util.AESUtil;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017-11-21 下午2:57:06
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class IndexControllerTest extends BaseControllerTest {
	@Resource
	private IndexController indexController;
	private MockMvc mockMvc;
	
	@Before
	public void init() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();
	}

	@Test
	public void testLogin() throws Exception {
		
		String bizCode = "OES";
		String bizSecret = "c433195555cc49f9";
		String loginName = "xiao.fz";
		String password = AESUtil.encode("S18028", bizSecret);
		String deviceId = "test";
		
		MultiValueMap params = new LinkedMultiValueMap();
		params.add("loginName", loginName);
		params.add("password", password);
		params.add("deviceId", deviceId);
		params.add("bizCode", bizCode);
		
		int count = 5000;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			executor.submit(new ControllerTestTask(params, mockMvc, countDownLatch));
		}
		executor.shutdown();
		countDownLatch.await();
		long end = System.currentTimeMillis();
		long cost = end - start;
		System.out.println(count + " times, " + "cost:" + cost);
		System.out.println(" per request cost:" + cost/count);
	}
	
}
