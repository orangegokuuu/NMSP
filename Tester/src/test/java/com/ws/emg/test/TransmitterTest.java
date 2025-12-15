/**
 *
 */
package com.ws.emg.test;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ws.emg.pojo.MessageObject;
import com.ws.smpp.ConnectionException;
import com.ws.smpp.MessageException;
import com.ws.smpp.Receiver;
import com.ws.smpp.SmsRequest;
import com.ws.smpp.Transmitter;
import com.ws.util.StringUtil;
import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SmppConfig.class)
@Log4j2
public class TransmitterTest {
	private static Logger logger = LogManager.getLogger(TransmitterTest.class);

	@Autowired
	private Transmitter transmitter = null;
	
	@Autowired
	private Receiver receiver = null;

	private static String genContent(int len) {
		StringUtil.brpad("", len, 'X');
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= len / 10; i++) {
			sb.append(StringUtil.blpad(String.valueOf(i * 10), 10, 'X'));
		}
		for (int i = 1; i <= (len % 10); i++) {
			sb.append("X");
		}
		return sb.toString();
	}

	@Test
	public void testSubmitSM() throws MessageException, ConnectionException, UnsupportedEncodingException {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {

		}
		StopWatch sw = new StopWatch();

		SmsRequest messageObject = new MessageObject();

		messageObject.setSmsSeq(RandomUtils.nextInt());
		messageObject.setSource("88623445008");
		messageObject.setSourceTON(1);
		messageObject.setSourceNPI(1);
		messageObject.setDestination("959253093041");
		messageObject.setDestinationTON(1);
		messageObject.setDestinationNPI(1);
		messageObject.setRequestDR(false);
		messageObject.setDataCoding(0);
		// String MSG1 = genContent(120);
//		String MSG1 = "@£$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞ^{}\\[~]|€ÆæßÉ";
//		logger.debug("From SMPP, MSG=[{}]", MSG1);
//		messageObject.setMessage(MSG1);

		// messageObject.setMessage("1234567890");
		// messageObject.setMessage("ALL Char ~!@#$%^&*()_+
		// QWERTYUIOP{}|ASDFGHJKL:\"[]\\;'ZXCVBNM<>?,./ \n");

//		sw.start();
//		transmitter.sendSMS(messageObject);

//		String MSG = "tPq41cKysFS1b7Bl";
//		Base64 b64 = new Base64();
//		byte[] big5 = b64.decode(MSG);

//		String utf8Str = charEncode(big5, "Big5", "UTF8");
//		messageObject.setDataCoding(1);

		messageObject.setMessage("1abcdefghi2abcdefghi3abcdefghi4abcdefghi5abcdefghi6abcdefghi7abcdefghi8abcdefghi9abcdefghi10abcdefgh11abcdefgh12abcdefgh13abcdefgh14abcdefgh15abcdefgh16abcdefgh17abcdefgh18abcdefgh19abcdefgh20abcdefgh21abcdefgh22abcdefgh23abcdefgh24abcdefgh25abcdefgh26abcdefgh27abcdefgh28abcdefgh29abcdefgh30abcdefgh31abcdefgh32abcdefgh33abcdefgh34abcdefgh");
//		messageObject.setMessage(MSG);
		
		
		transmitter.sendSMS(messageObject);

		sw.suspend();
		logger.info("Test Submit Elasped[{}]", sw.getNanoTime() / 1000 / 1000 / 1000);

		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {

		}
	}

//	@Test
	public void testEncoding() throws UnsupportedEncodingException {
		String MSG = "tPq41cKysFS1b7Bl";
		Base64 b64 = new Base64();
		byte[] big5 = b64.decode(MSG);

		String utf8Str = charEncode(big5, "Big5", "UTF8");
		String big5Str = new String(big5, "Big5");
		byte[] utf8 = utf8Str.getBytes();
		log.debug("Big5 Bytes[{}], Big5 String[{}], UTF8 String[{}] UTF8 Bytes[{}]", Hex.encodeHexString(big5), big5Str,
				utf8Str, Hex.encodeHexString(utf8));
	}

	private static String charEncode(byte[] raw, String fromEnc, String toEnc) throws UnsupportedEncodingException {
		String fromStr = new String(raw, fromEnc);
		String toStr = new String(fromStr.getBytes(toEnc), toEnc);

		return toStr;
	}

//	@Test
	public void testChar() {
		for (int i = 0; i < 256; i++) {
			char c = (char) i;
			byte b = (byte) c;
			log.debug("Char [{}] = [{}]", c, Hex.encodeHexString(new byte[]{b}));
		}
	}
}
