import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokia.granite.analyzer.CommonUtils;


public class UpdateScvData {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		// TODO Auto-generated method stub
		//275
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			conn = CommonUtils.getConnection();
			//conn.setAutoCommit( false );
			ps = conn.prepareStatement( "select id,impactCaseCnt,impactScriptCnt from SCV_RECORDS order by id" );
			ps2 = conn.prepareStatement( "update SCV_RECORDS set impactCaseCnt=?, impactScriptCnt=? where id=?" );
			ps3 = conn.prepareStatement( "select caseId from scv_case_rel where scvId=?" );
			rs = ps.executeQuery();
			while(rs.next()) {
				int id = rs.getInt( "id" );
				int orimpactCaseCnt = rs.getInt("impactCaseCnt");
				int orimpactScriptCnt = rs.getInt("impactScriptCnt");
				int scriptCnt = 0;
				System.out.println("id:"+id+",orimpactCaseCnt="+orimpactCaseCnt+",orimpactScriptCnt="+orimpactScriptCnt);
				ps3.setInt( 1, id );
				rs2 = ps3.executeQuery();
				List<Integer> qcIds = new ArrayList<Integer>();
				while( rs2.next() ) {
					scriptCnt++;
					String caseId = rs2.getString( 1 );
					if(!caseId.startsWith( "(AUID" )) {
						System.err.println( "Invalid caseId :"+caseId );
						continue;
					}
						
					int qcId = CommonUtils.parseInt(caseId.substring( caseId.indexOf( "-" )+1, caseId.indexOf( ")" ) ), 0 );
					if( qcId == 0 ) {
						System.err.println( "Invalid qcId from: "+caseId );
						continue;
					}
					if(!qcIds.contains( qcId ))
						qcIds.add( qcId );
				}
				ps3.clearParameters();
				rs2.close();
				ps2.setInt( 1, qcIds.size() );
				ps2.setInt( 2, scriptCnt );
				ps2.setInt( 3, id );
				System.out.println("Id:"+id+",impactCaseCnt:"+qcIds.size()+",ImpactscriptCnt:"+scriptCnt);
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
			CommonUtils.closeQuitely( rs2 );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( ps3 );
			CommonUtils.closeQuitely( conn );
		}
	}

}
