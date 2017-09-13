package android.racer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ViewRace extends ListActivity implements OnClickListener {

	private static final int RACE_JOIN = 0;
	
	private Race currRace;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.raceview);
    	
        //Get the race selected.
    	currRace = (savedInstanceState == null) ? null :
            (Race) savedInstanceState.getSerializable("android.racer.raceChosen");
        if (currRace == null) {
            Bundle extras = getIntent().getExtras();
            currRace = extras != null ? (Race)extras.getSerializable("android.racer.raceChosen")
                                    : null;
        }
    	
        if (currRace != null) // If there was no selected race
		    setListAdapter(new ArrayAdapter<Player>(this,
		            android.R.layout.simple_list_item_1, currRace.getPlayers()));
        setTitle(R.string.app_name);
        
        Button watchBtn = (Button) findViewById(R.id.watchBtn);
        Button joinBtn = (Button) findViewById(R.id.joinBtn);
        watchBtn.setOnClickListener(this);
        joinBtn.setOnClickListener(this);
        
	}

    public void onClick(View v) {
    	String id = (String)v.getTag();
    	if (id.equalsIgnoreCase("watch")) {
            Intent i = new Intent(this, WatchRace.class);
            // Give race to watch
            i.putExtra("android.racer.raceChosen", currRace);
            startActivity(i);
    	
    	} else if (id.equalsIgnoreCase("join")) {
    		for (int i = 0; i < currRace.getPlayers().length; i++) {
    			if (currRace.getPlayers()[i].getName().equals(Player.Null.getName())) {
    				//There's a null spot!
    	            Intent in = new Intent(this, JoinRace.class);
    	            //give the race to join:
    	            in.putExtra("android.racer.raceChosen", currRace);
    	            startActivityForResult(in, RACE_JOIN);
    	            return;
    			}
    		}
    		//We have no spots empty. D=
    		Toast.makeText(this, "Sorry, but the race is full!", Toast.LENGTH_LONG).show();
    	}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RACE_JOIN) {
        	if (resultCode == RESULT_OK) {
        		//Add to database and refresh.
        		//For now, though, just add it to the displayed list.
        		Bundle b = intent.getExtras();
        		Player p = (Player)b.getSerializable("android.racer.player");
        		p.setCar((Car)b.getSerializable("android.racer.car"));
        		Player[] currPlayers = currRace.getPlayers();
        		//TODO Make so it they can click on a spot to register to.
        		for (int i = 0; i < currPlayers.length; i++) {
        			if (currPlayers[i].getName().equals(Player.Null.getName())) {
        				//Add here
        				currPlayers[i] = p;
        				break;
        			}
        		}
                setListAdapter(new ArrayAdapter<Player>(this,
                        android.R.layout.simple_list_item_1, currPlayers));
                //Toast.makeText(this, "Race array has been updated -- Name = " + raceName, Toast.LENGTH_LONG).show();
                //Go to the waiting screen
                Intent i = new Intent(this, PlayRace.class);
                i.putExtra("android.racer.race", currRace);
                startActivity(i); // actually race!  =D
        	}
        }
        // Do nothing, currently.
    }
    

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Do nothing for now
    }
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.putExtra("android.racer.race", currRace);
		setResult(RESULT_OK, i);
		finish();
	}
    	
}
