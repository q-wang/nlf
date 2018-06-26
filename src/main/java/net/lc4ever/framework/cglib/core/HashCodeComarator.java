/*
 * SinoNetFramework: net.lc4ever.framework.cglib.core.HashCodeComarator.java create by q-wang on May 3, 2015 8:16:16 PM
 * VCS Tag: $Id$
 */
package net.lc4ever.framework.cglib.core;

import java.util.Comparator;


/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 *
 */
public class HashCodeComarator<T> implements Comparator<T> {
	
	public static HashCodeComarator<Object> INSTANCE = new HashCodeComarator<Object>();
	
	@SuppressWarnings("unchecked")
	public static <T> HashCodeComarator<T> instance() {
		return (HashCodeComarator<T>) INSTANCE;
	}

	@Override
	public int compare(T o1, T o2) {
		return Integer.compare(o1.hashCode(), o2.hashCode());
	}
}
