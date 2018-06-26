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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class LoggingFieldVisitor extends FieldVisitor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final boolean isDebugEnabled = logger.isDebugEnabled();

	public LoggingFieldVisitor(FieldVisitor fv) {
		super(Opcodes.ASM5, fv);
	}

	/**
	 * @see org.objectweb.asm.FieldVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitAnnotation(String desc = '{}', boolean visible = '{}').", new Object[] { desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitAnnotation(desc, visible));
	}

	/**
	 * @see org.objectweb.asm.FieldVisitor#visitTypeAnnotation(int, org.objectweb.asm.TypePath, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		if (isDebugEnabled) {
			logger.debug("visitTypeAnnotation(int typeRef = '{}', TypePath typePath = '{}', String desc = '{}', boolean visible = '{}').", new Object[] { typeRef, typePath, desc, visible });
		}
		return new LoggingAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, desc, visible));
	}

	/**
	 * @see org.objectweb.asm.FieldVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute attr) {
		if (isDebugEnabled) {
			logger.debug("visitAttribute(Attribute attr = '{}').", new Object[] { attr });
		}
		super.visitAttribute(attr);
	}

	/**
	 * @see org.objectweb.asm.FieldVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		if (isDebugEnabled) {
			logger.debug("visitEnd().\n");
		}
		super.visitEnd();
	}
}
