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
package net.lc4ever.framework.remote;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.lc4ever.framework.Cost;

import org.aopalliance.intercept.MethodInvocation;

import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @author q-wang
 */
public class HessianProxyFactoryBean extends org.springframework.remoting.caucho.HessianProxyFactoryBean {
	
	public static final Map<Method, Cost> rpcCosts = new ConcurrentHashMap<>();
	
	private HessianProxyFactory proxyFactory = new HessianProxyFactory();
	
	public HessianProxyFactoryBean() {
		setProxyFactory(proxyFactory);
	}

	@Override
	public void setConnectionFactory(HessianConnectionFactory connectionFactory) {
		super.setConnectionFactory(connectionFactory);
		connectionFactory.setHessianProxyFactory(proxyFactory);
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return super.invoke(invocation);
		} finally {
			long cost = System.currentTimeMillis() - start;
			Cost item = rpcCosts.get(invocation.getMethod());
			if (item==null) {
				item = new Cost();
				rpcCosts.put(invocation.getMethod(), item);
			}
			item.cost(cost);
		}
	}

}
