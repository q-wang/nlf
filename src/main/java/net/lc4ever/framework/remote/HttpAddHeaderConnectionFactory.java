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
package net.lc4ever.framework.remote;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianURLConnectionFactory;

/**
 * @author q-wang
 *
 */
public class HttpAddHeaderConnectionFactory extends HessianURLConnectionFactory implements InitializingBean {

	private HttpHeadersProvider headersProvider;

	public void setHeadersProvider(HttpHeadersProvider headersProvider) {
		this.headersProvider = headersProvider;
	}

	private Map<String, String> additionalHeaders;

	public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
		this.additionalHeaders = additionalHeaders;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (additionalHeaders == null) {
			additionalHeaders = Collections.emptyMap();
		}
		if (headersProvider == null) {
			headersProvider = new HttpHeadersProvider() {
				@Override
				public Map<String, String> headers() {
					return Collections.emptyMap();
				}
			};
		}
	}

	@Override
	public HessianConnection open(URL url) throws IOException {
		HessianConnection connection = super.open(url);
		for (Entry<String, String> entry : additionalHeaders.entrySet()) {
			connection.addHeader(entry.getKey(), entry.getValue());
		}
		for (Entry<String, String> entry : headersProvider.headers().entrySet()) {
			connection.addHeader(entry.getKey(), entry.getValue());
		}
		return connection;
	}

}
