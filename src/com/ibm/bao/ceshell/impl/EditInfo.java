/**
 * 
 */
package com.ibm.bao.ceshell.impl;

/**
 *  EditInfo
 *
 * @author regier
 * @date   Aug 7, 2012
 */
public interface EditInfo {

	public abstract void setProperties(java.util.Properties props)
			throws Exception;

	public abstract void setProp(String propName, String propValue)
			throws Exception;

	public abstract void save();

}