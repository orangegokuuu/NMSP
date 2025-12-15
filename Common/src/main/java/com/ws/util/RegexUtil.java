package com.ws.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RegexUtil {
	public static final String allEx = "\\(\\w+\\)|\\[[\\w|-]+\\]|\\{[\\w|-|,]\\}|\\[b|B|d|D|s|S|w|W]|[*|+|?|^]";

	public static boolean exactMatch(String pattern, String value) {
		Pattern p = Pattern.compile(pattern);
		return p.matcher(value).matches();
	}
	
	public static boolean matchAny(String value, String... regexList) {
		for (String regex : regexList) {
			if (Pattern.matches(regex, value)) {
				return true;
			}
		}
		return false;
	}

	public static String longestMatch(Collection<String> pattern, String value) {
		List<String> matched = new ArrayList<String>();

		matched = pattern.stream().filter(e -> exactMatch(e, value)).sorted(new Comparator<String>() {
			@Override
			public int compare(String p1, String p2) {
				int charOnly1 = p1.replaceAll(allEx, "").length();
				int charOnly2 = p2.replaceAll(allEx, "").length();

				log.trace("Pattern[{}] matched, compare with[{}] length = [{} {}]",  p1, p2, charOnly1, charOnly2);
				
				return new Integer(charOnly1).compareTo(charOnly2) * -1;
			}
		}).collect(Collectors.toList());
		
		if (matched.isEmpty()) {
			return null;
		} else {
			return matched.get(0);
		}
	}
	

}
