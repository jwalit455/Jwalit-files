package android.racer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class JoinRace extends ListActivity {
	private ArrayList<Car> mCars = new ArrayList<Car>();
	private final String HOST = "152.8.113.30";
	private final int PORT = 8080;
	private Socket kkSocket;
	private PrintWriter out;
	private BufferedReader in;
	private static final int CAR_NAME = 0;
	private static final int JOIN_RACE = 1;

	//Choose car, then name it, then join the race referred to.
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.choosecar);
        //setListAdapter(new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, mStringsCars));
    	String carUUIDs = null;
    	try {
			carUUIDs = getFromServer(CARS);
		} catch (IOException e) {
			//We retry once, then count as failed.
			try {
				carUUIDs = getFromServer(CARS);
			} catch (IOException e1) {
				//Tell the user the error
				Toast.makeText(this, "Unable to communicate with the server.", Toast.LENGTH_LONG);
			} // We don't care, otherwise
		}
    	if (carUUIDs != null) {
    		String[] carNames = carUUIDs.split("#");
    		for (int i = 0; i < carNames.length; i++) {
		        Car c = new Car(carNames[i]); // the actual available cars!
		        mCars.add(c);
    		}
    	}
    	setListAdapter(new ArrayAdapter<Car>(this,
                android.R.layout.simple_list_item_1, mCars.toArray(new Car[0])));
        setTitle(R.string.app_name);
	}
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NameCar.class);
        startActivityForResult(i, CAR_NAME);
        // TODO Have the car put in the database for race.
    }
    
	public void onActivityResult(int request, int result, Intent data) {
		if (request == CAR_NAME) {
			Intent i = new Intent();
			i.putExtra("android.racer.player", new Player("Player 1"));
			i.putExtra("android.racer.car", data.getExtras().getSerializable("android.racer.car"));
			setResult(RESULT_OK, i);
			finish();
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
	
	private static final int CARS = 0;
	
	protected String getFromServer(int type) throws IOException {
		// TODO Auto-generated method stub
		if (in == null)
			return "";
		switch (type) {
		case CARS:
			sendToServer("getCars");
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
	
}
