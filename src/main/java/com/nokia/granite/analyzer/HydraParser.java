package com.nokia.granite.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.jaxen.SimpleNamespaceContext;

public class HydraParser {

	public static void main( String[] args )throws Exception {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification xmlns=\"http://hydra.nokia.com/schema/rest\" href=\"http://behdr001.china.nokia.com/sites/e19a12e5-691a-4e50-9f95-2ef6d7d83f54\"><resource>https://behdr001.china.nokia.com/services/d8d74bff-272c-426e-86dd-c1027995445d</resource><event>release-updated</event><site>Beijing</site><at>2013-02-11T14:10:42+00:00</at><attribute><name>release-name</name><value>NG1.0swu-130701</value></attribute><attribute><name>service-name</name><value>S40_eVo_sw_updates</value></attribute><attribute><name>release-state</name><value>Ready</value></attribute><attribute><name>service-uri</name><value>http://behdr001.china.nokia.com/services/d8d74bff-272c-426e-86dd-c1027995445d</value></attribute><attribute><name>release-uri</name><value>http://behdr001.china.nokia.com/releases/b51c409a-5e5c-4286-9b21-e5ad43dca295</value></attribute></notification>";
		parse(content);
		//parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><notification xmlns=\"http://hydra.nokia.com/schema/rest\" href=\"http://sahdrqa01.europe.nokia.com/sites/41a02a92-41fa-4d8d-8b9e-1d1561c97cc0\">  <resource>https://sahdrqa01.europe.nokia.com/services/822d0683-0d89-4ff8-8420-3ac093daeb9a</resource>  <event>release-created</event>  <site>Salo-QA</site>  <at>2012-11-22T07:41:24+00:00</at>  <attribute>    <name>release-name</name>    <value>diibadaaba</value>  </attribute>  <attribute>    <name>service-name</name>    <value>FSS_HYDRA_TEAM_ONLY</value>  </attribute>  <attribute>    <name>service-uri</name>    <value>http://sahdrqa01.europe.nokia.com/services/822d0683-0d89-4ff8-8420-3ac093daeb9a</value>  </attribute>  <attribute>    <name>release-state</name>    <value>Unpublished</value>  </attribute>  <attribute>    <name>release-uri</name>    <value>http://sahdrqa01.europe.nokia.com/releases/549dbf10-29f9-42ef-a033-c67291a50e12</value>  </attribute></notification>");
	}
	
	@SuppressWarnings( "unchecked" )
	public static void parse(String content ) throws Exception {
		/*ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data  = new byte[1024];
		int read = 0;
		while( (read=in.read( data )) !=-1 ) {
			bos.write( data, 0, read );
		}
		String content = new String(bos.toByteArray(),"UTF-8");*/
		//LogUtils.getServiceLog().info( "Receive Notification :"+content );
		Document doc = DocumentHelper.parseText( content );
		Element root = doc.getRootElement();
		if(root.getName().equals( "notification" )) {
			Map<String,String> map = new HashMap<String,String>();
			map.put( "hydra", "http://hydra.nokia.com/schema/rest" );
			XPath xp = root.createXPath(  "hydra:event" );
			xp.setNamespaceContext( new SimpleNamespaceContext( map ) );
			Element ele = (Element)xp.selectSingleNode( root );
			//Element ele = (Element)root.element( "event" );
			if( ele!=null && "release-updated".equalsIgnoreCase(ele.getStringValue())){
				XPath xp2 = root.createXPath( "hydra:attribute" );//[name='release-state']
				xp2.setNamespaceContext( new SimpleNamespaceContext( map ) );
				List<Element> els = xp2.selectNodes( root );
				for(Element ele2 : els) {
					if("release-state".equalsIgnoreCase(ele2.element( "name" ).getStringValue())) {
						if("Ready".equalsIgnoreCase(ele2.element( "value" ).getStringValue())) {
							LogUtils.getWebLog().info( "Start hydra job!" );
							//CommonUtils.invokeRemote( "http://becim010:8007/view/Hydra/job/Hydra-daily-release-getter_ds_target_F1/build?delay=0sec" );
							//CommonUtils.invokeRemote( "http://becim010:8007/view/Hydra/job/Hydra-daily-release-getter_ss_target_F1/build?delay=0sec" );
						}else {
							LogUtils.getWebLog().error( "Release state not fulfill:"+ele2.element( "value" ).getStringValue() );
						}
						break;
					}
				}
				
			}else{
				LogUtils.getWebLog().error("Not a  release-created event");
			}
		}
	}

}
