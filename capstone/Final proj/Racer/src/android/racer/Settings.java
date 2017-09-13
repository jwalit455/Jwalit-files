package android.racer;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Settings extends ListActivity {
	private String[] mStringsSettings = {"Setting1","Setting2","Setting3","Setting4"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.settingscreen);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStringsSettings));
        setTitle(R.string.settingsBtnStr);
	}
	
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
