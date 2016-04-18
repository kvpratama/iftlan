package com.ift.lan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class CreditActivity extends Activity implements OnClickListener{

	ImageButton back;
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credit);
		
		back = (ImageButton)findViewById(R.id.back);
		back.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == back){
			finish();
		}
	}

}
