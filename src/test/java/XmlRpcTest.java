import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class XmlRpcTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {

		WebServer webServer = new WebServer(8080);
        
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        /* Load handler definitions from a property file.
         * The property file might look like:
         *   Calculator=org.apache.xmlrpc.demo.Calculator
         *   org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.AdderImpl
         */
        phm.load(Thread.currentThread().getContextClassLoader(),
                 "MyHandlers.properties");

        /* You may also provide the handler classes directly,
         * like this:
         * phm.addHandler("Calculator",
         *     org.apache.xmlrpc.demo.Calculator.class);
         * phm.addHandler(org.apache.xmlrpc.demo.proxy.Adder.class.getName(),
         *     org.apache.xmlrpc.demo.proxy.AdderImpl.class);
         */
        xmlRpcServer.setHandlerMapping(phm);
      
        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        
		
        
     // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
        config.setEnabledForExtensions(true);  
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);

        XmlRpcClient client = new XmlRpcClient();
      
        // use Commons HttpClient as transport
        client.setTransportFactory( new XmlRpcCommonsTransportFactory(client));
        // set configuration
        client.setConfig(config);

        // make the a regular call
        Object[] params = new Object[]
            { new Integer(2), new Integer(3) };
        Integer result = (Integer) client.execute("Calculator.add", params);
        System.out.println("2 + 3 = " + result);
      
        // make a call using dynamic proxy
        ClientFactory factory = new ClientFactory(client);
        // Adder adder = (Adder) factory.newInstance(Adder.class);
        //int sum = adder.add(2, 4);
        //System.out.println("2 + 4 = " + sum);
	}

}
