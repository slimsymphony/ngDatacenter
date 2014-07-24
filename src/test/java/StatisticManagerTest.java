import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Map;

import org.joda.time.Duration;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.Testset;

public class StatisticManagerTest  {
	public static void main( String[] args ) throws Exception {
		//testAddTestsetFull();
		/*TestExecution te = StatisticManager.getExecutionById( 41 );
		for(TestResult tr:te.getResults()) {
			if(tr.getResult().equals( TestResult.FAIL ))
			System.out.println(tr.getMessage()+","+tr.getDetail());
		}*/
		/*List<String> fgs = StatisticManager.getFeatureGroupByExecution( 56 );
		for(String fg : fgs) {
			System.out.println(fg);
		}*/
		/*for(Product p : StatisticManager.getProducts(true)) {
			System.out.println(p);
		}
		for(Product p : StatisticManager.getProducts(false)) {
			System.out.println(p);
		}*/
		//testDuration();
		updateResult();
	}
	
	private static void updateResult() throws Exception {
		File file = new File( "Granite_njunit.xml" );
		StringWriter sw = new StringWriter();
		FileReader fr = new FileReader( file );
		char[] data = new char[100];
		int readed = 0;
		while ( ( readed = fr.read( data ) ) != -1 ) {
			sw.write( data, 0, readed );
		}
		CommonUtils.closeQuitely( fr );
		String content = sw.toString();
		if(!content.startsWith( "<!" )) {
			content = content.substring( content.indexOf( "<" ) );
		}
		TestExecution te = TestParser.parseTestExecution( "aquaDS", content.trim() );
		te.setName( "feature_test.testset" );
		te.setSw( "NG1.0rel-13w10.0" );
		te.setUrl( "http://becim019.rnd.nokia.com:8080/job/Granite-weekly-regression-F1/80/artifact/test_results/njunit/Granite_njunit.xml" );
		Timestamp ts = new Timestamp(System.currentTimeMillis() - (7*60*60*1000 + 20*60*1000) );
		te.setExecTime( ts );
		te.setType( "weekly" );
		StatisticManager.addExecution( te, content );
	}
	
	private static void testDuration() throws Exception {
		Map<TestCase,Integer> cases = StatisticManager.getTopDurationTestcases("aquaDS",20, null, null);
		for(TestCase cid:cases.keySet()) {
			Duration dur = Duration.standardSeconds( cases.get( cid ) );
			System.out.println(dur.getStandardMinutes()+ "  " +cid);
		}
		 cases = StatisticManager.getTopDurationTestcasesInTestset(1,20,null,null);
		 for(TestCase cid:cases.keySet()) {
				System.out.println(cases.get( cid )+ "  " +cid);
			}
	}
	
	private static void testAddTestsetFull() throws Exception {
		File file = new File( "feature_test_ss.testset" );
		StringWriter sw = new StringWriter();
		FileReader fr = new FileReader( file );
		char[] data = new char[100];
		int readed = 0;
		while ( ( readed = fr.read( data ) ) != -1 ) {
			sw.write( data, 0, readed );
		}
		CommonUtils.closeQuitely( fr );
		String content = sw.toString();
		if(!content.startsWith( "<!" )) {
			content = content.substring( content.indexOf( "<" ) );
		}
		Testset ts = TestParser.parseTestset( "aquaSS", content );
		ts.setName("feature_test_ss.testset");
//		Document doc = DocumentHelper.parseText( sw.toString() );
//		Element root = doc.getRootElement();
//		List<Element> cases = root.selectNodes("//testcase");
//		for( Element ele : cases ) {
//			ele.attributeValue( "name" );
//			ele.attributeValue( "feature" );
//			ele.attributeValue( "subarea" );
//			Element subele = (Element)ele.selectSingleNode( "testscript" );
//			subele.attributeValue( "directory" );
//		}
		StatisticManager.addTestset( ts );
	}

	private static void testAddTestset() {
		Testset ts = new Testset();
		ts.setName( "chinese_input" );
		ts.setProduct( "aquaDS" );
		System.out.println( ts );
		StatisticManager.addTestset( ts );
		System.out.println( ts );
	}
}
