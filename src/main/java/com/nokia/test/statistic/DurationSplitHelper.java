package com.nokia.test.statistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class DurationSplitHelper {
	final public static int AVG_DURATION = 150;
	public static List<Testset> split( Testset oriTs, int mod ) {
		List<Testset> result = new ArrayList<Testset>();
		if( mod < 0 )
			throw new RuntimeException("Split Mod is invalid:"+mod);
		if(mod==1) {
			result.add( oriTs );
			return result;
		}
		
		String oriTsName = oriTs.getName();
		List<Integer> dcounter = new ArrayList<Integer>();
		for ( int i = 0; i < mod; i++ ) {
			dcounter.add( 0 );
			Testset subTs = new Testset();
			String subName = oriTsName; 
			if(oriTsName.indexOf( "." )>0) {
				subName = oriTsName.substring( 0, oriTsName.lastIndexOf( "." ) ) + "_" + i + oriTsName.substring( oriTsName.lastIndexOf( "." ) );
			}else {
				subName = oriTsName+"_"+i;
			}
			subTs.setName( subName );
			result.add( subTs );
		}
		Connection conn = null;
		PreparedStatement ps = null;
		int noDurationCnt = 0;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select avg(duration) from stat_results where caseId=?";
			ps = conn.prepareStatement( sql );
			for ( Map.Entry<String, List<TestCase>> entry : oriTs.getTestcases().entrySet() ) {
				for ( TestCase tc : entry.getValue() ) {
					int duration = StatisticManager.getCaseAvgDuration( ps, tc.getId() );
					if(duration==0) {
						duration = AVG_DURATION;
						noDurationCnt ++;
					}
					int idx = getMinIdx(dcounter);
					dcounter.set( idx, dcounter.get( idx )+duration );
					result.get( idx ).addTestCase( tc.getFeatureGroup(), tc );
					LogUtils.getStatLog().debug( "Add TestCase[" + tc.getCaseName() + "," + tc.getFeatureGroup() + "] into subSet[" + idx + "]" );
				}
			}
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "DurationSplitHelper split failed.mode=" + mod + ",oriTs=" + oriTs, e );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		LogUtils.getStatLog().info( "Found ["+noDurationCnt+"] no history testcases, current default average duration for one case is:"+AVG_DURATION );
		for(int i=0;i<mod;i++) {
			LogUtils.getStatLog().info( "SubTestset["+i+"] contains [" + result.get( i ).getTestCaseCount()+"] testcases. And estimate duration is ["+dcounter.get( i )+"] seconds, about "+Duration.standardSeconds( dcounter.get( i ) ).toStandardHours() +" Hours" );
		}
		return result;
	}

	private static int getMinIdx(List<Integer> dcounter) {
		int tmp = dcounter.get( 0 );
		int idx = 0;
		for(int i=0; i<dcounter.size(); i++) {
			if(dcounter.get(i)<tmp) {
				idx = i;
			}
		}
		return idx;
	}
}
