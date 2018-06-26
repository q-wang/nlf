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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public final class ContextHolder {

	private static final ContextHolder instance = new ContextHolder();

	private ContextHolder() {}

	private ServletContext servletContext;

	private ApplicationContext applicationContext;
	
	private String contextPath;

	protected static ContextHolder getInstance() {
		return instance;
	}

	/**
	 * @param servletContext the servletContext to set
	 */
	protected void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
		this.contextPath = servletContext.getContextPath();
		if (!contextPath.endsWith("/")) {
			this.contextPath = contextPath.concat("/");
		}
	}

	/**
	 * @param applicationContext the applicationContext to set
	 */
	protected void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public static String getContextPath() {
		return instance.contextPath;
	}
	public static ServletContext getServletContext() {
		return instance.servletContext;
	}
	public static ApplicationContext getApplicationContext() {
		return instance.applicationContext;
	}

	public static <T> T getBean(final Class<T> clazz) {
		return instance.applicationContext.getBean(clazz);
	}

	public static <T> T getBean(final String name, final Class<T> clazz) {
		return instance.applicationContext.getBean(name, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(final String name) {
		return (T) instance.applicationContext.getBean(name);
	}
	
	private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<Map<String, Object>>();
	
	static void initThreadContext() {
		THREAD_LOCAL.set(new HashMap<String,Object>());
	}
	
	// require ContextFilter filting
	public static void setThreadAttribute(String name, Object value) {
		Map<String, Object> holder = THREAD_LOCAL.get();
		if (holder==null) {
			throw new IllegalStateException("ThreadContext not initialized, make sure you use ContextFilter?");
		}
		holder.put(name, value);
	}
	
	public static Object getThreadAttribute(String name) {
		return THREAD_LOCAL.get()==null?null:THREAD_LOCAL.get().get(name);
	}
	
	public static final void clearThreadAttributes() {
		if (THREAD_LOCAL.get()!=null) {
			THREAD_LOCAL.get().clear();
		}
		THREAD_LOCAL.remove();
	}

}
