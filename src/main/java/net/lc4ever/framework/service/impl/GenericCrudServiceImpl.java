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
package net.lc4ever.framework.service.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Cache;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate5.HibernateCallback;

import net.lc4ever.framework.dao.GenericDao;
import net.lc4ever.framework.domain.BaseEntity;
import net.lc4ever.framework.service.GenericCrudService;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 *
 */
public class GenericCrudServiceImpl implements GenericCrudService {

	private GenericDao genericDao;

	/**
	 * @param genericDao the genericDao to set
	 */
	public void setGenericDao(final GenericDao genericDao) {
		this.genericDao = genericDao;
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#list(java.lang.Class)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> list(final Class<E> clazz) {
		return genericDao.list(clazz);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#get(java.lang.Class, java.io.Serializable)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E get(final Class<E> clazz, final ID id) {
		return genericDao.get(clazz, id);
	}

	/**
	 * @param entity
	 * @see net.lc4ever.framework.dao.GenericDao#delete(net.lc4ever.framework.domain.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void delete(final E entity) {
		genericDao.delete(entity);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#save(net.lc4ever.framework.domain.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> ID save(final E entity) {
		return genericDao.save(entity);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#update(net.lc4ever.framework.domain.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void update(final E entity) {
		genericDao.update(entity);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#saveOrUpdate(net.lc4ever.framework.domain.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void saveOrUpdate(final E entity) {
		genericDao.saveOrUpdate(entity);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#count(java.lang.Class)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> long count(final Class<E> clazz) {
		return genericDao.count(clazz);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#hql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> hql(final String hql, final Object... args) {
		return genericDao.hql(hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#hql(long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> hql(final long firstResult, final long maxResults, final String hql, final Object... args) {
		return genericDao.hql(firstResult, maxResults, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#hql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> hql(final Class<T> expectType, final String hql, final Object... args) {
		return genericDao.hql(expectType, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#hql(java.lang.Class, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> hql(final Class<T> expectType, final long firstResult, final long maxResults, final String hql,
			final Object... args) {
		return genericDao.hql(expectType, firstResult, maxResults, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#sql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(final String sql, final Object... args) {
		return genericDao.sql(sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#sql(long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(final long firstResult, final long maxResults, final String sql, final Object... args) {
		return genericDao.sql(firstResult, maxResults, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#sql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final String sql, final Object... args) {
		return genericDao.sql(expectType, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#sql(java.lang.Class, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final long firstResult, final long maxResults, final String sql,
			final Object... args) {
		return genericDao.sql(expectType, firstResult, maxResults, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultHql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object uniqueResultHql(final String hql, final Object... args) {
		return genericDao.uniqueResultHql(hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultHql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultHql(final Class<T> expectType, final String hql, final Object... args) {
		return genericDao.uniqueResultHql(expectType, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultSql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object uniqueResultSql(final String sql, final Object... args) {
		return genericDao.uniqueResultSql(sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultSql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultSql(final Class<T> expectType, final String sql, final Object... args) {
		return genericDao.uniqueResultSql(expectType, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultByProperties(java.lang.Class, java.lang.String[], java.lang.Object[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperties(final Class<E> clazz,
			final String[] properties, final Object[] args) {
		return genericDao.uniqueResultByProperties(clazz, properties, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#uniqueResultByProperty(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperty(final Class<E> clazz,
			final String property, final Object arg) {
		return genericDao.uniqueResultByProperty(clazz, property, arg);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#queryByProperties(java.lang.Class, java.lang.String[], java.lang.Object[], org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz,
			final String[] properties, final Object[] args, final Order... orders) {
		return genericDao.queryByProperties(clazz, properties, args, orders);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#queryByProperties(java.lang.Class, long, long, java.lang.String[], java.lang.Object[], org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz,
			final long firstResult, final long maxResults, final String[] properties, final Object[] args,
			final Order... orders) {
		return genericDao.queryByProperties(clazz, firstResult, maxResults, properties, args, orders);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#queryByProperty(java.lang.Class, java.lang.String, java.lang.Object, org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz,
			final String property, final Object arg, final Order... orders) {
		return genericDao.queryByProperty(clazz, property, arg, orders);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#queryByProperty(java.lang.Class, long, long, java.lang.String, java.lang.Object, org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz,
			final long firstResult, final long maxResults, final String property, final Object arg,
			final Order... orders) {
		return genericDao.queryByProperty(clazz, firstResult, maxResults, property, arg, orders);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#criteria(org.hibernate.criterion.DetachedCriteria)
	 */
	@Override
	public List<?> criteria(final DetachedCriteria criteria) {
		return genericDao.criteria(criteria);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#callback(org.springframework.orm.hibernate3.HibernateCallback)
	 */
	@Override
	public <T> T callback(final HibernateCallback<T> callback) {
		return genericDao.callback(callback);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultHql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultHql(final Class<T> clazz, final String hql, final Object... args) {
		return genericDao.topResultHql(clazz, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultHql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object topResultHql(final String hql, final Object... args) {
		return genericDao.topResultHql(hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultSql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object topResultSql(final String sql, final Object... args) {
		return genericDao.topResultSql(sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultSql(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultSql(final Class<T> clazz, final String sql, final Object... args) {
		return genericDao.topResultSql(clazz, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultHql(java.lang.Class, int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultHql(final Class<T> clazz, final int top, final String hql, final Object... args) {
		return genericDao.topResultHql(clazz, top, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#topResultSql(java.lang.Class, int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultSql(final Class<T> clazz, final int top, final String sql, final Object... args) {
		return genericDao.topResultSql(clazz, top, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#iterate(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> Iterator<T> iterate(final Class<T> clazz, final String hql, final Object... args) {
		return genericDao.iterate(clazz, hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#closeIterator(java.util.Iterator)
	 */
	@Override
	public void closeIterator(final Iterator<?> iterator) {
		genericDao.closeIterator(iterator);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#update(java.lang.String, java.lang.Object[])
	 */
	@Override
	public int update(final String hql, final Object... args) {
		return genericDao.bulkUpdateHql(hql, args);
	}

	/**
	 * @see net.lc4ever.framework.dao.GenericDao#sqlUpdate(java.lang.String, java.lang.Object[])
	 */
	@Override
	public int sqlUpdate(final String sql, final Object... args) {
		return genericDao.bulkUpdateSql(sql, args);
	}

	// unimplements methods

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#sql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(final ResultTransformer resultTransformer, final String sql, final Object... args) {
		return genericDao.sql(resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#sql(org.hibernate.transform.ResultTransformer, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(final ResultTransformer resultTransformer, final long firstResult, final long maxResults,
			final String sql, final Object... args) {
		return genericDao.sql(resultTransformer, firstResult, maxResults, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#sql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final ResultTransformer resultTransformer, final String sql,
			final Object... args) {
		return genericDao.sql(expectType, resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#sql(java.lang.Class, org.hibernate.transform.ResultTransformer, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final ResultTransformer resultTransformer, final long firstResult,
			final long maxResults, final String sql, final Object... args) {
		return genericDao.sql(expectType, resultTransformer, firstResult, maxResults, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#uniqueResultSql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object uniqueResultSql(final ResultTransformer resultTransformer, final String sql, final Object... args) {
		return genericDao.uniqueResultSql(resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#uniqueResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultSql(final Class<T> expectType, final ResultTransformer resultTransformer, final String sql,
			final Object... args) {
		return genericDao.uniqueResultSql(expectType, resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#topResultSql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object topResultSql(final ResultTransformer resultTransformer, final String sql, final Object... args) {
		return genericDao.topResultSql(resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#topResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultSql(final Class<T> clazz, final ResultTransformer resultTransformer, final String sql,
			final Object... args) {
		return genericDao.topResultSql(clazz, resultTransformer, sql, args);
	}

	/**
	 * @see net.lc4ever.framework.service.GenericCrudService#topResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultSql(final Class<T> clazz, final ResultTransformer resultTransformer, final int top,
			final String sql, final Object... args) {
		return genericDao.topResultSql(clazz, resultTransformer, top, sql, args);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {
		genericDao.flush();
	}

	@Override
	public <T> List<T> named(Class<T> clazz, String name, Object... args) {
		return genericDao.named(clazz, name, args);
	}

	@Override
	public <T> List<T> named(String name, Object... args) {
		return genericDao.named(name, args);
	}

	@Override
	public <T> List<T> named(Class<T> clazz, long firstResult, long maxResults, String name, Object... args) {
		return genericDao.named(clazz, firstResult, maxResults, name, args);
	}

	@Override
	public <T> List<T> named(long firstResult, long maxResults, String name, Object... args) {
		return genericDao.named(firstResult, maxResults, name, args);
	}

	@Override
	public <T> T namedUniqueResult(Class<T> clazz, String name, Object... args) {
		return genericDao.namedUniqueResult(clazz, name, args);
	}

	@Override
	public <T> T namedUniqueResult(String name, Object... args) {
		return genericDao.namedUniqueResult(name, args);
	}

	@Override
	public <T extends BaseEntity<?>> void evict(T entity) {
		genericDao.evict(entity);
	}

	@Override
	public <T extends BaseEntity<?>> void refresh(T entity) {
		genericDao.refresh(entity);
	}

	@Override
	public LobHelper lobHelper() {
		return genericDao.lobHelper();
	}

	@Override
	public void clear() {
		genericDao.clear();
	}

	@Override
	public Cache getCache() {
		return genericDao.getCache();
	}

	@Override
	public Session getSession() {
		return genericDao.getSession();
	}

	@Override
	public <T> List<T> hql(Class<T> clazz, long firstResult, long maxResults, String hql, Map<String, Object> params) {
		return genericDao.hql(clazz, firstResult, maxResults, hql, params);
	}

	@Override
	public <T> T uniqueResultHql(Class<T> clazz, String hql, Map<String, Object> params) {
		return genericDao.uniqueResultHql(clazz, hql, params);
	}

}
