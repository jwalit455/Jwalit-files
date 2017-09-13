package android.racer;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Stats extends ListActivity {
	private String[] mStringsStats = {"Stat1", "Stat2", "Stat3"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.statscreen);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStringsStats ));
        setTitle(R.string.app_name);
	}
	
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
