package troskovnik.sql.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Create i Drop table
 * 
 * @author davor
 * 
 */
public class DBCreate extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "troskovnik";
	public static final int DATABASE_VERSION = 1;

	public DBCreate(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlCreate = DBProizvod.TABLE_CREATE;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBKategorija.TABLE_CREATE;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBLista.TABLE_CREATE;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBPopis.TABLE_CREATE;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBVrstaProizvoda.TABLE_CREATE;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		System.out.println("TABLE CREATE FINISH");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(this.getClass().getName(), "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
		String sqlCreate = DBProizvod.TABLE_DROP;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBKategorija.TABLE_DROP;		
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);

		sqlCreate = DBLista.TABLE_DROP;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBPopis.TABLE_DROP;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		sqlCreate = DBVrstaProizvoda.TABLE_DROP;
		db.execSQL(sqlCreate);
		System.out.println(sqlCreate);
		
		System.out.println("TABLE DROP FINISH");
		onCreate(db);
	}

}
