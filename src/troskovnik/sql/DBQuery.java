package troskovnik.sql;

import java.util.Date;

import troskovnik.sql.db.DBLista;
import troskovnik.sql.db.DBPopis;
import troskovnik.sql.db.DBProizvod;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

public class DBQuery {

	//
	// Proizvodi
	//

	public static Long newProizvod(String naziv, DBAdapter dba) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DBProizvod.KEY_naziv, naziv);
		if (naziv != null && naziv.length() > 0)
			return dba.db.insert(DBProizvod.TABLE_NAME, null, initialValues);
		else
			return -1l;
	}

	public static int updateProizvod(long id, String naziv, DBAdapter dba) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DBProizvod.KEY_naziv, naziv);
		if (naziv != null || naziv.length() > 0)
			return dba.db.update(DBProizvod.TABLE_NAME, initialValues, DBProizvod.KEY_id + "=" + id, null);
		else
			return 0;
	}

	public static void addToPopis(long listaId, long proizvodId, String kolicina, String cijena, boolean wishlist, DBAdapter dba) {
		ContentValues values = new ContentValues();
		values.put(DBPopis.KEY_lista_id, listaId);
		values.put(DBPopis.KEY_proizvod_id, proizvodId);
		values.put(DBPopis.KEY_cijena, parseToDouble(cijena));
		values.put(DBPopis.KEY_kolicina, parseToInteger(kolicina));
		values.put(DBPopis.KEY_wishlist, wishlist);
		dba.db.insert(DBPopis.TABLE_NAME, null, values);
	}

	public static void updatePopis(long popisId, String kolicina, String cijena, boolean wishlist, DBAdapter dba) {
		ContentValues values = new ContentValues();
		values.put(DBPopis.KEY_cijena, parseToDouble(cijena));
		values.put(DBPopis.KEY_kolicina, parseToInteger(kolicina));
		values.put(DBPopis.KEY_wishlist, wishlist);
		dba.db.update(DBPopis.TABLE_NAME, values, DBPopis.KEY_id + "=" + popisId, null);
	}

	public static long newLista(Date date, DBAdapter dba) {
		ContentValues values = new ContentValues();
		String datum = (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() +
									" " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
		values.put(DBLista.KEY_datum, datum);

		return dba.db.insert(DBLista.TABLE_NAME, null, values);
	}

	/**
	 * Order by 0 - bez poredka, 1 - naziv proizvoda, 2 - cijena, 3 - kolicina
	 * 
	 * @param listId
	 * @param dba
	 * @return
	 */
	public static Cursor getShopingList(Long listId, int orderBy, DBAdapter dba) {
		String strOrder = "";
		switch (orderBy) {
		case 1:
			strOrder = " order by p.naziv";
			break;
		case -1:
			strOrder = " order by p.naziv desc";
			break;
		case 2:
			strOrder = " order by ukcijena desc";
			break;
		case -2:
			strOrder = " order by ukcijena asc";
			break;
		case 3:
			strOrder = " order by pop.kolicina desc";
			break;
		case -3:
			strOrder = " order by pop.kolicina asc";
			break;
		default:
			strOrder = "";
			break;
		}

		String sql = "SELECT pop._id as _id, p.naziv as naziv, pop.cijena as cijena, " +
								"	pop.kolicina as kolicina, ( pop.cijena * pop.kolicina) as ukcijena " +
								"FROM proizvod p, popis pop " +
								" WHERE p._id=pop.proizvod_id and pop.wishlist=0 and pop.lista_id=" + listId +
								strOrder;
		return dba.db.rawQuery(sql, null);
	}

	public static int getListBrProizvoda(Long listId, boolean wishlist, DBAdapter dba) {
		if (listId == null)
			return 0;
		String sql = "";
		if (wishlist == false)
			sql = "SELECT sum(pop.kolicina) as kolicina FROM proizvod p, popis pop " +
								" WHERE p._id=pop.proizvod_id and pop.wishlist=0 and pop.lista_id=" + listId;
		else
			sql = "SELECT sum(pop.kolicina) as kolicina FROM proizvod p, popis pop " +
					" WHERE p._id=pop.proizvod_id and pop.wishlist=1 and pop.lista_id=" + listId;

		Cursor c = dba.db.rawQuery(sql, null);
		if (c != null) {
			if (c.moveToNext()) {
				int n = c.getInt(0);
				c.close();
				return n;
			} else {
				c.close();
				return 0;
			}
		} else {
			c.close();
			return 0;
		}
	}

	public static double getListSumCijeneProizvoda(Long listId, boolean wishlist, DBAdapter dba) {
		if (listId == null)
			return 0.0d;

		String sql = "";
		if (wishlist == false)
			sql = "SELECT pop.cijena as cijena, pop.kolicina as kolicina FROM proizvod p, popis pop " +
								" WHERE p._id=pop.proizvod_id and pop.wishlist=0 and pop.lista_id=" + listId;
		else
			sql = "SELECT pop.cijena as cijena, pop.kolicina as kolicina FROM proizvod p, popis pop " +
					" WHERE p._id=pop.proizvod_id and pop.wishlist=1 and pop.lista_id=" + listId;

		Cursor c = dba.db.rawQuery(sql, null);
		double ukCijena = 0.0d;
		int kolicina = 0;
		double cijena = 0.0d;
		if (c != null) {
			while (c.moveToNext()) {
				cijena = c.getDouble(0);
				kolicina = c.getInt(1);
				ukCijena += (cijena * (double) kolicina);
			}
			c.close();
			return ukCijena;
		} else {
			c.close();
			return 0;
		}
	}

	/**
	 * Order by 0 - bez poredka, 1 - naziv proizvoda, 2 - cijena, 3 - kolicina
	 * 
	 * @param listId
	 * @param dba
	 * @return
	 */
	public static Cursor getShopingWishList(Long listId, int orderBy, DBAdapter dba) {
		String strOrder = "";
		switch (orderBy) {
		case 1:
			strOrder = " order by p.naziv";
			break;
		case -1:
			strOrder = " order by p.naziv desc";
			break;
		case 2:
			strOrder = " order by ukcijena desc";
			break;
		case -2:
			strOrder = " order by ukcijena asc";
			break;
		case 3:
			strOrder = " order by pop.kolicina desc";
			break;
		case -3:
			strOrder = " order by pop.kolicina asc";
			break;
		default:
			strOrder = "";
			break;
		}

		String sql = "SELECT pop._id as _id, p.naziv as naziv, pop.cijena as cijena, " +
								"	pop.kolicina as kolicina, ( pop.cijena * pop.kolicina) as ukcijena " +
								"FROM proizvod p, popis pop " +
								" WHERE p._id=pop.proizvod_id and pop.wishlist=1 and pop.lista_id=" + listId +
								strOrder;
		return dba.db.rawQuery(sql, null);
	}

	public static Long getProizvodId(Long popisId, DBAdapter dba) {
		if (popisId == null || popisId < 0)
			return null;

		Cursor c = dba.db.query(DBPopis.TABLE_NAME, new String[] { DBPopis.KEY_proizvod_id }, DBPopis.KEY_id + "=" + popisId, null, null, null,
				null);
		if (c.moveToFirst()) {
			Long id = c.getLong(0);
			c.close();
			return id;
		} else {
			c.close();
			return null;
		}
	}

	public static Cursor getPopisProizvod(Long popisId, Long proizvodId, DBAdapter dba) {
		if (popisId != null && proizvodId != null) {
			String sql = "SELECT p._id, p.naziv, pop.cijena, pop.kolicina FROM proizvod p, popis pop " +
					"WHERE pop._id=" + popisId + " and p._id=pop.proizvod_id and p._id=" + proizvodId;
			Cursor c = dba.db.rawQuery(sql, null);
			if (c.moveToFirst())
				return c;
			else
				return null;
		} else
			return null;
	}

	public static Cursor getProizvod(Long proizvodId, DBAdapter dba) {
		if (proizvodId != null) {
			String sql = "SELECT p._id, p.naziv, p.opis FROM proizvod p " +
					"WHERE p._id=" + proizvodId;
			Cursor c = dba.db.rawQuery(sql, null);
			if (c.moveToFirst())
				return c;
			else
				return null;
		} else
			return null;
	}

	public static Long insertProizvod(String naziv, String opis, DBAdapter dba) {
		ContentValues values = new ContentValues();
		values.put(DBProizvod.KEY_naziv, naziv);
		values.put(DBProizvod.KEY_opis, opis);
		return dba.db.insert(DBProizvod.TABLE_NAME, null, values);
	}

	public static void updateProizvod(Long proizvodId, String naziv, String opis, DBAdapter dba) {
		ContentValues values = new ContentValues();
		values.put(DBProizvod.KEY_naziv, naziv);
		values.put(DBProizvod.KEY_opis, opis);
		dba.db.update(DBProizvod.TABLE_NAME, values, DBProizvod.KEY_id + "=" + proizvodId, null);
	}

	public static Cursor getListaShopinga(Date datum_od, Date datum_do, DBAdapter dba) {
		String sql = "";
		String datum = "";

		if (datum_od != null && datum_do != null) {
			long msDatumDo = datum_do.getTime() + (1 * 24 * 60 * 60 * 1000);
			Date d_do = new Date(msDatumDo);
			datum = " and (l.datum > '" + (datum_od.getYear() + 1900) + "-" + (datum_od.getMonth() + 1) + "-" + datum_od.getDate() + "' " +
								" and l.datum < '" + (d_do.getYear() + 1900) + "-" + (d_do.getMonth() + 1) + "-" + d_do.getDate() + "' )";
		}

//		sql = "select l._id as _id, l.datum as datum ,sum(p.kolicina) as kolicina,  sum(p.kolicina *  p.cijena) as uk_cijena, p.wishlist as list " +
//				"FROM lista l, popis p, proizvod pr " +
//				"WHERE p.lista_id=l._id and pr._id=p.proizvod_id " + datum +
//				"group by l._id,p.wishlist order by l.datum, p.wishlist desc";
			sql = "select l._id as _id, l.datum as datum ,sum(p.kolicina *  (p.wishlist-1) * -1 ) as kolicina,  sum(p.kolicina *  p.cijena *  (p.wishlist-1) * -1) as uk_cijena, sum(p.kolicina *  p.cijena *  (p.wishlist-1) * -1) as list " + 
					  "FROM lista l, popis p, proizvod pr " + 
					  "WHERE p.lista_id=l._id and pr._id=p.proizvod_id " + 
					  datum +
					  "group by l._id order by l.datum, p.wishlist  desc	";	

		return dba.db.rawQuery(sql, null);
	}

	public static Long getLastShoping(DBAdapter dba) {
		Cursor c = dba.db.query(DBLista.TABLE_NAME, new String[] { DBLista.KEY_id, DBLista.KEY_datum }, null, null, null, null,
				DBLista.KEY_datum
						+ " desc");
		if (c != null) {
			if (c.moveToNext()) {
				Long id = c.getLong(0);
				c.close();
				return id;
			} else {
				c.close();
				return null;
			}
		} else {
			c.close();
			return null;
		}
	}

	public static void deleteFromPopis(Long popisId, DBAdapter dba) {
		if (popisId != null)
			dba.db.delete(DBPopis.TABLE_NAME, DBPopis.KEY_id + "=" + popisId, null);
	}

	public static void moveList(Long popisId, boolean toWishList, DBAdapter dba) {
		if (popisId != null) {
			ContentValues values = new ContentValues();
			values.put(DBPopis.KEY_wishlist, toWishList);
			dba.db.update(DBPopis.TABLE_NAME, values, DBPopis.KEY_id + "=" + popisId, null);
		}
	}

	public static Cursor getListaArtikala(DBAdapter dba) {
		String sql = "select p._id as _id, p.naziv as naziv, avg(pop.cijena) as avr_cijena " +
									"FROM proizvod p " +
									"LEFT JOIN popis pop ON pop.proizvod_id=p._id " +
									"group by p._id order by p.naziv";

		return dba.db.rawQuery(sql, null);
	}

	public static String getProizvodOpis(Long popisId, DBAdapter dba) {
		Cursor c = dba.db.rawQuery("SELECT p.opis FROM proizvod p, popis pop " +
																" WHERE pop.proizvod_id=p._id and pop._id=" + popisId, null);
		if (c != null) {
			if (c.moveToNext()) {
				String opis = c.getString(0);
				c.close();
				return opis;
			} else {
				c.close();
				return null;
			}
		} else {
			c.close();
			return null;
		}
	}

	public static void deleteLista(Long listId, DBAdapter dba) {
		dba.db.delete(DBLista.TABLE_NAME, DBLista.KEY_id + "=" + listId, null);
		dba.db.delete(DBPopis.TABLE_NAME, DBPopis.KEY_lista_id + "=" + listId, null);
	}

	public static boolean deleteFreeArtikl(Long artiklId, DBAdapter dba) {
		Cursor c = dba.db.rawQuery("SELECT count(*) FROM popis WHERE proizvod_id=" + artiklId, null);
		if (c != null) {
			if (c.moveToNext()) {
				int count = c.getInt(0);
				c.close();
				if (count == 0) {
					// Obrisi artikl
					dba.db.delete(DBProizvod.TABLE_NAME, DBProizvod.KEY_id + "=" + artiklId, null);
					return true;
				} else
					return false;
			} else {
				c.close();
				return false;
			}
		} else {
			c.close();
			return false;
		}
	}

	public static Bundle getSumOfShopingLists(Date datum_od, Date datum_do, DBAdapter dba) {
		Bundle b = new Bundle();
		b.putLong("sum_artikala", 0l);
		b.putDouble("sum_cijena", 0d);

		String sql = "";
		String datum = "";

		if (datum_od != null && datum_do != null) {
			long msDatumDo = datum_do.getTime() + (1 * 24 * 60 * 60 * 1000);
			Date d_do = new Date(msDatumDo);
			datum = " and (l.datum > '" + (datum_od.getYear() + 1900) + "-" + (datum_od.getMonth() + 1) + "-" + datum_od.getDate() + "' " +
								" and l.datum < '" + (d_do.getYear() + 1900) + "-" + (d_do.getMonth() + 1) + "-" + d_do.getDate() + "' )";
		}

		sql = "SELECT sum(p.kolicina) as kolicina, sum(p.kolicina * p.cijena) as uk_cijena" +
				" FROM popis p, lista l" +
				" WHERE p.wishlist=0 and p.lista_id=l._id" + datum;

		Double ukCijena = 0.0d;
		long ukArt = 0;
		Cursor c = dba.db.rawQuery(sql, null);
		if (c != null) {
			if (c.moveToNext()) {
				ukArt = c.getLong(0);
				ukCijena = c.getDouble(1);
				c.close();
				b.putLong("sum_artikala", ukArt);
				b.putDouble("sum_cijena", ukCijena);
				return b;
			} else {
				c.close();
				return null;
			}
		} else {
			c.close();
			return null;
		}
	}

	public static Double parseToDouble(String input) {
		return Double.parseDouble(getNumber(input));
	}

	public static Integer parseToInteger(String input) {
		return Integer.parseInt(getNumber(input));
	}

	public static String getNumber(String input) {
		// decimalna toèka
		// ukloni sve osim brojeva
		String broj = "";
		int max = input.length();

		for (int n = 0; n < max; n++) {
			int c = input.charAt(n);
			if ((c >= 48 && c <= 57) || c == 46) // decimalna tocka
				broj += (char) c;
		}

		return broj;
	}

}
