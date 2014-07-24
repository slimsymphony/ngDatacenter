import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.makotogroup.joda.factory.SystemFactory;

public class JodaTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		//2013-2-20 6:49:26
		DateTime dateTime = new DateTime( 2013, 7, 11, 17, 47, 57, 1 );
		System.out.println(dateTime.getMillis());
		System.out.println( dateTime.plusDays( 30 ) );
		System.out.println( dateTime );
		System.out.println( dateTime.dayOfMonth().withMaximumValue() );
		System.out.println( dateTime.dayOfMonth().withMaximumValue().toDate() );
		System.out.println( dateTime.toString( "E MM/dd/yyyy HH:mm:ss.SSS" ) );
		// System.out.println(dateTime.toString( formatter ));
		System.out.println( SystemFactory.getClock().getDateTime() );
		LocalDate now = SystemFactory.getClock().getLocalDate();
		LocalTime nowT = SystemFactory.getClock().getLocalTime();
		System.out.println(now);
		System.out.println(nowT);
		LocalDate lastDayOfPreviousMonth = now.minusMonths( 1 ).dayOfMonth().withMaximumValue();
		System.out.println(lastDayOfPreviousMonth);
		System.out.println(new Timestamp(1365775204529L));
		System.out.println(new Timestamp(1375448192000L));
	}
}
