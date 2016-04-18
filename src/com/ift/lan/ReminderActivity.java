package com.ift.lan;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ReminderActivity extends Activity implements OnClickListener {

	Button back;
	Button[] deleteButton;
	TableLayout table;
	SQLiteDatabase sqldb = null;
	HashMap<Integer, String[]> theNews;

	public void dbOpenOrCreate(String namaDb, String namaTable) {
		String qry;
		try {
			sqldb = this.openOrCreateDatabase(namaDb, MODE_PRIVATE, null);
			qry = "CREATE TABLE IF NOT EXISTS "
					+ namaTable
					+ " (tanggal VARCHAR, penulis VARCHAR, judul VARCHAR, isi VARCHAR);";
			sqldb.execSQL(qry);
			System.out.println("Database berhasil dibuat!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Database gagal dibuat!");
			e.printStackTrace();
		}
	}

	private InputStream downloadURL(String urlString) {
		URL url;
		HttpURLConnection con = null;
		InputStream instream = null;

		try {
			url = new URL(urlString);
			con = (HttpURLConnection) url.openConnection();
			con.connect();
			instream = con.getInputStream();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return instream;
	}

	private Bitmap getWebImage(String urlString) {
		InputStream instream = null;
		Bitmap result = null;

		try {
			instream = downloadURL(urlString);
			result = BitmapFactory.decodeStream(instream);
			instream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public void placeImage(String id) {
		String uri = "http://10.0.2.2:8081/IFTLan/Image?id=" + id;
		TableRow row = new TableRow(this);

		ImageView image = new ImageView(this);
		image.setImageBitmap(getWebImage(uri));
		row.addView(image);
		TableLayout imageTable = new TableLayout(this);
		imageTable.addView(row);
		table.addView(imageTable);

		addSpace();
	}

	public void addSpace() {
		TableRow rows = new TableRow(this);
		TextView space = new TextView(this);
		space.setHeight(30);
		rows.addView(space);
		table.addView(rows);
	}

	public HashMap<Integer, String[]> dbSelectRow(String namaTable) {
		String qry;
		Cursor row;
		int colIdx;
		String[] result;
		theNews = new HashMap<Integer, String[]>();
		try {
			qry = "SELECT tanggal, penulis, judul, isi from " + namaTable;
			row = sqldb.rawQuery(qry, null);
			row.moveToFirst();
			if (row != null) {
				// System.out.print("select: ");
				int i = 0;
				while (!row.isLast()) {
					result = new String[4];
					colIdx = row.getColumnIndex("tanggal");
					result[0] = row.getString(colIdx);
					// System.out.print("tanggal: " + row.getString(colIdx));

					colIdx = row.getColumnIndex("penulis");
					result[1] = row.getString(colIdx);
					// System.out.print("penulis: " + row.getString(colIdx));

					colIdx = row.getColumnIndex("judul");
					result[2] = row.getString(colIdx);
					// System.out.print("judul: " + row.getString(colIdx));

					colIdx = row.getColumnIndex("isi");
					result[3] = row.getString(colIdx);
					// System.out.println("isi: " + row.getString(colIdx));

					theNews.put(i, result);
					// System.out.println("i: " + i);
					i++;
					row.moveToNext();
				}
				if (row.isLast() == true) {
					result = new String[4];
					colIdx = row.getColumnIndex("tanggal");
					result[0] = row.getString(colIdx);

					colIdx = row.getColumnIndex("penulis");
					result[1] = row.getString(colIdx);

					colIdx = row.getColumnIndex("judul");
					result[2] = row.getString(colIdx);

					colIdx = row.getColumnIndex("isi");
					result[3] = row.getString(colIdx);

					theNews.put(i, result);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Tidak Ada Data Ditemukan !");
			e.printStackTrace();
		}
		return theNews;
	}

	public void dbDeleteTable(String namaTable, String where) {
		try {
			sqldb.delete(namaTable, where, null);
			System.out.println("Delete Berhasil");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Delete Tidak Berhasil");
			e.printStackTrace();
		}
	}

	public void deleteRow(int i) {
		String where = "judul = " + "'" + theNews.get(i)[2] + "'";
		dbDeleteTable("berita", where);
		refresh();
	}

	public void placeText(HashMap<Integer, String[]> theNews) {
		TableRow row;
		if (!theNews.isEmpty()) {
			TextView[] a = new TextView[theNews.size() * 4];
			deleteButton = new Button[theNews.size()];
			int count = 0;

			for (int i = 0; i < theNews.size(); i++) {
				String[] e = theNews.get(i);
				for (int k = 0; k < 4; k++) {
					row = new TableRow(this);
					TextView title = new TextView(this);
					title.setHeight(20);
					title.setWidth(70);
					if (k == 0) {
						title.setText("Tanggal: ");
					} else if (k == 1) {
						title.setText("Penulis: ");
					} else if (k == 2) {
						title.setText("Judul: ");
					} else if (k == 3) {
						title.setText("Berita: ");
					}
					a[count] = new TextView(this);
					a[count].setText(e[k]);
					row.addView(title);
					row.addView(a[count]);
					count++;
					table.addView(row);
				}
				row = new TableRow(this);
				deleteButton[i] = new Button(this);
				deleteButton[i].setText("Delete");
				row.addView(deleteButton[i]);
				table.addView(row);
				addSpace();
			}
		} else {
			row = new TableRow(this);
			TextView noNews = new TextView(this);
			noNews.setText("No Reminder");
			row.addView(noNews);
			table.addView(row);
			addSpace();
		}
	}

	public void initialize() {
		ScrollView scroll = (ScrollView) findViewById(R.id.remainderScroll);
		table = new TableLayout(this);

		placeImage("3");

		TableRow row = new TableRow(this);

		back = new Button(this);
		back.setText("Back");
		row.addView(back);

		table.addView(row);

		scroll.addView(table);
	}
	
	public void refresh(){
//		placeImage("2");
//		table.addView(addButton());
//		newsFactory(getWebString(STRING_RES_URL + getDate()));
//		placeText();
		table.removeAllViews();
		theNews.clear();
		
		placeImage("3");
		
		TableRow row = new TableRow(this);
		back = new Button(this);
		back.setText("Back");
		row.addView(back);
		table.addView(row);
		dbOpenOrCreate("ift_news", "berita");
		placeText(dbSelectRow("berita"));
		registerListener();
	}

	public void registerListener() {
		back.setOnClickListener(this);
		if (deleteButton.length != 0) {
			for (int i = 0; i < deleteButton.length; i++) {
				deleteButton[i].setOnClickListener(this);
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder);

		initialize();
		dbOpenOrCreate("ift_news", "berita");
		placeText(dbSelectRow("berita"));
		registerListener();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == back) {
			sqldb.close();
			finish();
//			Intent i = new Intent(this, WelcomeActivity.class);
//			startActivity(i);
		} else {
			if (deleteButton.length != 0) {
				for (int i = 0; i < deleteButton.length; i++) {
					if (v == deleteButton[i]) {
						deleteRow(i);
//						Toast.makeText(this, theNews.get(i)[2],
//								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
}
