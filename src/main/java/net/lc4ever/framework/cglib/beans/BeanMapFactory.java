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

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.lc4ever.framework.cglib.core.AbstractClassGenerator;
import net.lc4ever.framework.cglib.core.ClassKey;
import net.lc4ever.framework.cglib.core.Constants;
import net.lc4ever.framework.cglib.core.HashCodeComarator;
import net.lc4ever.framework.cglib.core.KeyFactory;
import net.lc4ever.framework.cglib.util.CollectionUtils;
import net.lc4ever.framework.cglib.util.ReflectUtils;
import net.lc4ever.framework.cglib.util.TypeUtils;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class BeanMapFactory<T> extends AbstractClassGenerator<BeanMap<T>> implements Opcodes {

	private static final Type BEAN_MAP = TypeUtils.parseType("net.lc4ever.framework.cglib.beans.BeanMap");

	private static final Type FIXED_KEY_SET = TypeUtils.parseType("net.lc4ever.framework.cglib.beans.FixedKeySet");

//	private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
//
//	private static final Signature CSTRUCT_STRING_ARRAY = TypeUtils.parseConstructor("String[]");
//
//	private static final Signature BEAN_MAP_GET = TypeUtils.parseSignature("Object get(Object, Object)");
//
//	private static final Signature BEAN_MAP_PUT = TypeUtils.parseSignature("Object put(Object, Object, Object)");
//
//	private static final Signature KEY_SET = TypeUtils.parseSignature("java.util.Set keySet()");
//
//	private static final Signature NEW_INSTANCE = new Signature("newInstance", BEAN_MAP, new Type[] { Constants.TYPE_OBJECT });
//
//	private static final Signature GET_PROPERTY_TYPE = TypeUtils.parseSignature("Class getPropertyType(String)");

	private static final Source SOURCE = new Source(BeanMap.class.getName());

	private static final BeanMapKey KEY_FACTORY = KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);
	
	public static final Map<Class<?>, Class<?>> MAP_BOX;
	public static final Map<Class<?>, Class<?>> MAP_UNBOX;
	
	static {
		Map<Class<?>, Class<?>> boxMap = new HashMap<Class<?>, Class<?>>();
		boxMap.put(boolean.class, Boolean.class);
		boxMap.put(byte.class, Byte.class);
		boxMap.put(char.class, Character.class);
		boxMap.put(short.class, Short.class);
		boxMap.put(int.class, Integer.class);
		boxMap.put(long.class, Long.class);
		boxMap.put(float.class, Float.class);
		boxMap.put(double.class, Double.class);
		boxMap.put(void.class, Void.class);
		
		MAP_BOX = Collections.unmodifiableMap(boxMap);
		
		Map<Class<?>, Class<?>> unboxMap = new HashMap<Class<?>, Class<?>>();
		CollectionUtils.reverse(boxMap, unboxMap);
		MAP_UNBOX = Collections.unmodifiableMap(unboxMap);
	}

	interface BeanMapKey {

		public ClassKey newInstance(Class<?> type, int require);
	}

	public static <T> BeanMap<T> create(final Class<T> clazz) {
		if (clazz == null) throw new NullPointerException();
		BeanMapFactory<T> factory = new BeanMapFactory<T>(clazz);
		return factory.create();
	}

	private Class<? extends T> beanClass;

	private int options;

	private BeanMapFactory(Class<T> beanClass) {
		super(SOURCE);
		this.beanClass = beanClass;
	}

	private BeanMapFactory(Class<T> beanClass, int options) {
		this(beanClass);
		this.options = options;
	}

	@Override
	protected ClassLoader getDefaultClassLoader() {
		return beanClass.getClassLoader();
	}

	@Override
	protected ProtectionDomain getProtectionDomain() {
		return ReflectUtils.getProtectionDomain(beanClass);
	}

	/**
	 * Create a new instance of the <code>BeanMap</code>. An existing
	 * generated class will be reused if possible.
	 */
	public BeanMap<T> create() {
		if (beanClass == null) throw new IllegalArgumentException("Class of bean unknown");
		setNamePrefix(beanClass.getName());
		return super.create(KEY_FACTORY.newInstance(beanClass, options));
	}

	private Map<String, PropertyDescriptor> makePropertyMap(PropertyDescriptor[] props) {
		Map<String, PropertyDescriptor> names = new HashMap<String, PropertyDescriptor>();
		for (int i = 0; i < props.length; i++) {
			names.put(props[i].getName(), props[i]);
		}
		return names;
	}

	@Override
	public void generateClass(ClassVisitor cv) throws Exception {
		String className = getClassName();
		Type type = Type.getType("L" + className.replace('.', '/') + ";");
		Type beanType = Type.getType(beanClass);

		Map<String, PropertyDescriptor> getters = makePropertyMap(ReflectUtils.getBeanGetters(beanClass));
		Map<String, PropertyDescriptor> setters = makePropertyMap(ReflectUtils.getBeanSetters(beanClass));
		Map<String, PropertyDescriptor> allProps = new HashMap<String, PropertyDescriptor>();
		allProps.putAll(getters);
		allProps.putAll(setters);

		if (options != 0) {
			for (Iterator<String> it = allProps.keySet().iterator(); it.hasNext();) {
				String name = it.next();
				if ((((options & BeanMap.REQUIRE_GETTER) != 0) && !getters.containsKey(name)) || (((options & BeanMap.REQUIRE_SETTER) != 0) && !setters.containsKey(name))) {
					it.remove();
					getters.remove(name);
					setters.remove(name);
				}
			}
		}
		String[] allNames = allProps.keySet().toArray(new String[] {});
		Arrays.sort(allNames, HashCodeComarator.instance());
		
		{ // public class XXXX extends BeanMap<T>
			StringBuilder signature = new StringBuilder('L').append(BEAN_MAP.getInternalName()).append('<').append(beanType.getDescriptor()).append(">;");
			cv.visit(V1_7, ACC_PUBLIC | ACC_SUPER, type.getInternalName(), signature.toString(), BEAN_MAP.getInternalName(), null);
		}
		{ // private static final FixedKeySet keySet;
			FieldVisitor fv = cv.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "keySet", FIXED_KEY_SET.getDescriptor(), null, null);
			fv.visitEnd();
		}
		{ // static { keySet = new FixedKeySet(new String[]{...});
			MethodVisitor mv = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
	
			
			mv.visitCode();
			mv.visitTypeInsn(NEW, FIXED_KEY_SET.getInternalName());
			mv.visitInsn(DUP);
			mv.visitIntInsn(BIPUSH, allProps.size()); // TODO: size < 5
			mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
			for (int i = 0; i < allNames.length; i++) {
				mv.visitInsn(DUP);
				if (i > 5) {
					mv.visitIntInsn(BIPUSH, i);
				} else {
					mv.visitInsn(ICONST_0 + i);
				}
				mv.visitLdcInsn(allNames[i]);
				mv.visitInsn(AASTORE);
			}
			mv.visitMethodInsn(INVOKESPECIAL, FIXED_KEY_SET.getInternalName(), "<init>", "([Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTSTATIC, type.getInternalName(), "keySet", FIXED_KEY_SET.getDescriptor());
			mv.visitInsn(RETURN);
			mv.visitMaxs(6, 0);
			mv.visitEnd();
		}
		{ // private Constructor(Bean)
			StringBuilder desc = new StringBuilder("(").append(beanType.getDescriptor()).append(")V");
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", desc.toString(), null, null);
			mv.visitCode();
			Label label_start = new Label();
			mv.visitLabel(label_start);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, BEAN_MAP.getInternalName(), "<init>", "()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, type.getInternalName(), "bean", Constants.TYPE_OBJECT.getDescriptor());
			mv.visitInsn(RETURN);
			Label label_end = new Label();
			mv.visitLabel(label_end);
			mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
			mv.visitLocalVariable("bean", beanType.getDescriptor(), null, label_start, label_end, options);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{ // public Set<String> keySet();
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "keySet", "()Ljava/util/Set;", "()Ljava/util/Set<Ljava/lang/String;>;", null);
			mv.visitCode();
			mv.visitFieldInsn(GETSTATIC, type.getInternalName(), "keySet", FIXED_KEY_SET.getDescriptor());
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{ // public BeanMap<T> newInstance(T bean)
			StringBuilder desc = new StringBuilder("(").append(beanType.getDescriptor()).append(")").append(BEAN_MAP.getDescriptor());
			StringBuilder signature = new StringBuilder("(").append(beanType.getDescriptor()).append(")L").append(BEAN_MAP.getInternalName()).append("<");
			signature.append(beanType.getDescriptor()).append(">;");
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "newInstance", desc.toString(), signature.toString(), null);
			mv.visitCode();
			Label label_start = new Label();
			mv.visitLabel(label_start);
			mv.visitVarInsn(ALOAD, 1);
			Label label_nonnull = new Label();
			mv.visitJumpInsn(IFNONNULL, label_nonnull);
			mv.visitTypeInsn(NEW, "java/lang/NullPointerException");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "()V", false);
			mv.visitInsn(ATHROW);
			mv.visitLabel(label_nonnull);
			mv.visitFrame(F_SAME, 0, null, 0, null);
			mv.visitTypeInsn(NEW, type.getInternalName());
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", "(" + beanType.getDescriptor() + ")V", false);
			mv.visitInsn(ARETURN);
			Label label_end = new Label();
			mv.visitLabel(label_end);
			mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
			mv.visitLocalVariable("bean", beanType.getDescriptor(), null, label_start, label_end, 1);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
			
			// public bridge synthetic BeanMap newInstance(Object)
			mv = cv.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "newInstance", "(Ljava/lang/Object;)Lnet/lc4ever/framework/cglib/beans/BeanMap;", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, beanType.getInternalName());
			mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "newInstance", desc.toString(), false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
			
		}
		{ // public Class<?> getPropertyType(String)
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "getPropertyType", "(Ljava/lang/String;)Ljava/lang/Class;", "(Ljava/lang/String;)Ljava/lang/Class<*>;", null);
			mv.visitCode();
			Label label_start = new Label();
			mv.visitLabel(label_start);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
			
			
			Map<Class<?>, Label> labelMapper = new HashMap<Class<?>, Label>();
			for (PropertyDescriptor descriptor: allProps.values()) {
				if (!labelMapper.containsKey(descriptor.getPropertyType())) {
					labelMapper.put(descriptor.getPropertyType(), new Label());
				}
			}
			SwitchCase[] allCases = new SwitchCase[allNames.length];
			Label[] caseLabels = new Label[allNames.length];
			int[] hashCodes = new int[allNames.length];
			for (int i=0;i<allNames.length;i++) {
				SwitchCase case1 = new SwitchCase();
				case1.value = allNames[i];
				case1.hashCode = allNames[i].hashCode();
				case1.jump = labelMapper.get(allProps.get(allNames[i]).getPropertyType());
				case1.label = new Label();
				allCases[i] = case1;
				caseLabels[i] = case1.label;
				hashCodes[i] = case1.hashCode;
			}
			Label defaultLabel = new Label();
			mv.visitLookupSwitchInsn(defaultLabel, hashCodes, caseLabels);
			for (int i=0;i<allCases.length;i++) {
				SwitchCase case1 = allCases[i];
				mv.visitLabel(case1.label);
				if (i==0) {
					mv.visitFrame(F_APPEND, 1, new Object[] {"java/lang/String"}, 0, null);
				} else {
					mv.visitFrame(F_SAME, 0, null, 0, null);
				}
				mv.visitVarInsn(ALOAD, 2);
				mv.visitLdcInsn(case1.value);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
				mv.visitJumpInsn(IFNE, case1.jump);
				mv.visitJumpInsn(GOTO, defaultLabel);
			}
			for (Entry<Class<?>, Label> entry: labelMapper.entrySet()) {
				mv.visitLabel(entry.getValue());
				mv.visitFrame(F_SAME, 0, null, 0, null);
//				if (entry.getKey().isPrimitive()) {
//					mv.visitFieldInsn(GETSTATIC, TypeUtils.getBoxedType(Type.getType(entry.getKey())).getInternalName(), "TYPE", "Ljava/lang/Class;");
//				} else {
//					mv.visitLdcInsn(Type.getType(entry.getKey()));
//				}
				loadClass(entry.getKey(), mv);
				mv.visitInsn(ARETURN);
			}
			
			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_CHOP, 1, null, 0, null);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			Label label_end = new Label();
			mv.visitLabel(label_end);
			mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
			mv.visitLocalVariable("name", "Ljava/lang/String;", null, label_start, label_end, 1);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{ // public Object get(T, Object)
			StringBuilder desc = new StringBuilder("(").append(beanType.getDescriptor()).append(Constants.TYPE_OBJECT.getDescriptor()).append(')').append(Constants.TYPE_OBJECT.getDescriptor());
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "get", desc.toString(), null, null);
			mv.visitCode();
			Label label_start = new Label();
			mv.visitLabel(label_start);
		
			String[] getterNames = getters.keySet().toArray(new String[]{});
			Arrays.sort(getterNames, HashCodeComarator.instance());
			int[] hashCodes = new int[getterNames.length];
			Label[] caseLabels = new Label[getterNames.length];
			for (int i=0;i<getterNames.length;i++) {
				hashCodes[i] = getterNames[i].hashCode();
				caseLabels[i] = new Label();
			}
			Label label_default = new Label();
			
			mv.visitVarInsn(ALOAD, 2); // key
			mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
			Label label_validKey = new Label();
			mv.visitJumpInsn(IFNE, label_validKey);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitLabel(label_validKey);
			
			mv.visitFrame(F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, "java/lang/String");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
			mv.visitLookupSwitchInsn(label_default, hashCodes, caseLabels);
			for (int i=0;i<getterNames.length;i++) {
				mv.visitLabel(caseLabels[i]);
				if (i==0) {
					mv.visitFrame(F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
				} else {
					mv.visitFrame(F_SAME, 0, null, 0, null);
				}
				mv.visitVarInsn(ALOAD, 3);
				mv.visitLdcInsn(getterNames[i]);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
				mv.visitJumpInsn(IFEQ, label_default);
				
				mv.visitVarInsn(ALOAD, 1); // T
				PropertyDescriptor getter = getters.get(getterNames[i]);
				Type propertyType = Type.getType(getter.getPropertyType());
				mv.visitMethodInsn(INVOKEVIRTUAL, beanType.getInternalName(), getter.getReadMethod().getName(), "()" + propertyType.getDescriptor(), false);
				box(propertyType, mv);
				
				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(label_default);
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			Label label_end = new Label();
			mv.visitLabel(label_end);
			mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
			mv.visitLocalVariable("bean", beanType.getDescriptor(), null, label_start, label_end, 1);
			mv.visitLocalVariable("key", Constants.TYPE_OBJECT.getDescriptor(), null, label_start, label_end, 2);
			mv.visitMaxs(1, 3);
			mv.visitEnd();
			
			// public bridge synthetic Object get(Object, Object)
			mv = cv.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "get", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, beanType.getInternalName());
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "get", desc.toString(), false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		{ // public Object put(T, String, Object)
			StringBuilder desc = new StringBuilder("(").append(beanType.getDescriptor()).append(Constants.TYPE_STRING.getDescriptor()).append(Constants.TYPE_OBJECT.getDescriptor());
			desc.append(")").append(Constants.TYPE_OBJECT.getDescriptor());
			MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "put", desc.toString(), null, null);
			mv.visitCode();
			Label label_start = new Label();
			mv.visitLabel(label_start);
			
			String[] setterNames = setters.keySet().toArray(new String[]{});
			Arrays.sort(setterNames, HashCodeComarator.INSTANCE);
			int[] hashCodes = new int[setterNames.length];
			for (int i=0;i<setterNames.length;i++) {
				hashCodes[i] = setterNames[i].hashCode();
			}
			
			mv.visitVarInsn(ALOAD, 0); // this [this:type]
			mv.visitFieldInsn(GETFIELD, type.getInternalName(), "converter", Type.getType(Converter.class).getDescriptor()); // [converter]
			Label label_converter_not_null = new Label();
			mv.visitJumpInsn(IFNONNULL, label_converter_not_null);
			// no converter
			{
				Label[] caseLabels = new Label[setterNames.length];
				for (int i=0;i<setterNames.length;i++) {
					caseLabels[i] = new Label();
				}
				Label label_default = new Label();
				
				mv.visitVarInsn(ALOAD, 2); // key [key:string]
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false); // [key.hashCode()]
				mv.visitLookupSwitchInsn(label_default, hashCodes, caseLabels);
				for (int i=0;i<setterNames.length;i++) {
					String setterName = setterNames[i];
					PropertyDescriptor descriptor = setters.get(setterName);
					Type propertyType = Type.getType(descriptor.getPropertyType());
					mv.visitLabel(caseLabels[i]);
					if (i==0) {
						mv.visitFrame(F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
					} else {
						mv.visitFrame(F_SAME, 0, null, 0, null);
					}
					mv.visitVarInsn(ALOAD, 2);
					mv.visitLdcInsn(setterName);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
					mv.visitJumpInsn(IFEQ, label_default);
					mv.visitVarInsn(ALOAD, 1); // bean
					if (descriptor.getReadMethod()!=null) {
						mv.visitInsn(DUP);
						mv.visitMethodInsn(INVOKEVIRTUAL, beanType.getInternalName(), descriptor.getReadMethod().getName(), "()" + propertyType.getDescriptor(), false);
						box(propertyType, mv);
					} else {
						mv.visitInsn(ACONST_NULL);
					}
					
					mv.visitInsn(SWAP);
					
					mv.visitVarInsn(ALOAD, 3);
					mv.visitTypeInsn(CHECKCAST, TypeUtils.getBoxedType(propertyType).getInternalName());
						unbox(propertyType, mv);
					mv.visitMethodInsn(INVOKEVIRTUAL, beanType.getInternalName(), descriptor.getWriteMethod().getName(), "(" + propertyType.getDescriptor() + ")V", false);
					mv.visitInsn(ARETURN);
				}
				mv.visitLabel(label_default);
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ARETURN);
			}
			
			mv.visitLabel(label_converter_not_null);
			mv.visitFrame(F_CHOP, 1, null, 0, null);
			// use converter
			{
				Label[] caseLabels = new Label[setterNames.length];
				for (int i=0;i<setterNames.length;i++) {
					caseLabels[i] = new Label();
				}
				Label label_default = new Label();
				mv.visitVarInsn(ALOAD, 2); // key
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
				mv.visitLookupSwitchInsn(label_default, hashCodes, caseLabels);
				for (int i=0;i<setterNames.length;i++) {
					String setterName = setterNames[i];
					PropertyDescriptor descriptor = setters.get(setterName);
					Type propertyType = Type.getType(descriptor.getPropertyType());
					mv.visitLabel(caseLabels[i]);
					if (i==0) {
						mv.visitFrame(F_FULL, 5, new Object[]{
								type.getInternalName(),
								beanType.getInternalName(),
								"java/lang/String",
								"java/lang/Object",
								TOP
						}, 0, new Object[]{});
					} else {
						mv.visitFrame(F_SAME, 0, null, 0, null);
					}
					mv.visitVarInsn(ALOAD, 2); // key?
					mv.visitLdcInsn(setterName);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
					mv.visitJumpInsn(IFEQ, label_default);
					
					mv.visitVarInsn(ALOAD, 1); // bean
					if (descriptor.getReadMethod()!=null) {
						mv.visitInsn(DUP);
						mv.visitMethodInsn(INVOKEVIRTUAL, beanType.getInternalName(), descriptor.getReadMethod().getName(), "()" + propertyType.getDescriptor(), false);
						box(propertyType, mv);
					} else {
						mv.visitInsn(ACONST_NULL);
					}
					
					mv.visitInsn(SWAP);
					mv.visitVarInsn(ALOAD, 0); // this
					mv.visitFieldInsn(GETFIELD, type.getInternalName(), "converter", Type.getType(Converter.class).getDescriptor());
					mv.visitVarInsn(ALOAD, 3); // value
					loadClass(propertyType, mv); // clazz
					mv.visitVarInsn(ALOAD, 2); // key
					mv.visitVarInsn(ALOAD, 1); // from:bean
					mv.visitInsn(ACONST_NULL); // to:null
					mv.visitMethodInsn(INVOKEINTERFACE, "net/lc4ever/framework/cglib/beans/Converter", "convert", "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					mv.visitTypeInsn(CHECKCAST, TypeUtils.getBoxedType(propertyType).getInternalName());
					unbox(propertyType, mv);
					mv.visitMethodInsn(INVOKEVIRTUAL, beanType.getInternalName(), descriptor.getWriteMethod().getName(), "(" + propertyType.getDescriptor() + ")V", false);
					mv.visitInsn(ARETURN);
				}
				mv.visitLabel(label_default);
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ARETURN);
			}

			Label label_end = new Label();
			mv.visitLabel(label_end);
			mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
			mv.visitLocalVariable("bean", beanType.getDescriptor(), null, label_start, label_end, 1);
			mv.visitLocalVariable("key", Constants.TYPE_STRING.getDescriptor(), null, label_start, label_end, 2);
			mv.visitLocalVariable("value", Constants.TYPE_OBJECT.getDescriptor(), null, label_start, label_end, 3);
			mv.visitMaxs(1, 4);
			mv.visitEnd();
			
			// public bridge synthetic Object put(Object, String, String)
			mv = cv.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "put", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, beanType.getInternalName());
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "put", desc.toString(), false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		
		cv.visitEnd();
		//		begin_class(Constants.V1_2, Constants.ACC_PUBLIC, className, BEAN_MAP, null, Constants.SOURCE_FILE);
		//		EmitUtils.null_constructor(this);
		//		EmitUtils.factory_method(this, NEW_INSTANCE);
		//		generateConstructor();
		//
		//		Map<String, PropertyDescriptor> getters = makePropertyMap(ReflectUtils.getBeanGetters(type));
		//		Map<String, PropertyDescriptor> setters = makePropertyMap(ReflectUtils.getBeanSetters(type));
		//		Map<String, PropertyDescriptor> allProps = new HashMap<String, PropertyDescriptor>();
		//		allProps.putAll(getters);
		//		allProps.putAll(setters);
		//
		//		if (require != 0) {
		//			for (Iterator<String> it = allProps.keySet().iterator(); it.hasNext();) {
		//				String name = it.next();
		//				if ((((require & BeanMap.REQUIRE_GETTER) != 0) && !getters.containsKey(name)) || (((require & BeanMap.REQUIRE_SETTER) != 0) && !setters.containsKey(name))) {
		//					it.remove();
		//					getters.remove(name);
		//					setters.remove(name);
		//				}
		//			}
		//		}
		//		generateGet(type, getters);
		//		generatePut(type, setters);
		//
		//		String[] allNames = getNames(allProps);
		//		generateKeySet(allNames);
		//		generateGetPropertyType(allProps, allNames);
		//		end_class();
		//
		//		new BeanMapEmitter(cv, getClassName(), beanClass, options);
	}

	@Override
	protected BeanMap<T> firstInstance(Class<BeanMap<T>> type) {
		return ReflectUtils.newInstance(type, new Class<?>[]{beanClass}, new Object[]{null});
	}

	@Override
	protected BeanMap<T> nextInstance(BeanMap<T> instance) {
		return null;
	}
	
	private static class SwitchCase {
		public String value;
		public int hashCode;
		public Label jump;
		public Label label;
	}
	
	
	private static void box(Type type, MethodVisitor mv) {
		if (TypeUtils.isPrimitive(type) && (type.getSort() != Type.VOID)) {
			Type boxed = TypeUtils.getBoxedType(type);
			mv.visitMethodInsn(INVOKESTATIC, boxed.getInternalName(), "valueOf", "(" + type.getDescriptor() + ")" + boxed.getDescriptor(), false);
		}
	}

	private static void loadClass(Class<?> clazz, MethodVisitor mv) {
		loadClass(Type.getType(clazz), mv);
//		if (clazz.isPrimitive()) {
//			mv.visitFieldInsn(GETSTATIC, TypeUtils.getBoxedType(Type.getType(clazz)).getInternalName(), "TYPE", "Ljava/lang/Class;");
//		} else {
//			mv.visitLdcInsn(Type.getType(clazz));
//		}
	}
	private static void loadClass(Type type, MethodVisitor mv) {
		if (TypeUtils.isPrimitive(type)) {
			mv.visitFieldInsn(GETSTATIC, TypeUtils.getBoxedType(type).getInternalName(), "TYPE", "Ljava/lang/Class;");
		} else {
			mv.visitLdcInsn(type);
		}
	}

	private static void unbox(Type type, MethodVisitor mv) {
		Type box = TypeUtils.getBoxedType(type);
		switch (type.getSort()) {
			case Type.VOID:
				return;
			case Type.CHAR:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "charValue", "()C", false);
				break;
			case Type.BOOLEAN:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "booleanValue", "()Z", false);
				break;
			case Type.DOUBLE:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "doubleValue", "()D", false);
				break;
			case Type.FLOAT:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "floatValue", "()F", false);
				break;
			case Type.LONG:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "longValue", "()J", false);
				break;
			case Type.INT:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "intValue", "()I", false);
				break;
			case Type.SHORT:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "shortValue", "()S", false);
				break;
			case Type.BYTE:
				mv.visitMethodInsn(INVOKEVIRTUAL, box.getInternalName(), "byteValue", "()B", false);
				break;
			default:
				break;
		}
	}
	
}
