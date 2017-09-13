package android.racer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

public class PlayRace extends Activity implements SensorEventListener {
    
	float diff = 0;
	
	private final String HOST = "152.8.73.209";
	private final int PORT = 8080;
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.racewatch);
    	Bundle b = getIntent().getExtras();
    	Race r = (Race) b.getSerializable("android.racer.race");
        SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        try {
        	sm.registerListener(this, sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_GAME);
        } catch (Exception e) {
        	Toast.makeText(this, "You don't have an accelerometer sensor!", Toast.LENGTH_LONG);
        }
        if (r.getDefaultOrient() == Race.ORIENT_LANDSCAPE) {
        	diff = 9.8F;
        }
    }

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// We don't care if accuracy changes here, so we ignore it. =)
		
	}
	
	char newNS = '1', newEW = '0';
	private Socket kkSocket;
	private PrintWriter out;
	private BufferedReader in;

	public void onSensorChanged(SensorEvent event) {
		char oldNS = newNS, oldEW = newEW;
		
		if (event.values[0] + diff < -1.5) { //x is negative, turn EAST
        	 newEW = 'E';
         } else if (event.values[0] + diff > 1.5) { // Positive, turn west
        	 newEW = 'W';
         } else { // Stay straight
        	 newEW = '0';
         }
         if (event.values[2] - 1.5 < -1.5) { //z is negative, go BACKWARD
        	 newNS = 'S';
         } else if (event.values[2] - 1.5 > 1.5) { // Positive, go FORWARD
        	 newNS = 'N';
         } else { // Don't move
        	 newNS = '1';
         }
         if (oldNS != newNS) {
        	 sendToServer(String.valueOf(newNS));
        	 
         }
         if (oldEW != newEW) {
        	 sendToServer(String.valueOf(newEW));
         }		
	}
	protected void sendToServer(String string) {
		// create connections
		if (!initConnection())
			return;
		// TODO Auto-generated method stub
        String fromUser = string;
        if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                //input.append("Client: " + fromUser + '\n');
                out.println(fromUser);
        }
	}
	
	public static final int TYPE1 = 0;
	
	protected String getFromServer(int type) throws IOException {
		// TODO Auto-generated method stub
		if (in == null)
			return "";
		switch (type) {
		case TYPE1:
			sendToServer("Type 1");
		}
		String fromServer = in.readLine();
        closeConnection();
        return fromServer;
	}
	
	public boolean initConnection() {
        try {
            //ClientTestActivity.kkSocket = new Socket("nerketur-lappy-win7.ncat.edu", 8080);
            
        	kkSocket = new Socket(HOST, PORT);
        	//kkSocket = new Socket("152.8.86.41", 8080);
            //kkSocket = new Socket("google.com", 80);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            return true;
        } catch (UnknownHostException e) {
        	System.err.println("Don't know about host: '" + HOST + "'.");
            Toast.makeText(this, "Don't know about host: '" + HOST + "'.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //status.setText("No IO! D=");
            Toast.makeText(this, "No IO! D= Port " + PORT, Toast.LENGTH_SHORT).show();
        }
        return false;
	}
	
	public void closeConnection() {
		try {
			in.close();
			out.close();
			kkSocket.close();
		} catch (IOException e) {}
	}
	public void onBackPressed() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this); // prevents it continuing after application exits.
		finish();
	}

}
