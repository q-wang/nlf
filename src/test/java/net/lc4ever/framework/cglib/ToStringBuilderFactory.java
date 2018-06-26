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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import net.lc4ever.framework.cglib.beans.BeanCopier;
import net.lc4ever.framework.cglib.core.AbstractClassGenerator;
import net.lc4ever.framework.cglib.core.ClassKey;
import net.lc4ever.framework.cglib.core.Constants;
import net.lc4ever.framework.cglib.core.KeyFactory;
import net.lc4ever.framework.cglib.util.ReflectUtils;

/**
 * @author q-wang
 * @formatter:off
 */
public abstract class ToStringBuilderFactory {

	public static <T> ToStringBuilder<T> create(Class<T> clazz) {
		return new Generator<>(clazz).create();
	}

	public static <T> ToStringBuilder<T> create(T object) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) object.getClass();
		return create(clazz);
	}

	public static StringBuilder toString(Object obj, StringBuilder builder, String prefix) {
		return builder;
	}

	private static final ToStringBuilderKey KEY_FACTORY = KeyFactory.create(ToStringBuilderKey.class);

	interface ToStringBuilderKey {
		public ClassKey newInstance(Class<?> source);
	}

	public static class Generator<T> extends AbstractClassGenerator<ToStringBuilder<T>> implements Constants {

		private static final Source SOURCE = new Source(BeanCopier.class.getName());

		private Class<T> clazz;

		protected Generator(Class<T> clazz) {
			super(SOURCE);
			if (!Modifier.isPublic(clazz.getModifiers())) {
				setNamePrefix(clazz.getName());
			}
			this.clazz = clazz;
		}

		public ToStringBuilder<T> create() {
			return create(KEY_FACTORY.newInstance(clazz));
		}

		@Override
		protected ClassLoader getDefaultClassLoader() {
			return clazz.getClassLoader();
		}

		@Override
		protected ToStringBuilder<T> firstInstance(Class<ToStringBuilder<T>> type) throws Exception {
			return ReflectUtils.newInstance(type);
		}

		@Override
		protected ToStringBuilder<T> nextInstance(ToStringBuilder<T> instance) throws Exception {
			return instance;
		}

		@Override
		public void generateClass(ClassVisitor cv) throws Exception {
			PropertyDescriptor[] descriptors = ReflectUtils.getBeanGetters(clazz);

			String className = getClassName();
			Type type = Type.getObjectType(className.replace('.', '/'));
			Type genericType = Type.getType(clazz);

			String typeInternal = type.getInternalName();
			String genericInternal = genericType.getInternalName();

			String genericDesc = genericType.getDescriptor();

			// descriptor: (Lnet/lc4ever/framework/cglib/ToStringBuilderTestBean;)Ljava/lang/String;
			String desc1 = "(" + genericDesc + ")Ljava/lang/String;";
			// descriptor: (Lnet/lc4ever/framework/cglib/ToStringBuilderTestBean;Ljava/lang/StringBuilder;Ljava/lang/String;)Ljava/lang/StringBuilder;
			String desc2 = "(" + genericDesc + "Ljava/lang/StringBuilder;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/StringBuilder;";

			{ // public class ${ClassName} extends java.lang.Object implements net.lc4ever.framework.cglib.ToStringBuilder<net.lc4ever.framework.cglib.ToStringBuilderTestBean>
				// Ljava/lang/Object;Lnet/lc4ever/framework/cglib/ToStringBuilder<Lnet/lc4ever/framework/cglib/ToStringBuilderTestBean;>;
				String signature = "Ljava/lang/Object;Lnet/lc4ever/framework/cglib/ToStringBuilder<" + genericDesc + ">;";

				cv.visit(V1_6, ACC_PUBLIC + ACC_SUPER, typeInternal, signature, "java/lang/Object", new String[] { "net/lc4ever/framework/cglib/ToStringBuilder" });
				cv.visitSource(getClassName().substring(getClassName().lastIndexOf('.')+1)+".java", null);
			}

			{ // default constructor
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}

			{	// public java.lang.String toString(net.lc4ever.framework.cglib.ToStringBuilderTestBean);
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "toString", desc1, null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
				mv.visitMethodInsn(INVOKEVIRTUAL, typeInternal, "toString", desc2, false);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(5, 2);
				mv.visitEnd();
			}
			{	// public java.lang.StringBuilder toString(net.lc4ever.framework.cglib.ToStringBuilderTestBean, java.lang.StringBuilder, java.lang.String);
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "toString", desc2, null, null);
				mv.visitCode();

				// if (builder==null) buidler = new StringBuilder();
				Label builderNotNull = new Label();
				mv.visitVarInsn(ALOAD, 2);
				mv.visitJumpInsn(IFNONNULL, builderNotNull);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
				mv.visitVarInsn(ASTORE, 2);
				mv.visitLabel(builderNotNull);

				// if (prefix !=null) else
				Label prefixIsNull = new Label();
				Label prefixIsNullEnd = new Label();
				mv.visitVarInsn(ALOAD, 1);
				mv.visitJumpInsn(IFNULL, prefixIsNull);
				// prefix != null
				// begin iterate property descriptors
				for (PropertyDescriptor descriptor: descriptors) {
					Method getter = descriptor.getReadMethod();
					descriptor.getName();
					Class<?> propertyType = descriptor.getPropertyType();

					if (propertyType.isPrimitive()) { // no if null detection

					} else if (propertyType.isArray()) {

					} else if (propertyType.isAssignableFrom(Collection.class)){

					} else if (propertyType.isAssignableFrom(ToStringBuilder.class)) {

					} else { // normal object, detected it has toString()?

					}
				}

				mv.visitJumpInsn(GOTO, prefixIsNullEnd);
				mv.visitLabel(prefixIsNull);
				// prefix == null

				mv.visitLabel(prefixIsNullEnd);

				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}

			{
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "toString", "(Ljava/lang/Object;)Ljava/lang/String;", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, genericInternal);
				mv.visitMethodInsn(INVOKEVIRTUAL, typeInternal, "toString", desc1, false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}

			{
				MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "toString", "(Ljava/lang/Object;Ljava/lang/StringBuilder;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/StringBuilder;", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, genericInternal);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitVarInsn(ALOAD, 4);
				mv.visitMethodInsn(INVOKEVIRTUAL, typeInternal, "toString", desc2, false);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(5, 5);
				mv.visitEnd();
			}
		}
	}

}
