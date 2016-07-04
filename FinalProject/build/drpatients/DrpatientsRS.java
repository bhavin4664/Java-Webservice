package drpatients;

import java.util.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class DrpatientsRS {
    @Context 
    private ServletContext sctx;          // dependency injection
    private static DrpatientsList DrpatList; // set in populate()

    public DrpatientsRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(DrpatList, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }


    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
	checkContext();
	return DrpatList.toString();
    }

    //** utilities
    private void checkContext() {
	if (DrpatList == null) populate();
    }

    private void populate() {
		DrpatList = new DrpatientsList();
        int drIdval=0;
		String filename = "/WEB-INF/data/patients.db";
		InputStream in = sctx.getResourceAsStream(filename);
	
		String filename2 = "/WEB-INF/data/drs.db";
		InputStream in2 = sctx.getResourceAsStream(filename2);	
		// Read the data into the array of Predictions. 
		
		
		if (in  != null || in2  != null ) {
			try {
				String record=null ;
				String record2=null;
				
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2));
				
				while  ((record2 = reader2.readLine()) != null) {
					
					String[] docInfo = record2.split("!");
					String drName=docInfo[0];
					String patCount=docInfo[1];
					int counter=1;
					int attept=1;
					drIdval=drIdval+1;
					while (counter <=Integer.parseInt(patCount)) {
				  // Print the content on the console
					
						record = reader.readLine();
						String[] patInfo = record.split("!");
						String patName=patInfo[0];
						String patIns=patInfo[1];
						
						if (attept==1){
						   addDrPatient(drIdval,patCount,drName+"--",patName, patIns);
						   attept=attept+1;
						}
						else {
							drName=" ";
							addDrPatient(drIdval,patCount,drName,patName, patIns);
						}
						
						counter=counter+1;
					}
					
				}
				
			}
			catch (Exception e) { 
				throw new RuntimeException("I/O failed!"); 
			}
			
			
		}
						
    }

    // Add a new drpatients to the list.
    private int addDrPatient(int drIdval,String numPat, String drwho,String whoPat, String whatId) {
	int id = DrpatList.add(drIdval,numPat,drwho,whoPat, whatId);
	return id;
    }

   
	
	// Patient --> PLAIN document
    private String toPlain(Patient drpatient) {
	String plain = "";
	try {
	    plain = drpatient.toString();
	}
	catch(Exception e) { }
	return plain;
    }

    
    
	@GET
	 @Path("/plain/{id: \\d+}")
	 @Produces({MediaType.TEXT_PLAIN})
     public Response getPlain(@PathParam("id") int id) {
		 checkContext();
		 String drPat="";
		 //return toRequestedType(id, "application/plain");
		 String msg = null;
		 List<Patient> drpatlist = DrpatList.find(id);
		 if (drpatlist.size() == 0) {
			msg = id + " is a bad ID.\n";
			return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
		}
		else{
		 //msg= String.format("%2d: ", id) + drpatlist.getWho() + " ==> " + drpatlist.getWhat() + "\n";
		 for (int i=0; i<drpatlist.size(); i++){
			 drPat=drPat+drpatlist.get(i).toString();
			
		 }
		 
		 return Response.ok(drPat,MediaType.TEXT_PLAIN).build(); // plain
		}
    }
	
	@DELETE
	@Produces({MediaType.TEXT_PLAIN})
	@Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
		
		 checkContext();
		
		 String msg = null;
		 List<Patient> drpatlist = DrpatList.find(id);
		 
		 if (drpatlist.size() == 0) {
			msg = "There is no Doctor Paient Information with ID " + id + ". Cannot delete.\n";
			return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
		}
		else{
		 //msg= String.format("%2d: ", id) + drpatlist.getWho() + " ==> " + drpatlist.getWhat() + "\n";
		 
		 for (Patient dp: drpatlist){
			 DrpatList.getDrpatients().remove(dp);
			 msg = "Doctor Patient Informaiton  " + id + " is deleted.\n";
		 }
		 
		 // msg = "Doctor Patient Informaiton  " + id + " is deleted.\n";
		
		}
		
		 return Response.ok(msg, "text/plain").build(); // plain
		
		
		
    }
	
	//curl –-request PUT -–data "id=3&dr=JohnDr!2"  http://localhost:8080/drpatients/resourcesP/update
    
    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") String drId,@FormParam("dr") String drInfo) {
		checkContext();

		// Check that sufficient data are present to do an edit.
		String msg = null;
		//Drpatient dP=new Drpatient();
		if (drId == null && drInfo == null) 
			msg = "Neither Dr. Id nor Dr. Info is given: nothing to edit.\n";

		List<Patient> drInformation = DrpatList.find(Integer.parseInt(drId));
		if (drInformation.size() == 0){
			msg = "There is no Doctor Information with given Dr. Id " + drId + "\n";

		
		
		}
		else {
			// Update.
			
			String[] drPar = drInfo.split("!");
	    	String drName=drPar[0];
	    	String patCount=drPar[1];
			Patient dP=new Patient();
			
			for (Patient p : drInformation) {
				if (p.getIddr() == Integer.parseInt(drId)) {
				dP=p;
				break;
				}
			}	
			//dP=drInformation.get(Integer.parseInt(drId)-1); //Integer.parseInt(drId)-1
			String patCountActual=dP.getNumpat();
			
			if ( patCount.equals(patCountActual)) {
				
				
				if (drName != null) dP.setDrWho(drName);
				if (patCount != null) dP.setNumpat(patCount);
				msg = "Doctor #" + drId + " has been updated.\n";
			}
			else {
				
				msg = "Number of Patient count for a doctor Id " + drId + " does not match, Please have atleast "+patCount+ " <> "+patCountActual+" paients "+"\n";
			}
			
		}
		if (msg != null)
			return Response.status(Response.Status.BAD_REQUEST).
											   entity(msg).
											   type(MediaType.TEXT_PLAIN).
											   build();
		
		return Response.ok(msg, "text/plain").build();
    }
	
	
   
	
	
	
	//curl –-request POST -–data "id=5&movieName=UP!5"  http://localhost:8080/ranking2/create : Working
	@POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create") //id=3&dr=JohnDr!2&paients=MaySick!CC001,LoySick!CC002
    public Response create(@FormParam("id") String drId,@FormParam("dr") String drInfo,@FormParam("paients") String patInfo) {
		checkContext();
		String msg = "";
		// Require both properties to create.
		if (drId == null || drInfo == null || patInfo == null) {
			msg = "drPatientInfo is missing.\n";
			return Response.status(Response.Status.BAD_REQUEST).
											   entity(msg).
											   type(MediaType.TEXT_PLAIN).
											   build();
		}	    
		else {
    	  
    	    String[] drPart = drInfo.split("!");
	    	String drName=drPart[0];
	    	String patCount=drPart[1];
	    	int counter=1;
	    	int attept=1;
	    	int j=0;
		    while (counter <=Integer.parseInt(patCount)) {
		      // Print the content on the console
		        
		    	String[] patients = patInfo.split(",");
		    	String[] patInsurace = patients[j].split("!");
		    	String patName=patInsurace[0];
		    	String patIns=patInsurace[1];
		    	
		    	if (attept==1){
			       String drPatients=drName+patName+" ==> "+patIns;
				   msg=msg+drPatients;
				   addDrPatient(Integer.parseInt(drId),patCount,drName+"--",patName, patIns);
			       attept=attept+1;
		    	}
		    	else {
		    		drName=" ";
		    		String drPatients=drName+patName+" ==> "+patIns;
					msg=msg+drPatients;
					addDrPatient(Integer.parseInt(drId),patCount,drName,patName, patIns);
		    	}
		    	j=j+1;
		    	counter=counter+1;
		    }
    	  
		}
		msg ="New Doctor and Paients Information is Stored which is ==> "+drId+msg + "\n";
		return Response.ok(msg, "text/plain").build();
    }
	
    // Generate an HTTP error response or typed OK response.
	
    private Response toRequestedType(int id, String type) {
		List<Patient> drpatlist = DrpatList.find(id);
		DrpatientsList dplist2=new DrpatientsList() ;
		String msg =null;
		Patient drPat=new Patient();
		
		if (drpatlist == null) {
			msg = id + " is a bad ID.\n";
			return Response.status(Response.Status.BAD_REQUEST).
											   entity(msg).
											   type(MediaType.TEXT_PLAIN).
											   build();
		}
		
		else {
			
			for (int i=0; i<drpatlist.size(); i++){
			 drPat=drpatlist.get(i);
			 int id2 = dplist2.add(drPat.getIddr(),drPat.getNumpat() ,drPat.getDrWho(),drPat.getWhoPat(), drPat.getWhatId());
		 }
		 
		 return Response.ok(dplist2,type).build(); //xml
		}
		
		
		
		
			
		  
		
	}
}


