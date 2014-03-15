package troskovnik.sql.db;

public class DBProizvod {
	public static final String TABLE_NAME = "proizvod";
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
																								" _id integer primary key autoincrement," +
																								" naziv varchar(100) NOT NULL," +
																								" opis VARCHAR(300)," +
																								" slika VARCHAR(500)," +																																														
																								" vrsta_id integer" +
																								");";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	// Atributi
	public static final String KEY_id = "_id";
	public static final String KEY_naziv = "naziv";
	public static final String KEY_opis = "opis";
	public static final String KEY_slika = "slika";
	public static final String KEY_vrsta_id = "vrsta_id";
}
