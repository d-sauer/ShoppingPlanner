package troskovnik.gui;

import troskovnik.sql.DBAdapter;
import troskovnik.sql.DBQuery;
import troskovnik.sql.db.DBLista;
import troskovnik.sql.db.DBPopis;
import troskovnik.sql.db.DBProizvod;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Proizvod extends Activity {

	public Long listaId = null;
	public Long popisId = null;
	public Long proizvodId = null;

	public EditText naziv = null;
	public TextView kolicina = null;
	public TextView cijena = null;
	public TextView ukupna_cijena = null;

	private int p_kolicina = 0;
	private String p_cijena = "";

	public DBAdapter dba = null;

	private static final int MENU_ACCEPT_ID = Menu.FIRST;
	private static final int MENU_CANCEL_ID = Menu.FIRST + 1;
	private static final int MENU_DELETE_FROM_POPIS_ID = Menu.FIRST + 2;
	private static final int MENU_FIND_EXISTING_ID = Menu.FIRST + 3;

	// popis
	private boolean wishlist = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proizvod);
		dba = new DBAdapter(this);
		dba.open();

		naziv = (EditText) findViewById(R.id.p_proizvod);
		kolicina = (TextView) findViewById(R.id.p_kolicina);
		cijena = (TextView) findViewById(R.id.p_cijena);
		ukupna_cijena = (TextView) findViewById(R.id.p_ukupnaCijena);

		// procitaj prosljeðene parametre, extras
		Bundle extras = getIntent().getExtras();
		wishlist = extras != null ? extras.getBoolean(DBPopis.KEY_wishlist) : false;
		listaId = extras != null ? extras.getLong(DBLista.TABLE_NAME + "." + DBLista.KEY_id) : null;
		if (listaId <= 0)
			listaId = null;

		popisId = extras != null ? extras.getLong(DBPopis.TABLE_NAME + "." + DBPopis.KEY_id) : null;
		if (popisId <= 0)
			popisId = null;
		
		if(popisId==null) {
			proizvodId = extras != null ? extras.getLong(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id) : null;
			if (proizvodId <= 0)
				proizvodId = null;
		}else {
			proizvodId = DBQuery.getProizvodId(popisId, dba);			
		}


		if (proizvodId != null && popisId != null)
			fillForm();
		else if(proizvodId != null && popisId == null)
			addExistinProizvod();

		if (listaId == null)
			finish();

	}

//	@Override
//	protected void onDestroy() {
//		dba.close();
//		super.onDestroy();
//	}
	
	public void fillForm() {
		Cursor c = DBQuery.getPopisProizvod(popisId, proizvodId, dba);
		if (c != null) {
			naziv.setText(c.getString(1));
			p_cijena = getNumber(c.getString(2), true);
			if (p_cijena == "0")
				p_cijena = "";
			cijena.setText(formatCijena(p_cijena, "kn"));
			kolicina.setText(c.getString(3));
			p_kolicina = Integer.parseInt(c.getString(3));
			ukupnaCijena();
			c.close();
		}
	}
	
	public void addExistinProizvod() {
		Cursor c = DBQuery.getProizvod(proizvodId, dba);
		if (c != null) {
			naziv.setText(c.getString(1));			
			c.close();
		}
	}

	public void save(View v) {
		saveState();
		setResult(RESULT_OK, null);
		finish();
	}

	public void saveState() {
		if (proizvodId == null) {
			proizvodId = DBQuery.newProizvod(naziv.getText().toString(), dba);
		} else {
			DBQuery.updateProizvod(proizvodId, naziv.getText().toString(), dba);
		}

		// ako je kolicina 0, a proizvod se stavlja na listu, tada nema smisla imati
		// proizvod na popisu koji nema kolicinu, odnosno nije u kosarici, tada se 
		// takav proizvod prebacuje u WISHLIST
		if (p_kolicina == 0 && wishlist == false)
			wishlist = true;

		// dodaj proizvod na popis, ili azuriraj
		if (proizvodId != null) { // dodaj proizvod i kolicinu na popis
			String cijenaNumber = getNumber(cijena.getText().toString(), false);
			if (popisId == null) { // dodaj novi proizvod na popis
				DBQuery.addToPopis(listaId, proizvodId, kolicina.getText().toString(),cijenaNumber, wishlist, dba);
			} else { // azuriraj kolicinu proizvoda na popisu
				DBQuery.updatePopis(popisId, kolicina.getText().toString(),cijenaNumber,wishlist, dba);
			}
		}
	}

	public void close(View v) {
		setResult(RESULT_OK, null);
		finish();
	}

	//
	// >> Kontekstualni izbornik
	//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ACCEPT_ID, 0, R.string.menu_p_accept);
		menu.add(0, MENU_CANCEL_ID, 0, R.string.menu_p_cancel);
		if (popisId != null)
			menu.add(0, MENU_DELETE_FROM_POPIS_ID, 0, R.string.menu_del_from_popis);
		else
			menu.add(0, MENU_FIND_EXISTING_ID, 0, R.string.menu_p_find);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCEPT_ID:
			saveState();
			setResult(RESULT_OK, null);
			finish();
			break;
		case MENU_CANCEL_ID:
			setResult(RESULT_OK, null);
			finish();
			break;
		case MENU_FIND_EXISTING_ID:
			break;

		case MENU_DELETE_FROM_POPIS_ID:
			DBQuery.deleteFromPopis(popisId, dba);
			setResult(RESULT_OK, null);
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	//
	// << Kontekstualni izbornik
	//

	//
	// Gumbici na ekranu
	//

	public void setKol(View v) {
		// dodaj kolicinu
		if (v.getId() == R.id.p_kolAdd) {
			p_kolicina++;
		}
		// oduzmi kolicinu
		if (v.getId() == R.id.p_kolSub) {
			if (p_kolicina > 0)
				p_kolicina--;
		}

		kolicina.setText("" + p_kolicina);
		ukupnaCijena();
	}

	public void resetCijena(View v) {
		p_cijena = "";
		cijena.setText(formatCijena(p_cijena, "kn"));
	}

	public String formatCijena(String input, String valuta) {
		if (input.length() == 0)
			input = "0.00";
		else if (input.length() == 1)
			input = "0.0" + input;
		else if (input.length() == 2)
			input = "0." + input;
		else
			input = input.substring(0, input.length() - 2) + "." + input.substring(input.length() - 2);

		// ukloni vodecu nulu (023.87)
		if (input.length() > 4 && input.startsWith("0")) {
			input = input.substring(1);
			p_cijena = p_cijena.substring(1);
		}

		return input + " " + valuta;
	}

	public String parseCijenaFromDouble(Double d) {
		String output = d.toString();
		if (output.indexOf(".") == output.length() - 2)
			output = output + "0";
		return output;
	}

	public void addBroj(View v) {
		if (p_cijena.length() < 7) {
			String input = p_cijena;
			int id = v.getId();
			if (id == R.id.p_broj0)
				input += "0";
			else if (id == R.id.p_broj1)
				input += "1";
			else if (id == R.id.p_broj2)
				input += "2";
			else if (id == R.id.p_broj3)
				input += "3";
			else if (id == R.id.p_broj4)
				input += "4";
			else if (id == R.id.p_broj5)
				input += "5";
			else if (id == R.id.p_broj6)
				input += "6";
			else if (id == R.id.p_broj7)
				input += "7";
			else if (id == R.id.p_broj8)
				input += "8";
			else if (id == R.id.p_broj9)
				input += "9";

			p_cijena = input;
			cijena.setText(formatCijena(input, "kn"));
			ukupnaCijena();
		}
	}

	public static String getNumber(String input, boolean onlyNumber) {
		// decimalna toèka
		// ukloni sve osim brojeva
		String broj = "";
		int max = input.length();

		for (int n = 0; n < max; n++) {
			int c = input.charAt(n);
			if ((c >= 48 && c <= 57) || (c == 46 && onlyNumber == false)) // decimalna
				// tocka ili
				// zarez
				broj += (char) c;
		}

		return broj;
	}

	public void ukupnaCijena() {
		double cijena = Double.parseDouble(formatCijena(p_cijena, ""));
		double ukupnaCijena = cijena * (double) p_kolicina;
		String c = TUtil.formatCijena(ukupnaCijena, "kn");
		ukupna_cijena.setText(c);
	}

	
	
	
}
