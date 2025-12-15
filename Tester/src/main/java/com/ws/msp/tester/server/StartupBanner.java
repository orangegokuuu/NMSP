/**
 * 
 */
package com.ws.msp.tester.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

public class StartupBanner implements Banner {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.boot.Banner#printBanner(org.springframework.core.
	 * env.Environment, java.lang.Class, java.io.PrintStream)
	 */
	@Override
	public void printBanner(Environment environment, Class<?> sourceClass,
			PrintStream out) {
		try {
			ClassPathResource banner = new ClassPathResource("/signature.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					banner.getInputStream()));
			String line = null;
			out.println();
			while ((line = br.readLine()) != null) {
				out.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
