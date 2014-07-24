package com.nokia.granite.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.DocumentHelper;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public final class CommonUtils {

	private static SimpleDateFormat wkFmt = new SimpleDateFormat( "yyww" );
	private static SimpleDateFormat wkFmt2 = new SimpleDateFormat( "yy'wk'ww" );
	private static SimpleDateFormat dayFmt = new SimpleDateFormat( "yywwFF" );
	private static SimpleDateFormat dayFmt2 = new SimpleDateFormat( "yy'Wk'wwFF" );

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName( com.mysql.jdbc.Driver.class.getName() );
			conn = DriverManager.getConnection( "jdbc:mysql://betstas01.china.nokia.com:3306/granite?autoReconnect=true&amp;characterEncoding=utf8", "frank", "frank" );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return conn;
	}

	public static String substring( String str, int startIdx, int endIdx ) {
		if( str == null )
			return null;
		if(startIdx<=endIdx)
			return str;
		if( str.length() <= endIdx)
			return str.substring( startIdx );
		if( str.length() <= startIdx)
			return "";
		return str.substring( startIdx, endIdx );
	}
	
	public static String getMax( String str, int maxLength) {
		if ( str != null && str.length() > maxLength )
			str = str.substring( 0, maxLength );
		return str;
	}
	
	public static String filterXmlSpecialCharactors( String content ) {
		if ( content == null )
			return "";
		return content.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ).replaceAll( "'", "&apos;" ).replaceAll( "\"", "&quot;" );
	}

	public static boolean checkXmlValidation( String content ) {
		try {
			DocumentHelper.parseText( content );
			return true;
		} catch ( Exception e ) {
			return false;
		}
	}

	public static byte[] zipFiles( Map<String, InputStream> oss ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream( baos );
		InputStream ins = null;
		try {
			for ( Entry<String, InputStream> entry : oss.entrySet() ) {
				try {
					zos.putNextEntry( new ZipEntry( entry.getKey() ) );
					byte[] buffer = new byte[1024];
					int size = 0;
					ins = entry.getValue();
					while ( ( size = ins.read( buffer, 0, buffer.length ) ) > 0 ) {
						zos.write( buffer, 0, size );
					}
				} finally {
					zos.closeEntry();
				}
			}
		} finally {
			zos.close();
		}
		return baos.toByteArray();
	}

	public synchronized static int getNextId( Connection conn, String tableName ) {
		boolean needCloseConn = ( conn != null ? false : true );
		String sql = "SHOW TABLE STATUS LIKE '" + tableName + "'";
		LogUtils.getDbLog().debug( "[getNextId]" + sql );
		PreparedStatement ps = null;
		ResultSet rs = null;
		int nextId = -1;
		try {
			if ( needCloseConn )
				conn = CommonUtils.getConnection();
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			rs.next();
			nextId = rs.getInt( "Auto_increment" );
		} catch ( SQLException e ) {
			LogUtils.getStatLog().error( "Get Next Id failed.Tablename=" + tableName, e );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			if ( needCloseConn )
				CommonUtils.closeQuitely( conn );
		}
		return nextId - 1;
	}

	public static String getWebRootPath( HttpServletRequest request, String path ) {
		return request.getSession().getServletContext().getRealPath( path );
	}

	public static String getErrorStack( Throwable t ) {
		StringWriter sw = new StringWriter();
		t.printStackTrace( new PrintWriter( sw ) );
		return sw.toString();
	}

	public static void rollback( Connection conn ) {
		try {
			conn.rollback();
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Connection Roll back failed.", e );
		}
	}

	public static void setCommit( Connection conn, boolean val ) {
		try {
			conn.setAutoCommit( val );
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Connection Set autocommit option failed. value=" + val, e );
		}
	}

	public static void closeQuitely( Connection conn ) {
		if ( conn != null ) {
			try {
				conn.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( ResultSet rs ) {
		if ( rs != null ) {
			try {
				rs.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( PreparedStatement ps ) {
		if ( ps != null ) {
			try {
				ps.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( InputStream in ) {
		if ( in != null ) {
			try {
				in.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( OutputStream out ) {
		if ( out != null ) {
			try {
				out.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( Reader reader ) {
		if ( reader != null ) {
			try {
				reader.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( Writer writer ) {
		if ( writer != null ) {
			try {
				writer.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static String getProperties( String propName, String propFile ) throws Exception {
		Properties props = new Properties();
		props.load( CommonUtils.class.getClassLoader().getResourceAsStream( "db.properties" ) );
		return ( String ) props.get( propName );
	}

	private static Gson json = new Gson();

	public static String toJson( Object o ) {
		return json.toJson( o );
	}

	public static <T> T fromJson( String jsonStr, Class<T> t ) {
		JsonReader reader = new JsonReader( new StringReader( jsonStr ) );
		reader.setLenient( true );
		return json.fromJson( reader, t );
	}

	public static <T> T fromJson( String jsonStr, Type type ) {
		JsonReader reader = new JsonReader( new StringReader( jsonStr ) );
		reader.setLenient( true );
		return json.fromJson( reader, type );
	}

	public static int getDay( Date date ) {
		/*
		 * Calendar cal = Calendar.getInstance(); cal.setTime( date ); return Integer.parseInt( String.valueOf( cal.get(
		 * Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) ) + String.valueOf(
		 * cal.get( Calendar.DAY_OF_WEEK ) ) );
		 */
		return parseInt( dayFmt.format( date ), 0 );
	}

	public static int getDay2( Date date ) {
		Calendar cal = Calendar.getInstance();
		cal.setTime( date );
		return Integer.parseInt( String.valueOf( cal.get( Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) )
				+ String.valueOf( cal.get( Calendar.DAY_OF_WEEK ) ) );

	}

	public static String getDay3( Date date ) {
		return dayFmt2.format( date );

	}

	public static int getWk( Date date ) {
		/*
		 * Calendar cal = Calendar.getInstance(); cal.setTime( date ); return Integer.parseInt( String.valueOf( cal.get(
		 * Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) ) );
		 */
		return parseInt( wkFmt.format( date ), 0 );
	}

	public static String getCurrentWk2() {
		return wkFmt2.format( new Date() );
	}

	public static String fillWk( int wk ) {
		if ( wk < 10 )
			return "0" + wk;
		else
			return String.valueOf( wk );
	}

	public static int getCurrentDay() {
		return getDay( new Date() );
	}

	public static int getCurrentWk() {
		int year = Calendar.getInstance().get( Calendar.YEAR );
		int wks = Calendar.getInstance().get( Calendar.WEEK_OF_YEAR );
		return Integer.parseInt( String.valueOf( year ).substring( 2 ) + String.valueOf( wks ) );
	}

	public static int parseInt( String str, int defaultValue ) {
		try {
			return Integer.parseInt( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}

	public static boolean parseBool( String str ) {
		return Boolean.parseBoolean( str );
	}

	public static float parseFloat( String str, float defaultValue ) {
		try {
			return Float.parseFloat( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}

	public static String notNull( String str, boolean forWeb ) {
		if ( str == null )
			return forWeb ? "&nbsp;" : "";
		return str;
	}

	public static long parseLong( String str, long defaultValue ) {
		try {
			return Long.parseLong( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}

	private static TimeZone gmtZone = TimeZone.getTimeZone( "GMT" );

	public static Timestamp local2gmt( Timestamp date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - localZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + gmtZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return new Timestamp( cal.getTimeInMillis() );
	}

	public static Timestamp gmt2local( Timestamp date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - gmtZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + localZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return new Timestamp( cal.getTimeInMillis() );
	}

	public static Date local2gmt( Date date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - localZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + gmtZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return cal.getTime();
	}

	public static Date gmt2local( Date date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - gmtZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + localZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return cal.getTime();
	}

	public static String getDateStr( Date date, String pattern ) {
		SimpleDateFormat sdf = new SimpleDateFormat( pattern );
		return sdf.format( date );
	}

	public static Date[] getIntervalFromWeek( int week ) {
		Calendar cal = Calendar.getInstance();
		int year = 2000 + Integer.parseInt( String.valueOf( week ).substring( 0, 2 ) );
		int wk = Integer.parseInt( String.valueOf( week ).substring( 2 ) );
		cal.set( Calendar.YEAR, year );
		cal.set( Calendar.WEEK_OF_YEAR, wk );
		cal.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		Date[] interval = new Date[2];
		interval[0] = cal.getTime();
		cal.set( Calendar.HOUR_OF_DAY, 23 );
		cal.set( Calendar.MINUTE, 59 );
		cal.set( Calendar.SECOND, 59 );
		cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
		interval[1] = cal.getTime();
		return interval;
	}

	public static void invokeRemote( String httpUrl ) {
		LogUtils.getServiceLog().info( "Start to invoke remote URL:" + httpUrl );
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet( httpUrl );
		HttpResponse response = null;
		try {
			response = client.execute( request );
			int statusCode = response.getStatusLine().getStatusCode();
			LogUtils.getServiceLog().info( "Retuen status Code:" + statusCode );
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Got Exception when invoke remote url:" + httpUrl, e );
		}
	}

	public static byte[] fetchRemote( String url ) {
		LogUtils.getStatLog().info( "Start to fetch remote content from :" + url );
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet( url );
		request.setHeader( "charset", "utf-8" );
		HttpResponse response = null;
		InputStream in = null;
		try {
			response = client.execute( request );
			in = response.getEntity().getContent();
			byte[] data = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int read = 0;
			while ( ( read = in.read( data ) ) != -1 ) {
				bos.write( data, 0, read );
			}
			bos.close();
			return bos.toByteArray();
		} catch ( Exception e ) {
			LogUtils.getStatLog().error( "Got Exception when invoke remote url:" + url, e );
		} finally {
			closeQuitely( in );
		}
		return null;
	}

	public static long stringToNum( String str ) {
		StringBuffer sb = new StringBuffer();
		for ( char c : str.toUpperCase().toCharArray() ) {
			sb.append( ( int ) c );
		}
		return Long.parseLong( sb.toString() );
	}

	public static String num2String( long num ) {
		StringBuffer sb = new StringBuffer();
		String ll = String.valueOf( num );
		char[] arr = ll.toCharArray();
		for ( int i = 0; i < arr.length; i += 2 ) {
			char c = ( char ) Integer.parseInt( arr[i] + "" + arr[i + 1] );
			sb.append( c );
		}
		return sb.toString();
	}

	public static TreeMap<String, Timestamp[]> parseWeeks( Date start, Date end ) {
		TreeMap<String, Timestamp[]> map = new TreeMap<String, Timestamp[]>();
		if ( !end.after( start ) )
			return map;
		Calendar cal = Calendar.getInstance();

		cal.setTime( start );
		int yStart = cal.get( Calendar.YEAR ) % 100;
		int wkStart = cal.get( Calendar.WEEK_OF_YEAR );
		cal.setTime( end );
		int yEnd = cal.get( Calendar.YEAR ) % 100;
		int wkEnd = cal.get( Calendar.WEEK_OF_YEAR );

		if ( wkStart == wkEnd ) { // in same week
			map.put( yStart + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( start.getTime() ), new Timestamp( end.getTime() ) } );
		} else {
			Date ws = start;
			cal.setTime( ws );
			if ( yStart == yEnd ) {
				while ( wkStart <= wkEnd ) {
					if ( wkStart == wkEnd ) {
						map.put( yStart + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( end.getTime() ) } );
						break;
					} else {
						cal.set( Calendar.DAY_OF_WEEK, 7 );
						cal.set( Calendar.HOUR_OF_DAY, 23 );
						cal.set( Calendar.MINUTE, 59 );
						cal.set( Calendar.SECOND, 59 );
						cal.set( Calendar.MILLISECOND, 999 );
						map.put( yStart + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( cal.getTime().getTime() ) } );
						ws = new Date( cal.getTime().getTime() + 1L );
						cal.setTime( ws );
						wkStart = cal.get( Calendar.WEEK_OF_YEAR );
						// yStart = cal.get( Calendar.YEAR );
					}
				}
			} else {
				cal.setTime( start );
				cal.set( Calendar.WEEK_OF_YEAR, 52 );
				cal.set( Calendar.DAY_OF_WEEK, 7 );
				cal.set( Calendar.HOUR_OF_DAY, 23 );
				cal.set( Calendar.MINUTE, 59 );
				cal.set( Calendar.SECOND, 59 );
				cal.set( Calendar.MILLISECOND, 999 );
				Date eoy = cal.getTime();
				cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) + 1 );
				cal.set( Calendar.WEEK_OF_YEAR, 1 );
				cal.set( Calendar.DAY_OF_WEEK, 1 );
				cal.set( Calendar.HOUR_OF_DAY, 0 );
				cal.set( Calendar.MINUTE, 0 );
				cal.set( Calendar.SECOND, 0 );
				cal.set( Calendar.MILLISECOND, 0 );
				Date bony = cal.getTime();
				cal.setTime( ws );
				while ( ws.before( eoy ) ) {
					if ( wkStart == 52 ) {
						map.put( yStart + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( eoy.getTime() ) } );
						ws = bony;
						break;
					} else {
						yStart = cal.get( Calendar.YEAR ) % 100;
						// wkStart = cal.get( Calendar.WEEK_OF_YEAR );
						cal.set( Calendar.DAY_OF_WEEK, 7 );
						cal.set( Calendar.HOUR_OF_DAY, 23 );
						cal.set( Calendar.MINUTE, 59 );
						cal.set( Calendar.SECOND, 59 );
						cal.set( Calendar.MILLISECOND, 999 );
						map.put( yStart + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( cal.getTime().getTime() ) } );
						ws = new Date( cal.getTime().getTime() + 1L );
						cal.setTime( ws );
						wkStart = cal.get( Calendar.WEEK_OF_YEAR );
					}
				}
				cal.setTime( bony );
				wkStart = cal.get( Calendar.WEEK_OF_YEAR );
				while ( ws.before( end ) ) {
					if ( wkStart == wkEnd ) {
						map.put( yEnd + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( end.getTime() ) } );
						break;
					} else {
						cal.set( Calendar.DAY_OF_WEEK, 7 );
						cal.set( Calendar.HOUR_OF_DAY, 23 );
						cal.set( Calendar.MINUTE, 59 );
						cal.set( Calendar.SECOND, 59 );
						cal.set( Calendar.MILLISECOND, 999 );
						map.put( yEnd + "w" + fillWk( wkStart ), new Timestamp[] { new Timestamp( ws.getTime() ), new Timestamp( cal.getTime().getTime() ) } );
						ws = new Date( cal.getTime().getTime() + 1L );
						cal.setTime( ws );
						wkStart = cal.get( Calendar.WEEK_OF_YEAR );
					}
				}

			}
		}

		return map;
	}

	public static Timestamp getEarliestTime( String project ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Timestamp t = null;
		try {
			conn = getConnection();
			String sql = "select min(time) from SCV_RECORDS where project=?";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, project );
			rs = ps.executeQuery();
			rs.next();
			t = rs.getTimestamp( 1 );
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "getEarliestTime for project:" + project + " failed.", e );
		} finally {
			closeQuitely( rs );
			closeQuitely( ps );
			closeQuitely( conn );
		}
		return t;
	}
}
