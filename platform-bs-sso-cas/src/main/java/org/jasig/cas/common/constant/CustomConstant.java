package org.jasig.cas.common.constant;

/**
 * 自定义常量
 * 
 * @author 石涛
 * @date 2017年12月14日 下午6:11:54
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface CustomConstant {

  interface CAPTCHA {
    /**
     * session中redis中验证码key值
     */
    String RANDOM_KEY = "RANDOM_KEY";

    /**
     * redis中验证码key：captchaCode+UUID.randomUUID().toString(
     */
    String CAPTCHA_CODE = "CAPTCHA_CODE";
  }

}
