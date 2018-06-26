/**
 * 
 */
package net.lc4ever.framework.remote.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.Hibernate;
import org.hibernate.collection.internal.AbstractPersistentCollection;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.CollectionSerializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;

/**
 * @author q-wang
 */
public class HibernateSerializerFacotry extends SerializerFactory {

	private Serializer serializer = new HibernateCollectionSerializer();

	private Serializer collectionSerializer = new CollectionSerializer();

	@SuppressWarnings("rawtypes")
	@Override
	public Serializer getSerializer(Class cl) throws HessianProtocolException {
		if (AbstractPersistentCollection.class.isAssignableFrom(cl)) {
			return serializer;
		}
		return super.getSerializer(cl);
	}

	private abstract class AbstractHibernateSerializer implements com.caucho.hessian.io.Serializer {

		protected abstract void delegate(Object obj, AbstractHessianOutput out) throws IOException;

		protected abstract void writeEmpty(Object obj, AbstractHessianOutput out) throws IOException;

		@Override
		public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
			if (Hibernate.isInitialized(obj)) {
				delegate(obj, out);
			} else {
				writeEmpty(obj, out);
			}
		}
	}

	private class HibernateCollectionSerializer extends AbstractHibernateSerializer {
		
		@Override
		protected void delegate(Object obj, AbstractHessianOutput out) throws IOException {
			collectionSerializer.writeObject(new ArrayList<>((Collection<?>)obj), out);
		}

		@Override
		protected void writeEmpty(Object obj, AbstractHessianOutput out) throws IOException {
			collectionSerializer.writeObject(Collections.emptyList(), out);
		}

	}

}
