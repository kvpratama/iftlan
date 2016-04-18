package com.ift.lan;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
//import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.TabHost.TabSpec;
//import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IFTLanActivity extends Activity implements OnClickListener {

	final String STRING_RES_URL = "http://10.0.2.2:8081/IFTLan/Select?tanggal=";
	TableLayout table;
	Button[] remind;
	Button back;

	HashMap<Integer, String[]> theNews = new HashMap<Integer, String[]>();

	SQLiteDatabase sqldb = null;

	public void dbOpenOrCreate(String namaDb, String namaTable) {
		String qry;
		try {
			sqldb = this.openOrCreateDatabase(namaDb, MODE_PRIVATE, null);
			qry = "CREATE TABLE IF NOT EXISTS "
					+ namaTable
					+ " "
					+ "(tanggal VARCHAR, penulis VARCHAR, judul VARCHAR, isi VARCHAR);";
			sqldb.execSQL(qry);
			System.out.println("Database berhasil dibuat!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Database gagal dibuat!");
			e.printStackTrace();
		}
	}

	public void dbInsertRow(String namaTable, String tanggal, String penulis,
			String judul, String isi) {
		String qry;
		try {
			qry = "INSERT INTO " + namaTable + " ";
			qry += "VALUES( '" + tanggal + "', '" + penulis + "', '" + judul
					+ "', '" + isi + "'  );";
			System.out.println("insert: " + tanggal + penulis + judul + isi);
			sqldb.execSQL(qry);
			System.out.println("Data Berhasil Disimpan !");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Data Tidak Berhasil Disimpan !");
			e.printStackTrace();
		}
	}

	// to separate word by * sign then insert to String[]
	public void newsFactory(String news) {
		if (!news.isEmpty()) {

			ArrayList<String> all = new ArrayList<String>();
			int j = 0;
			for (int i = 0; i < news.length(); i++) {
				if (news.charAt(i) == '*') {
					all.add(news.substring(j, i));
					j = i + 1;
				}
			}

			String[] n = new String[4];
			int count = 0;
			int countNews = 0;
			for (int i = 0; i < all.size() + 1; i++) {
				if (count < 4) {
					n[count] = all.get(i);
					count++;
				} else {
					theNews.put(countNews, n);
					if (i < all.size()) {
						countNews++;
						n = new String[4];
						count = 0;
						n[count] = all.get(i);
						count++;
					}
				}
			}
		}
	}

	public void placeText() {
		TableRow row;
		if (!theNews.isEmpty()) {
			TextView[] a = new TextView[theNews.size() * 4];
			remind = new Button[theNews.size()];
			int count = 0;

			for (int i = 0; i < theNews.size(); i++) {
				String[] e = theNews.get(i);
				for (int k = 0; k < 4; k++) {
					row = new TableRow(this);
					TextView title = new TextView(this);
					title.setHeight(20);
					title.setWidth(70);
					// title.setBackgroundColor(color.black);
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
				remind[i] = new Button(this);
				remind[i].setText("Remind Me");
				row.addView(remind[i]);
				table.addView(row);
				addSpace();
			}
		} else {
			row = new TableRow(this);
			TextView noNews = new TextView(this);
			noNews.setText("No News Today");
			row.addView(noNews);
			table.addView(row);
			addSpace();
		}
	}

	public void addSpace() {
		TableRow rows = new TableRow(this);
		TextView space = new TextView(this);
		space.setHeight(30);
		rows.addView(space);
		table.addView(rows);
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

	public void initialize() {
		ScrollView scroll = (ScrollView) findViewById(R.id.mainScroll);

		// TabHost th = (TabHost) findViewById(android.R.id.tabhost);

		// TabSpec tsNews = th.newTabSpec("ts1");
		// tsNews.setIndicator("News").setContent(new Intent(this,
		// IFTLanActivity.class));

		// th.addTab(tsNews);

		table = new TableLayout(this);
		placeImage("2");
		table.addView(addButton());
		// th.addView(table);
		scroll.addView(table);
	}

	public TableRow addButton() {
		back = new Button(this);
//		refresh = new Button(this);
		back.setText("Back");
//		refresh.setText("Refresh");
//		refresh.setWidth(80);
		TableRow r = new TableRow(this);
		r.addView(back);
//		r.addView(refresh);
		return r;
	}

	public void registerListener() {
		back.setOnClickListener(this);
//		refresh.setOnClickListener(this);
		if (!theNews.isEmpty()) {
			for (int i = 0; i < remind.length; i++) {
				remind[i].setOnClickListener(this);
			}
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

	public String createTextFromWeb(String webText) {
		short i = 0;
		String result = "";
		while (webText.charAt(i) != '*') {
			i++;
		}
		i++;
		while (webText.charAt(i) != '<') {
			result += webText.charAt(i);
			i++;
		}
		System.out.println("result: " + result);
		return result;
	}

	private String getWebString(String urlString) {
		InputStream instream = null;
		final int BUFFER_SIZE = 2000;
		int charRead;
		String result = "";
		char[] inputBuffer = new char[BUFFER_SIZE];

		try {
			instream = downloadURL(urlString);
			InputStreamReader isr = new InputStreamReader(instream);
			while ((charRead = isr.read(inputBuffer)) > 0) {
				String readString = String
						.copyValueOf(inputBuffer, 0, charRead);
				result += readString;
				inputBuffer = new char[BUFFER_SIZE];
			}
			instream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		return result;
		return createTextFromWeb(result);
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initialize();
		System.out.println(STRING_RES_URL + getDate());
		newsFactory(getWebString(STRING_RES_URL + getDate()));
		placeText();
		placeImage("1");
		registerListener();
		// refreshImage();
	}

	public String getDate() {
		Date date = new Date();
		Format format = new SimpleDateFormat("dd-MMM-yy");
		String s = format.format(date);
		return s;
	}

	public void insertToDatabase(int i) {
		dbOpenOrCreate("ift_news", "berita");
		dbInsertRow("berita", theNews.get(i)[0], theNews.get(i)[1],
				theNews.get(i)[2], theNews.get(i)[3]);
		sqldb.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == back) {
			finish();
//		} else if (v == refresh) {
//			table.removeAllViews();
//			placeImage("2");
//			table.addView(addButton());
//			theNews.clear();
//			newsFactory(getWebString(STRING_RES_URL + getDate()));
//			placeText();
//			placeImage("1");
//			registerListener();
		} else {
			if (remind.length != 0) {
				for (int i = 0; i < remind.length; i++) {
					if (v == remind[i]) {
						insertToDatabase(i);
						Toast.makeText(this, "The news has been saved",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
}