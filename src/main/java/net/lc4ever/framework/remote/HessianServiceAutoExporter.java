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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.caucho.hessian.io.SerializerFactory;

import net.lc4ever.framework.remote.annotation.Remote;

/**
 * @author q-wang
 */
public class HessianServiceAutoExporter implements BeanDefinitionRegistryPostProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected String[] excludes = new String[0];
	
	protected Object[] interceptors;
	
	protected SerializerFactory serializerFactory;
	
	public void setExcludes(String[] excludes) {
		Assert.notNull(excludes, "excludes[] can't be null.");
		Assert.noNullElements(excludes, "excludes[] can't cantains null element(s).");
		this.excludes = excludes;
	}
	
	public void setInterceptors(Object[] interceptors) {
		Assert.notNull(interceptors, "interceptors[] can't be null.");
		Assert.noNullElements(interceptors, "interceptors[] can't cantains null element(s).");
		this.interceptors = interceptors;
	}
	
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		Assert.notNull(serializerFactory, "serializerFactory can't be null.");
		this.serializerFactory = serializerFactory;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
		if (applicationContext==null) return;
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Remote.class);
		for (Map.Entry<String, Object> bean : beans.entrySet()) {
			String beanName = bean.getKey();
			Class<?> beanClass = bean.getValue().getClass();
			Class<?>[] interfaces = beanClass.getInterfaces();
			next:
			for (Class<?> itf : interfaces) {
				for (String exclude: excludes) {
					if (itf.getName().startsWith(exclude)) {
						logger.debug("Ignore interface: [{}] with exclude rule: [{}]", itf, exclude);
						continue next;
					}
				}
				Remote remote = itf.getAnnotation(Remote.class);
				if (remote == null) {
					continue; // without @Remote
				}
				String path = remote.path();
				if (StringUtils.isEmpty(path)) {
					path = remote.value();
				}
				if (StringUtils.isEmpty(path)) {
					Assert.hasText(path, "@Remote path and value can't both null.");
				}
				logger.info("Export: bean=[{}], path=[{}], interface=[{}]", beanName, path, itf);
				GenericBeanDefinition exportor = new GenericBeanDefinition();
//				exportor.setDependsOn(beanName);
				exportor.setScope(BeanDefinition.SCOPE_SINGLETON);
				exportor.setBeanClass(HessianServiceExporter.class);
				exportor.getPropertyValues().add("serviceInterface", itf);
				if (interceptors!=null) {
					exportor.getPropertyValues().add("interceptors", interceptors);
				}
				if (serializerFactory!=null) {
					exportor.getPropertyValues().add("serializerFactory", serializerFactory);
				}
				exportor.getPropertyValues().add("service", new RuntimeBeanReference(beanName, true));

				registry.registerBeanDefinition(path, exportor);
			}
		}
	}

}
