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
package net.lc4ever.framework.cglib.beans;

import java.security.ProtectionDomain;

import net.lc4ever.framework.cglib.core.AbstractClassGenerator;
import net.lc4ever.framework.cglib.core.ClassKey;
import net.lc4ever.framework.cglib.core.KeyFactory;
import net.lc4ever.framework.cglib.util.ReflectUtils;

import org.objectweb.asm.ClassVisitor;

/**
 * @author Juozas Baliuka
 */
abstract public class BulkBean<T> {

	private static final BulkBeanKey KEY_FACTORY = KeyFactory.create(BulkBeanKey.class);

	interface BulkBeanKey {

		public ClassKey newInstance(String target, String[] getters, String[] setters, String[] types);
	}

	protected Class<T> target;

	protected String[] getters, setters;

	protected Class<?>[] types;

	protected BulkBean() {
	}

	abstract public void getPropertyValues(Object bean, Object[] values);

	abstract public void setPropertyValues(Object bean, Object[] values);

	public Object[] getPropertyValues(Object bean) {
		Object[] values = new Object[getters.length];
		getPropertyValues(bean, values);
		return values;
	}

	public Class<?>[] getPropertyTypes() {
		return types.clone();
	}

	public String[] getGetters() {
		return getters.clone();
	}

	public String[] getSetters() {
		return setters.clone();
	}

	public static <T> BulkBean<T> create(Class<T> target, String[] getters, String[] setters, Class<?>[] types) {
		Generator<T> gen = new Generator<T>();
		gen.setTarget(target);
		gen.setGetters(getters);
		gen.setSetters(setters);
		gen.setTypes(types);
		return gen.create();
	}

	public static class Generator<T> extends AbstractClassGenerator<BulkBean<T>> {

		private static final Source SOURCE = new Source(BulkBean.class.getName());

		private Class<T> target;

		private String[] getters;

		private String[] setters;

		private Class<?>[] types;

		public Generator() {
			super(SOURCE);
		}

		public void setTarget(Class<T> target) {
			this.target = target;
		}

		public void setGetters(String[] getters) {
			this.getters = getters;
		}

		public void setSetters(String[] setters) {
			this.setters = setters;
		}

		public void setTypes(Class<?>[] types) {
			this.types = types;
		}

		@Override
		protected ClassLoader getDefaultClassLoader() {
			return target.getClassLoader();
		}

		@Override
		protected ProtectionDomain getProtectionDomain() {
			return ReflectUtils.getProtectionDomain(target);
		}

		public BulkBean<T> create() {
			setNamePrefix(target.getName());
			String targetClassName = target.getName();
			String[] typeClassNames = ReflectUtils.getNames(types);
			ClassKey key = KEY_FACTORY.newInstance(targetClassName, getters, setters, typeClassNames);
			return super.create(key);
		}

		@Override
		public void generateClass(ClassVisitor v) throws Exception {
			new BulkBeanEmitter(v, getClassName(), target, getters, setters, types);
		}

		@Override
		protected BulkBean<T> firstInstance(Class<BulkBean<T>> type) {
			BulkBean<T> instance = ReflectUtils.newInstance(type);
			instance.target = target;

			int length = getters.length;
			instance.getters = new String[length];
			System.arraycopy(getters, 0, instance.getters, 0, length);

			instance.setters = new String[length];
			System.arraycopy(setters, 0, instance.setters, 0, length);

			instance.types = new Class[types.length];
			System.arraycopy(types, 0, instance.types, 0, types.length);

			return instance;
		}

		@Override
		protected BulkBean<T> nextInstance(BulkBean<T> instance) {
			return instance;
		}
	}
}
