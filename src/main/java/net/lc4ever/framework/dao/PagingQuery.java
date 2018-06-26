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
package net.lc4ever.framework.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author q-wang
 */
public class PagingQuery<Q extends Serializable, T extends Serializable> implements Serializable {

	private Q query;

	private long firstResult;

	private int maxResults = 20;

	private List<T> results;

	private long count;

	private Map<String, Object> conditions;
	
	private boolean isPageQuery = true;//默认要进行最大值处理


	public boolean isPageQuery() {
		return isPageQuery;
	}

	public void setPageQuery(boolean isPageQuery) {
		this.isPageQuery = isPageQuery;
	}

	public Q getQuery() {
		return query;
	}

	public long getFirstResult() {
		return firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public PagingQuery<Q, T> setQuery(Q query) {
		this.query = query;
		return this;
	}

	public PagingQuery<Q, T> setFirstResult(long firstResult) {
		this.firstResult = firstResult;
		return this;
	}

	public PagingQuery<Q, T> setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	/** WTF wicket's itemsPerPage type was long */
	public PagingQuery<Q, T> setMaxResults(long maxResults) {
		if (maxResults > Integer.MAX_VALUE || maxResults < 0) {
			throw new IllegalArgumentException();
		}
		this.maxResults = (int) maxResults;
		return this;
	}

	public List<T> getResults() {
		return results;
	}

	public long getCount() {
		return count;
	}

	public PagingQuery<Q, T> setResults(List<T> results) {
		this.results = results;
		return this;
	}

	public PagingQuery<Q, T> setCount(long count) {
		this.count = count;
		return this;
	}

	// Conditions

	public Map<String, Object> getConditions() {
		return conditions;
	}

	public PagingQuery<Q, T> setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
		return this;
	}

	public Object getCondition(String key) {
		return conditions == null ? null : conditions.get(key);
	}

	public PagingQuery<Q, T> addCondition(String key, Object value) {
		if (conditions == null) {
			conditions = new HashMap<>();
		}
		conditions.put(key, value);
		return this;
	}

	public PagingQuery<Q, T> clearCondition() {
		if (conditions != null) {
			conditions.clear();
		}
		return this;
	}

	public PagingQuery<Q, T> removeCondition(String key) {
		if (conditions != null) {
			conditions.remove(key);
		}
		return this;
	}
}
