package troskovnik.sql.db;

public class DBKategorija {
	public static final String TABLE_NAME = "kategorija";
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
																								" _id integer primary key autoincrement," +
																								" naziv varchar(100) NOT NULL" +																								
																								");";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	// Atributi
	public static final String KEY_id = "_id";
	public static final String KEY_naziv = "naziv";
}
