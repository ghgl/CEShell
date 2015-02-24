/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *  Foo
 *
 * @author GaryRegier
 * @date   Nov 2, 2010
 */
public class Foo {
	public static void main(String[] args) throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while ( true ) {
			String cmdLine = null;
//			cmdLine = br.readLine();
//			System.out.println("input: " + cmdLine);
			int c = br.read();
			System.out.print("*");
		}
		
	}

}
