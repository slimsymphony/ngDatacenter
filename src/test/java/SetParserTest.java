import java.sql.Timestamp;
import java.util.Calendar;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.Testset;


public class SetParserTest {

	/**
	 * @param args
	 */
	public static void main( String[] args )throws Exception {
		String url = "http://becim010:8007/job/Granite-weekly-regression-F1/59/artifact/granite/framework/test_results/njunit/Granite_njunit.xml";//"http://becim010:8007/job/Granite-weekly-chineseInput-F1/ws/granite/test_sets/chinese_input.testset";//"http://becim010:8007/job/Granite-weekly-regression-F1/ws/granite/test_sets/feature_test.testset";
		String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
		if(!content.startsWith( "<!" )) {
			content = content.substring( content.indexOf( "<" ) );
		}
		/*Testset te = TestParser.parseTestset( "aquaDS", content.trim() );
		te.setName( "chinese_input.testset" );
		StatisticManager.addTestset( te );*/
		TestExecution te = TestParser.parseTestExecution( "aquaDS", content );
		te.setName( "Granite-weekly-regression-F1~59" );
		te.setSw( "13w03" );
		te.setUrl( url );
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.YEAR, 2013 );
		cal.set( Calendar.MONTH, 1 );
		cal.set( Calendar.DAY_OF_MONTH, 18 );
		cal.set( Calendar.HOUR_OF_DAY, 1 );
		cal.set( Calendar.MINUTE, 12 );
		cal.set( Calendar.SECOND, 52 );
		te.setExecTime( new Timestamp(cal.getTime().getTime()) );
		te.setType( "weekly" );
		StatisticManager.addExecution( te );
	}

}
