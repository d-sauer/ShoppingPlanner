package troskovnik.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Main extends Activity {

	private static final int SHOPING_CREATE = 0;
	private static final int SHOPING_EDIT = 1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentBasedOnLayout();		
	}

	private void setContentBasedOnLayout(){
		WindowManager winMan = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		if (winMan != null)		{
			int orientation = winMan.getDefaultDisplay().getOrientation();
			if (orientation == 0) {
				// Portrait
				setContentView(R.layout.main);
			}
			else if (orientation == 1) {
				// Landscape
				setContentView(R.layout.main_landscape);
			}
		}
	}

	public void newShoping(View v) {
		Intent i = new Intent(this, Shoping.class);
		startActivity(i);
	}

	public void lastShoping(View v) {
		Intent i = new Intent(this, Shoping.class);
		i.putExtra("LAST_SHOPING", true);
		startActivity(i);
	}

	public void shopingList(View v) {
		Intent i = new Intent(this, ListShoping.class);
		startActivity(i);
	}

	public void artikli(View v) {
		Intent i = new Intent(this, Artikli.class);
		startActivity(i);
	}
}