/**
 * 
 */
package net.lc4ever.framework.dao.hibernate;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * @author JH
 *
 */
public class Mysql5InnoDBDialect extends MySQL5InnoDBDialect {

	public Mysql5InnoDBDialect() {
		super();
		registerKeyword("describe");
		registerKeyword("type");
	}

	
}
