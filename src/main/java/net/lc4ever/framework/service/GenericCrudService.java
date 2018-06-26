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
package net.lc4ever.framework.service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Cache;
import org.hibernate.LobHelper;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.transform.ResultTransformer;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.lc4ever.framework.domain.BaseEntity;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public interface GenericCrudService {

	/**
	 * 列表操作.
	 * 
	 * eg: select * from TABLE
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param clazz EntityBean 类型
	 * @return select结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> list(final Class<E> clazz);

	/**
	 * 按ID取数据库条目.
	 * 
	 * eg: select * from TABLE where ID = :id
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param clazz EntityBean 类型
	 * @param id 数据库条目主键值
	 * @return 数据库条目
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> E get(final Class<E> clazz, final ID id);

	/**
	 * 删除数据库条目.
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param entity 要删除的数据库条目,注意,如果仅知道条目id,请先做get操作,然后delete该结果.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public <E extends BaseEntity<ID>, ID extends Serializable> void delete(final E entity);

	/**
	 * 保存数据库条目.
	 * 
	 * eg: insert into TABLE (col1,col2,...) values (val1,val2,...)
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param entity 数据库条目
	 * @return 被保存的数据库条目的ID值,在id为自动生成值时,可以由此得到id值,也可以entity.getId
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public <E extends BaseEntity<ID>, ID extends Serializable> ID save(final E entity);

	/**
	 * 更新数据库条目.
	 * 
	 * eg: update TABLE set (col1=val1,col2=val2....) where ID = :entity.id
	 * 注意此更新操作为按条目主键更新.
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param entity 要更新的条目
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public <E extends BaseEntity<ID>, ID extends Serializable> void update(final E entity);

	/**
	 * 保存或更新数据库条目.
	 * 
	 * 如果数据库存在此记录id的条目,则进行update操作,否则进行insert.
	 * 
	 * @see #save(BaseEntity)
	 * @see #update(BaseEntity)
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param entity 要保存或更新的条目
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public <E extends BaseEntity<ID>, ID extends Serializable> void saveOrUpdate(final E entity);

	/**
	 * 统计数据库表记录数.
	 * 
	 * eg: select count(*) from TABLE
	 * 
	 * @param <E> EntityBean类型泛参
	 * @param <ID> EntityBean ID类型泛参
	 * @param clazz EntityBean 类型
	 * @return 条目数量.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> long count(final Class<E> clazz);

	/**
	 * TODO 详细补齐此操作的DOC文档
	 * TODO 修改此分页操作,hqlQueryBuilder,criteriaBuilder,detachCriteriaBuilder
	 * 
	 * @param <T>
	 * @param pager
	 * @return
	 */
	/*
	 * @Transactional(propagation = Propagation.SUPPORTS)
	 * public <T extends Serializable> Pager<T> page(Pager<T> pager);
	 */

	/**
	 * 数据库HQL查询.
	 * 
	 * eg: hql: from ENTITY where property1 = ? and property2 = ?
	 * args: property1 value,property2 value
	 * 
	 * @param hql HQL查询语句
	 * @param args HQL查询参数
	 * @return 查询结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> hql(final String hql, final Object... args);

	/**
	 * 数据库HQL查询.
	 * 
	 * eg: hql: from ENTITY where property1 = ? and property2 = ?
	 * args: property1 value,property2 value
	 * 
	 * @param firstResult 起始条目数
	 * @param maxResults 最大结果数
	 * @param hql HQL查询语句
	 * @param args HQL查询参数
	 * @return 查询结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> hql(final long firstResult, final long maxResults, final String hql, final Object... args);

	/**
	 * 数据库HQL查询.
	 * 
	 * @param <T> 返回结果类型泛参
	 * @param expectType 期望返回结果集类型
	 * @see #hql(String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> hql(final Class<T> expectType, final String hql, final Object... args);

	/**
	 * 
	 * @param <T> 返回结果类型泛参
	 * @param expectType 期望返回结果集类型
	 * @param firstResult 起始条目数
	 * @param maxResults 最大结果集
	 * @param hql hql查询语句
	 * @param args 参数列表
	 * @return 结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> hql(final Class<T> expectType, final long firstResult, final long maxResults, final String hql,
			final Object... args);

	/**
	 * 数据库原生SQL查询.
	 * 
	 * eg: SQL:select * from TABLE where col1 = ? and col2 = ?
	 * args: col1 value, col2 value
	 * 
	 * @param sql SQL语句
	 * @param args SQL查询参数
	 * @return 查询结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> sql(final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #sql(String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> sql(final ResultTransformer resultTransformer, final String sql, final Object... args);

	/**
	 * 
	 * @param firstResult 起始条目数
	 * @param maxResults 最大结果集
	 * @param sql sql查询语句
	 * @param args 参数列表
	 * @return 查询结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> sql(final long firstResult, final long maxResults, final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #sql(long, long, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> sql(final ResultTransformer resultTransformer, final long firstResult, final long maxResults,
			final String sql, final Object... args);

	/**
	 * 数据库原生SQL查询.
	 * 
	 * @param <T> 返回结果类型泛参
	 * @param expectType 期望返回结果集类型
	 * @see #sql(String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> sql(final Class<T> expectType, final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #sql(Class, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> sql(final Class<T> expectType, final ResultTransformer resultTransformer, final String sql,
			final Object... args);

	/**
	 * 数据库原生SQL查询.
	 * 
	 * @param <T> 返回结果类型泛参
	 * @param expectType 期望返回结果集类型
	 * @param firstResult 起始条目数
	 * @param maxResults 最大结果数
	 * @param sql sql查询语句
	 * @param args 参数列表
	 * @return 结果集
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> sql(final Class<T> expectType, final long firstResult, final long maxResults, final String sql,
			final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #sql(Class, long, long, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> sql(final Class<T> expectType, final ResultTransformer resultTransformer, final long firstResult,
			final long maxResults, final String sql, final Object... args);

	/**
	 * 单一结果HQL查询.
	 * 
	 * @return 结果
	 * @see #hql(String, Object...)
	 * @throws org.hibernate.NonUniqueResultException
	 *             返回结果数量不是1(expect 1)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object uniqueResultHql(final String hql, final Object... args);

	/**
	 * 单一结果HQL查询.
	 * 
	 * @return 结果
	 * @see #hql(Class, String, Object...)
	 * @throws org.hibernate.NonUniqueResultException
	 *             返回结果数量不是1(expect 1)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T uniqueResultHql(final Class<T> expectType, final String hql, final Object... args);

	/**
	 * 单一结果SQL查询.
	 * 
	 * @return 结果
	 * @see #sql(String, Object...)
	 * @throws org.hibernate.NonUniqueResultException
	 *             返回结果数量不是1(expect 1)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object uniqueResultSql(final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #uniqueResultSql(String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object uniqueResultSql(ResultTransformer resultTransformer, String sql, Object... args);

	/**
	 * 单一结果SQL查询.
	 * 
	 * @return 结果
	 * @see #sql(Class, String, Object...)
	 * @throws org.hibernate.NonUniqueResultException
	 *             返回结果数量不是1(expect 1)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T uniqueResultSql(final Class<T> expectType, final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #uniqueResultSql(Class, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T uniqueResultSql(Class<T> expectType, ResultTransformer resultTransformer, String sql, Object... args);

	/**
	 * DetachedCriteria查询.
	 * 
	 * @see DetachedCriteria
	 * @param criteria
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<?> criteria(final DetachedCriteria criteria);

	/**
	 * Callback方式调用Hibernate,提供Hibernate session.
	 * 
	 * @param <T> 返回结果类型
	 * @param callback HibernateCallback实现
	 * @return callback返回结果
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public <T> T callback(final HibernateCallback<T> callback);

	/**
	 * 根据Properties对应Value取得唯一结果.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param properties 属性列表
	 * @param args 参数列表
	 * @return Entity, or null if no result, throws
	 *         {@link NonUniqueResultException}
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperties(final Class<E> clazz,
			final String[] properties, final Object[] args);

	/**
	 * 根据Property对应Value取得唯一结果.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param property 属性
	 * @param arg 参数
	 * @return Entity, or null if no result, throws
	 *         {@link NonUniqueResultException}
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> E uniqueResultByProperty(final Class<E> clazz,
			final String property, final Object arg);

	/**
	 * 根据Properties对应Value取得结果列表.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param properties 属性列表
	 * @param args 参数列表
	 * @return List&lt;Entity&gt;, NOTE: empty list if no result.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz,
			final String[] properties, final Object[] args, final Order... orders);

	/**
	 * 根据Properties对应Value取得结果列表.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param firstResult 起始条目数
	 * @param maxResults 最大结果数
	 * @param properties 属性列表
	 * @param args 参数列表
	 * @return List&lt;Entity&gt;, NOTE: empty list if no result.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperties(final Class<E> clazz,
			final long firstResult, final long maxResults, final String[] properties, final Object[] args,
			final Order... orders);

	/**
	 * 根据Property对应Value取得结果列表.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param property 属性
	 * @param arg 参数
	 * @param orders 排序
	 * @return List&lt;Entity&gt;, NOTE: empty list if no result.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz,
			final String property, final Object arg, final Order... orders);

	/**
	 * 根据Property对应Value取得结果列表.
	 * 
	 * 如果Value不为null,使用=操作;
	 * 否则使用is null操作.
	 * 
	 * @param clazz 实体类型(Domain Class)
	 * @param firstResult 起始条目数
	 * @param maxResults 最大返回结果数
	 * @param property 属性
	 * @param arg 参数
	 * @param orders 排序
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <E extends BaseEntity<ID>, ID extends Serializable> List<E> queryByProperty(final Class<E> clazz,
			final long firstResult, final long maxResults, final String property, final Object arg,
			final Order... orders);

	/**
	 * Only First Result Version of {@link #hql(Class, String, Object...)}.
	 * 
	 * @param clazz 期望返回类型
	 * @param hql hql查询语句
	 * @param args 参数列表
	 * @return Entity if result not emtpy, or null.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T topResultHql(final Class<T> clazz, final String hql, final Object... args);

	/**
	 * Only First Result Version of {@link #hql(String, Object...)}.
	 * 
	 * @param hql hql查询语句
	 * @param args 参数列表
	 * @return Entity if result not emtpy, or null.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object topResultHql(final String hql, final Object... args);

	/**
	 * Only First Result Version of {@link #sql(String, Object...)}.
	 * 
	 * @param sql sql查询语句
	 * @param args 参数列表
	 * @return Entity if result not emtpy, or null.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object topResultSql(final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #topResultSql(String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Object topResultSql(ResultTransformer resultTransformer, String sql, Object... args);

	/**
	 * Only First Result Version of {@link #sql(Class, String, Object...)}.
	 * 
	 * @param clazz 期望返回类型
	 * @param sql sql查询语句
	 * @param args 参数列表
	 * @return Entity if result not emtpy, or null.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T topResultSql(final Class<T> clazz, final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #topResultSql(Class, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> T topResultSql(Class<T> clazz, ResultTransformer resultTransformer, String sql, Object... args);

	/**
	 * Max ResultSize Version of {@link #hql(Class, String, Object...)}.
	 * 
	 * @param clazz 期望返回类型
	 * @param top 最大结果数
	 * @param hql hql查询语句
	 * @param args 参数列表
	 * @return List&lt;Entity&gt;, NOTE: empty list if no result.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> topResultHql(final Class<T> clazz, final int top, final String hql, final Object... args);

	/**
	 * Max ResultSize Version of {@link #sql(Class, String, Object...)}.
	 * 
	 * @param clazz 期望返回类型
	 * @param top 最大结果数
	 * @param sql sql查询语句
	 * @param args 参数列表
	 * @return List&lt;Entity&gt;, NOTE: empty list if no result.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> topResultSql(final Class<T> clazz, final int top, final String sql, final Object... args);

	/**
	 * @param resultTransformer {@link ResultTransformer}
	 * @see #topResultHql(Class, int, String, Object...)
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> List<T> topResultSql(Class<T> clazz, ResultTransformer resultTransformer, int top, String sql,
			Object... args);

	/**
	 * Iterator version of {@link #hql(Class, String, Object...)}.
	 * 
	 * WARN: finally use {@link #closeIterator(Iterator)} close this iterator.
	 * 
	 * @param clazz 期望返回列行.
	 * @param hql hql查询语句.
	 * @param args 参数列表
	 * @return Itegrator of results.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public <T> Iterator<T> iterate(final Class<T> clazz, final String hql, final Object... args);

	/**
	 * 关闭Iterator.
	 * 
	 * @see #iterate(Class, String, Object...)
	 * @param iterator 要关闭的iterator
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public void closeIterator(final Iterator<?> iterator);

	/**
	 * HQL大数据量更新操作.
	 * 
	 * Support delete,update
	 * @param hql hql查询语句.
	 * @param args 参数列表.
	 * @return 更新条数?
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int update(final String hql, final Object... args);

	/**
	 * SQL大数据量更新操作.
	 * 
	 * Support delete,update
	 * @param sql sql查询语句.
	 * @param args 参数列表.
	 * @return 更新条数?
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int sqlUpdate(final String sql, final Object... args);

	/**
	 * Flush current session.
	 */
	public void flush();

	/**
	 * @see Session#clear()
	 */
	public void clear();

	/**
	 * @see SessionFactory#getCache()
	 */
	public Cache getCache();

	public Session getSession();

	public LobHelper lobHelper();

	public <T> List<T> named(Class<T> clazz, String name, Object... args);

	public <T> List<T> named(String name, Object... args);

	public <T> List<T> named(Class<T> clazz, long firstResult, long maxResults, String name, Object... args);

	public <T> List<T> named(long firstResult, long maxResults, String name, Object... args);

	public <T> T namedUniqueResult(Class<T> clazz, String name, Object... args);

	public <T> T namedUniqueResult(String name, Object... args);

	public <T extends BaseEntity<?>> void evict(T entity);

	public <T extends BaseEntity<?>> void refresh(T entity);
	//	public <T> List<T> query(DetachedCriteria criteria, Class<T> resultType);
	//	
	//	public long count(DetachedCriteria criteria);

	public <T> List<T> hql(Class<T> clazz, long firstResult, long maxResults, String hql, Map<String, Object> params);

	public <T> T uniqueResultHql(Class<T> clazz, String hql, Map<String, Object> params);

}
