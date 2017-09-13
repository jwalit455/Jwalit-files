package android.racer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateActivity extends Activity {

    private int maxRacers = 1;
    RadioGroup orient;
    EditText raceNameBox;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createrace);
        setTitle(R.string.createRaceBtnStr);
        
        orient = (RadioGroup) findViewById(R.id.orient);
        raceNameBox = (EditText) findViewById(R.id.raceNameBox);
        Spinner numRacersSpin = (Spinner) findViewById(R.id.numRacers);
        //numRacersSpin.setAdapter(new SpinAdaptList(1, 4));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numRacersSpin.setAdapter(adapter);
        numRacersSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

	    	public void onItemSelected(AdapterView<?> parent,
	    	        View view, int pos, long id) {
	    	      SetMax(pos+1);
	    	      
	    	}
	
	
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}
        	
        });

        Button createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//Check they entered a name.
				Intent i = new Intent();
				i.putExtra("android.racer.orientation", findViewById(orient.getCheckedRadioButtonId()).getTag().toString());
				i.putExtra("android.racer.raceName", raceNameBox.getText());
				//Toast.makeText(getContext(), "Name = " + raceNameBox.getText(), Toast.LENGTH_LONG).show();
				i.putExtra("android.racer.maxRacers", GetMax());
				setResult(RESULT_OK, i);
				finish();
			}
        	
        });
	}

	protected int GetMax() {
		return maxRacers;
	}

	protected void SetMax(int i) {
		maxRacers = i;
	}

}
