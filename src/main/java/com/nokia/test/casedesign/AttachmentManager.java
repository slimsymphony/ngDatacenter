package com.nokia.test.casedesign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.nokia.granite.analyzer.CommonUtils;
import com.nokia.granite.analyzer.LogUtils;

public class AttachmentManager {
	public static int addAttachment( Attachment attach, InputStream in ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "insert into des_attachments(name,description,content,refid) values(?,?,?,?)";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, attach.getName() );
			ps.setString( 2, attach.getDescription() );
			ps.setBlob( 3, in );
			ps.setInt( 4, attach.getRefId() );
			ps.executeUpdate();
			int newId = CommonUtils.getNextId( conn, "des_attachments" );
			attach.setId( newId );
			return newId;
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Add new Attachment failed.attach:"+attach, e );
		}finally {
			CommonUtils.closeQuitely( in );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return 0;
	}
	
	public static int addAttachment( Attachment attach, byte[] in ) {
		return addAttachment( attach, new ByteArrayInputStream(in));
	}
	
	public static InputStream getAttachmentContentById( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ByteArrayInputStream bins = null;
		ByteArrayOutputStream baos = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select content from des_attachments where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if(rs.next()) {
				InputStream in = null;
				try {
					in = rs.getBlob( 1 ).getBinaryStream();
					baos = new ByteArrayOutputStream();
					IOUtils.copy( in, baos );
					bins = new ByteArrayInputStream(baos.toByteArray());
				}finally {
					CommonUtils.closeQuitely( in );
					CommonUtils.closeQuitely( baos );
				}
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Attachment failed.attachid:"+id, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return bins;
	}
	
	public static List<Attachment> getAttachmentsByRefId( int refId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Attachment> attaches = new ArrayList<Attachment>();
		Attachment attach = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select id,name,description,refId from des_attachments where refId=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, refId );
			rs = ps.executeQuery();
			while(rs.next()) {
				attach = new Attachment();
				attach.setId(rs.getInt( "id" ));
				attach.setDescription( rs.getString("description") );
				attach.setRefId( rs.getInt("refId") );
				attach.setName( rs.getString( "name" ) );
				attaches.add(attach);
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Get Attachment failed.attachid:"+refId, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return attaches;
	}
	
	public static Attachment getAttachmentById( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Attachment attach = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "select id,name,description,refId from des_attachments where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			rs = ps.executeQuery();
			if(rs.next()) {
				attach = new Attachment();
				attach.setId(rs.getInt( "id" ));
				attach.setDescription( rs.getString("description") );
				attach.setRefId( rs.getInt("refId") );
				attach.setName( rs.getString( "name" ) );
			}
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Del Attachment failed.attachid:"+id, e );
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return attach;
	}
	
	public static boolean delAttachmentById( int id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "delete from des_attachments where id=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, id );
			ps.executeUpdate();
			return true;
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Del Attachment failed.attachid:"+id, e );
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return false;
	}
	
	public static boolean delAttachmentsByRefId( int refid ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = CommonUtils.getConnection();
			String sql = "delete from des_attachments where refid=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, refid );
			ps.executeUpdate();
			return true;
		}catch(Exception e) {
			LogUtils.getDesignLog().error( "Del Attachments failed.refid:"+refid, e );
		}finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return false;
	}
	
}
