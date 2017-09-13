package android.racer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;

public class WatchRace extends Activity implements OnClickListener {

	private static final int RACE_SETTINGS = 0;
	private static final int RACE_STATS = 1;
	private String TAG = "WatchRace";

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.racewatch);
        setTitle(R.string.watchBtnStr);
        Button setts = (Button)findViewById(R.id.settingsBtn);
        Button stats = (Button)findViewById(R.id.statsBtn);
        
        setts.setOnClickListener(this);
        stats.setOnClickListener(this);
	}
	
	public void onClick(View v) {
    	String id = (String)v.getTag();
    	Log.v(TAG , "onClick: tag is '" + id + "'");
    	if (id.equalsIgnoreCase("settings")) {
            Intent i = new Intent(this, Settings.class);
        	Log.v(TAG , "onClick: starting settings activity");
            startActivityForResult(i, RACE_SETTINGS);
    		
    	} else if (id.equalsIgnoreCase("stats")) {
            Intent i = new Intent(this, Stats.class);
        	Log.v(TAG , "onClick: starting stats activity");
            startActivityForResult(i, RACE_STATS);
    	} else {
        	Log.wtf(TAG , "onClick: Unknown button clicked with tag '" + id + "'");
    		setResult(RESULT_CANCELED);
    		finish();
    	}
	}
	
	public void onActivityResult(int request, int result, Intent data) {
    	Log.v(TAG , "onActivityResult: Finished activity with result '" + result + "'");
	}
	
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

}
