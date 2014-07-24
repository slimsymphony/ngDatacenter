package com.nokia.test.casedesign;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LDAPAuthenticator;
import com.nokia.granite.analyzer.LogUtils;

public class FileuploadAction extends HttpServlet {
	private static final long serialVersionUID = 2308787978675145923L;
	private Logger log = LogUtils.getDesignLog();
	 // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "upload";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		throw new RuntimeException("Upload not support in GET mode");
	}
	
	@Override
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		 // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
        	response(response, "Error: Form must has enctype=multipart/form-data.");
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk 
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("")
                + File.separator + UPLOAD_DIRECTORY;
         
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        String queryString = null;
        String description = "";
        int refId = 0;
        InputStream in = null;
        String fileName = null;
        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField() || item.getFieldName().equals( "file" )) {
                        fileName = new File(item.getName()).getName();
                        in = item.getInputStream();
                    }else {
                    	 String fieldName = item.getFieldName();
                    	 if(fieldName.equalsIgnoreCase( "queryString" )) {
                    		 queryString = item.getString();
                    	 }else if(fieldName.equalsIgnoreCase( "refId" )) {
                    		 refId = CommonUtils.parseInt( item.getString(), 0 );
                    	 }else if(fieldName.equalsIgnoreCase( "description" )) {
                    		 description =item.getString();
                    	 }
                    }
                }
            }
            if( in!=null && refId>0 ) {
                try {
                	Attachment attach = new Attachment();
                	attach.setDescription( description );
                	attach.setName( fileName );
                	attach.setRefId( refId );
                	int id = AttachmentManager.addAttachment( attach, in );
                	attach.setId( id );
                	LogUtils.getDesignLog().info( "New Attach uploaded.attach="+attach );
                	OperationRecord op = new OperationRecord();
                	try{
                		LDAPAuthenticator auth = ( LDAPAuthenticator ) request.getSession().getAttribute( "auth" );
                		String currUser = "";
                		if ( auth != null ) {
                			currUser = auth.getNoe();
                		}
                		op.setUser(currUser);
                		op.setOperation_type(OperationRecord.Operation.ATTACH);
                		op.setRel_caseId(refId);
                		op.setExtension( "Attachment:"+attach );
                		CaseDesignManager.addOperationRecord(op);
                	}catch(Exception ex){
                		LogUtils.getDesignLog().error("Attach Operation Record failed:"+op.toString(), ex);
                	}
                }finally {
                	CommonUtils.closeQuitely( in );
                }
            }else {
            	LogUtils.getDesignLog().error( "New Attach upload failed, invalid upload file or refId." );
            }
            //response( response, "true" );
            response.sendRedirect( "queryCases.jsp?"+queryString );
        } catch (Exception ex) {
        	response(response,"Upload failed:"+ex.getMessage() );
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
