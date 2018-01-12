package com.wonhigh.bs.sso.server.manager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wonhigh.bs.sso.server.common.model.SsoUser;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017-11-21 下午2:57:06
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class SsoUserManagerImplTest extends BaseManagerTest {
	private static Logger logger = LoggerFactory.getLogger(SsoUserManagerImplTest.class);
	@Resource
	private SsoUserManager ssoUserManager;

	@Before
	public void init() throws Exception {

	}

	@Test
	public void testGetLoginDn() throws Exception {
		/*String uid = "liu.wp";
		String password = "a65350";
		long start = System.currentTimeMillis();
		int count = 100;
		for(int i = 0; i < count; i++) {
			SsoUser ssoUser = ssoUserManager.getLoginDn(uid,password);
			System.out.println("11111" + ssoUser.getSureName());
		}
		long end = System.currentTimeMillis();
		
		long cost = end - start;
		System.out.println(count + " times, " + "cost:" + cost);
		System.out.println(" per request cost:" + cost/count);*/

		final int N = 100;
		final String uid = "liu.wp";
		final String password = "a65350";
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(N);
		final CountDownLatch latch = new CountDownLatch(N);
		for (int i = 0; i < N; i++) {
			executor.execute(new Runnable(){
				@Override
				public void run() {
					try{
						SsoUser ssoUser = ssoUserManager.getLoginDn(uid,password);
						System.out.println("userName: " + ssoUser.getSureName());
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						latch.countDown();
					}

				}
			});
		}
		executor.shutdown();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		long end = System.currentTimeMillis();
		long cost = end - start;
		System.out.println(N + " times, " + "cost:" + cost);
	}

}
