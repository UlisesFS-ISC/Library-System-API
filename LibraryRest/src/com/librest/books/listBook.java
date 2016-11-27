package com.librest.books;

import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.spi.inject.ServerSide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




 
@Path("/listBooks")
public class listBook {
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

		return this.makeCORS(Response.status(200), result);		  		  
	  }

	  
	  @POST
	  @Produces("application/json")
	  @Path("/removeEntry")
	  public Response removeEntry(@FormParam("id") String id
	                     
	                     ) {
		  DBConnection dbInstance= new DBConnection();
		  JSONObject jsonObject = new JSONObject();
		  int rs=dbInstance.tableRemoval(id, "books");
		  try {
				if(rs<1)
				jsonObject.put("queryResult", "Action could not be performed " );	
				else if(rs>0)
				jsonObject.put("queryResult", "Books deleted: " + rs);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  String result =""+ jsonObject;
		  return this.makeCORS(Response.status(200), result);		  	      
	  }
	  

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
		  JSONArray jsonObject = new JSONArray();
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
		  	jsonObject = database.tableQuery(searchCriteria, orderFields,"books");
		  	
		  JSONObject superjson= new JSONObject();	
		  if(jsonObject.length() < 1)
				try {
					superjson.put("queryResult", "No entries matching the search criteria");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  
		  
		  try {
			superjson.put("Result", "OK");
			 superjson.put("TotalRecordCount", 10);
			  superjson.put("Records", jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		  String result = ""+superjson;
		
		return this.makeCORS(Response.status(200), result);			  		  
	  }
	  
	  @POST
		@Path("/newBook")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
	  	@Produces(MediaType.APPLICATION_JSON)
		public Response newBook(
				@FormDataParam("Title") String title,
				@FormDataParam("Author") String author,
				@FormDataParam("Pages") int pages,
				@FormDataParam("Desc") String desc,
				@FormDataParam("Cover") String coverName,
				@FormDataParam("file") InputStream fileInputStream,
				@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
		  String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ coverName;
		  HashMap<String,String> insertionValues = new HashMap<String,String>();
		  insertionValues.put("Author", author);
		  insertionValues.put("Title", title);
		  insertionValues.put("Cover", coverName);
		  insertionValues.put("Pages",""+ pages);
		  DBConnection database= new DBConnection();
		  JSONObject jsonObject = new JSONObject();
		  int rs= database.tableInsertion(insertionValues, "books");
			try {
				if(rs<1)
				jsonObject.put("queryResult", "Action could not be performed ");	
				else if(rs>0)
				jsonObject.put("queryResult", "Book inserted succesfully " + rs);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  String result =""+ jsonObject;
		  

			// save the file to the server
			saveFile(fileInputStream, filePath);

		  
			 return this.makeCORS(Response.status(200),result);

		}
	  
	  
	  	@POST
		@Path("/updateBook")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
	  	@Produces(MediaType.APPLICATION_JSON)
		public Response updateBook(
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
			  int rs=dbInstance.tableUpdate(updateValues, ID, "books");
				try {
					if(rs==-1)
					jsonObject.put("queryResult", "Action could not be performed: ");	
					else
					jsonObject.put("queryResult", "Updated entries: "+ rs );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  String result =""+ jsonObject;
			

			 return this.makeCORS(Response.status(200),result);

		}
	  
	  	

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
	  
	  

	  private Response makeCORS(ResponseBuilder req,String entity, String returnMethod) {
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

	  private Response makeCORS(ResponseBuilder req, String entity) {
	     return makeCORS(req,entity,"");
	  }
	  

}