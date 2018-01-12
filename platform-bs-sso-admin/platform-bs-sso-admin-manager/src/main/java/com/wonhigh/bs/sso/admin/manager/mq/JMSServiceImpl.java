package com.wonhigh.bs.sso.admin.manager.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service("jmsService")
public class JMSServiceImpl implements JMSService {
	@Autowired(required = false)
	JmsTemplate jmsTemplate;

	@Override
	public boolean send(Object model) {
		if(null==jmsTemplate){
			return false;
		}
		final String data = makeJson(model);
		jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(data);
				return message;
			}
		});
		return true;
	}

	@SuppressWarnings("deprecation")
	public static final String makeJson(Object model) {
		try {
			/*ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.getSerializationConfig().withSerializationInclusion(Inclusion.NON_NULL);
			String json = objectMapper.writeValueAsString(model);*/
			String json = JSONObject.toJSONString(model);
			return json.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
