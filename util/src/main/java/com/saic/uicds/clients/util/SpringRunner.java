package com.saic.uicds.clients.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringRunner {

	public SpringClient getSpringClient() {

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "contexts/util-context.xml" });
		SpringClient springClient = (SpringClient) context.getBean("springClient");
		if (springClient == null) {
			System.err.println("Could not instantiate springClient");
		}
		return springClient;
	}
}
