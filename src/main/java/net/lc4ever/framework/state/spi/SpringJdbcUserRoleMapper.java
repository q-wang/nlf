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
package net.lc4ever.framework.state.spi;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.Assert;

/**
 * @author q-wang
 */
public class SpringJdbcUserRoleMapper extends JdbcDaoSupport implements UserRoleMapper {
	
	private String sqlRolesByUser;
	
	private String sqlConstraintByUser;
	
	@Required
	public void setSqlRolesByUser(String sqlRolesByUser) {
		this.sqlRolesByUser = sqlRolesByUser;
	}
	
	public void setSqlConstraintByUser(String sqlConstraintByUser) {
		this.sqlConstraintByUser = sqlConstraintByUser;
	}

	@Override
	protected void initDao() throws Exception {
		super.initDao();
		Assert.hasText(sqlRolesByUser, "sqlRolesByUser can't empty.");
		Assert.hasText(sqlConstraintByUser, "sqlConstraintByUser can't empty.");
	}

	@Override
	public boolean userInRoles(String user, Set<String> roles) {
		return rolesByUser(user).removeAll(roles);
	}

	@Override
	public List<String> rolesByUser(String user) {
		return getJdbcTemplate().queryForList(sqlRolesByUser, String.class, user);
	}

	@Override
	public String constraint(String user) {
		return getJdbcTemplate().queryForObject(sqlConstraintByUser, String.class, user);
	}
}
