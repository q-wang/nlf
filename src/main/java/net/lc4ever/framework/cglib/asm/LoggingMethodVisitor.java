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

import static net.lc4ever.framework.cglib.asm.LoggingClassVisitor.access;
import static org.objectweb.asm.util.Printer.OPCODES;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class LoggingMethodVisitor extends MethodVisitor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final boolean isDebugEnabled = logger.isDebugEnabled();
	
	private static final String[] FRAME_TYPE = new String[]{"F_NEW","F_FULL","F_APPEND","F_CHOP","F_SAME","F_SAME1"};
	public static String frame(int type) {
		return FRAME_TYPE[type+1];
	}

	public LoggingMethodVisitor(MethodVisitor mv) {
		super(Opcodes.ASM5, mv);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitParameter(java.lang.String, int)
	 */
	@Override
	public void visitParameter(String name, int access) {
		if (isDebugEnabled) {
			logger.debug("visitParameter({}, {}).", new Object[] { name, access(access) });
		}
		super.visitParameter(name, access);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
	 */
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		if (isDebugEnabled) {
			logger.debug("visitAnnotationDefault().");
		}
		return new LoggingAnnotationVisitor(super.visitAnnotationDefault());
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitAnnotation({}, {}).", new Object[] { desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitAnnotation(desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitTypeAnnotation(int, org.objectweb.asm.TypePath, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitTypeAnnotation({}, {}, {}, {}).", new Object[] { typeRef, typePath, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitParameterAnnotation({}, {}, {}).", new Object[] { parameter, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitParameterAnnotation(parameter, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute attr) {
		if (isDebugEnabled) {
			logger.debug("visitAttribute({}).", new Object[] { attr });
		}
		super.visitAttribute(attr);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitCode()
	 */
	@Override
	public void visitCode() {
		if (isDebugEnabled) {
			logger.debug("visitCode().");
		}
		super.visitCode();
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int, java.lang.Object[], int, java.lang.Object[])
	 */
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		if (isDebugEnabled) {
			logger.debug("visitFrame({}, {}, {}, {}, {}).", new Object[] { frame(type), nLocal, local, nStack, stack });
		}
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
	 */
	@Override
	public void visitInsn(int opcode) {
		if (isDebugEnabled) {
			logger.debug("visitInsn({}).", new Object[] { OPCODES[opcode] });
		}
		super.visitInsn(opcode);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
	 */
	@Override
	public void visitIntInsn(int opcode, int operand) {
		if (isDebugEnabled) {
			logger.debug("visitIntInsn({}, {}).", new Object[] { OPCODES[opcode], operand });
		}
		super.visitIntInsn(opcode, operand);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
	 */
	@Override
	public void visitVarInsn(int opcode, int var) {
		if (isDebugEnabled) {
			logger.debug("visitVarInsn({}, {}).", new Object[] { OPCODES[opcode], var });
		}
		super.visitVarInsn(opcode, var);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int, java.lang.String)
	 */
	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (isDebugEnabled) {
			logger.debug("visitTypeInsn({}, {}).", new Object[] { OPCODES[opcode], type });
		}
		super.visitTypeInsn(opcode, type);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if (isDebugEnabled) {
			logger.debug("visitFieldInsn({}, {}, {}, {}).", new Object[] { OPCODES[opcode], owner, name, desc });
		}
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 * @deprecated
	 */
	@Override
	@Deprecated
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (isDebugEnabled) {
			logger.debug("visitMethodInsn({}, {}, {}, {}).", new Object[] { OPCODES[opcode], owner, name, desc });
		}
		super.visitMethodInsn(opcode, owner, name, desc);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		if (isDebugEnabled) {
			logger.debug("visitMethodInsn({}, {}, {}, {}, {}).", new Object[] { OPCODES[opcode], owner, name, desc, itf });
		}
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitInvokeDynamicInsn(java.lang.String, java.lang.String, org.objectweb.asm.Handle, java.lang.Object[])
	 */
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		if (isDebugEnabled) {
			logger.debug("visitInvokeDynamicInsn({}, {}, {}, {}).", new Object[] { name, desc, bsm, bsmArgs });
		}
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (isDebugEnabled) {
			logger.debug("visitJumpInsn({}, {}).", new Object[] { OPCODES[opcode], label });
		}
		super.visitJumpInsn(opcode, label);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLabel(org.objectweb.asm.Label)
	 */
	@Override
	public void visitLabel(Label label) {
		if (isDebugEnabled) {
			logger.debug("visitLabel({}).", new Object[] { label });
		}
		super.visitLabel(label);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
	 */
	@Override
	public void visitLdcInsn(Object cst) {
		if (isDebugEnabled) {
			logger.debug("visitLdcInsn({}).", new Object[] { cst });
		}
		super.visitLdcInsn(cst);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
	 */
	@Override
	public void visitIincInsn(int var, int increment) {
		if (isDebugEnabled) {
			logger.debug("visitIincInsn({}, {}).", new Object[] { var, increment });
		}
		super.visitIincInsn(var, increment);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int, org.objectweb.asm.Label, org.objectweb.asm.Label[])
	 */
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		if (isDebugEnabled) {
			logger.debug("visitTableSwitchInsn(int min = '{}', int max = '{}', Label dflt = '{}', Label... labels = '{}').", new Object[] { min, max, dflt, labels });
		}
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLookupSwitchInsn(org.objectweb.asm.Label, int[], org.objectweb.asm.Label[])
	 */
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		if (isDebugEnabled) {
			logger.debug("visitLookupSwitchInsn(Label dflt = '{}', int[] keys = '{}', Label[] labels = '{}').", new Object[] { dflt, keys, labels });
		}
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitMultiANewArrayInsn(java.lang.String, int)
	 */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		if (isDebugEnabled) {
			logger.debug("visitMultiANewArrayInsn(String desc = '{}', int dims = '{}').", new Object[] { desc, dims });
		}
		super.visitMultiANewArrayInsn(desc, dims);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitInsnAnnotation(int, org.objectweb.asm.TypePath, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitInsnAnnotation(int typeRef = '{}', TypePath typePath = '{}', String desc = '{}', boolean visible = '{}').", new Object[] { typeRef, typePath, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitInsnAnnotation(typeRef, typePath, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label, java.lang.String)
	 */
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		if (isDebugEnabled) {
			logger.debug("visitTryCatchBlock(Label start = '{}', Label end = '{}', Label handler = '{}', String type = '{}').", new Object[] { start, end, handler, type });
		}
		super.visitTryCatchBlock(start, end, handler, type);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitTryCatchAnnotation(int, org.objectweb.asm.TypePath, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitTryCatchAnnotation(int typeRef = '{}', TypePath typePath = '{}', String desc = '{}', boolean visible = '{}').", new Object[] { typeRef, typePath, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitTryCatchAnnotation(typeRef, typePath, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.String, java.lang.String, java.lang.String, org.objectweb.asm.Label, org.objectweb.asm.Label, int)
	 */
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (isDebugEnabled) {
			logger.debug("visitLocalVariable({}, {}, {}, {}, {}, {}).", new Object[] { name, desc, signature, start, end, index });
		}
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLocalVariableAnnotation(int, org.objectweb.asm.TypePath, org.objectweb.asm.Label[], org.objectweb.asm.Label[], int[], java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitLocalVariableAnnotation(int typeRef = '{}', TypePath typePath = '{}', Label[] start = '{}', Label[] end = '{}', int[] index = '{}', String desc = '{}', boolean visible = '{}').", new Object[] { typeRef, typePath, start, end, index, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitLineNumber(int line, Label start) {
		if (isDebugEnabled) {
			logger.debug("visitLineNumber(int line = '{}', Label start = '{}').", new Object[] { line, start });
		}
		super.visitLineNumber(line, start);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
	 */
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (isDebugEnabled) {
			logger.debug("visitMaxs({}, {}).", new Object[] { maxStack, maxLocals });
		}
		super.visitMaxs(maxStack, maxLocals);
	}

	/**
	 * @see org.objectweb.asm.MethodVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		if (isDebugEnabled) {
			logger.debug("visitEnd().\n");
		}
		super.visitEnd();
	}

}
