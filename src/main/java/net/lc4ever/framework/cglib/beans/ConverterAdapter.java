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
package net.lc4ever.framework.cglib.beans;



/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class ConverterAdapter<S,T> implements Converter<S, T> {
	
	/** NOP Converter, passthrough only */
	public static final Converter<? extends Object, ? extends Object> NOP = new ConverterAdapter<Object, Object>();
	/** Ignore Setter if value is null */
	public static final Converter<? extends Object, ? extends Object> IGNORE_NULL = new IgnoreNull<Object, Object>();
	/** Ignore Setter if value is null or null value is zero or char value is zero, NOTE: not ignore boolean value */
	public static final Converter<? extends Object, ? extends Object> IGNORE_NULL_ZERO = new IgnoreNullZero<Object, Object>();
	/** Ignore Setter if value is null or number value is zero or char value is zero, or false */
	public static final Converter<? extends Object, ? extends Object> IGNORE_NULL_ZERO_FALSE = new IgnoreNullZeroFalse<Object, Object>();
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <C> C convert(Object value, Class<C> target, String context, S from, T to) {
		return (C) value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignore(Object value, Class<?> target, String context, S from, T to) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <S,T> Converter<S, T> nop() {
		return (Converter<S, T>) NOP;
	}
	
	@SuppressWarnings("unchecked")
	public static <S,T> Converter<S, T> ignoreNull() {
		return (Converter<S, T>) IGNORE_NULL;
	}
	
	@SuppressWarnings("unchecked")
	public static <S,T> Converter<S,T> ignoreNullZero() {
		return (Converter<S, T>) IGNORE_NULL_ZERO;
	}
	
	@SuppressWarnings("unchecked")
	public static <S,T> Converter<S, T> ignoreNullZeroFalse() {
		return (Converter<S, T>) IGNORE_NULL_ZERO_FALSE;
	}

	public static class IgnoreNull<S,T> extends ConverterAdapter<S, T> {
		@Override
		public boolean ignore(Object value, Class<?> targetClass, String property, S from, T to) {
			return value==null;
		}
	}
	
	public static class IgnoreNullZero<S,T> extends ConverterAdapter<S, T> {
		@Override
		public boolean ignore(Object value, Class<?> target, String context, S from, T to) {
			if (value == null) {
				return true;
			} else if (target.isPrimitive()) {
				if (target.equals(char.class)) {
					return ((Character) value).charValue() == 0;
				} else if (target.isAssignableFrom(Number.class)) {
					return ((Number) value).longValue() == 0;
				}
			}
			return false;
		}
	}
	
	public static class IgnoreNullZeroFalse<S,T> extends ConverterAdapter<S, T> {
		@Override
		public boolean ignore(Object value, Class<?> target, String context, S from, T to) {
			if (value == null) {
				return true;
			} else if (target.isPrimitive()) {
				if (target.equals(char.class)) {
					return ((Character) value).charValue() == 0;
				} else if (target.isAssignableFrom(Number.class)) {
					return ((Number) value).longValue() == 0;
				} else if (target.equals(boolean.class)) {
					return !((Boolean)value).booleanValue();
				}
			}
			return false;
		}
	}
}
