package com.ws.jms.message;

import java.util.Date;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import com.ws.api.util.HttpApiUtils;
import com.ws.emg.pojo.MessageObject;
 
@Component("jmsConsumer")
public class JmsConsumer {
    
	static final Logger logger = LogManager.getLogger(JmsConsumer.class);
    
//	@Autowired
//	private ConnectionFactory connectionFactory;
	
//	@Autowired
//	private JmsTemplate jmsTemplate;
	
	@Autowired
	@Qualifier("consumerJmsTemplate")
	private JmsTemplate consumerJmsTemplate;
	
	@Autowired
    ApplicationContext context;
	
	public MessageObject receiverMessage(){
		//logger.info("destinationName [{}]", consumerJmsTemplate.getDefaultDestinationName());
		MessageObject msg = (MessageObject) consumerJmsTemplate.receiveAndConvert();
		
		return msg;
	}
	
	public MessageObject receiverMessage(String destinationName){
		MessageObject msg = (MessageObject) consumerJmsTemplate.receiveAndConvert(destinationName);
		//logger.info("receiverMessage [{}] [{}]",destinationName, msg.toString());
		return msg;
	}
	
	public boolean isListener(){
		JmsListenerEndpointRegistry customRegistry =
	            context.getBean(JmsListenerEndpointRegistry.class);
		return customRegistry.isRunning();
	}
	
	public void stopListener(String id){
		JmsListenerEndpointRegistry customRegistry =
	            context.getBean(JmsListenerEndpointRegistry.class);
		Set<String> listenerContainerIds = customRegistry.getListenerContainerIds();
		for (String ids : listenerContainerIds) {
			if(id.equals(ids)){
				 MessageListenerContainer listenerContainer = customRegistry.getListenerContainer(ids);
				 listenerContainer.stop();
				 logger.info("*** stop Listener ,name:[{}]" , id);
			}
		   
		}
//		if(customRegistry.isRunning()){
//			customRegistry.stop();
//		}
	}
	
	public void startListener(){
		JmsListenerEndpointRegistry customRegistry =
	            context.getBean(JmsListenerEndpointRegistry.class);
		if(!customRegistry.isRunning()){
			customRegistry.start();
		}
	}

//	public MessageObject receiver(String destinationName){
//		MessageObject msg = null;
//		Connection connection = null;
//		try {
//			connection = connectionFactory.createConnection();
//			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		    Destination destination = session.createQueue(destinationName);
//		    MessageConsumer consumer = session.createConsumer(destination);
//		    connection.start();
//		    ObjectMessage objectMessage = (ObjectMessage)consumer.receive(5000);
//		    msg =(MessageObject) objectMessage.getObject();
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try {
//				connection.close();
//			} catch (JMSException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	   return msg; 
//	}
	
//    @JmsListener(destination = "sms.mt")
//    public void receiveMessage(final Message message) throws JMSException {
//    	logger.info("==receiveMessage==");
//        ObjectMessage objectMessage = (ObjectMessage)message;
//        MessageObject msg = (MessageObject)objectMessage.getObject();
//        System.out.println("MSGID:"+msg.getWsMessageId()+"==date:"+System.currentTimeMillis());
//    }
}
