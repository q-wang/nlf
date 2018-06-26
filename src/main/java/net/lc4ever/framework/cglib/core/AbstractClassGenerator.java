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
package net.lc4ever.framework.cglib.core;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.lc4ever.framework.cglib.util.ReflectUtils;

import org.objectweb.asm.ClassReader;

/**
 * Abstract class for all code-generating CGLIB utilities.
 * In addition to caching generated classes for performance, it provides hooks for
 * customizing the <code>ClassLoader</code>, name of the generated class, and transformations
 * applied before generation.
 */
abstract public class AbstractClassGenerator<G> implements ClassGenerator {

	private static final ThreadLocal<AbstractClassGenerator<?>> CURRENT = new ThreadLocal<AbstractClassGenerator<?>>();

	private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;

	private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;

	private Source source;

	private ClassLoader classLoader;

	private String namePrefix;

	private Object key;

	private boolean useCache = true;

	private String className;

	private boolean attemptLoad;

	protected static class Source {

		String name;

		Map<ClassLoader, Map<ClassKey, WeakReference<Class<?>>>> cache = new WeakHashMap<ClassLoader, Map<ClassKey,WeakReference<Class<?>>>>();
		Map<ClassLoader, Set<String>> classNameCache = new WeakHashMap<ClassLoader, Set<String>>();

		public Source(String name) {
			this.name = name;
		}
	}

	protected AbstractClassGenerator(Source source) {
		this.source = source;
	}

	protected void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	final protected String getClassName() {
		if (className == null) className = getClassName(getClassLoader());
		return className;
	}

	private String getClassName(final ClassLoader loader) {
		final Set<String> nameCache = getClassNameCache(loader);
		return namingPolicy.getClassName(namePrefix, source.name, key, new Predicate() {

			@Override
			public boolean evaluate(Object arg) {
				return nameCache.contains(arg);
			}
		});
	}

	private Set<String> getClassNameCache(ClassLoader loader) {
		return source.classNameCache.get(loader);
	}

	/**
	 * Set the <code>ClassLoader</code> in which the class will be generated.
	 * Concrete subclasses of <code>AbstractClassGenerator</code> (such as <code>Enhancer</code>)
	 * will try to choose an appropriate default if this is unset.
	 * <p>
	 * Classes are cached per-<code>ClassLoader</code> using a <code>WeakHashMap</code>, to allow
	 * the generated classes to be removed when the associated loader is garbage collected.
	 * @param classLoader the loader to generate the new class with, or null to use the default
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Override the default naming policy.
	 * @see DefaultNamingPolicy
	 * @param namingPolicy the custom policy, or null to use the default
	 */
	public void setNamingPolicy(NamingPolicy namingPolicy) {
		if (namingPolicy == null) namingPolicy = DefaultNamingPolicy.INSTANCE;
		this.namingPolicy = namingPolicy;
	}

	/**
	 * @see #setNamingPolicy
	 */
	public NamingPolicy getNamingPolicy() {
		return namingPolicy;
	}

	/**
	 * Whether use and update the static cache of generated classes
	 * for a class with the same properties. Default is <code>true</code>.
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	/**
	 * @see #setUseCache
	 */
	public boolean getUseCache() {
		return useCache;
	}

	/**
	 * If set, CGLIB will attempt to load classes from the specified
	 * <code>ClassLoader</code> before generating them. Because generated
	 * class names are not guaranteed to be unique, the default is <code>false</code>.
	 */
	public void setAttemptLoad(boolean attemptLoad) {
		this.attemptLoad = attemptLoad;
	}

	public boolean getAttemptLoad() {
		return attemptLoad;
	}

	/**
	 * Set the strategy to use to create the bytecode from this generator.
	 * By default an instance of {@see DefaultGeneratorStrategy} is used.
	 */
	public void setStrategy(GeneratorStrategy strategy) {
		if (strategy == null) strategy = DefaultGeneratorStrategy.INSTANCE;
		this.strategy = strategy;
	}

	/**
	 * @see #setStrategy
	 */
	public GeneratorStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Used internally by CGLIB. Returns the <code>AbstractClassGenerator</code>
	 * that is being used to generate a class in the current thread.
	 */
	public static AbstractClassGenerator<?> getCurrent() {
		return CURRENT.get();
	}

	public ClassLoader getClassLoader() {
		ClassLoader t = classLoader;
		if (t == null) {
			t = getDefaultClassLoader();
		}
		if (t == null) {
			t = getClass().getClassLoader();
		}
		if (t == null) {
			t = Thread.currentThread().getContextClassLoader();
		}
		if (t == null) { throw new IllegalStateException("Cannot determine classloader"); }
		return t;
	}

	abstract protected ClassLoader getDefaultClassLoader();

	/**
	 * Returns the protection domain to use when defining the class.
	 * <p>
	 * Default implementation returns <code>null</code> for using a default protection domain. Sub-classes may
	 * override to use a more specific protection domain.
	 * </p>
	 *
	 * @return the protection domain (<code>null</code> for using a default)
	 */
	protected ProtectionDomain getProtectionDomain() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected G create(ClassKey key) {
		try {
			Class<G> gen = null;

			synchronized (source) {
				ClassLoader loader = getClassLoader();
				ProtectionDomain protectionDomain = getProtectionDomain();
				Map<ClassKey, WeakReference<Class<?>>> cache2 =  source.cache.get(loader);
				if (cache2 == null) {
					cache2 = new HashMap<ClassKey, WeakReference<Class<?>>>();
					
//					Map<Object, Set<String>> nameCache2 = new HashMap<Object, Set<String>>();
//					nameCache2.put(NAME_KEY, new HashSet<String>());
					source.classNameCache.put(loader, new HashSet<String>());
					source.cache.put(loader, cache2);
				} else if (useCache) {
					Reference<?> ref = cache2.get(key);
					gen = (Class<G>) ((ref == null) ? null : ref.get());
				}
				if (gen == null) {
					AbstractClassGenerator<?> save = CURRENT.get();
					CURRENT.set(this);
					try {
						this.key = key;

						if (attemptLoad) {
							try {
								gen = (Class<G>) loader.loadClass(getClassName());
							} catch (ClassNotFoundException e) {
								// ignore
							}
						}
						if (gen == null) {
							byte[] b = strategy.generate(this);
							String className = ClassNameReader.getClassName(new ClassReader(b));
							getClassNameCache(loader).add(className);
							if (protectionDomain == null) {
								gen = (Class<G>) ReflectUtils.defineClass(className, b, loader);
							} else {
								gen = (Class<G>) ReflectUtils.defineClass(className, b, loader, protectionDomain);
							}
						}

						if (useCache) {
							cache2.put(key, new WeakReference<Class<?>>(gen));
						}
						return firstInstance(gen);
					} finally {
						CURRENT.set(save);
					}
				}
			}
			return firstInstance(gen);
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Exception e) {
			throw new CodeGenerationException(e);
		}
	}

	abstract protected G firstInstance(Class<G> type) throws Exception;

	abstract protected G nextInstance(G instance) throws Exception;
}
