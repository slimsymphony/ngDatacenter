package com.nokia.granite.analyzer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * Service API for scv.
 * 
 * @author Frank Wang
 * @since Oct 30, 2012
 */
public class ServiceAPI extends HttpServlet {

	private static final long serialVersionUID = -2006145283018671755L;
	private List<String> operationList = new ArrayList<String>();
	private Logger log = LogUtils.getServiceLog();

	@Override
	public void init() {
		operationList.add( "ping" );
		operationList.add( "result" );
		operationList.add( "query" );
		operationList.add( "merge" );
	}

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service( request, response );
	}

	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service( request, response );
	}

	@Override
	public void service( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		try {
			String operation = request.getParameter( "operation" );
			if ( operation != null && operationList.contains( operation ) ) {
				if ( operation.toLowerCase().equals( "ping" ) ) {
					String info = request.getParameter( "info" );
					log.info( "Got Ping info:---" + info + "---" );
					ScvInfo si = CommonUtils.fromJson( info, ScvInfo.class );
					si.refreshQcIds();
					ScvInfoManager.ping( si );
					log.info( "Successful create scv info si="+si );
					response( response, String.valueOf( si.getId() ) );
				} else if ( operation.toLowerCase().equals( "result" ) ) {
					String id = request.getParameter( "id" );
					String result = request.getParameter( "result" );
					log.info( "Got result info:---id=" + id + ",result=" + result + "---" );
					ScvInfoManager.setResult( Integer.parseInt( id ), result );
					log.info( "Successful update scv result id="+id+",result="+result );
					response( response, "{ok}" );
				} else if ( operation.toLowerCase().equals( "query" ) ) {
					response( response, "{Not Support Currently}" );
				} else if ( operation.equalsIgnoreCase( "merge" ) ) {
					String refspec = request.getParameter( "refspec" );
					if(refspec != null) {
						log.info( "Got request to merge refspec:"+ refspec );	
						ScvInfoManager.updateStatus( refspec );
					}
					else
						log.error( "No valid refspec info provided." );	
				}
			} else {
				response( response, "Invalid operation type:" + operation );
			}
		} catch ( Exception e ) {
			log.error( "Service Api Got Exception.", e );
			response( response, "Service API Error:" + e.getMessage() );
		}
	}

	private void response( HttpServletResponse response, String content ) {
		Writer w = null;
		try {
			w = response.getWriter();
			w.write( content );
		} catch ( Exception e ) {
			log.error( "Write back to client failed.", e );
		} finally {
			IOUtils.closeQuietly( w );
		}
	}
}
