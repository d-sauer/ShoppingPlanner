package troskovnik.gui;

import java.util.Date;

import troskovnik.sql.DBAdapter;
import troskovnik.sql.DBQuery;
import troskovnik.sql.db.DBLista;
import troskovnik.sql.db.DBPopis;
import troskovnik.sql.db.DBProizvod;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.TabSpec;

public class Shoping extends Activity {

	private Long listaId = null;
	private Long popisId = null;
	private Long proizvodId = null;

	private static int selectedTab = 0;
	public static final int TAB_SHOPLIST = 0;
	public static final int TAB_WISHLIST = 1;
	private boolean wishlist = false;

	private TabHost tabHost = null;

	public DBAdapter dba = null;

	private static int order_ShopingList = 0;

	public static ListView shopingList = null;
	private TextView tvShopingListSumProizvoda = null;
	private TextView tvShopingListSumCijena = null;

	public static ListView shopingWishlist = null;
	private TextView tvShopingWishListSumProizvoda = null;
	private TextView tvShopingWishListSumCijena = null;

	private static final int MENU_INSERT_ID = Menu.FIRST;
	private static final int MENU_SORT_ID = Menu.FIRST + 1;
	private static final int MENU_ADD_EXISTING_ID = Menu.FIRST + 2;
	private static final int EDIT_EXISTING = 20;
	private static final int ADD_EXISTING = 21;

	private static final int CTM_LIST_DELETE = 11;
	private static final int CTM_TO_WISHLIST = 21;
	private static final int CTM_TO_LIST = 22;
	private static final int CTM_OPIS_ART = 23;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shoping);

		dba = new DBAdapter(this);
		dba.open();

		Resources res = getResources();
		tabHost = (TabHost) this.findViewById(R.id.my_tabhost);
		tabHost.setup();

		String resTitle = getResources().getString(R.string.shop_list);
		TabSpec tabSpec1 = tabHost.newTabSpec("shoping_list");
		tabSpec1.setIndicator(resTitle, res.getDrawable(R.drawable.ic_tab_list));
		tabSpec1.setContent(R.id.shoping_list);
		tabHost.addTab(tabSpec1);

		resTitle = getResources().getString(R.string.shop_wishlist);
		TabSpec tabSpec2 = tabHost.newTabSpec("shoping_wishlist");
		tabSpec2.setIndicator(resTitle, res.getDrawable(R.drawable.ic_tab_wishlist));
		tabSpec2.setContent(R.id.shoping_wishlist);
		tabHost.addTab(tabSpec2);

		tabHost.setCurrentTab(selectedTab);

		shopingList = (ListView) findViewById(R.id.list_list);
		tvShopingListSumProizvoda = (TextView) findViewById(R.id.list_br_artikala);
		tvShopingListSumCijena = (TextView) findViewById(R.id.list_suma);

		shopingWishlist = (ListView) findViewById(R.id.list_wishlist);
		tvShopingWishListSumProizvoda = (TextView) findViewById(R.id.wishlist_br_artikala);
		tvShopingWishListSumCijena = (TextView) findViewById(R.id.wishlist_suma);

		Bundle extras = getIntent().getExtras();
		listaId = extras != null ? extras.getLong(DBLista.TABLE_NAME + "." + DBLista.KEY_id) : null;
		boolean lastShoping = extras != null ? extras.getBoolean("LAST_SHOPING") : false;

		if (lastShoping == true)
			listaId = DBQuery.getLastShoping(dba);

		fillShopingList();
		fillWishList();

		// dodaj List listenere
		shopingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				editProizvod(id);
			}
		});

		shopingWishlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				editProizvod(id);
			}
		});

		ShopingList_Context();
		ShopingWishList_Context();

	}

	// @Override
	// protected void onDestroy() {
	// dba.close();
	// super.onDestroy();
	// }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long popisId = info.id;
		switch (item.getItemId()) {
		case CTM_LIST_DELETE:
			DBQuery.deleteFromPopis(popisId, dba);
			break;
		case CTM_TO_WISHLIST:
			DBQuery.moveList(popisId, true, dba);
			break;
		case CTM_TO_LIST:
			DBQuery.moveList(popisId, false, dba);
			break;
		case CTM_OPIS_ART:
			opisContext(popisId);
			break;
		}

		fillShopingList();
		fillWishList();
		ShopingList_Context();
		ShopingWishList_Context();
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_INSERT_ID, 0, R.string.menu_list_insert);
		menu.add(0, MENU_SORT_ID, 0, R.string.menu_sort);
		menu.add(1, MENU_ADD_EXISTING_ID, 1, R.string.menu_add_existing);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_INSERT_ID:
			if (listaId == null)
				createNewLista();

			Intent i = new Intent(this, Proizvod.class);

			selectedTab = tabHost.getCurrentTab();
			if (selectedTab == TAB_SHOPLIST)
				wishlist = false;
			if (selectedTab == TAB_WISHLIST)
				wishlist = true;

			i.putExtra(DBPopis.KEY_wishlist, wishlist);
			i.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, listaId);
			i.putExtra(DBPopis.TABLE_NAME + "." + DBPopis.KEY_id, -1l);

			startActivityForResult(i, MENU_INSERT_ID);
			break;
		case MENU_SORT_ID:
			CMPoredak();
			break;
		case MENU_ADD_EXISTING_ID:
			if (listaId == null)
				createNewLista();

			Intent i2 = new Intent(this, Artikli.class);

			selectedTab = tabHost.getCurrentTab();
			if (selectedTab == TAB_SHOPLIST)
				wishlist = false;
			if (selectedTab == TAB_WISHLIST)
				wishlist = true;

			i2.putExtra(DBPopis.KEY_wishlist, wishlist);
			i2.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, listaId);

			startActivityForResult(i2, MENU_ADD_EXISTING_ID);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == MENU_ADD_EXISTING_ID) {
			// otvori ekran za Proizvod
			if (intent != null) {
				Bundle extras = intent.getExtras();
				proizvodId = extras.getLong(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id);

				Intent i = new Intent(this, Proizvod.class);

				selectedTab = tabHost.getCurrentTab();
				if (selectedTab == TAB_SHOPLIST)
					wishlist = false;
				if (selectedTab == TAB_WISHLIST)
					wishlist = true;
				
				i.putExtra(DBPopis.KEY_wishlist, wishlist);
				i.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, listaId);
				i.putExtra(DBProizvod.TABLE_NAME + "." + DBProizvod.KEY_id, proizvodId);

				startActivityForResult(i, ADD_EXISTING);
			}
		} else {
			fillShopingList();
			fillWishList();
			ShopingList_Context();
			ShopingWishList_Context();
		}
	}

	public void opisContext(long popisId) {
		String opis = DBQuery.getProizvodOpis(popisId, dba);
		if (opis != null) {

			// set up dialog

			final Dialog dialog = new Dialog(Shoping.this);
			dialog.setContentView(R.layout.opis_artikla);
			dialog.setTitle("Opis artikla");
			dialog.setCancelable(true);
			// set up text
			TextView text = (TextView) dialog.findViewById(R.id.opis_text);
			text.setText(opis);
			// set up image view
			Button button = (Button) dialog.findViewById(R.id.Button01);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});

			// now that the dialog is set up, it's time to show it

			dialog.show();

		} else
			Toast.makeText(this, "Nema opisa artikla.", Toast.LENGTH_SHORT).show();
	}

	public void createNewLista() {
		Date date = new Date(System.currentTimeMillis());
		listaId = DBQuery.newLista(date, dba);
	}

	public void addNewProizvod(View v) {
		if (listaId == null)
			createNewLista();

		Intent i = new Intent(this, Proizvod.class);

		selectedTab = tabHost.getCurrentTab();
		if (selectedTab == TAB_SHOPLIST)
			wishlist = false;
		if (selectedTab == TAB_WISHLIST)
			wishlist = true;

		i.putExtra(DBPopis.KEY_wishlist, wishlist);
		i.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, listaId);
		i.putExtra(DBPopis.TABLE_NAME + "." + DBPopis.KEY_id, -1l);

		startActivityForResult(i, MENU_INSERT_ID);
	}

	public void fillShopingList() {
		if (listaId == null)
			return;

		final Cursor c = DBQuery.getShopingList(listaId, order_ShopingList, dba);
		startManagingCursor(c);

		String[] from = new String[] { DBProizvod.KEY_naziv, DBPopis.KEY_cijena, DBPopis.KEY_kolicina };
		int[] to = new int[] { R.id.sr_naziv, R.id.sr_cijena, R.id.sr_kolicina_cijena };

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(shopingList.getContext(), R.layout.shoping_row, c, from, to) {

			@Override
			public void setViewText(TextView v, String text) {
				int tId = v.getId();

				// ukupna cijena proizvoda x kolicina
				if (tId == R.id.sr_cijena) {
					double cijena = Double.parseDouble(text);
					int kolicina = Integer.parseInt(c.getString(3));
					double ukCijena = cijena * (double) kolicina;
					text = TUtil.formatCijena(ukCijena, "kn");
				} else if (tId == R.id.sr_kolicina_cijena) { // jedinicna cijena
					// proizvoda i kolicina
					double cijena = Double.parseDouble(c.getString(2));
					int kolicina = Integer.parseInt(text);
					text = kolicina + " x " + TUtil.formatCijena(cijena, "kn");
				}
				super.setViewText(v, text);
			}

		};

		shopingList.setAdapter(listAdapter);
		// ukupan broj proizvoda u kosarici
		int n = DBQuery.getListBrProizvoda(listaId, false, dba);
		tvShopingListSumProizvoda.setText(Integer.toString(n));

		// ukupna cijena proizvoda
		double ukCijena = DBQuery.getListSumCijeneProizvoda(listaId, false, dba);
		tvShopingListSumCijena.setText(TUtil.formatCijena(ukCijena, "kn"));
	}

	private void ShopingList_Context() {
		shopingList.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(0, CTM_LIST_DELETE, 0, R.string.s_delete);
				menu.add(0, CTM_TO_WISHLIST, 0, R.string.s_move_to_wishlist);
				menu.add(0, CTM_OPIS_ART, 0, R.string.s_opis);
				registerForContextMenu(v);
			}
		});
	}

	public void fillWishList() {
		if (listaId == null)
			return;

		final Cursor cw = DBQuery.getShopingWishList(listaId, order_ShopingList, dba);
		startManagingCursor(cw);

		String[] from = new String[] { DBProizvod.KEY_naziv, DBPopis.KEY_cijena, DBPopis.KEY_kolicina };
		int[] to = new int[] { R.id.sr_naziv, R.id.sr_cijena, R.id.sr_kolicina_cijena };

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(shopingWishlist.getContext(), R.layout.shoping_row, cw, from, to) {

			@Override
			public void setViewText(TextView v, String text) {
				int tId = v.getId();

				// ukupna cijena proizvoda x kolicina
				if (tId == R.id.sr_cijena) {
					double cijena = Double.parseDouble(text);
					int kolicina = Integer.parseInt(cw.getString(3));
					double ukCijena = cijena * (double) kolicina;
					text = TUtil.formatCijena(ukCijena, "kn");
				} else if (tId == R.id.sr_kolicina_cijena) { // jedinicna cijena
					// proizvoda i kolicina
					double cijena = Double.parseDouble(cw.getString(2));
					int kolicina = Integer.parseInt(text);
					text = kolicina + " x " + TUtil.formatCijena(cijena, "kn");
				}
				super.setViewText(v, text);
			}

		};

		shopingWishlist.setAdapter(listAdapter);
		// ukupan broj proizvoda u kosarici
		int n = DBQuery.getListBrProizvoda(listaId, true, dba);
		tvShopingWishListSumProizvoda.setText(Integer.toString(n));

		// ukupna cijena proizvoda
		double ukCijena = DBQuery.getListSumCijeneProizvoda(listaId, true, dba);
		tvShopingWishListSumCijena.setText(TUtil.formatCijena(ukCijena, "kn"));

	}

	private void ShopingWishList_Context() {
		shopingWishlist.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(0, CTM_LIST_DELETE, 0, R.string.s_delete);
				menu.add(0, CTM_TO_LIST, 0, R.string.s_move_to_list);
				menu.add(0, CTM_OPIS_ART, 0, R.string.s_opis);
				registerForContextMenu(v);
			}
		});
	}

	public void editProizvod(long id) {
		Intent i = new Intent(this, Proizvod.class);

		selectedTab = tabHost.getCurrentTab();
		if (selectedTab == TAB_SHOPLIST)
			wishlist = false;
		if (selectedTab == TAB_WISHLIST)
			wishlist = true;

		i.putExtra(DBPopis.KEY_wishlist, wishlist);
		i.putExtra(DBLista.TABLE_NAME + "." + DBLista.KEY_id, listaId);
		i.putExtra(DBPopis.TABLE_NAME + "." + DBPopis.KEY_id, id);

		startActivityForResult(i, EDIT_EXISTING);
	}

	public void CMPoredak() {
		Builder dialog = new Builder(this);
		dialog.setTitle("Poredak artikala prema");
		final CharSequence[] opcije = { "Cijeni", "Kolièini", "Nazivu" };
		dialog.setItems(opcije, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int mul = 1;
				switch (which) {
				case 0:
					if (order_ShopingList == 2)
						mul = -1;
					order_ShopingList = 2 * mul;
					break;
				case 1:
					if (order_ShopingList == 3)
						mul = -1;
					order_ShopingList = 3 * mul;
					break;
				case 2:
					if (order_ShopingList == 1)
						mul = -1;
					order_ShopingList = 1 * mul;
					break;
				}
				fillShopingList();
				fillWishList();
				ShopingList_Context();
				ShopingWishList_Context();
			}
		});

		dialog.show();
	}

}
