package troskovnik.sql.db;

public class DBLista {

	public static final String TABLE_NAME = "lista";
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
																								" _id integer primary key autoincrement," +
																								" datum datetime," +
																								" naziv varchar(500)," +
																								" placeno boolean" +
																								");";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	// Atributi
	public static final String KEY_id = "_id";
	public static final String KEY_datum = "datum";
	public static final String KEY_naziv = "naziv";

}
