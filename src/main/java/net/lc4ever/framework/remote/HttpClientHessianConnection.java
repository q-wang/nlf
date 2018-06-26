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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.caucho.hessian.client.HessianConnection;

/**
 * @author q-wang
 */
public class HttpClientHessianConnection implements HessianConnection {
	
	private PostMethod postMethod;
	
	private HttpClient httpClient;
	
	private ByteArrayOutputStream outputStream;
	
	public HttpClientHessianConnection(PostMethod post) {
		outputStream = new ByteArrayOutputStream();
		HessianRequestEntity entity = new HessianRequestEntity(outputStream);
		post.setRequestEntity(entity);
	}

	@Override
	public void addHeader(String key, String value) {
		postMethod.addRequestHeader(key, value);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	@Override
	public void sendRequest() throws IOException {
		httpClient.executeMethod(postMethod);
	}

	@Override
	public int getStatusCode() {
		return postMethod.getStatusCode();
	}

	@Override
	public String getStatusMessage() {
		return postMethod.getStatusText();
	}

	@Override
	public String getContentEncoding() {
		return postMethod.getResponseHeader("Content-Encoding").getValue();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return postMethod.getResponseBodyAsStream();
	}

	@Override
	public void close() throws IOException {
		outputStream = new ByteArrayOutputStream();
		postMethod.setRequestEntity(new HessianRequestEntity(outputStream));
	}

	@Override
	public void destroy() throws IOException {
		outputStream = new ByteArrayOutputStream();
		postMethod.setRequestEntity(new HessianRequestEntity(outputStream));
	}

}
