import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.SubExecution;
import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.TestParser;


public class TestSubExecution {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( String[] args ) throws Exception {
		fixSubIdForResults();
	}
	
	public static void addSubExecution() throws Exception{
		SubExecution se = new SubExecution();
		int execId = 465;
		String product = "aquaSS";
		se.setExecId( execId );
		String  url = "http://becim019.rnd.nokia.com:8080/job/Weekly-regression-orionDS-subset/19/artifact/test_results/njunit/Granite_njunit.xml";
		se.setUrl( url );
		String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
		if ( !content.startsWith( "<!" ) ) {
			content = content.substring( content.indexOf( "<" ) );
		}
		se.setReport( content );
		String tsName = TestParser.parseTestsetName(content);
		List<TestCase> cases = TestParser.parseTestCasesFromExecution( product, content );
		int subId = CommonUtils.parseInt( tsName.substring( tsName.indexOf( "_" ) + 1, tsName.indexOf( ".testset" ) ), 0 );
		se.setSubId( subId );
		//StatisticManager.appendSubExecution( se );
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "update stat_results set subid=? where execid=? and caseId=?" );
			for( TestCase tc:cases) {
				ps.setInt( 1, subId );
				ps.setInt( 2, execId );
				ps.setInt( 3, tc.getId() );
				ps.executeUpdate();
				ps.clearParameters();
			}
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	public static void fixSubIdForResults() throws Exception {
		SubExecution se = new SubExecution();
		int execId = 481;
		String product = "aquaDS";
		se.setExecId( execId );
		String  url = "http://becim019.rnd.nokia.com:8080/job/Weekly-regression-aquaDS-subset/92/artifact/test_results/njunit/Granite_njunit.xml";
		se.setUrl( url );
		String content = new String( CommonUtils.fetchRemote( url ), "UTF-8" );
		if ( !content.startsWith( "<!" ) ) {
			content = content.substring( content.indexOf( "<" ) );
		}
		se.setReport( content );
		String tsName = TestParser.parseTestsetName(content);
		System.out.printf( "tsName:%s",tsName );
		List<TestCase> cases = TestParser.parseTestCasesFromExecution( product, content );
		int subId = CommonUtils.parseInt( tsName.substring( tsName.lastIndexOf( "_" ) + 1, tsName.indexOf( ".testset" ) ), 0 );
		se.setSubId( subId );
		//StatisticManager.appendSubExecution( se );
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "update stat_results set subid=? where execid=? and caseId=?" );
			for( TestCase tc:cases) {
				if(tc==null)
					continue;
				ps.setInt( 1, subId );
				ps.setInt( 2, execId );
				ps.setInt( 3, tc.getId() );
				ps.executeUpdate();
				ps.clearParameters();
			}
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
}
