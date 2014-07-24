import java.io.FileWriter;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.test.statistic.StatisticManager;
import com.nokia.test.statistic.Testset;

public class RerunTestsetCreator {
	public static void main(String[] args) throws Exception {
		Testset ts = StatisticManager.createRerunTestset(343);
		FileWriter fw = null;
		try{
			fw = new FileWriter("rerun.testset");
			fw.write( ts.toTestset() );
		}finally {
			CommonUtils.closeQuitely( fw );
		}
		
	}
}
