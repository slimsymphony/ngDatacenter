import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.ScvInfo;
import com.nokia.granite.analyzer.ScvInfoManager;


public class ScvCleaner {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( "select * from SCV_RECORDS where changeId in ( select changeId from SCV_RECORDS group by changeId having count(*)>1)" );
			rs = ps.executeQuery();
			ScvInfo si = null;
			Map<String,ScvInfo> map = new HashMap<String,ScvInfo>();
			while(rs.next()) {
				si = ScvInfoManager.extractScv( rs );
				if(map.containsKey( si.getChangeId() )) {
					if(map.get( si.getChangeId() ).getGerritId()<si.getGerritId())
						map.put( si.getChangeId(), si );
				}else
					map.put( si.getChangeId(), si );
			}
			
			for(ScvInfo siv : map.values()) {
				System.out.println("Handling "+siv);
				ScvInfoManager.handleRetriger(conn, siv);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
				
		
	}

}
