import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.TestExecution;
import com.nokia.test.statistic.TestParser;
import com.nokia.test.statistic.TestResult;

public class GetBackOriResults {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select id,product,report from stat_executions where length(report) is not null order by id desc";
			ps = conn.prepareStatement( sql );
			String sql2 = "update stat_results set oriresult=? where execId=? and caseId=?";
			ps2 = conn.prepareStatement( sql2 );
			rs = ps.executeQuery();
			int execId = 0;
			Clob report = null;
			Reader reader = null;
			String product = null;
			while ( rs.next() ) {
				execId = rs.getInt( 1 );
				product = rs.getString( 2 );
				report = rs.getClob( 3 );
				if ( report != null ) {
					try {
						reader = report.getCharacterStream();
						StringWriter sw = new StringWriter();
						IOUtils.copy( reader, sw );
						handle( execId, product, sw.toString(), ps2 );
					} catch ( Exception ex ) {
						ex.printStackTrace();
					} finally {
						CommonUtils.closeQuitely( reader );
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

	private static void handle( int execId, String product, String content, PreparedStatement ps ) throws Exception {
		try {
			TestExecution te = TestParser.parseTestExecution( product, content );
			for( TestResult tr : te.getResults() ) {
				ps.setString( 1, tr.getResult() );
				ps.setInt( 2, execId );
				ps.setInt( 3, tr.getCaseId() );
				ps.executeUpdate();
				ps.clearParameters();
			}
			
		} finally {
			ps.clearParameters();
		}
	}

}
