

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/sensorLocation")

public class sensorLocation extends HttpServlet {
	
// Collects or returns data for a parameter called "distance"
	private static final long serialVersionUID = 1L;

	private String distanceSensorValueStr = "0";
	private int distanceSensorValue = 1;
	
    public sensorLocation() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setStatus(HttpServletResponse.SC_OK);

	    // Check to see whether the client is requesting data or sending it
	    String getdata = request.getParameter("getdata");

	    // if no getdata parameter, client is sending data
	    if (getdata == null){
	    		// see if there is a parameter called "distance". If so, get its value
	    		if (request.getParameter("distance") != null) {
	    			  // parameter values always treated as strings
		  		  distanceSensorValueStr = request.getParameter("distance");
		  		  
		  		  try {
					// optional - convert string value to int (not always needed)
					distanceSensorValue = Integer
							.parseInt(distanceSensorValueStr);
		  		  	} catch (Exception e) {// TODO: handle exception	}
						// debug print
			  		System.out.println("DEBUG: Looking at distance values");
			  		System.out.println("DEBUG: Sensor Value: New: "+distanceSensorValueStr);

			  		// optional return string of data to confirm
			  		// sendJSONString(response);
			  			
			  			
		  		  }
		  	  }
	    }
	    else {  // Display current data (JSON format)
	    	   sendJSONString(response);
	    }

	}

	private void sendJSONString(HttpServletResponse response) throws IOException{
	      response.setContentType("text/plain");
	      String json = "{\"sensor\": {\"distance\": \"" + distanceSensorValueStr + "\"}}";
	      PrintWriter out = response.getWriter();
	      System.out.println("UploadLocation JSON: "+json);
	      // alter comment to send back plain text or json
	      // out.print(json);
	      out.print(distanceSensorValueStr);
	      out.close();
	
		
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}

}
