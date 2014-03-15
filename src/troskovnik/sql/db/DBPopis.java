package troskovnik.sql.db;

public class DBPopis {
	public static final String TABLE_NAME = "popis";
	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
																								" _id integer primary key autoincrement," +
																								" lista_id integer NOT NULL," +
																								" proizvod_id integer NOT NULL," +
																								" cijena decimal(10,2)," +		
																								" kolicina integer," +
																								" wishlist boolean" +
																								");";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	// Atributi
	public static final String KEY_id = "_id";
	public static final String KEY_lista_id = "lista_id";
	public static final String KEY_proizvod_id = "proizvod_id";
	public static final String KEY_cijena = "cijena";
	public static final String KEY_kolicina = "kolicina";
	public static final String KEY_wishlist = "wishlist";
}
