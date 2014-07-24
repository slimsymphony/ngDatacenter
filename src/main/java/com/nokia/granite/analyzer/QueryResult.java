package com.nokia.granite.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class QueryResult {
	private Map<String, ScvInfo> scvInfos;
	private Map<String, Map<String, String>> cases;

	
	public Map<String, ScvInfo> getScvInfos() {
		return scvInfos;
	}

	public void setScvInfos( Map<String, ScvInfo> scvInfos ) {
		this.scvInfos = scvInfos;
	}

	public Map<String, Map<String, String>> getCases() {
		return cases;
	}

	public void setCases( Map<String, Map<String, String>> cases ) {
		this.cases = cases;
	}

	public synchronized void addScvInfo( ScvInfo scv ) {
		if ( scv == null )
			return;
		if ( scvInfos == null )
			scvInfos = new HashMap<String, ScvInfo>();
		ScvInfo ori = scvInfos.get( scv.getChangeId() );
		if ( ori == null || (ori.getGerritId() < scv.getGerritId()) ) {
			scvInfos.put( scv.getChangeId(), scv );
		}
	}

	public synchronized void addCase( String type, String caseId, int scvId ) {
		if ( type == null )
			type = "Update";
		if ( caseId == null )
			return;
		if ( cases == null )
			cases = new HashMap<String, Map<String, String>>();
		if( caseId.indexOf( "(" ) < 0 || caseId.indexOf( ")" ) < 0 || caseId.indexOf( "AUID" )<0 ) {
			System.out.println( "Not a formal testcase:"+caseId+", from scv:"+scvId );
			return;
		}
		String cid = caseId.substring( caseId.indexOf( "(" ), caseId.indexOf( ")" ) + 1 );
		if ( type.equals( "Add" ) ) {
			Map<String,String> addList = cases.get( "Add" );
			if( addList == null ) {
				addList = new HashMap<String,String>();
				cases.put("Add",addList);
			}
			boolean canInsert = true;
			String removeKey = null;
			for( Entry<String,String> entry: addList.entrySet()) {
				int nscvid = Integer.parseInt(entry.getKey().split( "%" )[0]);
				String val = entry.getValue();
				val = val.substring( val.indexOf( "(" ), val.indexOf( ")" ) + 1 );
				if(val.equalsIgnoreCase( cid )) {
					if(scvId<=nscvid) {
						canInsert = false;
					} else {
						removeKey = entry.getKey();
					}
				}
			}
			if(canInsert) {
				addList.remove( removeKey );
				addList.put( scvId+"%"+cid, caseId );
			}
				
		} else {
			Map<String,String> updateList = cases.get( "Update" );
			if( updateList == null ) {
				updateList = new HashMap<String,String>();
				cases.put("Update",updateList);
			}
			boolean canInsert = true;
			String removeKey = null;
			for( Entry<String,String> entry: updateList.entrySet()) {
				int nscvid = Integer.parseInt(entry.getKey().split( "%" )[0]);
				String val = entry.getValue();
				val = val.substring( val.indexOf( "(" ), val.indexOf( ")" ) + 1 );
				if(val.equalsIgnoreCase( cid )) {
					if(scvId>nscvid) {
						canInsert = false;
					} else {
						removeKey = entry.getKey();
					}
				}
			}
			if(canInsert) {
				updateList.remove( removeKey );
				updateList.put( scvId+"%"+cid, caseId );
			}
		}
	}
}
