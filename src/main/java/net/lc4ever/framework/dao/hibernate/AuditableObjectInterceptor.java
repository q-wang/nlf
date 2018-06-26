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
package net.lc4ever.framework.dao.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import net.lc4ever.framework.domain.AuditableObject;
import net.lc4ever.framework.domain.TimestampObject;
import net.lc4ever.framework.state.spi.CurrentUserProvider;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class AuditableObjectInterceptor extends EmptyInterceptor implements InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected CurrentUserProvider userProvider;

	@Autowired(required = false)
	public void setUserProvider(CurrentUserProvider userProvider) {
		this.userProvider = userProvider;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//		try {
		//			if (ContextHolder.getApplicationContext()==null) {
		//				return;
		//			}
		//			userProvider = ContextHolder.getBean(CurrentUserProvider.class);
		//		} catch (NoSuchBeanDefinitionException e) {
		//			throw e;
		//		}
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState,
			final Object[] previousState, final String[] propertyNames, final Type[] types) {
		if (entity instanceof TimestampObject<?>) {
			logger.trace("onFlushDirty set modifyTimestamp for entity: {}, id: {}", entity.getClass(), id);
			Date now = new Date();
			for (int i = 0; i < propertyNames.length; i++) {
				String property = propertyNames[i];
				if (property.equals("modifyTimestamp")) {
					currentState[i] = now;
					break;
				}
			}
			if (entity instanceof AuditableObject<?>) {
				for (int i = 0; i < propertyNames.length; i++) {
					String property = propertyNames[i];
					if (property.equals("modifiersId")) {
						currentState[i] = currentUser();
					}
				}
			}
			return true;
		}
		return false;
	}

	protected String currentUser() {
		return userProvider == null ? "-1" : userProvider.userId() == null ? "-1" : userProvider.userId();
	}

	/**
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id, final Object[] state,
			final String[] propertyNames, final Type[] types) {
		if (entity instanceof TimestampObject<?>) {
			logger.trace("onSave set createTimestamp for entity: {}, id: {}", entity.getClass(), id);
			Date now = new Date();
			for (int i = 0; i < propertyNames.length; i++) {
				String property = propertyNames[i];
				if (property.equals("createTimestamp")) {
					state[i] = now;
				} else if (property.equals("modifyTimestamp")) {
					state[i] = now;
				}
			}
			if (entity instanceof AuditableObject<?>) {
				for (int i = 0; i < propertyNames.length; i++) {
					String property = propertyNames[i];
					if (property.equals("creatorsId")) {
						state[i] = currentUser();
					} else if (property.equals("modifiersId")) {
						state[i] = currentUser();
					}
				}
			}
			return true;
		}
		return false;
	}

}
