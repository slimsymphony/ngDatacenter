import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.DataAnalyzer;


public class DataAnalyzerTest {
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		String topfailureCases = DataAnalyzer.getTopFailureCases( "aquaDS", null, null, 5, false );
		TypeToken<List<String[]>> tt = new TypeToken<List<String[]>>() {
		};
		List<String[]> xsa = CommonUtils.fromJson( topfailureCases, tt.getType() );
		System.out.println( topfailureCases );
		String topfailureGroups = DataAnalyzer.getTopFailureFeatureGroup( "aquaDS", null, null );
		System.out.println( topfailureGroups );
		String failureTrend = DataAnalyzer.getFailureTrend( "aquaDS", null, null );
		System.out.println( failureTrend );
	}
}
