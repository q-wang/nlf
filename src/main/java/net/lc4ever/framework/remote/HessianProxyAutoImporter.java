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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

import com.caucho.hessian.client.HessianConnectionFactory;

import net.lc4ever.framework.remote.annotation.Remote;

/**
 * TODO: 在service interface可引用，但mask不允许的情况下，使用NotAllowedRemoteProxy提示调用者.
 * 
 * @author q-wang
 */
public class HessianProxyAutoImporter implements BeanDefinitionRegistryPostProcessor, Ordered {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String[] basePackages;

	protected int mask;

	protected String baseUri;

	protected TypeFilter[] includeFilters;

	protected TypeFilter[] excludeFilters;

	protected boolean mocking;

	protected HessianConnectionFactory hessianConnectionFactory;

	@Required
	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	@Required
	public void setBaseUri(String baseUri) {
		Assert.isTrue(StringUtils.endsWith(baseUri, "/"), "BaseUri can't be empty.");
		this.baseUri = baseUri;
	}

	public void setMocking(boolean mocking) {
		this.mocking = mocking;
	}

	public void setHessianConnectionFactory(HessianConnectionFactory hessianConnectionFactory) {
		this.hessianConnectionFactory = hessianConnectionFactory;
	}
	
	public void setIncludeFilters(TypeFilter[] includeFilters) {
		this.includeFilters = includeFilters;
	}

	public void setExcludeFilters(TypeFilter[] excludeFilters) {
		this.excludeFilters = excludeFilters;
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE + 100;
	}

	/**
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	/**
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Scanner scanner = new Scanner(registry);
		scanner.scan(basePackages);
	}

	protected HessianProxyFactoryBean buildHessianProxyFactoryBean(Class<?> serviceInterface, String serviceName) {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceInterface(serviceInterface);
		factory.setServiceUrl(serviceName);
		return factory;
	}

	private class Scanner extends ClassPathBeanDefinitionScanner {

		public Scanner(BeanDefinitionRegistry registry) {
			super(registry, false);
			addIncludeFilter(new TypeFilter() {

				@Override
				public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
						throws IOException {
					if (!metadataReader.getAnnotationMetadata().hasAnnotation(Remote.class.getName())) {
						return false;
					}
					if (!metadataReader.getClassMetadata().isInterface()) {
						return false;
					}
					return true;
				}
			});

			if (includeFilters != null) {
				for (TypeFilter filter : includeFilters) {
					addIncludeFilter(filter);
				}
			}
			if (excludeFilters != null) {
				for (TypeFilter filter : excludeFilters) {
					addExcludeFilter(filter);
				}
			}

			setBeanNameGenerator(new AnnotationBeanNameGenerator() {

				@Override
				protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
					String beanName = super.determineBeanNameFromAnnotation(annotatedDef);
					if (StringUtils.isBlank(beanName)) {
						Map<String, Object> attributes = annotatedDef.getMetadata()
								.getAnnotationAttributes(Remote.class.getName());
						return attributes == null ? null
								: attributes.get("name") == null ? null : attributes.get("name").toString();
					}
					return beanName;
				}
			});
		}

		@Override
		protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
			HessianProxyAutoImporter.this.logger.info("Scanning Packages: " + Arrays.toString(basePackages));
			Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
			for (BeanDefinitionHolder holder : holders) {
				ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
				Map<String, Object> attributes = definition.getMetadata()
						.getAnnotationAttributes(Remote.class.getName());

				String path = (String) attributes.get("path");
				if (StringUtils.isEmpty(path)) {
					path = (String) attributes.get("value");
				}
				if (path != null)
					path = path.replaceAll("^/+", "");
				Assert.hasText(path, "HessianProxyAutoImporter: @Remote path and value can't both null.");
				String serviceUrl = baseUri + path;
				String serviceInterface = definition.getBeanClassName();

				definition.getPropertyValues().add("serviceUrl", serviceUrl);
				definition.getPropertyValues().add("serviceInterface", serviceInterface);
				if (mocking) {
					definition.setBeanClassName("net.lc4ever.framework.remote.MockitoMockerFactoryBean");
				} else {
					if (hessianConnectionFactory != null) {
						definition.getPropertyValues().add("connectionFactory", hessianConnectionFactory);
					}
					definition.getPropertyValues().add("overloadEnabled", attributes.get("enableOverload"));
					definition.setBeanClass(HessianProxyFactoryBean.class);
				}

				HessianProxyAutoImporter.this.logger.debug("AutoImport: Name: [{}], Interface: [{}], Path: [/{}].",
						holder.getBeanName(), serviceInterface, path);
			}
			return holders;
		}

		@Override
		protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
			Map<String, Object> values = beanDefinition.getMetadata().getAnnotationAttributes(Remote.class.getName());
			return (((Integer) values.get("mask")).intValue() & mask) != 0;
		}
	}
}
