package troskovnik.gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import troskovnik.sql.DBAdapter;
import troskovnik.sql.DBQuery;
import troskovnik.sql.db.DBLista;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListShoping extends ListActivity {

	public DBAdapter dba = null;
	private static final int DELETE_ID = Menu.FIRST;
	public static final String[] dani = { "nedjelja", "ponedjeljak", "utorak", "srijeda", "èetvrtak", "petak", "subota" };
	private static TextView txtSumArtikala = null;
	private static TextView txtSumUkCijena = null;
	public static Date datum_od = null;
	public static Date datum_do = null;
	public static boolean datum_error = false;
	

	public static final int MENU_DATE_PERIOD = 10;
	public static final int MENU_DATE_RESET = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_shoping);
		dba = new DBAdapter(this);
		dba.open();

		txtSumArtikala = (TextView) findViewById(R.id.list_br_artikala);
		txtSumUkCijena = (TextView) findViewById(R.id.list_suma);

		fillForm();
		registerForContextMenu(getListView());

	}

	// @Override
	// protected void onDestroy() {
	// dba.close();
	// super.onDestroy();
	// }

	public void fillForm() {
		Cursor c = DBQuery.getListaShopinga(datum_od, datum_do, dba);
		startManagingCursor(c);

		String[] from = new String[] { "datum", "datum", "kolicina", "uk_cijena", "list" };
		int[] to = new int[] { R.id.lsr_datum, R.id.lsr_dan, R.id.lsr_brArtikala, R.id.lsr_ukCijena, R.id.lsr_list };

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.list_shoping_row, c, from, to) {
			@Override
			public void setViewText(TextView v, String text) {
				String tmp = text;
				int tId = v.getId();

				if (tId == R.id.lsr_datum) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date d = df.parse(tmp);
						tmp = TUtil.addZeroBefore(d.getDate(), 2) + "." + TUtil.addZeroBefore((d.getMonth() + 1), 2) + "." + (d.getYear() + 1900);
					} catch (ParseException e) {

					}
				} 
				if (tId == R.id.lsr_list) {
					Double lc = Double.parseDouble(tmp);
					if(lc<=0) { //wishlist
						tmp = "*";
					}else
						tmp = "";
					
				} else if (tId == R.id.lsr_dan) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						Date d = df.parse(tmp);
						tmp = dani[d.getDay()] + "  " + TUtil.addZeroBefore(d.getHours(), 2) + ":" + TUtil.addZeroBefore(d.getMinutes(), 2);
					} catch (ParseException e) {

					}
				} else if (tId == R.id.lsr_brArtikala) {
					tmp = "(" + tmp + ")";
				} else if (tId == R.id.lsr_ukCijena) {
					Double d = Double.parseDouble(tmp);
					tmp = TUtil.formatCijena(d, "kn");
				}

				super.setViewText(v, tmp);
			}
		};
		setListAdapter(listAdapter);

		// filtextbox
		Bundle b = DBQuery.getSumOfShopingLists(datum_od, datum_do, dba);
		if (b != null) {
			long ukArt = b.getLong("sum_artikala");
			double ukCijena = b.getDouble("sum_cijena");
			txtSumArtikala.setText(Long.toString(ukArt));
			txtSumUkCijena.setText(TUtil.formatCijena(ukCijena, "kn"));
		}
		
		if(datum_od!=null && datum_do!=null && datum_error==false) {
			Toast.makeText(this, "Prikaz popisa kupovina u periodu\n" +
														"od " + datum_od.getDate()+ "."+ (datum_od.getMonth()+1) + "." + (datum_od.getYear()+1900)  + 
														"\ndo " + datum_do.getDate()+ "."+ (datum_do.getMonth()+1) + "." + (datum_do.getYear()+1900), Toast.LENGTH_LONG).show();
		}
		if(datum_error==true) {
			datum_error=false;
			Toast.makeText(this, "Neispravno unesen datum!", Toast.LENGTH_LONG).show();
		}
			

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, Shoping.class);
		i.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, id);
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.ls_delete_shoping);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			DBQuery.deleteLista(info.id, dba);
			fillForm();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_DATE_PERIOD, 0, R.string.lsd_period);
		menu.add(0, MENU_DATE_RESET, 0, R.string.lsd_reset);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DATE_PERIOD:
			periodContext();
			break;

		case MENU_DATE_RESET:
			datum_od = null;
			datum_do = null;
			fillForm();
			//Toast.makeText(this, "Prikaz svih popisa kupovina!",Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void periodContext() {
		final Dialog dialog = new Dialog(ListShoping.this);
		dialog.setContentView(R.layout.ls_date);
		dialog.setTitle("Period prikaza popisa kupovina");
		dialog.setCancelable(true);

		final TextView txtDatumOd = (TextView) dialog.findViewById(R.id.lsd_datum_od);
		final TextView txtDatumDo = (TextView) dialog.findViewById(R.id.lsd_datum_do);

		// Postavi datume od danas do pocetka mjeseca
		datum_do = new Date(System.currentTimeMillis());
		long tmpD = datum_do.getTime() - ((datum_do.getDate() - 1) * 24 * 60 * 60 * 1000);
		datum_od = new Date(tmpD);

		String strDatumOd = datum_od.getDate() + "." + (datum_od.getMonth() + 1) + "." + (datum_od.getYear() + 1900);
		txtDatumOd.setText(strDatumOd);
		String strDatumDo = datum_do.getDate() + "." + (datum_do.getMonth() + 1) + "." + (datum_do.getYear() + 1900);
		txtDatumDo.setText(strDatumDo);

		// gumbi Prihvati , Odustani
		Button button1 = (Button) dialog.findViewById(R.id.lsd_btn_cancel);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		Button button2 = (Button) dialog.findViewById(R.id.lsd_btn_accept);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// parse date
				String datum = txtDatumOd.getText().toString();
				datum_od = TUtil.parseDate(datum);
				if(datum_od==null)
					datum_error=true;
				
				datum = txtDatumDo.getText().toString();
				datum_do = TUtil.parseDate(datum);
				if(datum_do==null)
					datum_error=true;
				
				fillForm();
				dialog.cancel();
			}
		});

		dialog.show();
	}

}
