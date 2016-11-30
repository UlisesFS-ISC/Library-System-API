package com.librest.books;


import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import javax.ws.rs.core.MediaType;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




 

@Path("/Members")
public class Members {
	//---------------------------------------------------------------------Cors Handling Methods------------------//
	
	  @OPTIONS
	  @Path("/validateToken")
	  public Response optvalidateToken() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	  
	  @OPTIONS
	  @Path("/getMemberTable")
	  public Response optgetMemberTable() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	//---------------------------------------------------------------------Cors Handling Methods END---------------//	
	  
	  
	  
	  //Inserts a member into the database with the provided data inside the request's body
	  
	  @POST
	  @Consumes(MediaType.MULTIPART_FORM_DATA)
	  @Produces(MediaType.APPLICATION_JSON)
	  @Path("/Register")
	  public Response Register(@FormDataParam("Email") String Email,
			  @FormDataParam("FirstName") String FirstName,
			  @FormDataParam("LastName") String LastName,
			  @FormDataParam("Password") String Password,
			  @FormDataParam("Phone") String Phone,
			  @FormDataParam("Street") String Street,
			  @FormDataParam("City") String City,
			  @FormDataParam("State") String State,
			  @FormDataParam("PostalCode") String PostalCode){
		  int statusCode=400;
		  int rs=-1;
		  int emailFound=0;
		  String result;
		  DBConnection databaseInstance= new DBConnection();
		  HashMap<String,String> memberData = new HashMap<String,String>();
		  JSONObject jsonObject= new JSONObject();	
		 
		  if(Email.equals("")||FirstName.equals("")||LastName.equals("")||Password.equals("")||Phone.equals("")||Street.equals("")||City.equals("")||State.equals("")||PostalCode.equals("")) 
			  return listBook.makeCORS(Response.status(statusCode), "");
		  memberData.put("Email", Email);
		  emailFound=retrieveMember(memberData).length();  
		  
		  memberData.put("FirstName", FirstName);		  
		  memberData.put("LastName", LastName);		  
		  memberData.put("Password",Password);		  
		  memberData.put("Phone", Phone);		  
		  memberData.put("Street", Street);			  
		  memberData.put("City", City);
		  memberData.put("PostalCode", PostalCode);
		  
		 if(emailFound==0){
		  rs= databaseInstance.tableInsertion(memberData, "members");  
		 }		 
		  try {
			  	if(rs==-1){
					statusCode=401;
					jsonObject.put("msg", "Email is already in use");
				}
				if(rs>0){
					statusCode=200;
					jsonObject.put("msg", "Register succesful " + rs);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   result =""+ jsonObject;
		 
		
		return listBook.makeCORS(Response.status(statusCode), result);			  		  
	  }
	  
	  //Checks the data from the request, verifies its validity with a database query
	  //if the data is valid, a token will be created and returned in the response
	  @POST
	  @Consumes(MediaType.MULTIPART_FORM_DATA)
	  @Produces(MediaType.APPLICATION_JSON)
	  @Path("/Login")
	  public Response Login(@FormDataParam("Email") String Email,
			  @FormDataParam("Password") String Password){
		  int statusCode=400;
		  int memberID=0;
		  String memberRole="Visitor";
		  String result;
		  TokenFactory tokens= listBook.tokens;
		  JSONObject jsonObject= new JSONObject();
		  
		  HashMap<String,String> memberData = new HashMap<String,String>();
		  if(Email.equals("")) return listBook.makeCORS(Response.status(statusCode), "");
			  memberData.put("Email", Email);
		  if(Password.equals("")) return listBook.makeCORS(Response.status(statusCode), "");
			  memberData.put("Password", Password);
		  
		  jsonObject=retrieveMember(memberData);
		  if(jsonObject.length()>0){
				  try {
					memberID=jsonObject.getInt("ID");
					memberRole=jsonObject.getString("Role");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
			  
		  if(memberID==0){
				  try {
					statusCode=404;
					jsonObject.put("msg", "Member not found");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }		  
		  else  {
				  try {
					  String token= tokens.tokenProduce(memberID, memberRole);
					  statusCode=200;
					  JSONObject verifyToken =tokens.tokenConsume(token);
					  if(verifyToken.length()<1) return listBook.makeCORS(Response.status(401), ""); 
					  if(verifyToken.getInt("ID")<1)  return listBook.makeCORS(Response.status(401), ""); 
					   memberRole=verifyToken.getString("Role");
					  jsonObject.put("msg", "Logged in!");
					  jsonObject.put("token", token);
					  jsonObject.put("role", memberRole);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		   result = ""+jsonObject;
		
		return listBook.makeCORS(Response.status(statusCode), result);			  		  
	  }
	 
	  
	//---------------------------------------------------------------------Login Restricted Methods ----------------------//
	  //Returns a table with the rental information of a the members token
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  @Path("/getMemberTable")
	  public Response getMemberTable(@HeaderParam("Authorization") String token){
		  int statusCode=404;
		  JSONArray tableData= new JSONArray();
		  String result;
		  //Checks the token's status
		  JSONObject jsonObject= checkTokenIntegrity(token);
		  DBConnection databaseInstance= new DBConnection();
		  try {
			if(!jsonObject.getBoolean("isValid") && ( !jsonObject.getString("Role").equals("Client") || !jsonObject.getString("Role").equals("Admin") ))
					 return listBook.makeCORS(Response.status(401), "");
			 HashMap<String,String> memberData = new HashMap<String,String>();	
			 memberData.put("ID_Member", jsonObject.getInt("ID")+"");
			 memberData.put("Delivered", 0+"");
			 tableData=databaseInstance.tableQuery(memberData, new HashMap<>(), "rentals");
			  
			  if(tableData.length()>0){
				  statusCode=200;
				  jsonObject.put("Result", "OK");
				  jsonObject.put("TotalRecordCount", 10);
				  jsonObject.put("data", tableData);
			  }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 
		  
		   result = ""+jsonObject;
		   System.out.println(result);
		return listBook.makeCORS(Response.status(statusCode), result);			  		  
	  }
	  
	  //Receives the token from the request header, checks its validity and returns the role claim and its validity status inside a JSONobject
	  @POST
	  @Produces("application/json")
	  @Path("/validateToken")
	  public Response validateToken(@HeaderParam("Authorization") String token){
		  String result="";
		  String Role="Visitor";
		  boolean isValid=false;
		  TokenFactory tokens= listBook.tokens;
		  JSONObject jsonObject= new JSONObject();  
		  System.out.println(token);
		  JSONObject verifyToken =tokens.tokenConsume(token);
		  if(verifyToken.length()>1){
				  try {
					if(verifyToken.getInt("ID")>0){
						isValid=true;
						HashMap<String,String> memberData = new HashMap<String,String>();
						memberData.put("ID_Member", verifyToken.getInt("ID")+"");
						memberData.put("Role", verifyToken.getString("Role"));  
						if(retrieveMember(memberData).length()>0){
							Role=verifyToken.getString("Role");
						}
					  }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
			  			  
			try {
				jsonObject.put("isValid", isValid);
				jsonObject.put("Role", Role);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result=jsonObject+"";
			return listBook.makeCORS(Response.status(200), result);	
  	 }
	//---------------------------------------------------------------------Login Restricted Methods END ----------------------// 
	  
	 //Retrieves a member information inside a jsonObject, if the query didnt find any members the JSONobject will be sent as an empty one
	private JSONObject retrieveMember(HashMap<String,String> memberData){
		JSONArray jsonArray= new JSONArray();
		JSONObject jsonObj= new JSONObject();
		DBConnection databaseInstance= new DBConnection();
		jsonArray = databaseInstance.tableQuery(memberData, new HashMap<String,String>(),"members");
		
		if(jsonArray.length()>0){ 	 
			try {
				jsonObj=(JSONObject) jsonArray.get(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonObj;
	}
	
	//Method created to double-check the token by checking its validity and
	//checking it's content matching a Database Query using the member id 
	 public JSONObject checkTokenIntegrity(String token){
		  String result="";
		  String Role="Visitor";
		  int tokenID=0;
		  boolean isValid=false;
		  TokenFactory tokens= listBook.tokens;
		  JSONObject jsonObject= new JSONObject();  
		  System.out.println(token);
		  JSONObject verifyToken =tokens.tokenConsume(token);
		  //checks if the token consuming response is valid,if its valid it will validate its contents with the the database member entry
		  if(verifyToken.length()>1){
				  try {
					if(verifyToken.getInt("ID")>0){
						isValid=true;
						HashMap<String,String> memberData = new HashMap<String,String>();
						tokenID= verifyToken.getInt("ID");
						memberData.put("ID_Member", verifyToken.getInt("ID")+"");
						memberData.put("Role", verifyToken.getString("Role"));
						if(retrieveMember(memberData).length()>0){
							tokenID=verifyToken.getInt("ID");
							Role=verifyToken.getString("Role");
						}
					  }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
			  			  
			try {
				jsonObject.put("isValid", isValid);
				jsonObject.put("ID", tokenID);
				jsonObject.put("Role", Role);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsonObject;
 	 }

}