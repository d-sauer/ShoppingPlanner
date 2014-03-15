package troskovnik.gui;

import troskovnik.sql.DBAdapter;
import troskovnik.sql.DBQuery;
import troskovnik.sql.db.DBProizvod;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Artikl extends Activity {

	private Long proizvodId = null;
	public DBAdapter dba = null;
	private EditText textNaziv = null;
	private EditText textOpis = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artikl);

		dba = new DBAdapter(this);
		dba.open();

		textNaziv = (EditText) findViewById(R.id.art_proizvod);
		textOpis = (EditText) findViewById(R.id.art_opis);

		if (getIntent() != null) {
			Bundle extras = getIntent().getExtras();
			proizvodId = extras != null ? extras.getLong(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id) : null;
			if (proizvodId==null || proizvodId <= 0) {
				proizvodId = null;
			} else {
				fillForm();
			}
		} else {
			proizvodId = null;
		}
	}

	// @Override
	// protected void onDestroy() {
	// dba.close();
	// super.onDestroy();
	// }

	private void fillForm() {
		Cursor c = DBQuery.getProizvod(proizvodId, dba);
		textNaziv.setText(c.getString(1));
		textOpis.setText(c.getString(2));
	}

	private void saveState() {
		if (proizvodId == null) {
			// insert
			proizvodId = DBQuery.insertProizvod(textNaziv.getText().toString(), textOpis.getText().toString(), dba);
		} else {
			// update
			DBQuery.updateProizvod(proizvodId, textNaziv.getText().toString(), textOpis.getText().toString(), dba);
		}
	}

	public void save(View v) {
		saveState();
		setResult(RESULT_OK, null);
		finish();
	}

	public void close(View v) {
		setResult(RESULT_CANCELED, null);
		finish();
	}

}
