<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.text.*"%>
<%@page import="org.dom4j.*"%>
<%@page import="org.jaxen.SimpleNamespaceContext"%>
<%
	InputStream	in = null;
	try{
		in = request.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data  = new byte[1024];
		int read = 0;
		while( (read=in.read( data )) !=-1 ) {
			bos.write( data, 0, read );
		}
		String content = new String(bos.toByteArray(),"UTF-8");
		if(content==null||content.trim().equals(""))
			return;
		LogUtils.getWebLog().info( "Receive SNC Notification :"+content );
		CommonUtils.invokeRemote("http://becim010.china.nokia.com:8007/job/Granite-checkout/build?delay=0sec");
	}catch(Exception e){
		LogUtils.getWebLog().error("SNC Hook got exception:",e);
	}finally{
		CommonUtils.closeQuitely(in);
	}
	
%>