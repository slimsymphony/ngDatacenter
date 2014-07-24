import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.TestResult;
import com.nokia.test.statistic.Testset;


public class TestParserTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		File file = new File("chinese_input.testset");
		FileReader fr = new FileReader(file);
		StringWriter sw = new StringWriter();
		char[] data = new char[1000];
		int read = 0;
		/*while( (read = fr.read( data )) != -1 ) {
			sw.write( data, 0, read );
		}
		fr.close();
		Testset ts = TestParser.parseTestset("aqua_ds", sw.toString());
		System.out.println(ts);
		for(Entry<String,List<TestCase>> entry : ts.getTestcases().entrySet()) {
			System.out.println(entry.getKey()+":");
			for(TestCase cas:entry.getValue()) {
				System.out.println("\t"+cas.toString());
			}
		}*/
		fr = new FileReader("Granite_njunit.xml");
		sw = new StringWriter();
		read = 0;
		while( (read = fr.read( data )) != -1 ) {
			sw.write( data, 0, read );
		}
		fr.close();
		//Thread.sleep( 30000 );
		TestExecution te = TestParser.parseTestExecution("aquaDs", sw.toString());
		System.out.println(te.getFailCnt());
		System.out.println(te.getNoResultCnt());
		/*for(TestResult tr : te.getResults()) {
			System.out.println("\t"+tr.toString());
		}*/
	}
}
