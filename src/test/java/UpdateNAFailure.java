import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.nokia.granite.analyzer.CommonUtils;

public class UpdateNAFailure {

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		File file = new File( "c:/results.txt" );
		BufferedReader br = new BufferedReader( new FileReader( file ) );
		String line = null;
		String currTc = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "update stat_results set result='NORESULT',message=? where execid=361 and caseid=(select id from stat_testcases where product='aquaDS' and caseid=?)" );
			while ( ( line = br.readLine() ) != null ) {
				line = line.trim();
				if ( line.startsWith( "<testcase" ) ) {
					currTc = line;
				} else if ( line.indexOf( "<na message" ) >= 0 ) {
					String caseId = currTc.substring( currTc.indexOf( "AUID" ), currTc.indexOf( ")" ) );
					String message = line.substring(line.indexOf( "\"" )+1, line.indexOf( "\" detail" ));
					ps.setString( 1, message );
					ps.setString( 2, caseId );
					ps.executeUpdate();
					ps.clearParameters();
				}
			}
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

}
