package org.jasig.cas.authentication;

import javax.security.auth.login.AccountException;

/**
 * 账户锁定异常类
 * 
 * @author user
 * @date 2017年11月7日 上午11:50:25
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class CustomAccountLockedException extends AccountException {

	private static final long serialVersionUID = -5647587915354321392L;

	public CustomAccountLockedException() {
	}

	public CustomAccountLockedException(final String msg) {
		super(msg);
	}
}
