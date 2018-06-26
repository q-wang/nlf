/*
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.lc4ever.framework.cglib.core;

public interface Converter<S,T> {

	<C> C convert(Object value, Class<C> targetClass, String property, S from, T to);
	
	boolean ignore(Object value, Class<?> targetClass, String property, S from, T to);
	
	Converter<? extends Object, ? extends Object> DEFAULT = new Adapter<Object, Object>();
	
	public class Adapter<S,T> implements Converter<S, T> {

		@SuppressWarnings("unchecked")
		@Override
		public <C> C convert(Object value, Class<C> target, String context, S from, T to) {
			return (C) value;
		}

		@Override
		public boolean ignore(Object value, Class<?> target, String context, S from, T to) {
			return false;
		}
	}
}
