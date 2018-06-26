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
package net.lc4ever.framework.state;

import java.util.List;

import org.springframework.context.annotation.Profile;

import net.lc4ever.framework.state.spi.CurrentUserProvider;

/**
 * @author q-wang
 */
@Profile("test")
public class MockingCurrentUserProvier implements CurrentUserProvider {

	private String constraint;

	private String userId;

	private List<String> roles;

	/**
	 * @see net.lc4ever.framework.state.spi.CurrentUserProvider#constraint()
	 */
	@Override
	public String constraint() {
		return constraint;
	}

	/**
	 * @see net.lc4ever.framework.state.spi.CurrentUserProvider#userId()
	 */
	@Override
	public String userId() {
		return userId;
	}

	/**
	 * @see net.lc4ever.framework.state.spi.CurrentUserProvider#roles()
	 */
	@Override
	public List<String> roles() {
		return roles;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
