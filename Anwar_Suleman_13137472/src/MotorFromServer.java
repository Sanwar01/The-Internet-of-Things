import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.OutputChangeEvent;
import com.phidgets.event.ServoPositionChangeEvent;
import com.phidgets.event.ServoPositionChangeListener;

public class MotorFromServer {

	/*
	 * Demo phidget code to allow a call to poll a server and request value.
	 * Returned value is used to turn motor to the required angle
	 * No validation is done. Value is straightforward text
	 */
	
	
    // time to wait before asking server for another value
    public static int secondsToPoll = 2;
    int servoNumber = 0;  
    public static String sensorServerURL = "http://localhost:8081/PhidgetServer/sensorToDB";
    public static int receivedMotorValue, lastMotorValue;
    static 	AdvancedServoPhidget servo;
    public static String sensorValue;
    
	public static final void main(String args[]) throws Exception {
		System.out.println(Phidget.getLibraryVersion());	
		servo = new AdvancedServoPhidget();
		setServoListeners();
	// main code starts here
		new MotorFromServer();
	}
	
	public MotorFromServer() throws PhidgetException {
		

		lastMotorValue=0;
		int sleepTime = 1000*secondsToPoll;
		boolean loopForever = true;
		for (; loopForever;) {
			receivedMotorValue = getMotorValue();
			System.out.println("DEBUG: Old motor value "+lastMotorValue+" new motor value "+receivedMotorValue);
			//if statement, if light is less than 500 then turn motor to 90. 
			//If light is more than 500 then turn motor to 180.
			if (receivedMotorValue != lastMotorValue) {
				// move motor
				if(receivedMotorValue<500)
				{
				moveServoTo(90);
				}
				else{
					moveServoTo(180);
				}
				lastMotorValue=receivedMotorValue;
				System.out.println("DEBUG: Moved motor to "+receivedMotorValue);
			}
			// sleep for a while
			try {Thread.sleep(sleepTime);
            } catch (Throwable t) {t.printStackTrace();
            }	
		}
		
	} 
    
    private int getMotorValue() {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?getdata=true"+"&SensorName=lightSensor";
       System.out.println("DEBUG: Requesting motor data via: "+fullURL);
        String line;
        String result = "";
        try {
           url = new URL(fullURL);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           while ((line = rd.readLine()) != null) {
              result += line;
           }
           rd.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        System.out.println("DEBUG: received from server value>"+result+"<");  
        
        //Result is stored in JSONArray, extract the object which is named 'light sensor'.
        try {
			JSONArray jsonArr = new JSONArray(result);
			for(int i = 0; i<jsonArr.length(); i++)
			{
				JSONObject jsonObj = new JSONObject();
				jsonObj = jsonArr.getJSONObject(i);
				sensorValue = jsonObj.getString("lightSensor");
			}
			
			
			System.out.println("SensorValue" + sensorValue);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        int resultInt = lastMotorValue;
        try
           { int tempInt = Integer.parseInt(sensorValue);
             resultInt = tempInt;
             System.out.println("DEBUG: Conversion to numeric OK");
           }
        catch (NumberFormatException nfe)
           { System.out.println("NumberFormatException: " + nfe.getMessage()); 
           }
        return resultInt;
      }

    
    public void moveServoTo(int position){
        // utility method to move motor to indicated position
        try {
            servo.openAny();
    		servo.waitForAttachment();
    	    servo.setEngaged(servoNumber, false);
    	    servo.setSpeedRampingOn(servoNumber, false);
    	    
    	    servo.setPosition(servoNumber, position);
    	    servo.setEngaged(servoNumber, true);

    	    servo.close();
        }
        catch (PhidgetException pe) {System.out.println("Motor error "+pe);}
    }   

    
public void inputChanged(InputChangeEvent arg0) {
        System.out.println(arg0);
    }

    public void attached(AttachEvent arg0) {
        System.out.println(arg0);
    }

    public void detached(DetachEvent arg0) {
        System.out.println(arg0);
    }

    public void error(ErrorEvent arg0) {
        System.out.println(arg0);
    }

    public void outputChanged(OutputChangeEvent arg0) {
        System.out.println(arg0);
    }
 
    public static void setServoListeners(){
    	servo.addAttachListener(new AttachListener() {
    	public void attached(AttachEvent ae) {
    		System.out.println("attachment of " + ae);
    		}
    	});

    	servo.addDetachListener(new DetachListener() {
    	public void detached(DetachEvent ae) {
    		System.out.println("detachment of " + ae);
    		}
    	});

    	servo.addErrorListener(new ErrorListener() {
    	public void error(ErrorEvent ee) {
    		System.out.println("error event for " + ee);
    		}
    	});

    	servo.addServoPositionChangeListener(new ServoPositionChangeListener()
    	{

    		public void servoPositionChanged(ServoPositionChangeEvent oe)
    	{
    	// 
    	// System.out.println(oe);
    	}
    	});

    }
    
    
    
}