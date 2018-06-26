/*
 * MIT License
 *
 * Copyright (c) 2008-2017 q-wang, &lt;apeidou@gmail.com&gt;
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.lc4ever.framework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import javax.persistence.Table;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import net.lc4ever.framework.service.SystemSetupService;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	protected static final String FORMAT_KEYVALUE = "\tKEY: [%50s]: [%s]\n";
	protected static final String FORMAT_NUMBER = "\tKEY: [%50s]: [%d]\n";
	protected static final String FORMAT_MEMORY = "\tKEY: [%50s]: [%d] [%dM]\n";
	protected static final String FORMAT_CLASS = "\tKEY: [%50s]: [%s]\n";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected ServletContext servletContext;

	protected ApplicationContext applicationContext;

	/**
	 * @see org.springframework.web.context.ContextLoaderListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00")); // TMP used for WTF weblogic
		servletContext = event.getServletContext();
		printEnvironment();
		ContextHolder.getInstance().setServletContext(event.getServletContext());
		super.contextInitialized(event);
		applicationContext = getCurrentWebApplicationContext();
		ContextHolder.getInstance().setApplicationContext(applicationContext);
		servletContext.setAttribute("contextPath", servletContext.getContextPath());

		runSetupService();
	}

	private void runSetupService() {
		Map<String, SystemSetupService> services = applicationContext.getBeansOfType(SystemSetupService.class);
		List<SystemSetupService> list = new ArrayList<>(services.values());
		AnnotationAwareOrderComparator.sort(list);
		if (!list.isEmpty()) {
			logger.info("Loading SystemSetupService...");
		}
		for (SystemSetupService service : list) {
			logger.info("Running SystemSetupService: {}", service.getClass());
			try {
				service.setup();
				logger.info("SystemSetupService: {} done.", service.getClass());
			} catch (Exception e) {
				logger.error("SystemSetupService Failed for: " + service.getClass(), e);
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else {
					throw new RuntimeException(e);
				}
			}
		}

	}

	private void printEnvironment() {
		if (logger.isDebugEnabled()) {
			Properties properties = System.getProperties();
			Map<String, String> env = System.getenv();

			logger.debug("Beginning of System.getProperties.");
			for (Entry<Object, Object> entry : properties.entrySet()) {
				logger.debug("System.getProperty: property:{}\t\tvalue:{}", entry.getKey(), entry.getValue());
			}
			logger.debug("End of Syste.getProperties.");

			logger.debug("Beginning of Syste.getEnv.");
			for (Entry<String, String> entry : env.entrySet()) {
				logger.debug("System.getEnv: name:{}\t\tvalue:{}", entry.getKey(), entry.getValue());
			}
			logger.debug("End of System.getEnv.");
		}

		System.out.println("Application with Context Path: '" + servletContext.getContextPath() + "'");
		System.out.println("Server Info: '" + servletContext.getServerInfo() + "'");

		System.out.println("System.environment:");
		Map<String, String> env = System.getenv();
		List<String> keys = new ArrayList<>(env.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.printf(FORMAT_KEYVALUE, key, env.get(key));
		}
		System.out.println("System.properties:");
		Properties properties = System.getProperties();
		keys = new ArrayList<>(properties.stringPropertyNames());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.printf(FORMAT_KEYVALUE, key, properties.getProperty(key));
		}

		Runtime runtime = Runtime.getRuntime();
		System.out.println("Runtime:");
		System.out.printf(FORMAT_MEMORY, "Free Memory", runtime.freeMemory(), runtime.freeMemory() / 1048576);
		System.out.printf(FORMAT_MEMORY, "Max Memory", runtime.maxMemory(), runtime.maxMemory() / 1048576);
		System.out.printf(FORMAT_MEMORY, "Total Memory", runtime.totalMemory(), runtime.totalMemory() / 1048576);
		System.out.printf(FORMAT_NUMBER, "Total CPU", runtime.availableProcessors());

		System.out.println("XML Factory:");
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		System.out.printf(FORMAT_CLASS, "javax.xml.stream.XMLEventFactory", eventFactory.getClass());
		System.out.printf(FORMAT_CLASS, "javax.xml.parsers.DocumentBuilderFactory", builderFactory.getClass());

		// detected class
		System.out.printf(FORMAT_CLASS, "JPA: javax.persistence.Table", Table.class.getClassLoader().getResource("javax/persistence/Table.class"));
	}

	/**
	 * @see org.springframework.web.context.ContextLoaderListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		this.servletContext = null;
		super.contextDestroyed(event);
	}

}
