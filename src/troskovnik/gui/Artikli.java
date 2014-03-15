package troskovnik.gui;

import troskovnik.sql.DBAdapter;
import troskovnik.sql.DBQuery;
import troskovnik.sql.db.DBLista;
import troskovnik.sql.db.DBPopis;
import troskovnik.sql.db.DBProizvod;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Artikli extends Activity {

	private Long listaId = null;
	private boolean wishlist = false;

	public DBAdapter dba = null;
	public static ListView listArtikli = null;
	public static EditText filterText = null;

	private static final int CTM_LIST_DELETE = 10;
	private static final int SHOW_ARTIKL = 20;
	private static final int SHOW_POPIS_ARTIKL = 21;
	private static final int MENU_ADD_ARTIKL = Menu.FIRST;

	private static SimpleCursorAdapter cAdapter = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artikli);

		dba = new DBAdapter(this);
		dba.open();

		// dohvati popisId i listaId
		Bundle extras = getIntent().getExtras();
		wishlist = extras != null ? extras.getBoolean(DBPopis.KEY_wishlist) : false;
		listaId = extras != null ? extras.getLong(DBLista.TABLE_NAME + "." + DBLista.KEY_id) : null;
		if (listaId!=null && listaId < 0)
			listaId = null;

		listArtikli = (ListView) findViewById(R.id.ar_list);
		listArtikli.setTextFilterEnabled(true);
		listArtikli.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				editProizvod(id);
			}
		});

		fillForm();
		artiklContext();
	}

//	@Override
//	protected void onDestroy() {
//		dba.close();
//		super.onDestroy();
//	}
	
	private void fillForm() {
		Cursor c = DBQuery.getListaArtikala(dba);

		String[] from = { "naziv", "avr_cijena" };
		int[] to = { R.id.arr_naziv, R.id.arr_avr_cijena };

		cAdapter = new SimpleCursorAdapter(this, R.layout.artikli_row, c, from, to) {
			@Override
			public void setViewText(TextView v, String text) {
				int tId = v.getId();
				// ukupna cijena proizvoda x kolicina
				if (tId == R.id.arr_avr_cijena) {
					if(text.length()>0) {
						double d = Double.parseDouble(text);
						text = "~ " + TUtil.formatCijena(d, "kn");
					}					
				}
				super.setViewText(v, text);
			}
		};
		listArtikli.setAdapter(cAdapter);
	}
	
	

	public void editProizvod(long id) {
		if (listaId == null) {
			Intent i = new Intent(this, Artikl.class);
			i.putExtra(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id, id);
			startActivityForResult(i, 1);
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id, id);
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			finish();			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		fillForm();
		artiklContext();
	}
	
	private void artiklContext() {
		listArtikli.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(0, CTM_LIST_DELETE, 0, R.string.s_delete);
				registerForContextMenu(v);
			}
		});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long artiklId = info.id;
		switch (item.getItemId()) {
		case CTM_LIST_DELETE:
			boolean isDeleted = DBQuery.deleteFreeArtikl(artiklId, dba);
			if(isDeleted==false)
				Toast.makeText(this, "Artikl se ne može obrisati jer se koristi u kupovnim listama!", Toast.LENGTH_LONG).show();
			else {
				fillForm();
				artiklContext();
			}
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADD_ARTIKL, 0, "Novi artikl");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD_ARTIKL:
			Intent i = new Intent(this, Artikl.class);
			startActivityForResult(i, MENU_ADD_ARTIKL);
			break;

		}
		return super.onMenuItemSelected(featureId, item);
	}
	
}
