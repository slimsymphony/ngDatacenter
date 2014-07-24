package com.nokia.test.statistic;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class SplitAPI extends HttpServlet {

	private static final long serialVersionUID = -7093644256163571574L;
	private List<String> operationList = new ArrayList<String>();
	private Logger log = LogUtils.getStatLog();

	@Override
	public void init() {
		operationList.add( "split" );
	}

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service(request, response);
	}
	
	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service(request, response);
	}
	
	protected void service( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		try {
			String operation = request.getParameter( "operation" );
			if ( operation != null && operationList.contains( operation ) ) {
				if( operation.equalsIgnoreCase( "split" ) ) {
					String url = request.getParameter( "url" );
					String product = request.getParameter( "product" );
					String tsName = request.getParameter( "name" );
					int mod = CommonUtils.parseInt( request.getParameter( "mod" ), 0 );
					log.info( "Start to split a testset for product:"+ product+", and name:" + tsName +", with mod="+mod + ", from " + url );
					byte[] data = CommonUtils.fetchRemote( url );
					String content = new String(data);
					if ( !content.startsWith( "<!" ) ) {
						content = content.substring( content.indexOf( "<" ) );
					}
					Testset ts = TestParser.parseTestset( product, content );
					ts.setName( tsName );
					StatisticManager.fetchCaseIdsForTestset(ts);
					List<Testset> tss = DurationSplitHelper.split( ts, mod );
					Map<String,InputStream> mps = new HashMap<String,InputStream>();
					for(Testset tts : tss) {
						log.info("Split subtestset ---"+tts.getTestCaseCount()+"---");
						FileWriter fw = new FileWriter(tts.getName());
						fw.write( tts.toTestset() );
						fw.close();
						mps.put( tts.getName().trim(), new ByteArrayInputStream(tts.toTestset().getBytes()) );
					}
					
					byte[] zipbin = CommonUtils.zipFiles( mps );
					log.info("Package final splitted testset into zip:" + zipbin.length);
					response.setContentLength( zipbin.length );
					//response.setContentType( "" );
					OutputStream os = null;
					try{
						os = response.getOutputStream();
						os.write( zipbin );
					}catch(Exception e) {
						response( response, "Send zip file met problem:" + CommonUtils.getErrorStack( e ) );
					}finally {
						CommonUtils.closeQuitely( os );
					}
				}
			}else {
				response( response, "Invalid operation type:" + operation );
			}
		}catch(Exception e) {
			response( response, CommonUtils.getErrorStack( e ) );
		}
	}
	
	private void response( HttpServletResponse response, String content ) {
		Writer w = null;
		try {
			response.setContentType( "text/plain" );
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
