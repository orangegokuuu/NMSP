package com.ws.jms.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.emg.pojo.MessageObject;
import com.ws.jms.configuration.Jmsconfig;
import com.ws.jms.service.JmsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=Jmsconfig.class,loader=AnnotationConfigContextLoader.class)
public class ReceiverTest {

	@Autowired
	JmsService jmsService;
	
//	@Autowired
//	JmsTemplate jmsTemplate;
	
	@Autowired
	@Qualifier("consumerJmsTemplate")
	private JmsTemplate consumerJmsTemplate;
		
	@Test
	public void receiverMessage(){
		System.out.println("====  receiver message  ====");
		/*try {
			for(int i=1;i<10;i++){
				//MessageObject msg = jmsService.receiver("sms.mo.intra");
				MessageObject msg = jmsService.receiverMessage("sms.mo.intra");
				System.out.println("msg:"+msg.toString());
			}
		} catch (JmsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			//jmsService.receiverMessage();
			//jmsService.receiverMessage("sms.mt");getDefaultDestination
			//System.out.println("destinationName:"+consumerJmsTemplate.getDefaultDestinationName());
			consumerJmsTemplate.receive();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JmsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
