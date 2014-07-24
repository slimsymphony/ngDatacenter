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
		LogUtils.getWebLog().info( "Receive Notification :"+content );
		Document doc = DocumentHelper.parseText( content );
		Element root = doc.getRootElement();
		if(root.getName().equals( "notification" )) {
			Map<String,String> map = new HashMap<String,String>();
			map.put( "hydra", "http://hydra.nokia.com/schema/rest" );
			XPath xp = root.createXPath( "hydra:event" );
			xp.setNamespaceContext( new SimpleNamespaceContext( map ) );
			Element ele = (Element)xp.selectSingleNode( root );
			//Element ele = (Element)root.element( "event" );
			//"release-created".equalsIgnoreCase(ele.getStringValue()) || 
			if( ele!=null && "release-updated".equalsIgnoreCase(ele.getStringValue())){
				XPath xp2 = root.createXPath( "hydra:attribute" );//[name='release-state']
				xp2.setNamespaceContext( new SimpleNamespaceContext( map ) );
				List<Element> els = xp2.selectNodes( root );
				for(Element ele2 : els) {
					if("release-state".equalsIgnoreCase(ele2.element( "name" ).getStringValue())) {
						if("Ready".equalsIgnoreCase(ele2.element( "value" ).getStringValue())) {
							LogUtils.getWebLog().info( "Start weekly hydra job!" );
							CommonUtils.invokeRemote("http://becim019.rnd.nokia.com:8080/job/Hydra-weekly-release-getter-orionDS/build?delay=0sec");
							CommonUtils.invokeRemote("http://becim019.rnd.nokia.com:8080/job/Hydra-weekly-release-getter-orionSS/build?delay=0sec");
						}else {
							LogUtils.getWebLog().error( "Release state not fulfill:"+ele2.element( "value" ).getStringValue() );
						}
						break;
					}
				}
				
			}else{
				LogUtils.getWebLog().error("Not a  release-created event");
			}
		} else {
			LogUtils.getWebLog().error("Not a notification event");
		}
	}catch(Exception e){
		LogUtils.getWebLog().error("hydraMonitor got exception:",e);
	}finally{
		CommonUtils.closeQuitely(in);
	}
	
%>