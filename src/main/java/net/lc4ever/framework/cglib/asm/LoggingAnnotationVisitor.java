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
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:apeidou@gmail.com">Q-Wang</a>
 */
public class LoggingAnnotationVisitor extends AnnotationVisitor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final boolean isDebugEnabled = logger.isDebugEnabled();

	public LoggingAnnotationVisitor(AnnotationVisitor av) {
		super(Opcodes.ASM5, av);
	}

	/**
	 * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
	 */
	@Override
	public void visit(String name, Object value) {
		if (isDebugEnabled) {
			logger.debug("visit(String name = '{}', Object value = '{}').", new Object[] { name, value });
		}
		super.visit(name, value);
	}

	/**
	 * @see org.objectweb.asm.AnnotationVisitor#visitEnum(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitEnum(String name, String desc, String value) {
		if (isDebugEnabled) {
			logger.debug("visitEnum(String name = '{}', String desc = '{}', String value = '{}').",
					new Object[] { name, desc, value });
		}
		super.visitEnum(name, desc, value);
	}

	/**
	 * @see org.objectweb.asm.AnnotationVisitor#visitAnnotation(java.lang.String, java.lang.String)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		if (isDebugEnabled) {
			logger.debug("visitAnnotation(String name = '{}', String desc = '{}').", new Object[] { name, desc });
		}
		return new LoggingAnnotationVisitor(super.visitAnnotation(name, desc));
	}

	/**
	 * @see org.objectweb.asm.AnnotationVisitor#visitArray(java.lang.String)
	 */
	@Override
	public AnnotationVisitor visitArray(String name) {
		if (isDebugEnabled) {
			logger.debug("visitArray(String name = '{}').", name);
		}
		return new LoggingAnnotationVisitor(super.visitArray(name));
	}

	/**
	 * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		if (isDebugEnabled) {
			logger.debug("visitEnd().");
		}
		super.visitEnd();
	}

}
