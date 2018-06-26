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
package net.lc4ever.framework.dao.generic;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 *
 */
public interface HqlBasedAccess {

	public List<?> hql(String hql, Object... args);

	public <T> List<T> hql(Class<T> expectType, String hql, Object... args);

	public Object uniqueResultHql(String hql, Object... args);

	public <T> T uniqueResultHql(Class<T> expectType, String hql, Object... args);

	public <T> T topResultHql(Class<T> clazz, final String hql, final Object... args);

	public Object topResultHql(final String hql, final Object... args);

	public <T> List<T> topResultHql(Class<T> clazz, final int top, final String hql, final Object... args);

	public int bulkUpdateHql(String hql, Object... args);


	public Query namedQuery(String queryName);
	public SQLQuery namedSQLQuery(String queryName);

}
