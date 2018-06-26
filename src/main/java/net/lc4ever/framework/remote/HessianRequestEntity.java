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
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * @author q-wang
 *
 */
public class HessianRequestEntity implements RequestEntity {
	
	private ByteArrayOutputStream outputStream;
	
	public HessianRequestEntity(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#isRepeatable()
	 */
	@Override
	public boolean isRepeatable() {
		return false;
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#writeRequest(java.io.OutputStream)
	 */
	@Override
	public void writeRequest(OutputStream out) throws IOException {
		outputStream.writeTo(out);
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentLength()
	 */
	@Override
	public long getContentLength() {
		return outputStream.size();
	}

	/**
	 * @see org.apache.commons.httpclient.methods.RequestEntity#getContentType()
	 */
	@Override
	public String getContentType() {
		return "x-application/hessian";
	}

}
