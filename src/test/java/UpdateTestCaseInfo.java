import java.io.FileReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map.Entry;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.TestCase;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.Testset;

public class UpdateTestCaseInfo {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		FileReader fr = new FileReader( "chinese_input.testset" );
		StringWriter sw = new StringWriter();
		char[] data = new char[100];
		int read = 0;
		while ( -1 != ( read = fr.read( data ) ) ) {
			sw.write( data, 0, read );
		}
		fr.close();
		String content = sw.toString();
		if ( !content.startsWith( "<!" ) ) {
			content = content.substring( content.indexOf( "<" ) );
		}
		Testset ts = TestParser.parseTestset( "aquaDS", content );
		StatisticManager.fetchCaseIdsForTestset( ts );
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "update stat_testcases set directory=?,file=?,class=?,method=? where id=?" );
			for ( Entry<String, List<TestCase>> entry : ts.getTestcases().entrySet() ) {
				for ( TestCase ca : entry.getValue() ) {
					ps.setString(1, ca.getDirectory());
					ps.setString( 2, ca.getFile() );
					ps.setString( 3, ca.getTestClass() );
					ps.setString( 4, ca.getMethod() );
					ps.setInt( 5, ca.getId() );
					ps.executeUpdate();
					ps.clearParameters();
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

}
