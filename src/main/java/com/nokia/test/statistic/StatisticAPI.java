package com.nokia.test.statistic;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

/**
 * Facade for all the statistic response.
 * 
 * @author Frank Wang
 * @since Dec 14, 2012
 */
public class StatisticAPI extends HttpServlet {

	private static final long serialVersionUID = -4536519964789500356L;
	private List<String> operationList = new ArrayList<String>();
	private Logger log = LogUtils.getStatLog();

	@Override
	public void init() {
		operationList.add( "testset" );
		operationList.add( "execution" );
		operationList.add( "query" );
		operationList.add( "fetch" );
		operationList.add( "update" );
		operationList.add( "append" );
		operationList.add( "touch" );
	}

	/*
	 * public void perform(HttpServletRequest request, HttpServletResponse response) throws IOException,
	 * ServletException{ }
	 */

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		String operation = request.getParameter( "operation" );
		if ( operation != null && operationList.contains( operation ) ) {
			if ( operation.toLowerCase().equals( "testset" ) ) {
				String content = request.getParameter( "content" );
				try {
					Testset ts = TestParser.parseTestset( request.getParameter( "product" ), content );
					StatisticManager.addTestset( ts );
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "Parse testset content failed.", e );
					response( response, "{failure}" );
				}
			} else if ( operation.toLowerCase().equals( "execution" ) ) {
				String content = request.getParameter( "content" );
				try {
					TestExecution te = TestParser.parseTestExecution( request.getParameter( "product" ), content );
					StatisticManager.addExecution( te );
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "Parse execution content failed.", e );
					response( response, "{failure}" );
				}
			} else if ( operation.toLowerCase().equals( "append" ) ) {
				String url = request.getParameter( "url" );
				String product = request.getParameter( "product" );
				//String tsName = request.getParameter( "tsName" );
				try {
					log.info( "Update receive request for Fetch url:" + url );
					String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
					if ( !content.startsWith( "<!" ) ) {
						content = content.substring( content.indexOf( "<" ) );
					}
					//if ( url.endsWith( "njunit.xml" ) ) {
					if ( url.toLowerCase().contains( "njunit" ) ) {
						String tsName = TestParser.parseTestsetName(content);
						TestExecution te = TestParser.parseTestExecution( product, content.trim() );
						SubExecution se = new SubExecution();
						//int subId = StatisticManager.getNextSubId(te.getId());//CommonUtils.parseInt( tsName.substring( tsName.lastIndexOf( "_" ) + 1, tsName.indexOf( ".testset" ) ), 0 );
						se.setReport( content );
						//se.setSubId( subId );
						se.setUrl(url);
						te.setName( request.getParameter( "name" ) );
						StatisticManager.appendExecutionResults( te, se );
					} else {
						log.info( "Update result:Fetch content:" + content );
					}
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "Fetch content failed.", e );
					response( response, "{failure}" );
				}
			} else if ( operation.toLowerCase().equals( "update" ) ) {
				String url = request.getParameter( "url" );
				String product = request.getParameter( "product" );
				try {
					log.info( "Update receive request for Fetch url:" + url );
					String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
					if ( !content.startsWith( "<!" ) ) {
						content = content.substring( content.indexOf( "<" ) );
					}
					//if ( url.endsWith( "njunit.xml" ) ) {
					if ( url.toLowerCase().contains( "njunit" ) ) {
						TestExecution te = TestParser.parseTestExecution( product, content.trim() );
						te.setName( request.getParameter( "name" ) );
						if( te.getPassCnt()>0 ) {
							StatisticManager.updateExecutionResults( te );
						}else {
							log.info( "No passed case detected for test execution:" + content );
						}
					} else {
						log.info( "Update result:Fetch content:" + content );
					}
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "Fetch content failed.", e );
					response( response, "{failure}" );
				}
			} else if ( operation.toLowerCase().equals( "touch" ) ) {
				// touch a empty test execution result and wait for update.
				String product = request.getParameter( "product" );
				String testset = request.getParameter( "testset" );
				String name = request.getParameter( "name" );
				String sw = request.getParameter( "sw" );
				String exectimestr = request.getParameter( "exectime" );
				if ( exectimestr == null ) {
					exectimestr = String.valueOf( System.currentTimeMillis() );
				}
				String type = request.getParameter( "type" );
				Timestamp execTime = new Timestamp( Long.parseLong( exectimestr ) );
				try {
					log.info( "Touch a execution result for product:"+ product+", and testset:" + testset );
					TestExecution te = new TestExecution();
					te.setName( name );
					te.setProduct( product );
					te.setSw( sw );
					te.setType( type );
					te.setUrl( "" );
					te.setExecTime( execTime );
					Testset ts = StatisticManager.getTestsetOnly( testset, product );
					if(ts == null) {
						throw new NullPointerException("Parse Testset Failed:testset:"+testset+", product:"+product);
					}
					te.setTestsetId( ts.getId() );
					StatisticManager.addExecution( te );
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "touch execution failed.", e );
					response( response, "{failure}" );
				}
			} else if ( operation.toLowerCase().equals( "fetch" ) ) {
				String product = request.getParameter( "product" );
				String urlstr = request.getParameter( "urls" );
				String[] urls = urlstr.split( "~" );
				try {
					for ( String url : urls ) {
						log.info( "Fetch url:" + url ); 
						String[] arr = url.split( "\\/" );
						String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
						if ( !content.startsWith( "<!" ) ) {
							content = content.substring( content.indexOf( "<" ) );
						}
						//.endsWith( "njunit.xml" )
						if ( url.toLowerCase().contains( "njunit" ) ) {
							String exectimestr = request.getParameter( "exectime" );
							if ( exectimestr == null ) {
								exectimestr = String.valueOf( System.currentTimeMillis() );
							}
							String sw = request.getParameter( "sw" );
							String from = request.getParameter("from");
							String type = request.getParameter( "type" );
							Timestamp execTime = new Timestamp( Long.parseLong( exectimestr ) );
							TestExecution te = TestParser.parseTestExecution( product, content.trim() );
							te.setName( request.getParameter( "name" ) );
							te.setSw( sw );
							te.setFrom(from);
							te.setUrl( url );
							te.setExecTime( execTime );
							te.setType( type );
							try {
								StatisticManager.addExecution( te, content );
							} catch ( Exception ex ) {
								log.error( "Add execution with njunit content failed.", ex );
								StatisticManager.addExecution( te );
							}
						} else if ( url.endsWith( ".testset" ) ) {
							Testset ts = TestParser.parseTestset( product, content.trim() );
							ts.setName( arr[arr.length - 1] );
							StatisticManager.addTestset( ts );
						} else {
							log.info( "Fetch content:" + content );
						}
					}
					response( response, "{ok}" );
				} catch ( Exception e ) {
					log.error( "Fetch content failed.", e );
					response( response, "{failure}" );
				}
			}

		} else {
			response( response, "Invalid operation type:" + operation );
		}
	}

	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
	}

	private void response( HttpServletResponse response, String content ) {
		Writer w = null;
		try {
			response.setStatus( 500 );
			w = response.getWriter();
			w.write( content );
		} catch ( Exception e ) {
			log.error( "Write back to client failed.", e );
		} finally {
			IOUtils.closeQuietly( w );
		}
	}
}
