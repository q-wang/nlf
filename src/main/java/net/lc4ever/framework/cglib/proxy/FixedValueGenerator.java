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
package net.lc4ever.framework.cglib.proxy;

import java.util.Iterator;
import java.util.List;

import net.lc4ever.framework.cglib.core.ClassEmitter;
import net.lc4ever.framework.cglib.core.CodeEmitter;
import net.lc4ever.framework.cglib.core.MethodInfo;
import net.lc4ever.framework.cglib.core.Signature;
import net.lc4ever.framework.cglib.util.TypeUtils;

import org.objectweb.asm.Type;

class FixedValueGenerator implements CallbackGenerator {

	public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();

	private static final Type FIXED_VALUE = TypeUtils.parseType("net.lc4ever.framework.cglib.proxy.FixedValue");

	private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");

	@Override
	public void generate(ClassEmitter ce, Context context, List<MethodInfo> methods) {
		for (Iterator<MethodInfo> it = methods.iterator(); it.hasNext();) {
			MethodInfo method = it.next();
			CodeEmitter e = context.beginMethod(ce, method);
			context.emitCallback(e, context.getIndex(method));
			e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
			e.unbox_or_zero(e.getReturnType());
			e.return_value();
			e.end_method();
		}
	}

	@Override
	public void generateStatic(CodeEmitter e, Context context, List<MethodInfo> methods) {
	}
}
