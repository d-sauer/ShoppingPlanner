package troskovnik.sql;

import troskovnik.sql.db.DBCreate;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter  {

	private DBCreate dbHelper = null;
	public SQLiteDatabase db = null;
	private Context ctx = null;

	 /**
   * Constructor - takes the context to allow the database to be
   * opened/created
   * 
   * @param ctx the Context within which to work
   */
	public DBAdapter(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * Otvara/kreira bazu podataka
	 * @return
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		dbHelper = new DBCreate(ctx);
		db = dbHelper.getWritableDatabase();
		db = ctx.openOrCreateDatabase(DBCreate.DATABASE_NAME,ctx.MODE_WORLD_READABLE, null);
		
		return this;
	}

	/**
	 * Zatvara konekciju prema bazi
	 */
	public void close() {
		dbHelper.close();
	}
	
	public DBAdapter recreate() throws SQLException {
		dbHelper = new DBCreate(ctx);
		db = dbHelper.getWritableDatabase();
		dbHelper.onUpgrade(db, 1, 1);
		return this;
	}
	
	
}
