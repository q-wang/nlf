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
package net.lc4ever.framework.cglib.beans;

import static net.lc4ever.framework.cglib.core.Constants.TYPE_OBJECT;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.lc4ever.framework.cglib.core.AbstractClassGenerator;
import net.lc4ever.framework.cglib.core.ClassKey;
import net.lc4ever.framework.cglib.core.Constants;
import net.lc4ever.framework.cglib.core.KeyFactory;
import net.lc4ever.framework.cglib.util.ReflectUtils;
import net.lc4ever.framework.cglib.util.TypeUtils;
/**
 * @author Chris Nokleberg
 */
abstract public class BeanCopierFactory implements Opcodes {

	private static final BeanCopierKey KEY_FACTORY = KeyFactory.create(BeanCopierKey.class);

	interface BeanCopierKey {
		public ClassKey newInstance(String source, String target);
	}

	public static <S, T> BeanCopier<S, T> create(Class<S> source, Class<T> target) {
		Generator<S, T> gen = new Generator<S, T>();
		gen.setSource(source);
		gen.setTarget(target);
		return gen.create();
	}
	public static <T> BeanCopier<T, T> create(Class<T> type) {
		return create(type, type);
	}
	
	public static class Generator<S, T> extends AbstractClassGenerator<BeanCopier<S, T>> implements Opcodes {

		private static final Source SOURCE = new Source(BeanCopier.class.getName());

		private Class<S> source;

		private Class<T> target;

		public Generator() {
			super(SOURCE);
		}

		public void setSource(Class<S> source) {
			if (!Modifier.isPublic(source.getModifiers())) {
				setNamePrefix(source.getName());
			}
			this.source = source;
		}

		public void setTarget(Class<T> target) {
			// FIXME: override namePrefix -> setSource ?
			if (!Modifier.isPublic(target.getModifiers())) {
				setNamePrefix(target.getName());
			}
			this.target = target;
		}

		@Override
		protected ClassLoader getDefaultClassLoader() {
			return source.getClassLoader();
		}

		@Override
		protected ProtectionDomain getProtectionDomain() {
			return ReflectUtils.getProtectionDomain(source);
		}

		public BeanCopier<S, T> create() {
			ClassKey key = KEY_FACTORY.newInstance(source.getName(), target.getName());
			return super.create(key);
		}

		@Override
		public void generateClass(ClassVisitor cv) {
			// TODO source or target is interface?
			Type sourceType = Type.getType(source);
			Type targetType = Type.getType(target);
			PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);
			PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);
			
			Arrays.sort(setters, new Comparator<PropertyDescriptor>() {
				@Override
				public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			Map<String, PropertyDescriptor> names = new HashMap<String, PropertyDescriptor>();
			for (int i = 0; i < getters.length; i++) {
				names.put(getters[i].getName(), getters[i]);
			}
			
			String className = getClassName();
			Type type = Type.getObjectType(className.replace('.', '/'));
			
			Type converterType = Type.getType(Converter.class);
			Type beanCopierType = Type.getType(BeanCopier.class);
			
			{ // public class ${ClassName} implements BeanCopier<S,T>
				StringBuilder signature = new StringBuilder(TYPE_OBJECT.getDescriptor());
				signature.append('L').append(beanCopierType.getInternalName()).append('<');
				signature.append(sourceType.getDescriptor()).append(targetType.getDescriptor()).append(">;");

				cv.visit(V1_6, ACC_PUBLIC + ACC_SUPER, type.getInternalName(), signature.toString(), TYPE_OBJECT.getInternalName(), new String[] { beanCopierType.getInternalName() });
				cv.visitSource(getClassName().substring(getClassName().lastIndexOf('.')+1)+".java", null);
			}

			{ // default constructor
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, Constants.TYPE_OBJECT.getInternalName(), "<init>", "()V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
//			if (false) 
			{ // void copy(S from, T to, Converter<S,T>)
				StringBuilder desc = new StringBuilder("(");
				desc.append(sourceType.getDescriptor()).append(targetType.getDescriptor());
				desc.append(converterType.getDescriptor());
				desc.append(")V");

				StringBuilder signature = new StringBuilder("(");
				signature.append(sourceType.getDescriptor()).append(targetType.getDescriptor());
				signature.append('L').append(converterType.getInternalName()).append('<');
				signature.append(sourceType.getDescriptor()).append(targetType.getDescriptor());
				signature.append(">;)V");

				// void copy(S from, T to, Converter<S,T>)
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "copy", desc.toString(), signature.toString(), null);
				mv.visitCode();

				Label label_start = new Label();
				mv.visitLabel(label_start);
				
				boolean frame = false;
				for (int i = 0; i < setters.length; i++) {
					PropertyDescriptor setter = setters[i];
					PropertyDescriptor getter = names.get(setter.getName());
					if (getter != null) {
						// String property = "${propertyName}";
						mv.visitLdcInsn(setter.getName());
						mv.visitVarInsn(ASTORE, 4);

						// from.getProperty
						mv.visitVarInsn(ALOAD, 3); // converter
						mv.visitVarInsn(ALOAD, 1); // from
						mv.visitMethodInsn(INVOKEVIRTUAL, sourceType.getInternalName(), getter.getReadMethod().getName(), Type.getType(getter.getReadMethod()).getDescriptor(), false); // value
						// convter.ignore(value, type, propertyName, from, to)
						box(Type.getType(getter.getPropertyType()), mv); // value
						loadClass(setter.getPropertyType(), mv); // type
						mv.visitVarInsn(ALOAD, 4); // propertyName
						mv.visitVarInsn(ALOAD, 1); // from
						mv.visitVarInsn(ALOAD, 2); // to
						mv.visitMethodInsn(INVOKEINTERFACE, converterType.getInternalName(), "ignore", "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Z", true);
						// if (converter.ignore...) {
						Label l0 = new Label();
						mv.visitJumpInsn(IFNE, l0);

						// to.setProperty(converter.convert(from.getProperty(), type, propertyName, from, to)
						mv.visitVarInsn(ALOAD, 2); // to
						mv.visitVarInsn(ALOAD, 3); // converter
						mv.visitVarInsn(ALOAD, 1); // from
						mv.visitMethodInsn(INVOKEVIRTUAL, sourceType.getInternalName(), getter.getReadMethod().getName(), Type.getType(getter.getReadMethod()).getDescriptor(), false); // value
						box(Type.getType(getter.getPropertyType()), mv); // value
						loadClass(setter.getPropertyType(), mv); // type

						mv.visitVarInsn(ALOAD, 4); // propertyName
						mv.visitVarInsn(ALOAD, 1); // from
						mv.visitVarInsn(ALOAD, 2); // to
						mv.visitMethodInsn(INVOKEINTERFACE, converterType.getInternalName(), "convert",
								"(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
						mv.visitTypeInsn(CHECKCAST, TypeUtils.getBoxedType(Type.getType(setter.getPropertyType())).getInternalName());
						if (setter.getPropertyType().isPrimitive()) {
							unbox(Type.getType(setter.getPropertyType()), mv);
						}
						mv.visitMethodInsn(INVOKEVIRTUAL, targetType.getInternalName(), setter.getWriteMethod().getName(), Type.getType(setter.getWriteMethod()).getDescriptor(), false);

						mv.visitLabel(l0); // } end if
						if (frame) {
							mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						} else {
							mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/String" }, 0, null);
							frame = true;
						}
					}
				}
				mv.visitInsn(RETURN);
				
				Label label_end = new Label();
				mv.visitLabel(label_end);
				mv.visitLocalVariable("this", type.getDescriptor(), null, label_start, label_end, 0);
				mv.visitLocalVariable("from", sourceType.getDescriptor(), null, label_start, label_end, 1);
				mv.visitLocalVariable("to", targetType.getDescriptor(), null, label_start, label_end, 2);
				mv.visitLocalVariable("converter", converterType.getDescriptor(), "Lnet/lc4ever/framework/cglib/beans/Converter<L"+ sourceType.getInternalName() + ";L" + targetType.getInternalName() +";>;", label_start, label_end, 3);
//				mv.visitLocalVariable("context", "Ljava/lang/String;", null, label_start, label_end, 4);
				mv.visitMaxs(7, 5);
				mv.visitEnd();
			}
//			if (false)
			{ // void copy(S from, T to)
				StringBuilder desc = new StringBuilder("(");
				desc.append(sourceType.getDescriptor()).append(targetType.getDescriptor());
				desc.append(")V");
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "copy", desc.toString(), null, null);
				mv.visitCode();
				Label label_start = new Label();
				mv.visitLabel(label_start);

				for (int i = 0; i < setters.length; i++) {
					PropertyDescriptor setter = setters[i];
					PropertyDescriptor getter = names.get(setter.getName());
					// TODO box and unbox?
					if (getter != null && compatible(getter, setter)) {
						mv.visitVarInsn(ALOAD, 2);
						mv.visitVarInsn(ALOAD, 1);
						mv.visitMethodInsn(INVOKEVIRTUAL, sourceType.getInternalName(), getter.getReadMethod().getName(), Type.getType(getter.getReadMethod()).getDescriptor(), false);
						mv.visitMethodInsn(INVOKEVIRTUAL, targetType.getInternalName(), setter.getWriteMethod().getName(), Type.getType(setter.getWriteMethod()).getDescriptor(), false);
					}
				}
				mv.visitInsn(RETURN);
				Label label_end = new Label();
				mv.visitLabel(label_end);
				mv.visitLocalVariable("this", "L"+getClassName().replace('.', '/') + ";", null, label_start, label_end, 0);
				mv.visitLocalVariable("from", sourceType.getDescriptor(), null, label_start, label_end, 1);
				mv.visitLocalVariable("to", targetType.getDescriptor(), null, label_start, label_end, 2);
				mv.visitMaxs(2, 3);
				mv.visitEnd();
			}
//			if (false)
			{
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, sourceType.getInternalName());
				mv.visitVarInsn(ALOAD, 2);
				mv.visitTypeInsn(CHECKCAST, targetType.getInternalName());
				StringBuilder desc = new StringBuilder("(");
				desc.append(sourceType.getDescriptor()).append(targetType.getDescriptor());
				desc.append(")V");
				mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "copy", desc.toString(), false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(3, 3);
				mv.visitEnd();
			}
//			if (false)
			{
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "copy", "(Ljava/lang/Object;Ljava/lang/Object;Lnet/lc4ever/framework/cglib/beans/Converter;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0); // this
				mv.visitVarInsn(ALOAD, 1); // from
				mv.visitTypeInsn(CHECKCAST, sourceType.getInternalName());
				mv.visitVarInsn(ALOAD, 2); // to
				mv.visitTypeInsn(CHECKCAST, targetType.getInternalName());
				mv.visitVarInsn(ALOAD, 3); // converter
				StringBuilder desc = new StringBuilder("(");
				desc.append(sourceType.getDescriptor()).append(targetType.getDescriptor()).append(converterType.getDescriptor());
				desc.append(")V");
				mv.visitMethodInsn(INVOKEVIRTUAL, type.getInternalName(), "copy", desc.toString(), false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(4, 4);
				mv.visitEnd();
			}
			cv.visitEnd();
			
		}

		private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
			// TODO: allow automatic widening conversions?
			return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
		}

		@Override
		protected BeanCopier<S, T> firstInstance(Class<BeanCopier<S, T>> type) {
			return ReflectUtils.newInstance(type);
		}

		@Override
		protected BeanCopier<S, T> nextInstance(BeanCopier<S, T> instance) {
			return instance;
		}

		private static void box(Type type, MethodVisitor mv) {
			if (TypeUtils.isPrimitive(type) && (type.getSort() != Type.VOID)) {
				Type boxed = TypeUtils.getBoxedType(type);
				mv.visitMethodInsn(INVOKESTATIC, boxed.getInternalName(), "valueOf", "(" + type.getDescriptor() + ")" + boxed.getDescriptor(), false);
			}
		}

		private static void loadClass(Class<?> clazz, MethodVisitor mv) {
			if (clazz.isPrimitive()) {
				mv.visitFieldInsn(GETSTATIC, TypeUtils.getBoxedType(Type.getType(clazz)).getInternalName(), "TYPE", "Ljava/lang/Class;");
			} else {
				mv.visitLdcInsn(Type.getType(clazz));
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
}
