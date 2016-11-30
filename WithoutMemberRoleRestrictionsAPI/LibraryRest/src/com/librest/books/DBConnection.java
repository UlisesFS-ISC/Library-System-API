package com.librest.books;

import java.sql.*;

import org.json.JSONArray;
import java.util.*;

public class DBConnection {
	
	 // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/library";
	   
	   //  Database credentials
	   static final String USER = "Admin";
	   static final String PASS = "ulises";
	   private boolean isConnected=false;
	   
	   Connection conn = null;
	   
	   public DBConnection(){
		   
		   try {
			  Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			isConnected=true; 
			} catch (SQLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	   
	   public void closeConnection(){
		   if(!isConnected) return ;
		   try {
			conn.close();
			isConnected=false; 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	   }
	
	
	public int tableInsertion(HashMap<String,String> insertionValues,String table){
		   if(!isConnected) return 0;
		   int numInsertions=-1;
		   try {
				
			    StringBuilder customQuery= new StringBuilder();
			    customQuery.append("Insert into " + table + " ");
			    DBHelpers.insertionValuesConstructor(insertionValues, customQuery);
			    Statement stmt = conn.createStatement();
				numInsertions= stmt.executeUpdate(customQuery.toString());
  
			  }catch(SQLException  e){
				  e.printStackTrace();
			  }finally{
				  this.closeConnection(); 
			  }
		   		
		   return numInsertions;   
	   }
	
	
	public JSONArray tableQuery(HashMap<String, String> searchCriteria, HashMap<String, String> orderFields, String table) {
		JSONArray itemList=new JSONArray();
		if(!isConnected) return itemList;
		   ResultSet rs=null;
		   try {
			Statement stmt = conn.createStatement();	    
		    StringBuilder customQuery=new StringBuilder();
			customQuery.append("SELECT * FROM "+table);
		    if(table.equals("rentals"))
		    	 customQuery.append(" INNER JOIN books ");
		    
		    if(!searchCriteria.isEmpty()){
		    	if(table.equals("rentals"))
			    	 customQuery.append(" ON rentals.Id_Book= books.id ");
		    DBHelpers.whereClauseConstructor(searchCriteria, customQuery);
		    }
		    if(!orderFields.isEmpty())
		    DBHelpers.orderClauseConstructor(orderFields, customQuery);
		    
		    rs = stmt.executeQuery(customQuery.toString());
		    if(table.equals("books"))
		    	DBHelpers.jsonArrayBookBuilder(itemList, rs);
		    if(table.equals("members"))
		    	DBHelpers.jsonArrayMemberBuilder(itemList, rs);	
		    if(table.equals("rentals"))
		    	DBHelpers.jsonArrayRentalBuilder(itemList, rs);	
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			  this.closeConnection(); 
		  }
		   return itemList;
	} 
	
	
	public int tableUpdate(HashMap<String,String> updateValues, int entryID, String table){ //******
		   if(!isConnected) return 0;
		   int numUpdates=-1;
		   try {

				 StringBuilder customQuery=new StringBuilder();
				 customQuery.append("UPDATE "+ table +" SET ");
				 DBHelpers.updateValuesConstructor(updateValues, customQuery);
				 customQuery.append(" WHERE id = " + entryID);				 			 
				 	Statement stmt = conn.createStatement();
					numUpdates= stmt.executeUpdate(customQuery.toString());
					
			  }catch(SQLException e){
				  e.printStackTrace();
			  }finally{
				  this.closeConnection(); 
			  }
		   return numUpdates;   
		   
	   }
	
	 public int tableRemoval(String id, String table){
		   if(!isConnected) return 0;
		   int numRemovals=-1;
			  try {
				 String sql;
			      sql = "DELETE from "+table+" WHERE id = "+ id;
				Statement stmt = conn.createStatement();
				numRemovals= stmt.executeUpdate(sql); 
				this.closeConnection();
			  }catch(SQLException e){
				  e.printStackTrace();
			  }finally{
				  this.closeConnection(); 
			  }
			return numRemovals;
	   }
	 
	 /*------------------------Table Specific Actions------------------------- */
	 
	 public int bookLend(String action,int id, int MemberorRentalID){
		   if(!isConnected) return 0;
		   int numUpdates=-1;
		   Statement stmt;
			  try {
				 String sqlBooks,sqlRentals;
				 	if(action.equals("Return")){
				 		sqlBooks="UPDATE books SET Availability = Availability+1 WHERE id = " + id;
				 		sqlRentals="Update rentals SET Delivered=1, DateReturned=NOW() WHERE ID_Rental = "+ MemberorRentalID;
				 	}
				 	else{
				 		ResultSet rs=null;
				 		sqlBooks="Select * from books WHERE id = " + id;
				 		stmt = conn.createStatement();
				 		rs= stmt.executeQuery(sqlBooks); 
				 		rs.next();
						if(rs.getInt("Availability")<1) return numUpdates;
						sqlBooks="UPDATE books SET Availability = Availability-1 WHERE id = " + id;
						sqlRentals="INSERT INTO `rentals` (`ID_Rental`, `ID_Member`, `ID_Book`, `RentalDate`, `DeadLine`, `Delivered`) VALUES (NULL, "+MemberorRentalID+", "+rs.getInt("id")+" , NOW(), NOW() + INTERVAL 5 DAY, '0')";
				 	}
				 	stmt = conn.createStatement();
					numUpdates= stmt.executeUpdate(sqlBooks); 
				
					if(numUpdates>0){
						stmt = conn.createStatement();
						numUpdates= stmt.executeUpdate(sqlRentals); 
					}
				this.closeConnection();
			  }catch(SQLException e){
				  e.printStackTrace();
			  }finally{
				  this.closeConnection(); 
			  }
			return numUpdates;
	   }
	
	
}
