/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import org.python.core.PyObject;
import org.python.util.InteractiveConsole;

/**
 *  XInteractiveConsole
 *
 * @author regier
 * @date   Dec 31, 2011
 */
public class PyInteractiveConsole extends InteractiveConsole {

	
	public PyInteractiveConsole() {
		super();
		 if (System.getProperty("python.home") == null) {
	            System.setProperty("python.home", "");
	        }
		 InteractiveConsole.initialize(System.getProperties(),
	                                      null, new String[0]);
		 
	}
	
	 public String raw_input(PyObject prompt) {
		 
		 String cmd = super.raw_input(prompt);
		 try {
			if ("exit".equals(cmd)) {
				 throw new ConsoleDone();
			 }
			return cmd;
		} catch (ConsoleDone e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
}
