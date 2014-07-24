import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.qc.QcHelper;


public class SyncQcInfo {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		QcHelper helper = new QcHelper("", "",null, null,"Aqua DS");
		try {
			String ret = helper.auth();
			if(ret==null)
				throw new RuntimeException("Auth failed.");
			conn = CommonUtils.getConnection();
			//conn.setAutoCommit( false );
			ps = conn.prepareStatement( "select * from STAT_TESTCASES" );
			ps2 = conn.prepareStatement( "update STAT_TESTCASES set qcIdentifier=? where qcid=?" );
			rs = ps.executeQuery();
			while(rs.next()) {
				int id = rs.getInt( "id" );
				int qcId = rs.getInt( "qcId" );
				int qcIdentifier = 0;
				try {
					qcIdentifier = helper.queryTestIdentidier(qcId);
				}catch(Exception e) {
					e.printStackTrace();
					continue;
				}
				System.out.println("id:"+id+",qcid="+qcId+",qcIdentifier="+qcIdentifier);
				ps2.setInt( 1, qcIdentifier );
				ps2.setInt( 2, qcId );
				ps2.executeUpdate();
				ps2.clearParameters();
			}
			//conn.commit();
		}catch(Exception e) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
	}

}
