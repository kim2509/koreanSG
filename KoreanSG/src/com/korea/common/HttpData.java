package com.korea.common;

import java.util.Hashtable;

public class HttpData {
	public String content;
	@SuppressWarnings("rawtypes")
	public Hashtable cookies = new Hashtable();
	@SuppressWarnings("rawtypes")
	public Hashtable headers = new Hashtable();
}