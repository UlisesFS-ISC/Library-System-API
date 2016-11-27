package com.librest.books;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelpers {
	
	public static final String BOOK_IMAGE_SERVER_LOCATION="http://localhost/imageServer/img/";
	public static final String BOOK_IMAGE_STRING="<img class='resized-img ' placeholder='image' src='"
							+BOOK_IMAGE_SERVER_LOCATION;
	

	//**********************************************HELPER METHODS********************************************************************	
	//**********************************************HELPER METHODS********************************************************************	 
	//**********************************************HELPER METHODS********************************************************************	  
	public static void whereClauseConstructor(HashMap<String, String> searchCriteria, StringBuilder customQuery){
		 	int i=0;
		    customQuery.append(" WHERE ");
		    for (Map.Entry<String,String> entryField: searchCriteria.entrySet()) {
				 if(i==0){ i++;
				customQuery.append(entryField.getKey()+" = \'"+ entryField.getValue()+"\' ");	
				 }
				 else
				customQuery.append(" AND "+entryField.getKey()+" = \'"+ entryField.getValue()+"\' ");	
			}
	}
	
	
	public static void orderClauseConstructor(HashMap<String, String> orderFields, StringBuilder customQuery){
		int i=0;
		customQuery.append(" ORDER BY ");
		for (Map.Entry<String,String> entryField: orderFields.entrySet()) {
			 if(i==0){ i++;
			customQuery.append(entryField.getKey()+"  "+ entryField.getValue()+" ");	
			 }
			 else
			customQuery.append(" , "+entryField.getKey()+"  "+ entryField.getValue()+" ");	
		}
	}
	
	
	public static void insertionValuesConstructor(HashMap<String, String> insertionValues, StringBuilder customQuery){
		int i=0;
		    
		    for (Map.Entry<String,String> entryField: insertionValues.entrySet()) {
				 if(i==0){ i++;
				customQuery.append("("+entryField.getKey());	
				 }
				 else
				customQuery.append(" , "+entryField.getKey());	
			}
		    i=0;
		    customQuery.append(") values ");
		    for (Map.Entry<String,String> entryField: insertionValues.entrySet()) {
				 if(i==0){ i++;
				customQuery.append("(\'"+entryField.getValue()+"\' ");	
				 }
				 else
				customQuery.append(" , \'"+entryField.getValue()+"\' ");	
			}
		    customQuery.append(")");
	}
	
	public static void updateValuesConstructor(HashMap<String, String> updateValues, StringBuilder customQuery){
		int i=0;
		    
		for (Map.Entry<String,String> entryField: updateValues.entrySet()) {
			 if(i==0){ i++;
			customQuery.append(entryField.getKey()+" = \'" +entryField.getValue() +"\' ");	
			 }
			 else
				 customQuery.append(" , "+entryField.getKey()+" = \'" +entryField.getValue() +"\' ");	
		}
		 
	}
	
	public static JSONObject jsonBookBuilder(JSONObject bookList,ResultSet rs){
		int i=1;
		try {
			while(rs.next()){	    	  
				  JSONObject innerJson = new JSONObject();
				  innerJson.put("id", rs.getInt("id"));
				  innerJson.put("Title", rs.getString("Title"));
				  innerJson.put("Author", rs.getString("Author"));
				  innerJson.put("Pages", rs.getInt("Pages"));
				  innerJson.put("Cover", rs.getString("Cover"));
				  bookList.put(""+(i++),innerJson);
			}
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return bookList;
	}
	
	public static JSONArray jsonArrayBookBuilder(JSONArray bookList,ResultSet rs){
		try {
			while(rs.next()){	    	  
				  JSONObject innerJson = new JSONObject();
				  innerJson.put("id", rs.getInt("id"));
				  innerJson.put("Title", rs.getString("Title"));
				  innerJson.put("Author", rs.getString("Author"));
				  innerJson.put("Pages", rs.getInt("Pages"));
				  innerJson.put("Cover", BOOK_IMAGE_STRING+rs.getString("Cover") +  "'>");
				  bookList.put(innerJson);
			}
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return bookList;
	}
	
}
