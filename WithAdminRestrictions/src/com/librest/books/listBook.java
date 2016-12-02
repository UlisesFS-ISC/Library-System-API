package com.librest.books;

import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




 
@Path("/listBooks")
public class listBook {
	public static TokenFactory tokens= new TokenFactory();
	public final static String CORS_HEADER_ORIGIN="Access-Control-Allow-Origin";
	public final static String CORS_HEADER_ORIGIN_VALUE="*";
	public final static String CORS_HEADER_CREDENTIALS="Access-Control-Allow-Credentials";
	public final static String CORS_HEADER_CREDENTIALS_VALUE="true";
	public final static String CORS_HEADER_METHODS="Access-Control-Allow-Methods";
	public final static String CORS_HEADER_METHODS_VALUE="GET, POST, DELETE, PUT, OPTIONS";
	public final static String CORS_HEADER_ALLOWED_HEADERS="Access-Control-Allow-Headers";
	public final static String CORS_HEADER_ALLOWED_HEADERS_VALUE="Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With, Accept, X-Session-ID";
	// url to a external image-server
	public final static String SERVER_UPLOAD_LOCATION_FOLDER="C:\\xampp\\htdocs\\ImageServer\\img\\";
	
	//Returns all the books entries contained in the database
	  @GET
	  @Produces("application/json")
	  public Response listBooks(
			  ) throws JSONException {
		  JSONArray jsonObject= new JSONArray();
		  DBConnection dbInstance= new DBConnection();
		  jsonObject = dbInstance.tableQuery(new HashMap<String,String>(), new HashMap<String,String>(),"books");
		  JSONObject superjson= new JSONObject();
		  superjson.put("draw", "1");
		  superjson.put("recordsTotal", jsonObject.length());
		  superjson.put("recordsFiltered", jsonObject.length());
		  superjson.put("data", jsonObject);
		String result = ""+superjson;

		return listBook.makeCORS(Response.status(200), result);		  		  
	  }
	  
	  //Performs a query to the database looking for certain data contained in the request which can be sorted
	  @POST
	  @Produces("application/json")
	  @Path("/seekEntry")
	  public Response seekEntry(@FormParam("id") String id,
			  			 @FormParam("Author") String author,
	                     @FormParam("Title") String title,
	                     @FormParam("Pages") String pages,
	                     @FormParam("Orderid") String Orderid,
			  			 @FormParam("OrderAuthor") String OrderAuthor,
	                     @FormParam("OrderTitle") String OrderTitle,
	                     @FormParam("OrderPages") String OrderPages) {
		  DBConnection database= new DBConnection();
		  JSONArray tableData = new JSONArray();
		  HashMap<String,String> searchCriteria = new HashMap<String,String>();
		  HashMap<String,String> orderFields = new HashMap<String,String>();
		  
		  if(!id.equals("")) 	
			  searchCriteria.put("id", id);
		  if(!author.equals(""))
			  searchCriteria.put("Author", author);
		  if(!title.equals(""))
			  searchCriteria.put("Title", title);
		  if(!pages.equals(""))
			  searchCriteria.put("Pages", pages);
		  
		  if(!Orderid.equals(""))
			  orderFields.put("id", Orderid);
		  if(!OrderAuthor.equals(""))
			  orderFields.put("Author", OrderAuthor);
		  if(!OrderTitle.equals(""))
			  orderFields.put("Title", OrderTitle);
		  if(!OrderPages.equals(""))
			  orderFields.put("Pages", OrderPages);
		  tableData = database.tableQuery(searchCriteria, orderFields,"books");
		  	
		  JSONObject superjson= new JSONObject();	
		  if(tableData.length() < 1)
				try {
					superjson.put("queryResult", "No entries matching the search criteria");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  
		  
		  try {
			superjson.put("Result", "OK");
			superjson.put("TotalRecordCount", 10);
			superjson.put("Records", tableData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		  String result = ""+superjson;
		
		return listBook.makeCORS(Response.status(200), result);			  		  
	  }
	  
	  //----------------------------------------------------------------------CORS HANDLING METHODS------------------//
	  @OPTIONS  
	  @Path("/removeBook")
	  public Response optRemove() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	  
	  @OPTIONS  
	  @Path("/newBook")
	  public Response optnewBook() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	  
	  @OPTIONS  
	  @Path("/updateBook")
	  public Response optupdateBook() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	  
	  @OPTIONS  
	  @Path("/setAvailability")
	  public Response optsetAvailability() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	  
	  @OPTIONS  
	  @Path("/takeBook")
	  public Response opttakeBook() {
		  return listBook.makeCORS(Response.status(200), "");
	  }
	//----------------------------------------------------------------------CORS HANDLING METHODS END------------------//
	  
	//----------------------------------------------------------------------MEMBER ONLY METHODS -----------------------//  
	  //Deletes a book entry using the id provided inside the request
	  @POST
	  @Produces("application/json")
	  @Path("/removeBook")
	  public Response removeBook(
			  @HeaderParam("Authorization") String token,
			  @FormParam("ID") String id ) {
		  int rs=-1;
		  int statusCode=400;
		  if(id==null)  return listBook.makeCORS(Response.status(statusCode), "");
		  if(!tokens.isValid(token,"Admin")) return listBook.makeCORS(Response.status(401), "");
		  DBConnection dbInstance= new DBConnection();
		  JSONObject jsonObject = new JSONObject();
		  rs=dbInstance.tableRemoval(id, "books");
		  try {
				if(rs<1){
					statusCode=404;
					jsonObject.put("queryResult", "Action could not be performed " );	
				}
				else if(rs>0){
					statusCode=200;
					jsonObject.put("queryResult", "Books deleted: " + rs);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  String result =""+ jsonObject;
		  return listBook.makeCORS(Response.status(200), result);		  	      
	  }
	  

	 
	  //Inserts a new book entry using data from the request
	  @POST
		@Path("/newBook")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
	  	@Produces(MediaType.APPLICATION_JSON)
		public Response newBook(
				@HeaderParam("Authorization") String token,
				@FormDataParam("Title") String title,
				@FormDataParam("Author") String author,
				@FormDataParam("Pages") int pages,
				@FormDataParam("Desc") String desc,
				@FormDataParam("Cover") String coverName,
				@FormDataParam("file") InputStream fileInputStream,
				@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
		  int rs=-1;
		  int statusCode=400;
		  if(token.equals("") ||title.equals("") || author.equals("") || coverName.equals("") || pages<0 )  return listBook.makeCORS(Response.status(statusCode),"");
		  if(!tokens.isValid(token,"Admin")) return listBook.makeCORS(Response.status(401), "");
		  String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ coverName;
		  HashMap<String,String> insertionValues = new HashMap<String,String>();
		  insertionValues.put("Author", author);
		  insertionValues.put("Title", title);
		  insertionValues.put("Cover", coverName);
		  insertionValues.put("Pages",""+ pages);
		  DBConnection database= new DBConnection();
		  JSONObject jsonObject = new JSONObject();
		  rs= database.tableInsertion(insertionValues, "books");
			try {
				if(rs<1){
					statusCode=404;
					jsonObject.put("queryResult", "Insertion could not be performed ");	
				}
				else if(rs>0){
					statusCode=200;
					jsonObject.put("queryResult", "Book inserted succesfully " + rs);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  String result =""+ jsonObject;
		  

			// save the file to the server
			saveFile(fileInputStream, filePath);

		  
			 return listBook.makeCORS(Response.status(statusCode),result);

		}
	  
	  	//Updates a book entry's values with those inside the request
	  	@POST
		@Path("/updateBook")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
	  	@Produces(MediaType.APPLICATION_JSON)
		public Response updateBook(
				@HeaderParam("Authorization") String token,
				@FormDataParam("ID") int ID,
				@FormDataParam("Title") String title,
				@FormDataParam("Author") String author,
				@FormDataParam("Pages") int pages,
				@FormDataParam("Description") String description,
				@FormDataParam("Cover") String coverName,
				@FormDataParam("file") InputStream fileInputStream,
				@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
	  		 DBConnection dbInstance= new DBConnection();
			 JSONObject jsonObject = new JSONObject();
	  		 HashMap<String,String> updateValues = new HashMap<String,String>();
			 String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ coverName;
			 int rs=-1;
			 int statusCode=400;
			 if(ID<1)  return listBook.makeCORS(Response.status(statusCode),"");
			 if(!tokens.isValid(token,"Admin")) return listBook.makeCORS(Response.status(401), "");
			 if(!author.equals(""))
				  updateValues.put("Author", author);
			  if(!title.equals(""))
				  updateValues.put("Title", title);
			  if(!description.equals(""))
				  updateValues.put("Description", description);
			  if(!coverName.equals("")){
				  updateValues.put("Cover", coverName);
				  saveFile(fileInputStream, filePath);
			  }
			  if(pages>0)
				  updateValues.put("Pages", ""+ pages);		  	
			   	  rs=dbInstance.tableUpdate(updateValues, ID, "books");
				try {
					if(rs==-1){
						statusCode=404;
						jsonObject.put("queryResult", "Action could not be performed: ");	
					}
					else{
						statusCode=200;
						jsonObject.put("queryResult", "Updated entries: "+ rs );
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  String result =""+ jsonObject;
			

			 return listBook.makeCORS(Response.status(statusCode),result);

		}
	  	
	  	  //Changes the book's availability on it's database entry with the provided data inside the request
	  	  @POST
		  @Produces("application/json")
		  @Path("/setAvailability")
		  public Response setAvailability(
				  @HeaderParam("Authorization") String token,
				  @FormParam("ID") int ID,
				  @FormParam("numChange") int numChange){
	  		int statusCode=400;
	  		if(ID<1 || numChange<1 || token.equals("")) return listBook.makeCORS(Response.status(statusCode), "");
	  		if(!tokens.isValid(token,"Admin")) return listBook.makeCORS(Response.status(401), "");
			  DBConnection dbInstance= new DBConnection();
			  JSONObject jsonObject= new JSONObject();
			  String result="";
			  HashMap<String,String> updateValues = new HashMap<String,String>();
			  if(numChange>0 && numChange!=0)
				  updateValues.put("Availability", numChange+"");
			  int rs=dbInstance.tableUpdate(updateValues, ID, "books");
			  
			  try {
					if(rs<1){
						statusCode=404;
						jsonObject.put("queryResult", "Action could not be performed " );
					}
					else if(rs>0){
						statusCode=200;
						jsonObject.put("queryResult", "Availabilty updated: ");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
			  result =""+ jsonObject;
			  return listBook.makeCORS(Response.status(statusCode), result);	
	  	 }
	  	  
	  	  
	  	  
	  	  @POST
		  @Produces("application/json")
		  @Path("/takeBook")
		  public Response takeBook(
				  @HeaderParam("Authorization") String token,
				  @FormParam("ID") int ID,
				  @FormParam("ID_Rental") int ID_Rental,
		  		  @FormParam("Action") String Action){
	  		  int statusCode=400;
	  		  int MemberorRentalID=0;
	  		  String result="";
			  int rs=-1;
			  DBConnection dbInstance= new DBConnection();
			  JSONObject jsonObject= new JSONObject();
			  JSONObject verifyToken =tokens.tokenConsume(token);
			  if(verifyToken.length()>1){
				  try {
					if(verifyToken.getInt("ID")>0){
						if(Action.equals("Take"))
							MemberorRentalID=verifyToken.getInt("ID");
						else
							MemberorRentalID=ID_Rental;
					}
					else return listBook.makeCORS(Response.status(401), "");
					
					}catch(JSONException e){
						e.printStackTrace();
					}
			  }else  return listBook.makeCORS(Response.status(401), "");
				
			 
			  if(ID<1 || ID_Rental<0) return listBook.makeCORS(Response.status(statusCode), "");	
			  if(Action.equals("Return") || Action.equals("Take"))  
			  rs=dbInstance.bookLend(Action, ID, MemberorRentalID);
			  
			  try {
					if(rs<0){
						statusCode=404;
						jsonObject.put("queryResult", "Action could not be performed " );	
					}
					else if(rs>0){
						statusCode=200;
						jsonObject.put("queryResult", Action+" Performed: " + rs);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
			  result =""+ jsonObject;
			  return listBook.makeCORS(Response.status(statusCode), result);	
	  	 }
	  
	  //----------------------------------------------------------------------MEMBER ONLY METHODS END-------------------//	
	  	//Saves files (images) sent inside requests and stores the files in an external server  
		private void saveFile(InputStream uploadedInputStream,
				String serverLocation) {

			try {
				OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
				int read = 0;
				byte[] bytes = new byte[1024];

				
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					outpuStream.write(bytes, 0, read);
				}
				outpuStream.flush();
				outpuStream.close();
				uploadedInputStream.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	  
	  
	 //Creates Responses for CORS problems regarding requests, there is one method for general cors handling and another for specific ones which
	 // include the extra header from the request as a String returnMethod
	  public static Response makeCORS(ResponseBuilder req,String entity, String returnMethod) {
	     ResponseBuilder rb = req.header(CORS_HEADER_ORIGIN,CORS_HEADER_ORIGIN_VALUE)
					.header(CORS_HEADER_CREDENTIALS, CORS_HEADER_CREDENTIALS_VALUE)
		            .header(CORS_HEADER_METHODS, CORS_HEADER_METHODS_VALUE)
		            .header(CORS_HEADER_ALLOWED_HEADERS, CORS_HEADER_ALLOWED_HEADERS_VALUE)
		            .header("Access-Control-Max-Age", 1000)
		            .entity(entity);
	     if (!"".equals(returnMethod)) {
	        rb.header("Access-Control-Allow-Headers", returnMethod);
	     }

	     return rb.build();
	  }

	  public static Response makeCORS(ResponseBuilder req, String entity) {
	     return makeCORS(req,entity,"");
	  }
	  

}