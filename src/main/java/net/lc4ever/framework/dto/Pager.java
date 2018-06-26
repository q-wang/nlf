/**
 *
 */
package net.lc4ever.framework.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author q-wang
 */
public class Pager<T extends Serializable, Q extends Serializable> implements Serializable {

	private List<T> result;

	private Q query;

	private long count;

	private long offset;

	private int pageSize;

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Q getQuery() {
		return query;
	}

	public void setQuery(Q query) {
		this.query = query;
	}

}
