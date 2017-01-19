package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;


public class JavaObjectNode implements Node {

	public static Node forObject( Object obj ) { return null == obj ? MissingObjectNode.instance() : new JavaObjectNode(obj) ; }
	
	private final Object m_object ;
	private final NavigableMap<String,PropertyDescriptor> m_propertyNames = new TreeMap<String,PropertyDescriptor>();
	
	public JavaObjectNode( Object obj ) {
		Objects.requireNonNull(obj) ;
		m_object = obj ;
		BeanInfo info ;
		try {
			info = java.beans.Introspector.getBeanInfo( m_object.getClass() ) ;
			Arrays.stream(info.getPropertyDescriptors())
				.forEach( (d)->m_propertyNames.put(d.getName(),d) )
				;
		} catch ( IntrospectionException ex ) {
			/* no properties */
		}
	}
	
	@Override public String value() { return m_object.toString() ; }

	@Override public void assign(String newValue) { throw new UnsupportedOperationException("node does not support assignment") ; }

	@Override public boolean isEmpty() { return m_propertyNames.isEmpty() ; }

	@Override public String keyPreceding(String key) { return m_propertyNames.lowerKey(key) ; }
	@Override public String keyFollowing(String key) { return m_propertyNames.higherKey(key) ; }
	
	protected <T> T subscriptModificationNotSupported() { throw new UnsupportedOperationException("node does not support modifying subscripts") ; }
	
	@Override public String firstKey() { return isEmpty() ? null : m_propertyNames.firstKey() ; }
	@Override public String lastKey() { return isEmpty() ? null : m_propertyNames.lastKey() ; }
	
	@Override public Node get(String key) {
		PropertyDescriptor prop = m_propertyNames.get(key) ;
		if ( null == prop )
			return subscriptModificationNotSupported() ;
		return nodeForProperty(prop) ;
	}
	
	@Override public void merge(NodeMap srcNodeMap) { subscriptModificationNotSupported() ; }
	@Override public Node create(String key) { return subscriptModificationNotSupported() ; }
	@Override public void insert(String key, Node node) { subscriptModificationNotSupported() ; }
	@Override public void drop(String key) { subscriptModificationNotSupported() ; }
	@Override public void dropAll() { subscriptModificationNotSupported() ; }
	
	@Override public Node at(String key) {
		PropertyDescriptor prop = m_propertyNames.get(key) ;
		if ( null == prop )
			throw new IllegalArgumentException("no such key \"" + key + "\"") ;
		return nodeForProperty(prop) ;
	}
	
	private Node nodeForProperty(PropertyDescriptor prop) {
		try {
			Object propValue = prop.getReadMethod().invoke(m_object) ;
			if ( null == propValue )
				return MissingObjectNode.instance() ;
			return new JavaObjectNode(propValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("value for key \"" + prop.getName() + "\" not available") ;
		}
	}

	@Override
	public Object toObject(Class<?> ofType) {
		return ofType.cast( m_object ) ;
	}
}
