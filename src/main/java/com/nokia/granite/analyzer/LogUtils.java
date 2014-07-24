package com.nokia.granite.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
	private static Logger serviceLog = LoggerFactory.getLogger( "service" );
	private static Logger webLog = LoggerFactory.getLogger( "web" );
	private static Logger dbLog = LoggerFactory.getLogger( "db" );
	private static Logger statLog = LoggerFactory.getLogger( "stat" );
	private static Logger designLog = LoggerFactory.getLogger( "design" );
	
	public static Logger getLog( String name ) {
		return LoggerFactory.getLogger( name );
	}
	
	public static Logger getServiceLog( ) {
		return serviceLog;
	}
	
	public static Logger getWebLog( ) {
		return webLog;
	}
	
	public static Logger getDbLog( ) {
		return dbLog;
	}
	
	public static Logger getStatLog( ) {
		return statLog;
	}
	
	public static Logger getDesignLog( ) {
		return designLog;
	}
}
