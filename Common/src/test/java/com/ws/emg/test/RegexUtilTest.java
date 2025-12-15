package com.ws.emg.test;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.ws.util.RegexUtil;

public class RegexUtilTest {

	@Test
	public void testExactMatching() {
		String s1 = "01234[0-9]00800";
		String s2 = "01234[0-9]008[0-9]{2}";
		String s3 = "01234[0-9]{6}";

		String value = "01234500800";
		
		Assertions.assertTrue(RegexUtil.exactMatch(s1, value));
		Assertions.assertTrue(RegexUtil.exactMatch(s2, value));
		Assertions.assertTrue(RegexUtil.exactMatch(s3, value));
	}
	
	@Test
	public void testLongestMatching() {
		String s1 = "01234[0-9]00800";
		String s2 = "01234[0-9]008[0-9]{2}";
		String s3 = "01234[0-9]{6}";
		String s4 = "01235[0-9]9083[\\d]";

		String value = "01234500800";
		
		List<String> pattern = new ArrayList<String>();
		pattern.add(s3);
		pattern.add(s2);
		pattern.add(s4);
		pattern.add(s1);
		
		String top = RegexUtil.longestMatch(pattern, value);
		Assertions.assertEquals(s1, top);
	}

}
