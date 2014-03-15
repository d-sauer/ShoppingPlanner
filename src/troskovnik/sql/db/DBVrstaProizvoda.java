package troskovnik.sql.db;

public class DBVrstaProizvoda {
	public static final String TABLE_NAME = "vrsta_proizvoda";
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
																								" _id integer primary key autoincrement," +
																								" naziv varchar(100) NOT NULL," +
																								" kategorija_id VARCHAR(300)" +
																								");";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	// Atributi
	public static final String KEY_id = "_id";
	public static final String KEY_naziv = "naziv";
	public static final String KEY_kategorija_id = "kategorija_id";
}
