package com.ibm.bao.ceshell;

import java.util.ArrayList;

public class ArgsParser {
	
	static final char 
		DOUBLE_QUOTE = '\"',
		SINGLE_QUOTE = '\'',
		SPACE = ' ',
		TAB = '\t';

	
	boolean startDoubleQuotedString = false;
	boolean startSingleQuotedString = false;

	ArrayList<String> tokens = new ArrayList<String>();
	char[] chars = null;
	String input = null;
	ArrayList<Character> nextToken = null;
	
	static final int 
		STATE_START = 0,
		STATE_READ_RAW_TOKEN = 1,
		STATE_READ_DBL_QUOTE = 2,
		STATE_READ_SINGLE_QUOTE = 3,
		STATE_READ_SPACE = 4;
		
	
	
	public ArgsParser(String input) {
		this.input = input;
		if (input != null) {
			input = input.trim();
			this.chars = input.toCharArray();
		}
	}
	public String[] parse() throws Exception {
		
		if (this.input == null) {
			return new String[0];
		}
		
		int currentCase = ArgsParser.STATE_START;
		for (int i = 0; i < this.chars.length; i++) {
			char c = this.chars[i];
			switch(currentCase) {
				case ArgsParser.STATE_START:
					startNewToken();
					if (c == ArgsParser.DOUBLE_QUOTE) {
						currentCase = ArgsParser.STATE_READ_DBL_QUOTE;
						break;
					}
					if (c == ArgsParser.SINGLE_QUOTE) {
						currentCase = ArgsParser.STATE_READ_SINGLE_QUOTE;
						break;
					} else {
						currentCase = ArgsParser.STATE_READ_RAW_TOKEN;
						addCharToToken(c);
						break;
					}
				case ArgsParser.STATE_READ_DBL_QUOTE:
					if (c == ArgsParser.DOUBLE_QUOTE) {
						endToken();
						currentCase = ArgsParser.STATE_READ_SPACE;
						break;
					} else {
						addCharToToken(c);
						break;
					}
				case ArgsParser.STATE_READ_SINGLE_QUOTE:
					if (c == ArgsParser.SINGLE_QUOTE) {
						endToken();
						currentCase = ArgsParser.STATE_READ_SPACE;
						break;
					} else {
						addCharToToken(c);
						break;
					}
				case ArgsParser.STATE_READ_RAW_TOKEN:
					if (c == ArgsParser.SPACE) {
						endToken();
						currentCase = ArgsParser.STATE_READ_SPACE;
						break;
					} else {
						addCharToToken(c);
						break;
					}
				case ArgsParser.STATE_READ_SPACE:
					if (c == ArgsParser.DOUBLE_QUOTE) {
						currentCase = ArgsParser.STATE_READ_DBL_QUOTE;
						break;
					} else if (c == ArgsParser.STATE_READ_SINGLE_QUOTE) {
						currentCase = ArgsParser.STATE_READ_SINGLE_QUOTE;
						break;
					} else if (c == ArgsParser.SPACE) {
						break; //** Just continue eating spaces
					} else {
						addCharToToken(c);
						currentCase = ArgsParser.STATE_READ_RAW_TOKEN;
					}
					
				default:
					// TODO:
			}
		}
		if ( (currentCase == ArgsParser.STATE_READ_DBL_QUOTE) || 
				(currentCase == ArgsParser.STATE_READ_SINGLE_QUOTE)) {
			throw new IllegalStateException("Missing close quote charager");
		}
		endToken();
		String[] result = new String[tokens.size()];
		result =(String[]) tokens.toArray(result);
		return result;
	}
	
	private void addCharToToken(char c) {
		nextToken.add(new Character(c));
	}
	

	private void endToken() {
		if (nextToken == null || nextToken.size() == 0) {
			return;
		}
		StringBuffer buf = new StringBuffer();
		for(Character c: nextToken) {
			buf.append(c.charValue());
		}
		String token = buf.toString();
		tokens.add(token);
		startNewToken();
	}
	private void startNewToken() {
		nextToken = new ArrayList<Character>();
		
	}
}
