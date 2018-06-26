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
package net.lc4ever.framework.remote.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import net.lc4ever.framework.Cost;

/**
 * @author q-wang
 */
public class RemoteInvokeInterceptor implements MethodInterceptor {

	public static final Map<Method, Cost> rpcCosts = new ConcurrentHashMap<>();
	
	public static final ThreadLocal<Method> METHOD_LOCAL = new ThreadLocal<>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		METHOD_LOCAL.set(method);
		if (logger.isDebugEnabled()) {
			logger.debug("Incoming RPC: " + ClassUtils.getQualifiedMethodName(method));
		}
		try {
			Cost cost = rpcCosts.get(method);
			if (cost == null) {
				cost = new Cost();
				rpcCosts.put(method, cost);
			}
			long start = System.currentTimeMillis();
			Object retVal = invocation.proceed();
			cost.cost(System.currentTimeMillis() - start);
			if (logger.isDebugEnabled()) {
				logger.debug("Finished RPC: " + ClassUtils.getQualifiedMethodName(method));
			}
			return retVal;
		} catch (Throwable ex) {
			if (ex instanceof RuntimeException || ex instanceof Error) {
				if (logger.isWarnEnabled()) {
					logger.warn("Processing RPC fatal exception: " + ClassUtils.getQualifiedMethodName(method), ex);
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Processing RPC exception: " + ClassUtils.getQualifiedMethodName(method), ex);
				}
			}
			throw ex;
		}
	}

}
