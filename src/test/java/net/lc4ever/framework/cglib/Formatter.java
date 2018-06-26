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
package net.lc4ever.framework.cglib;

/**
 * @author q-wang
 */
public interface Formatter {
//
//	Formatter value(String name, String value);
//	
//	Formatter value(String name, int value);
//	
//	Formatter value(String name, long value);
//	
//	Formatter value(String name, byte value);
//	
//	Formatter value(String name, char value);
//	
//	Formatter value(String name, float value);
//	
//	Formatter value(String name, double value);
//	
//	Formatter value(String name, short value);
//	
//	Formatter value(String name, boolean value);
//	
//	Formatter value(String name, Date value);
//	
//	Formatter value(String name, Calendar value);
//	
//	Formatter value(String name, BigDecimal value);
//	
//	Formatter value(String name, java.sql.Date value);
//	
//	Formatter value(String name, java.sql.Time value);
//	
//	Formatter value(String name, java.sql.Timestamp value);
//	
//	Formatter value(String name, Object value);
//	
//	
//	Formatter value(String name, int[] values);
//	
//	Formatter value(String name, long[] values);
//	
//	Formatter value(String name, byte[] values);
//	
//	Formatter value(String name, char[] values);
//	
//	Formatter value(String name, float[] values);
//	
//	Formatter value(String name, double[] values);
//	
//	Formatter value(String name, short[] values);
//	
//	Formatter value(String name, boolean[] values);
//	
//	Formatter value(String name, String[] values);
//	
//	Formatter value(String name, Object[] values);
//	
//	
//	Formatter value(String name, List<?> values);
//	
//	Formatter value(String name, Set<?> values);
//	
//	Formatter value(String name, Map<?, ?> mapper);
	
	Formatter value(String name, Object value);
	
	Formatter start(StringBuilder builder);
	
	StringBuilder result();
}
