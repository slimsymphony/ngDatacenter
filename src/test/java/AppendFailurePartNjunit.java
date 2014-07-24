import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.SubExecution;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;


public class AppendFailurePartNjunit {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		File file = new File("c:/Temp/Granite_njunit.xml");
		FileInputStream fin = new FileInputStream(file);
		byte[] data = new byte[(int)file.length()];
		fin.read(data);
		fin.close();
		String tsName = "feature_test.testset";
		String content = new String(data);
		TestExecution te = TestParser.parseTestExecution( "aquaDS", content );
		te.setName( "Granite-weekly-regression-F1~108" ); 
		SubExecution se = new SubExecution();
		int subId = 1;
		se.setReport( content );
		se.setSubId( subId );
		se.setUrl("");
		StatisticManager.appendExecutionResults( te, se );
	}

}
