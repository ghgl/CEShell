
package com.ibm.bao.ceshell.util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

/**
 * PropertyUtil
 * <p>
 * This class makes it easier to deal with properties file.
 * The premise is that a JavaBean should be used to hold and access data 
 * in-memory, and the properties file is used for serialization and 
 * deserialization. 
 * <p>
 * Certain conventions need to be followed for this to work. The Java
 * introspection is used to read and write the values to and from the 
 * properties file and the JavaBean.
 * <p>
 * convention:
 * <br>
 * For a given member of Type T named fooBarBaz there should be a corresponding 
 * getter and setter methods 
 * <br>
 * <code> 
 * 	public T getFooBarBaz()</code>
 *  public void setFooBarBaz(T param)
 *  </code>
 *  <p> Sample Usage:
 *  <code>
 *  	ProperertyBean propBean = new ProperertyBean();
 *		File propFile = createFile(...); 
 *		propertyUtil.loadProperties(propBean, propFile);
 *  </code>
 *  <p>
 *  The types supported are all standard java types. Native types are not supported,
 *  so the wrapper class types should be used:
 *  <ol>
 * 		<li>String
 *		<li>Integer
 *		<li>Boolean
 *		<li>Long
 *		<li>Double
 *		<li>Short
 *		<li>Float
 *		<li>Character
 *	</ol>
 *
 * @author GaryRegier
 */

public class PropertyUtil {
	
	/**
	 * 
	 * @param relativeUri is a String value like "/config/com.acme.MyBean.properties"
	 * @return File as the resource
	 * @throws Exception
	 */
	public File fetchResourceAsFile(String relativeUri) throws Exception {
		File file = null;
		String fileName = null;
		URL url = this.getClass().getResource(relativeUri);
		
		if (url == null) {
			throw new IllegalArgumentException("Resource not found: " + relativeUri);
		}
		 
		fileName = url.getFile();
		file = new File(fileName);
		return file;
	}
	
	public String encodeBase64(String raw) {
		
		//return new sun.misc.BASE64Encoder().encode(raw.getBytes());
		return org.apache.commons.codec.binary.Base64.encodeBase64String(raw.getBytes());
		
		
	}
	
	public String decodeBase64(String encString) throws IOException {
//		byte[] b = null;
//		b = new sun.misc.BASE64Decoder().decodeBuffer(encString);
//		return new String(b);
		
		byte[] b = null;
		b = org.apache.commons.codec.binary.Base64.decodeBase64(encString);
		return new String(b);
	}
	
	
	public void loadProperties(Object srcBean, String relativeUrl) throws Exception {
		Properties properties = new Properties();
		InputStream is = null;
		
		try {
			is = this.getClass().getResourceAsStream(relativeUrl);
			properties.load(is);	
			applyPropertiesToBean(srcBean, properties);
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				}catch (Exception e) {} // no-op
			}
		}
	}
	
	/**
	 * This method load properties from a properties file and applies the values
	 * to a JavaBean
	 * 
	 * 
	 * @param srcBean is the JavaBean whose properties will be populated.
	 * @param propertiesFile File in Java properties file format that holds the values.
	 * @throws Exception
	 */
	public void loadProperties(Object srcBean, File propertiesFile) throws Exception {
		Properties properties = null;					
		properties = loadPropertiesFromFile(propertiesFile);
		applyPropertiesToBean(srcBean, properties);
	}
	/**
	 * 
	 * @param srcBean
	 * @param properties
	 * @throws IntrospectionException
	 * @throws Exception
	 */
	private void applyPropertiesToBean(Object srcBean, Properties properties)
			throws IntrospectionException, Exception {
		BeanInfo beanInfo;
		@SuppressWarnings("rawtypes")
		Class nextClass = null;
		Method nextMethod;
		PropertyDescriptor nextDescriptor;
		String propName;
		PropertyDescriptor[] beanPropertyDescriptors = null;
		beanInfo = java.beans.Introspector.getBeanInfo(srcBean.getClass());
		beanPropertyDescriptors = beanInfo.getPropertyDescriptors();
		
		try {
			for(int i = 0; i < beanPropertyDescriptors.length; i++) {
				nextDescriptor = beanPropertyDescriptors[i]; 
				propName = nextDescriptor.getName();
				nextMethod = nextDescriptor.getWriteMethod();
				nextClass = nextDescriptor.getPropertyType();
				if (! isReadOnlyProperty(nextMethod)) {
					if (properties.containsKey(propName)) {
						String value = properties.getProperty(propName);
						applyPropertyValue(srcBean, nextMethod, nextClass, value);
					}
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * storeProperties
	 * 
	 * @param srcBean
	 * @param propertiesFile
	 * @throws Exception
	 */
	public void storeProperties(Object srcBean, File propertiesFile) throws Exception {
		Properties properties = new Properties();
		@SuppressWarnings("rawtypes")
		Class nextClass = null;
		BeanInfo beanInfo = null;
		Method readMethod = null;
		PropertyDescriptor nextDescriptor = null;
		String propName = null;
		
		try {	
			PropertyDescriptor[] beanPropertyDescriptors = null;
			beanInfo = java.beans.Introspector.getBeanInfo(srcBean.getClass());
			beanPropertyDescriptors = beanInfo.getPropertyDescriptors();
			
			for(int i = 0; i < beanPropertyDescriptors.length; i++) {
				nextDescriptor = beanPropertyDescriptors[i]; 
				propName = nextDescriptor.getName();
				readMethod = nextDescriptor.getReadMethod();
				nextClass = nextDescriptor.getPropertyType();
				Method writeMethod = nextDescriptor.getWriteMethod();
				if (! this.isReadOnlyProperty(writeMethod)) {
					String valueAsString = 
							readPropertyValue(srcBean,
									nextClass,
									readMethod);
					properties.setProperty(propName, valueAsString);
				}
			}
			storePropertiesToFile(
					properties, 
					propertiesFile, 
					srcBean.getClass().getName());
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void storePropertiesToFile(
			Properties properties,
			File propertiesFile, 
			String beanName) throws Exception {
		OutputStream out = null;
		String dateStr = null;
		
		try {
			out = new FileOutputStream(propertiesFile);
			if (beanName != null) {
				StringBuffer comments = new StringBuffer();
				comments.append("Properties saved from " + beanName);
				dateStr = new java.text.SimpleDateFormat()
						.format(new java.util.Date());
				comments.append("\n#\tStored on " + dateStr);
				properties.store(out, comments.toString());
			} else {
				properties.store(out, null);
			}
			
			out.close();
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}

	private String readPropertyValue(Object srcBean, 
			@SuppressWarnings("rawtypes") Class propertyClass, 
			Method readMethod) throws Exception  {
		
		Object value = null;
		
		value = readMethod.invoke(srcBean, (Object[]) null);
		if (value != null) {
			return value.toString();
		} else {
			return "";
		}	
	}
	
	public Properties loadProperties(String resourceUri) 
		throws IOException {
		Properties properties = new Properties();
		InputStream is = null;
		
		try {
			is = this.getClass().getResourceAsStream(resourceUri);
			if (is == null) {
				throw new IllegalArgumentException("No resource found: " + resourceUri);
			}
			properties.load(is);	
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				}catch (Exception e) {} // no-op
			}
		}
		
		return properties;
	}

	public Properties loadPropertiesFromFile(File propertiesFile) 
			throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		InputStream is = null;
		
		try {
			is = new FileInputStream(propertiesFile);
			properties.load(is);	
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				}catch (Exception e) {} // no-op
			}
		}
		
		return properties;
	}

	private void applyPropertyValue(Object srcBean, Method nextMethod,
			Class<?> nextClass, String value) throws Exception {
		
		if (nextClass.getName().equals("java.lang.String")) {
			applyStringProperty(srcBean, nextMethod, value);
		} else if (nextClass.getName().equals("java.lang.Integer")) {
			applyIntegerProperty(srcBean, nextMethod, value);
		} else if (nextClass.getName().equals("java.lang.Boolean")) {
			applyBooleanProperty(srcBean, nextMethod, value);					
		} else if (nextClass.getName().equals("java.lang.Long")) {
			applyLongProperty(srcBean, nextMethod, value);			
		} else if (nextClass.getName().equals("java.lang.Double")) {
			applyDoubleProperty(srcBean, nextMethod, value);
		} else if (nextClass.getName().equals("java.lang.Short")) {
			applyShortProperty(srcBean, nextMethod, value);
		} else if (nextClass.getName().equals("java.lang.Float")) {
			applyFloatProperty(srcBean, nextMethod, value);
		} else if (nextClass.getName().equals("java.lang.Character")) {
			applyCharacterProperty(srcBean, nextMethod, value);
		}
	}
	
	private void applyStringProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		nextMethod.invoke(srcBean, value);
	}
	
	private void applyIntegerProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		nextMethod.invoke(srcBean, Integer.parseInt(value));
	}
	
	private void applyLongProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		nextMethod.invoke(srcBean, Long.parseLong(value));
	}
	
	private void applyDoubleProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		Double d = new Double(Double.parseDouble(value));
		
		nextMethod.invoke(srcBean, d);
	}
	
	private void applyShortProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		nextMethod.invoke(srcBean, new Short(Short.parseShort(value)));
	}
	
	private void applyFloatProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		nextMethod.invoke(srcBean, new Float(Float.parseFloat(value)));
	}
	
	private void applyCharacterProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		
		if ( (value == null) || (value.length() == 0) ) {
			return;
		}
		
		nextMethod.invoke(srcBean, new Character(value.toCharArray()[0]));
	}
		
	/**
	 * If the string starts with a "t" then true
	 * Otherwise false
	 * @param srcBean
	 * @param nextMethod
	 * @param value
	 * @throws Exception
	 */
	private void applyBooleanProperty(Object srcBean, 
			Method nextMethod, 
			String value) throws Exception {
		Boolean result = Boolean.FALSE;
		if (value != null) {
			if (value.toLowerCase().startsWith("t")) {
				result = Boolean.TRUE;
			}	
		}
		nextMethod.invoke(srcBean, result);
	}
	
	/**
	 * The introspector returns null if the property is a read-only property
	 * 
	 * @param nextMethod
	 * @return
	 */
	private boolean isReadOnlyProperty(Method nextMethod) {
		return (nextMethod == null);
	}
}
