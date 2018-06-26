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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lc4ever.framework.format.DateFormatter;

/**
 * @author q-wang
 */
public class ToStringBuilderTestBean {

	private String string = "string";

	private int int1 = 1;

	private boolean boolean1 = true;

	private Date date = DateFormatter.now();

	private int[] ints = new int[] { 1, 3, 5 };

	private boolean[] booleans = new boolean[] { true, false, true };

	private Integer integer = 200;

	private double double1 = 100;

	private Double double2 = 300d;

	private String[] strings1 = new String[] { "str1", "str2" };

	private List<String> strings2 = Arrays.asList(strings1);

	private Map<String, String> map = Collections.emptyMap();

	private Set<String> strings3 = Collections.emptySet();

	private ToStringBuilderTestBean inner;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public int getInt1() {
		return int1;
	}

	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public boolean isBoolean1() {
		return boolean1;
	}

	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int[] getInts() {
		return ints;
	}

	public void setInts(int[] ints) {
		this.ints = ints;
	}

	public boolean[] getBooleans() {
		return booleans;
	}

	public void setBooleans(boolean[] booleans) {
		this.booleans = booleans;
	}

	public Integer getInteger() {
		return integer;
	}

	public void setInteger(Integer integer) {
		this.integer = integer;
	}

	public double getDouble1() {
		return double1;
	}

	public void setDouble1(double double1) {
		this.double1 = double1;
	}

	public Double getDouble2() {
		return double2;
	}

	public void setDouble2(Double double2) {
		this.double2 = double2;
	}

	public String[] getStrings1() {
		return strings1;
	}

	public void setStrings1(String[] strings1) {
		this.strings1 = strings1;
	}

	public List<String> getStrings2() {
		return strings2;
	}

	public void setStrings2(List<String> strings2) {
		this.strings2 = strings2;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public Set<String> getStrings3() {
		return strings3;
	}

	public void setStrings3(Set<String> strings3) {
		this.strings3 = strings3;
	}

	public ToStringBuilderTestBean getInner() {
		return inner;
	}

	public void setInner(ToStringBuilderTestBean inner) {
		this.inner = inner;
	}

	public static class ToStringBuilder
			implements net.lc4ever.framework.cglib.ToStringBuilder<ToStringBuilderTestBean> {

		private ToStringBuilderTestBean bean;
		
		private Formatter formatter;
		
		public ToStringBuilder(Formatter formatter, ToStringBuilderTestBean bean) {
			this.bean = bean;
		}

		@Override
		public StringBuilder toString(StringBuilder builder) {
			formatter.start(builder);
			formatter.value("string", bean.getString());
			formatter.value("strings2", bean.getStrings2());
			return formatter.result();
		}

		@Override
		public String toString() {
			return toString(null).toString();
		}

	}

	public static void main(String[] args) {
		ToStringBuilderTestBean bean = new ToStringBuilderTestBean();
		bean.inner = new ToStringBuilderTestBean();
		bean.inner.inner = null;
		ToStringBuilder builder = new ToStringBuilder(null, bean);
		System.out.println(builder.toString(null));
	}

	public StringBuilder toString(StringBuilder builder, String prefix, String suffix) {
		return ToStringBuilderFactory.toString(this, builder, prefix);
	}
}
