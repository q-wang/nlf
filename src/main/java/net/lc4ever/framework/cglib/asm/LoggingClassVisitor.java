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
package net.lc4ever.framework.cglib.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class LoggingClassVisitor extends ClassVisitor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final boolean isDebugEnabled = logger.isDebugEnabled();

	public LoggingClassVisitor(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (isDebugEnabled) {
			logger.debug("\n\t\t\tStarting Class: {}", name);
			logger.debug("visit({}, {}, {}, {}, {}, {}).",
					new Object[] { version, access(access), name, signature, superName, interfaces });
		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitSource(java.lang.String, java.lang.String)
	 */
	@Override
	public void visitSource(String source, String debug) {
		if (isDebugEnabled) {
			logger.debug("visitSource({}, {}).", new Object[] { source, debug });
		}
		super.visitSource(source, debug);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitOuterClass(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		if (isDebugEnabled) {
			logger.debug("visitOuterClass({}, {}, {}).", new Object[] { owner, name, desc });
		}
		super.visitOuterClass(owner, name, desc);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitAnnotation({}, {}).", new Object[] { desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitAnnotation(desc, visible));
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitTypeAnnotation(int, org.objectweb.asm.TypePath, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitTypeAnnotation({}, {}, {}, {}).", new Object[] { typeRef, typePath, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute attr) {
		if (isDebugEnabled) {
			logger.debug("visitAttribute({}).", new Object[] { attr });
		}
		super.visitAttribute(attr);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitInnerClass(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if (isDebugEnabled) {
			logger.debug("visitInnerClass({}, {}, {}, {}).",
					new Object[] { name, outerName, innerName, access(access) });
		}
		super.visitInnerClass(name, outerName, innerName, access);
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (isDebugEnabled) {
			logger.debug("visitField({}, {}, {}, {}, {}).",
					new Object[] { access(access), name, desc, signature, value });
		}
		return new LoggingFieldVisitor(super.visitField(access, name, desc, signature, value));
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (isDebugEnabled) {
			logger.debug("visitMethod({}, {}, {}, {}, {}).",
					new Object[] { access(access), name, desc, signature, exceptions });
		}
		return new LoggingMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}

	/**
	 * @see org.objectweb.asm.ClassVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		if (isDebugEnabled) {
			logger.debug("visitEnd().\n");
		}
		super.visitEnd();
	}

	public byte[] toByteArray() {
		return ((ClassWriter) cv).toByteArray();
	}

	/**
	 * Pseudo access flag used to distinguish class access flags.
	 */
	private static final int ACCESS_CLASS = 262144;

	/**
	 * Pseudo access flag used to distinguish field access flags.
	 */
	private static final int ACCESS_FIELD = 524288;

	/**
	 * Pseudo access flag used to distinguish inner class flags.
	 */
	private static final int ACCESS_INNER = 1048576;

	public static String access(final int access) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		if ((access & Opcodes.ACC_PUBLIC) != 0) {
			buf.append("ACC_PUBLIC");
			first = false;
		}
		if ((access & Opcodes.ACC_PRIVATE) != 0) {
			buf.append("ACC_PRIVATE");
			first = false;
		}
		if ((access & Opcodes.ACC_PROTECTED) != 0) {
			buf.append("ACC_PROTECTED");
			first = false;
		}
		if ((access & Opcodes.ACC_FINAL) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_FINAL");
			first = false;
		}
		if ((access & Opcodes.ACC_STATIC) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_STATIC");
			first = false;
		}
		if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			if ((access & ACCESS_CLASS) == 0) {
				buf.append("ACC_SYNCHRONIZED");
			} else {
				buf.append("ACC_SUPER");
			}
			first = false;
		}
		if ((access & Opcodes.ACC_VOLATILE) != 0 && (access & ACCESS_FIELD) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_VOLATILE");
			first = false;
		}
		if ((access & Opcodes.ACC_BRIDGE) != 0 && (access & ACCESS_CLASS) == 0 && (access & ACCESS_FIELD) == 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_BRIDGE");
			first = false;
		}
		if ((access & Opcodes.ACC_VARARGS) != 0 && (access & ACCESS_CLASS) == 0 && (access & ACCESS_FIELD) == 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_VARARGS");
			first = false;
		}
		if ((access & Opcodes.ACC_TRANSIENT) != 0 && (access & ACCESS_FIELD) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_TRANSIENT");
			first = false;
		}
		if ((access & Opcodes.ACC_NATIVE) != 0 && (access & ACCESS_CLASS) == 0 && (access & ACCESS_FIELD) == 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_NATIVE");
			first = false;
		}
		if ((access & Opcodes.ACC_ENUM) != 0
				&& ((access & ACCESS_CLASS) != 0 || (access & ACCESS_FIELD) != 0 || (access & ACCESS_INNER) != 0)) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_ENUM");
			first = false;
		}
		if ((access & Opcodes.ACC_ANNOTATION) != 0 && ((access & ACCESS_CLASS) != 0 || (access & ACCESS_INNER) != 0)) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_ANNOTATION");
			first = false;
		}
		if ((access & Opcodes.ACC_ABSTRACT) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_ABSTRACT");
			first = false;
		}
		if ((access & Opcodes.ACC_INTERFACE) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_INTERFACE");
			first = false;
		}
		if ((access & Opcodes.ACC_STRICT) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_STRICT");
			first = false;
		}
		if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_SYNTHETIC");
			first = false;
		}
		if ((access & Opcodes.ACC_DEPRECATED) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_DEPRECATED");
			first = false;
		}
		if ((access & Opcodes.ACC_MANDATED) != 0) {
			if (!first) {
				buf.append(" + ");
			}
			buf.append("ACC_MANDATED");
			first = false;
		}
		if (first) {
			buf.append('0');
		}
		return buf.toString();
	}
}
