import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nokia.granite.analyzer.CommonUtils;

public class FindInvalidTestCases {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int cc = 0;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select product,qcid,count(*) from stat_testcases group by product,qcid having count(*)>1" );
			ps2 = conn.prepareStatement( "select id,caseId from stat_testcases where product=? and qcId=?" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				String product = rs.getString( 1 );
				int qcId = rs.getInt( 2 );
				int cnt = rs.getInt( 3 );
				ps2.setString( 1, product );
				ps2.setInt( 2, qcId );
				rs2 = ps2.executeQuery();
				boolean isMatch = true;
				while ( rs2.next() ) {
					int id = rs2.getInt( "id" );
					String caseId = rs2.getString( "caseId" );
					String vt = caseId.substring( caseId.indexOf( "-" )-4, caseId.indexOf( "-" ) );
					String total = vt.substring( 0, 2 );
					int ta = CommonUtils.parseInt( total, 0 );
					if(ta!=cnt) {
						System.out.println(caseId+"--"+vt+"--"+total+"--"+id);
						isMatch = false;
					}
				}
				if(!isMatch) {
					System.out.println("product:"+product+"~~qcid:"+qcId+"~~cnt:"+cnt);
					cc++;
				}
				ps2.clearParameters();
			}
			System.out.println(cc);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}

}
