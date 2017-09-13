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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RacerActivity extends ListActivity {

    private ArrayList<Race> mRaceObjects = new ArrayList<Race>();
    private int requestedPos = 0; 
	private final String HOST = "152.8.113.30";
	private final int PORT = 8080;
	private Socket kkSocket;
	private PrintWriter out;
	private BufferedReader in;
    private static final int RACE_CREATE=0;
    private static final int RACE_VIEW=1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setListAdapter(new ArrayAdapter<Race>(this,
                android.R.layout.simple_list_item_1, mRaceObjects.toArray(new Race[] {})));
        getListView().setTextFilterEnabled(true);
        Button create = (Button)findViewById(R.id.createRaceBtn);
        create.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startCreateAct();
			}
        	
        });
    }
    
    protected void startCreateAct() {
        Intent i = new Intent(this, CreateActivity.class);
        startActivityForResult(i, RACE_CREATE);
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if ("racerList".equalsIgnoreCase((String)l.getTag())) {
            Intent i = new Intent(this, ViewRace.class);
            i.putExtra("android.racer.raceChosen", mRaceObjects.get(position));
            requestedPos = position;
            startActivityForResult(i, RACE_VIEW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RACE_CREATE) {
        	if (resultCode == RESULT_OK) {
        		//Add to database and refresh.
        		//For now, though, just add it to the displayed list.
        		Bundle b = intent.getExtras();
        		String raceName = b.getCharSequence("android.racer.raceName").toString();
        		Race r = new Race(b.getInt("android.racer.maxRacers"));
        		r.setName(raceName);
        		r.setDefaultOrient(b.getCharSequence("android.racer.orientation").toString());
        		mRaceObjects.add(r);
        		//Send the new race to the Python server.
        		sendRace(r);
                setListAdapter(new ArrayAdapter<Race>(this,
                        android.R.layout.simple_list_item_1, mRaceObjects.toArray(new Race[] {})));
        		
        	}
        }
        if (requestCode == RACE_VIEW) {
        	if (resultCode == RESULT_OK) {
        		//Add to database and refresh.
        		//For now, though, just add it to the displayed list.
        		Bundle b = intent.getExtras();
        		Race r = (Race)b.getSerializable("android.racer.race");
        		mRaceObjects.set(requestedPos, r);
                setListAdapter(new ArrayAdapter<Race>(this,
                        android.R.layout.simple_list_item_1, mRaceObjects.toArray(new Race[] {})));
        		
        	}
        }
    }

	private void sendRace(Race r) {
		//Races are in the same format.
		
		// TODO Auto-generated method stub
		
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