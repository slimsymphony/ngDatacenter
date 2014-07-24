package com.nokia.test.casedesign;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class DownloadAttachment extends HttpServlet {
	
	private static final long serialVersionUID = 6686922997859706934L;
	
	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service(request,response);
	}
	
	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		service(request,response);
	}
	
	public void service( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		int attId = CommonUtils.parseInt( request.getParameter("attId"), 0);
		if(attId<=0) {
			LogUtils.getDesignLog().error( "Received invalid attachment Id:"+attId );
			return;
		}
		Attachment attach = AttachmentManager.getAttachmentById( attId );
		if(attach==null) {
			LogUtils.getDesignLog().error( "Can't find attachment for :"+attId );
			return;
		}
		InputStream in = null;
		OutputStream os = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + attach.getName() + "\"");
			in = AttachmentManager.getAttachmentContentById( attId );
			os = response.getOutputStream();
			IOUtils.copy( in, os );
		}catch( Exception e ) {
			LogUtils.getDesignLog().error( "Download Attachment error, attid:"+attId, e );
		}finally {
			CommonUtils.closeQuitely( os );
			CommonUtils.closeQuitely( in );
		}
	}
}
