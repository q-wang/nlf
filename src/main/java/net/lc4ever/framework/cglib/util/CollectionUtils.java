/*
 * Copyright 2003,2004 The Apache Software Foundation
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
package net.lc4ever.framework.cglib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.lc4ever.framework.cglib.core.Predicate;
import net.lc4ever.framework.cglib.core.Transformer;

/**
 * @author Chris Nokleberg
 * @version $Id: CollectionUtils.java,v 1.7 2004/06/24 21:15:21 herbyderby Exp $
 */
public class CollectionUtils {

	private CollectionUtils() {
	}

	public static <S,T> Map<T,List<S>> bucket(Collection<S> c, Transformer<S,T> t) {
		Map<T,List<S>> buckets = new HashMap<T,List<S>>();
		for (Iterator<S> it = c.iterator(); it.hasNext();) {
			S value = it.next();
			T key = t.transform(value);
			List<S> bucket = buckets.get(key);
			if (bucket == null) {
				buckets.put(key, bucket = new LinkedList<S>());
			}
			bucket.add(value);
		}
		return buckets;
	}

	public static <K,V> void reverse(Map<K,V> source, Map<V,K> target) {
		for (Iterator<K> it = source.keySet().iterator(); it.hasNext();) {
			K key = it.next();
			target.put(source.get(key), key);
		}
	}

	public static <T> Collection<T> filter(Collection<T> c, Predicate p) {
		Iterator<T> it = c.iterator();
		while (it.hasNext()) {
			if (!p.evaluate(it.next())) {
				it.remove();
			}
		}
		return c;
	}

	public static <S,T> List<T> transform(Collection<? extends S> c, Transformer<S,T> t) {
		List<T> result = new ArrayList<T>(c.size());
		for (Iterator<? extends S> it = c.iterator(); it.hasNext();) {
			result.add(t.transform(it.next()));
		}
		return result;
	}

	public static <T> Map<T,Integer> getIndexMap(List<T> list) {
		Map<T,Integer> indexes = new HashMap<T,Integer>();
		int index = 0;
		for (Iterator<T> it = list.iterator(); it.hasNext();) {
			indexes.put(it.next(), new Integer(index++));
		}
		return indexes;
	}
}
