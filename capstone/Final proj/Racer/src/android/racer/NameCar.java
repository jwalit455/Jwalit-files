package android.racer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NameCar extends Activity {

	private EditText nameBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.choosename);
        setTitle("Name your Car.");
        Button confirm = (Button)findViewById(R.id.confirmBtn);
        nameBox = (EditText)findViewById(R.id.carNameBox);
        confirm.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("android.racer.car", new Car(nameBox.getText()));
				setResult(RESULT_OK, i);
				finish();
			}
        });
	}
	
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
