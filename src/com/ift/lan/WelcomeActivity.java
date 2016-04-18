package com.ift.lan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class WelcomeActivity extends Activity implements OnClickListener {

	ImageButton close, news, reminder, credit;
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		close = (ImageButton) findViewById(R.id.close);
		news = (ImageButton) findViewById(R.id.todayNews);
		reminder = (ImageButton) findViewById(R.id.myReminder);
		credit = (ImageButton) findViewById(R.id.credit);
		
		close.setOnClickListener(this);
		news.setOnClickListener(this);
		reminder.setOnClickListener(this);
		credit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == close){
			finish();
		}else if(v == news){
			Intent i = new Intent(this, IFTLanActivity.class);
			startActivity(i);
		}else if(v == reminder){
			Intent i = new Intent(this, ReminderActivity.class);
			startActivity(i);
		}else if(v == credit){
			Intent i = new Intent(this, CreditActivity.class);
			startActivity(i);
		}
	}
}
