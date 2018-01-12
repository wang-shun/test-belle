package com.wonhigh.bs.sso.server.controller.api;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年12月13日 上午9:43:15
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class ControllerTestTask implements Callable<String>{
	
	private MultiValueMap params;
	private MockMvc mockMvc;
	private CountDownLatch countDownLatch;
	
	public ControllerTestTask(MultiValueMap params, MockMvc mockMvc,CountDownLatch countDownLatch) {
		super();
		this.params = params;
		this.mockMvc = mockMvc;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public String call() throws Exception {
		String r = null;
		try{
		 r = this.login();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			countDownLatch.countDown();
		}
		return r;
	}
	
	private String login() throws Exception{
		ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/login").params(params));
        MvcResult mvcResult = resultActions.andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(Thread.currentThread().getName()+"--客户端获得反馈数据:" + result);
		return result;
	}
	
	

}
