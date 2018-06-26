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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author q-wang
 */
public class InterceptorChain extends EmptyInterceptor implements InitializingBean {

	private List<Interceptor> interceptors;

	public void setInterceptors(final List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (interceptors == null) {
			interceptors = new ArrayList<Interceptor>(1);
		}
		if (interceptors.isEmpty()) {
			interceptors.add(EmptyInterceptor.INSTANCE);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#onLoad(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onLoad(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onLoad(entity, id, state, propertyNames, types);
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState, final Object[] previousState, final String[] propertyNames, final Type[] types) throws CallbackException {
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onSave(entity, id, state, propertyNames, types);
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.onDelete(entity, id, state, propertyNames, types);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#onCollectionRecreate(java.lang.Object, java.io.Serializable)
	 */
	@Override
	public void onCollectionRecreate(final Object collection, final Serializable key) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionRecreate(collection, key);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#onCollectionRemove(java.lang.Object, java.io.Serializable)
	 */
	@Override
	public void onCollectionRemove(final Object collection, final Serializable key) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionRemove(collection, key);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#onCollectionUpdate(java.lang.Object, java.io.Serializable)
	 */
	@Override
	public void onCollectionUpdate(final Object collection, final Serializable key) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionUpdate(collection, key);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#preFlush(java.util.Iterator)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void preFlush(final Iterator entities) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.preFlush(entities);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#postFlush(java.util.Iterator)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void postFlush(final Iterator entities) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			interceptor.postFlush(entities);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#isTransient(java.lang.Object)
	 */
	@Override
	public Boolean isTransient(final Object entity) {
		Boolean result = null;
		for (Interceptor interceptor : interceptors) {
			if ((result=interceptor.isTransient(entity))!=null) {
				break;
			}
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public int[] findDirty(final Object entity, final Serializable id, final Object[] currentState, final Object[] previousState, final String[] propertyNames, final Type[] types) {
		// TODO not implement yet
		return super.findDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	/**
	 * @see org.hibernate.Interceptor#instantiate(java.lang.String, org.hibernate.EntityMode, java.io.Serializable)
	 */
	@Override
	public Object instantiate(final String entityName, final EntityMode entityMode, final Serializable id) throws CallbackException {
		Object result = null;
		for (Interceptor interceptor : interceptors) {
			if ((result = interceptor.instantiate(entityName, entityMode, id)) != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#getEntityName(java.lang.Object)
	 */
	@Override
	public String getEntityName(final Object object) throws CallbackException {
		String result = null;
		for (Interceptor interceptor : interceptors) {
			if ((result = interceptor.getEntityName(object)) != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#getEntity(java.lang.String, java.io.Serializable)
	 */
	@Override
	public Object getEntity(final String entityName, final Serializable id) throws CallbackException {
		Object result = null;
		for (Interceptor interceptor : interceptors) {
			if ((result = interceptor.getEntity(entityName, id)) != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * @see org.hibernate.Interceptor#afterTransactionBegin(org.hibernate.Transaction)
	 */
	@Override
	public void afterTransactionBegin(final Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionBegin(tx);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#beforeTransactionCompletion(org.hibernate.Transaction)
	 */
	@Override
	public void beforeTransactionCompletion(final Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.beforeTransactionCompletion(tx);
		}
	}

	/**
	 * @see org.hibernate.Interceptor#afterTransactionCompletion(org.hibernate.Transaction)
	 */
	@Override
	public void afterTransactionCompletion(final Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionCompletion(tx);
		}
	}
}
