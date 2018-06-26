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
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate5.HibernateCallback;

import net.lc4ever.framework.dao.GenericDaoStatistics;
import net.lc4ever.framework.domain.BaseEntity;

/**
 * 
 * @revision $Revision:$
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class GenericDAOHibernateStatics extends GenericDaoHibernate {

	private GenericDaoStatistics statistics = new GenericDaoStatistics();

	public GenericDaoStatistics getStatistics() {
		return statistics;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#list(java.lang.Class)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> list(final Class<E> clazz) {
		long start = System.currentTimeMillis();
		List<E> result = super.list(clazz);
		statistics.listCost(clazz, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#get(java.lang.Class,
	 *      java.io.Serializable)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E get(final Class<E> clazz, final ID id) {
		long start = System.currentTimeMillis();
		E result = super.get(clazz, id);
		statistics.getCost(clazz, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#delete(sinonet.framework.bean.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void delete(final E entity) {
		long start = System.currentTimeMillis();
		statistics.deleteCost(entity.getClass(), System.currentTimeMillis() - start);
		super.delete(entity);
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#save(sinonet.framework.bean.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> ID save(final E entity) {
		long start = System.currentTimeMillis();
		ID result = super.save(entity);
		statistics.saveCost(entity.getClass(), System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#update(sinonet.framework.bean.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void update(final E entity) {
		long start = System.currentTimeMillis();
		super.update(entity);
		statistics.updateCost(entity.getClass(), System.currentTimeMillis() - start);
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#saveOrUpdate(sinonet.framework.bean.BaseEntity)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> void saveOrUpdate(final E entity) {
		long start = System.currentTimeMillis();
		statistics.saveOrUpdateCost(entity.getClass(), System.currentTimeMillis() - start);
		super.saveOrUpdate(entity);
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#count(java.lang.Class)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> long count(final Class<E> clazz) {
		long start = System.currentTimeMillis();
		long result = super.count(clazz);
		statistics.countCost(clazz, System.currentTimeMillis() - start);
		return result;
	}

	// /**
	// * @see
	// sinonet.framework.dao.hibernate.GenericDAOHibernate#page(sinonet.framework.dao.Pager)
	// */
	// @Override
	// public <T extends Serializable> Pager<T> page(final Pager<T> pager) {
	// long start = System.currentTimeMillis();
	// Pager<T> result = super.page(pager);
	// statistics.pageCost(pager.toString(), System.currentTimeMillis() -
	// start);
	// return result;
	// }

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#hql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public List<?> hql(final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.hql(hql, args);
		statistics.hqlCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#hql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> hql(final Class<T> expectType, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.hql(expectType, hql, args);
		statistics.hqlClassCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#sql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public List<?> sql(final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.sql(sql, args);
		statistics.sqlCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#sql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.sql(expectType, sql, args);
		statistics.sqlClassCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#uniqueResultHql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public Object uniqueResultHql(final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.uniqueResultHql(hql, args);
		statistics.uniqueResultHqlCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#uniqueResultHql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultHql(final Class<T> expectType, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		T result = super.uniqueResultHql(expectType, hql, args);
		statistics.uniqueResultHqlClassCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#uniqueResultSql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public Object uniqueResultSql(final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.uniqueResultSql(sql, args);
		statistics.uniqueResultSqlCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#uniqueResultSql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultSql(final Class<T> expectType, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		T result = super.uniqueResultSql(expectType, sql, args);
		statistics.uniqueResultSqlClassCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#criteria(org.hibernate.criterion.DetachedCriteria)
	 */
	@Override
	public List<?> criteria(final DetachedCriteria criteria) {
		long start = System.currentTimeMillis();
		List<?> result = super.criteria(criteria);
		statistics.criteriaCost(criteria.getClass(), System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#callback(org.springframework.orm.hibernate3.HibernateCallback)
	 */
	@Override
	public <T> T callback(final HibernateCallback<T> callback) {
		long start = System.currentTimeMillis();
		T result = super.callback(callback);
		statistics.callbackCost(callback.getClass(), System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultHql(java.lang.Class,
	 *      int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultHql(final Class<T> clazz, final int top, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.topResultHql(clazz, top, hql, args);
		statistics.topResultHqlClassCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultSql(java.lang.Class,
	 *      int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultSql(final Class<T> clazz, final int top, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.topResultSql(clazz, top, sql, args);
		statistics.topResultSqlClassCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultHql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultHql(final Class<T> clazz, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		T result = super.topResultHql(clazz, hql, args);
		statistics.topResultHqlClassCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultHql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public Object topResultHql(final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.topResultHql(hql, args);
		statistics.topResultHqlCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultSql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public Object topResultSql(final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.topResultSql(sql, args);
		statistics.topResultSqlCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#topResultSql(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultSql(final Class<T> clazz, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		T result = super.topResultSql(clazz, sql, args);
		statistics.topResultSqlClassCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#iterate(java.lang.Class,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> Iterator<T> iterate(final Class<T> clazz, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		Iterator<T> result = super.iterate(clazz, hql, args);
		statistics.iterateCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#closeIterator(java.util.Iterator)
	 */
	@Override
	public void closeIterator(final Iterator<?> iterator) {
		long start = System.currentTimeMillis();
		statistics.closeIterator(System.currentTimeMillis() - start);
		super.closeIterator(iterator);
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#bulkHqlUpdate(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public int bulkUpdateHql(final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		int result = super.bulkUpdateHql(hql, args);
		statistics.bulkUpdateCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see sinonet.framework.dao.hibernate.GenericDAOHibernate#bulkUpdateSql(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public int bulkUpdateSql(final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		int result = super.bulkUpdateSql(sql, args);
		statistics.bulkUpdateSqlCost(sql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#hql(long,
	 *      long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> hql(final long firstResult, final long maxResults, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.hql(firstResult, maxResults, hql, args);
		statistics.hqlCost(hql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#hql(java.lang.Class,
	 *      long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> hql(final Class<T> expectType, final long firstResult, final long maxResults, final String hql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.hql(expectType, firstResult, maxResults, hql, args);
		statistics.hqlClassCost(hql, System.currentTimeMillis() - start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(long,
	 *      long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(final long firstResult, final long maxResults, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.sql(firstResult, maxResults, sql, args);
		statistics.sqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(java.lang.Class,
	 *      long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(final Class<T> expectType, final long firstResult, final long maxResults, final String sql, final Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.sql(expectType, firstResult, maxResults, sql, args);
		statistics.sqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#uniqueResultByProperties(java.lang.Class,
	 *      java.lang.String[], java.lang.Object[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperties(final Class<E> clazz, final String[] properties, final Object[] args) {
		long start = System.currentTimeMillis();
		E result = super.uniqueResultByProperties(clazz, properties, args);
		statistics.uniqueResultHqlClassCost(clazz.getClass().getName(),System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#uniqueResultByProperty(java.lang.Class,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperty(final Class<E> clazz, final String property, final Object arg) {
		long start = System.currentTimeMillis();
		E result = super.uniqueResultByProperty(clazz, property, arg);
		statistics.uniqueResultHqlClassCost(clazz.getClass().getName(), System.currentTimeMillis()- start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#queryByProperties(java.lang.Class,
	 *      java.lang.String[], java.lang.Object[],
	 *      org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz, final String[] properties, final Object[] args, final Order... orders) {
		long start = System.currentTimeMillis();
		List<E> result = super.queryByProperties(clazz, properties, args, orders);
		statistics.hqlClassCost(clazz.getClass().getName(), System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#queryByProperty(java.lang.Class,
	 *      java.lang.String, java.lang.Object, org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz, final String property, final Object arg, final Order... orders) {
		long start = System.currentTimeMillis();
		List<E> result = super.queryByProperty(clazz, property, arg, orders);
		statistics.hqlClassCost(clazz.getClass().getName(), System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#queryByProperties(java.lang.Class,
	 *      long, long, java.lang.String[], java.lang.Object[],
	 *      org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz, final long firstResult, final long maxResults, final String[] properties, final Object[] args,
			final Order... orders) {
		long start = System.currentTimeMillis();
		List<E> result = super.queryByProperties(clazz, firstResult, maxResults, properties, args, orders);
		statistics.hqlClassCost(clazz.getClass().getName(), System.currentTimeMillis()- start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#queryByProperty(java.lang.Class,
	 *      long, long, java.lang.String, java.lang.Object,
	 *      org.hibernate.criterion.Order[])
	 */
	@Override
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz, final long firstResult, final long maxResults, final String property, final Object arg, final Order... orders) {
		long start = System.currentTimeMillis();
		List<E> result = super.queryByProperty(clazz, firstResult, maxResults, property, arg, orders);
		statistics.hqlClassCost(clazz.getClass().getName(), System.currentTimeMillis()-start);
		return result;
	}


	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.sql(resultTransformer,sql, args);
		statistics.sqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(org.hibernate.transform.ResultTransformer, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<?> sql(ResultTransformer resultTransformer, long firstResult, long maxResults, String sql, Object... args) {
		long start = System.currentTimeMillis();
		List<?> result = super.sql(resultTransformer, firstResult, maxResults, sql, args);
		statistics.sqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(Class<T> expectType, ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.sql(expectType, resultTransformer, sql, args);
		statistics.sqlClassCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#sql(java.lang.Class, org.hibernate.transform.ResultTransformer, long, long, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> sql(Class<T> expectType, ResultTransformer resultTransformer, long firstResult, long maxResults, String sql, Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.sql(expectType, resultTransformer, firstResult, maxResults, sql, args);
		statistics.sqlClassCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#uniqueResultSql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object uniqueResultSql(ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.uniqueResultSql(resultTransformer, sql, args);
		statistics.uniqueResultSqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#uniqueResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqueResultSql(Class<T> expectType, ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		T result = super.uniqueResultSql(expectType, resultTransformer, sql, args);
		statistics.uniqueResultSqlClassCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#topResultSql(org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object topResultSql(ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		Object result = super.topResultSql(resultTransformer, sql, args);
		statistics.topResultSqlCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#topResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T topResultSql(Class<T> clazz, ResultTransformer resultTransformer, String sql, Object... args) {
		long start = System.currentTimeMillis();
		T result = super.topResultSql(clazz, resultTransformer, sql, args);
		statistics.topResultSqlClassCost(sql, System.currentTimeMillis()-start);
		return result;
	}

	/**
	 * @see net.lc4ever.framework.dao.hibernate.GenericDaoHibernate#topResultSql(java.lang.Class, org.hibernate.transform.ResultTransformer, int, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> topResultSql(Class<T> clazz, ResultTransformer resultTransformer, int top, String sql, Object... args) {
		long start = System.currentTimeMillis();
		List<T> result = super.topResultSql(clazz, resultTransformer, top, sql, args);
		statistics.topResultSqlClassCost(sql, System.currentTimeMillis()-start);
		return result;
	}
}
