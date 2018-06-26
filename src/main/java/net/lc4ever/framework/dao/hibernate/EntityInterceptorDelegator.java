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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * @author q-wang
 */
public class EntityInterceptorDelegator implements Interceptor {

	private EntityInterceptor defaultInterceptor = new EmptyEntityInterceptor();

	private Map<Class<?>, EntityInterceptor> entityInterceptors = Collections.emptyMap();

	//	private Map<Class<?>, Class<? extends EntityInterceptor>> entityInterceptorClasses = Collections.emptyMap();

	protected EntityInterceptor findInterceptor(final Object entity) {
		EntityInterceptor interceptor;
		if ((interceptor=entityInterceptors.get(entity.getClass()))==null) {
			return defaultInterceptor;
		}
		return interceptor;
	}

	@Override
	public boolean onLoad(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		return findInterceptor(entity).onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState, final Object[] previousState, final String[] propertyNames, final Type[] types) throws CallbackException {
		return findInterceptor(entity).onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	@Override
	public boolean onSave(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		return findInterceptor(entity).onSave(entity, id, state, propertyNames, types);
	}

	@Override
	public void onDelete(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) throws CallbackException {
		findInterceptor(entity).onDelete(entity, id, state, propertyNames, types);
	}

	@Override
	public Boolean isTransient(final Object entity) {
		return findInterceptor(entity).isTransient(entity);
	}

	@Override
	public int[] findDirty(final Object entity, final Serializable id, final Object[] currentState, final Object[] previousState, final String[] propertyNames, final Type[] types) {
		return findInterceptor(entity).findDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	@Override
	public Object instantiate(final String entityName, final EntityMode entityMode, final Serializable id) throws CallbackException {
		return null;
	}

	@Override
	public String getEntityName(final Object object) throws CallbackException {
		return null;
	}

	@Override
	public Object getEntity(final String entityName, final Serializable id) throws CallbackException {
		return null;
	}

	@Override
	public void afterTransactionBegin(final Transaction tx) {
	}

	@Override
	public void beforeTransactionCompletion(final Transaction tx) {
	}

	@Override
	public void afterTransactionCompletion(final Transaction tx) {
	}

	@Override
	public String onPrepareStatement(final String sql) {
		return sql;
	}

	@Override
	public void onCollectionRecreate(final Object collection, final Serializable key) throws CallbackException {
	}

	@Override
	public void onCollectionRemove(final Object collection, final Serializable key) throws CallbackException {
	}

	@Override
	public void onCollectionUpdate(final Object collection, final Serializable key) throws CallbackException {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void preFlush(final Iterator entities) throws CallbackException {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void postFlush(final Iterator entities) throws CallbackException {
	}
}
