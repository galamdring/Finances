package com.luke.financesbridge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StockDBHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="stockDB";
	static final String TABLE_SYMBOLS="SYMBOLS";
	static final String KEY_ID="_id";
	static final String KEY_SYMBOL="symbol";
	static final String KEY_OWNED="owned";
	static final String KEY_PAID_PRICE="pprice";
	static final String KEY_DATE="date";
	static final String KEY_TIME="time";
	static final String KEY_DAY_OPEN="open";
	static final String KEY_DAY_LOW="low";
	static final String KEY_DAY_HIGH="high";
	static final String KEY_CURRENT_PRICE="price";
	SQLiteDatabase db = null;
	public StockDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SYMBOL_TABLE="CREATE TABLE " +TABLE_SYMBOLS + "("+KEY_ID+" INTEGER,"+KEY_SYMBOL+" TEXT,"+KEY_OWNED+" REAL,"+KEY_PAID_PRICE+" REAL,PRIMARY KEY("+KEY_ID+" ASC) )";
		db.execSQL(CREATE_SYMBOL_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SYMBOLS);
		onCreate(db);

	}
	
	/**
	 * This is where the CRUD happens.
	 */
	void createSymbolTable(String symbol){
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		String TABLE_NAME = symbol.toUpperCase(Locale.US);
		String CREATE_TABLE="CREATE TABLE "+TABLE_NAME + "("+KEY_ID+" INTEGER,"+KEY_DATE+" TEXT,"+KEY_TIME+" TEXT,"+KEY_DAY_OPEN+" REAL,"+KEY_DAY_LOW+" REAL,"+KEY_DAY_HIGH+" REAL,"+KEY_CURRENT_PRICE+" REAL, PRIMARY KEY("+KEY_ID+" ASC) )";
		db.execSQL(CREATE_TABLE);
	}
	
	boolean tableExists(String symbol){
		String TABLE_NAME=symbol.toUpperCase(Locale.US);
		Cursor c =null;
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		
		c=db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name='"+TABLE_NAME+"'", null);
		if(c!=null){
			if(c.getCount()>0){
				c.close();
				return true;
			}
			c.close();
		}
		
		return false;
	}

	void addQuote(Stock stock){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.US);
		String date=dateFormat.format(now.getTime());
		String time=timeFormat.format(now.getTime());
		String symbol = stock.getSymbol();
		ContentValues values = new ContentValues();
		values.put(KEY_DATE, date);
		values.put(KEY_TIME, time);
		values.put(KEY_DAY_OPEN, stock.getDayOpen());
		values.put(KEY_DAY_LOW, stock.getDayLow());
		values.put(KEY_DAY_HIGH, stock.getDayHigh());
		values.put(KEY_CURRENT_PRICE, stock.getCurrent());
		
		if (tableExists(symbol)){
			values.put(KEY_ID, getNewTableID(symbol));
			insertQuote(symbol, values);
		}else{
			createSymbolTable(symbol);
			values.put(KEY_ID, 0);
			insertQuote(symbol, values);
		}
		
	}
	public Stock getRecentQuote(String symbol){
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		long twenty_mins=20;
		Calendar now = Calendar.getInstance();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.US);
		String time=timeFormat.format(now.getTime());
		int inttime=Integer.parseInt(time);
		String TABLE=symbol.toUpperCase(Locale.US);
		if(tableExists(symbol)){
			Cursor c=db.rawQuery("SELECT "+KEY_ID+","+KEY_DATE+","+KEY_TIME+","+KEY_CURRENT_PRICE+" FROM "+TABLE+" WHERE "+KEY_TIME+" > ?",new String[] {String.valueOf(inttime-twenty_mins)});
			c.moveToFirst();
			boolean exists=(c.getCount()>0);
			if (exists){
				Log.d("Finances.StockDBHandler","Using existing quote");
				String lastupdated=c.getString(1)+c.getString(2);
				long numlastupdated=Long.parseLong(lastupdated);
				Stock quote = new Stock(Integer.parseInt(c.getString(0)),symbol,Float.parseFloat(c.getString(3)),null,null,null,null,null,null, numlastupdated);
				return quote;
			}
		}
		Log.d("Finances.StockDBHandler","Getting new quote");
		Stock quote = StockTicker.getInstance().getStockPrice(symbol);
		return quote;
	}

	void addPortItem(String symbol, Float shares, Float pprice){
		
		if (sharesOwned(symbol)){
			int id =getPortID(symbol);
			PortItem oldportItem=getPortItem(id);
			Float totalShares=shares+oldportItem.getOwned();
			Float oldpaid=pprice*shares;
			Float newpaid=oldportItem.getPaidPrice()*oldportItem.getOwned();
			Float totalpaid=oldpaid+newpaid;
			Float paid=totalpaid/totalShares;
			PortItem newportItem=new PortItem(id,symbol,totalShares,paid);
			updatePortItem(newportItem);
			
		}else{
			if(db == null || !db.isOpen()){
				db=getWritableDatabase();
			}
			if(db.isReadOnly()){
				db.close();
				db=getWritableDatabase();
			}
			ContentValues values = new ContentValues();
			values.put(KEY_SYMBOL, symbol);
			values.put(KEY_OWNED, shares);
			values.put(KEY_PAID_PRICE, pprice);
			values.put(KEY_ID, getNewTableID(TABLE_SYMBOLS));
			db.insert(TABLE_SYMBOLS, null, values);
		}
	}
	private int getPortID(String symbol) {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		Cursor c=db.rawQuery("SELECT "+KEY_ID+","+KEY_SYMBOL+" FROM "+TABLE_SYMBOLS+" where "+KEY_SYMBOL+" = ?",new String[]{symbol});
		c.moveToFirst();
		return(Integer.parseInt(c.getString(0)));
	}

	private boolean sharesOwned(String symbol) {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		Cursor c=db.rawQuery("SELECT "+KEY_SYMBOL+" FROM "+TABLE_SYMBOLS+" where "+KEY_SYMBOL+" = ?",new String[]{symbol});
		boolean exists=(c.getCount()>0);
		c.close();
		return exists;
	}
	public long getPortCount(){
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		long count=DatabaseUtils.queryNumEntries(db, TABLE_SYMBOLS);
		return count;
	}

	PortItem getPortItem(int id){
		PortItem portItem = null;
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		Cursor c = db.query(TABLE_SYMBOLS, new String[] {KEY_ID,KEY_SYMBOL, KEY_OWNED, KEY_PAID_PRICE}, KEY_ID+"=?", new String[]{String.valueOf(id)}, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			portItem=new PortItem(Integer.parseInt(c.getString(0)),c.getString(1),Float.parseFloat(c.getString(2)),Float.parseFloat(c.getString(3)));
		}
		
		return portItem;
	}
	
	private int getNewTableID(String symbol) {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String TABLE=symbol.toUpperCase(Locale.US);
		String getMaxID="SELECT MAX("+KEY_ID+") FROM "+TABLE;
		int maxId;
		Cursor c = db.rawQuery(getMaxID, null);
		c.moveToFirst();
		try{
			maxId=c.getInt(0);
		}catch(NumberFormatException e){
			maxId=-1;
		}
		return maxId+1;
	}

	private void insertQuote(String symbol, ContentValues values) {
		String TABLE=symbol.toUpperCase(Locale.US);
		if(db == null || !db.isOpen()){
				db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		db.insert(TABLE, null, values);
		
	}
	
	public Cursor fetchAllPort() {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		return db.query(TABLE_SYMBOLS, new String[] {KEY_ID,KEY_SYMBOL, KEY_OWNED, KEY_PAID_PRICE},null,null,null,null,null);
	}
	public void updatePortItem(PortItem portItem){
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		ContentValues values=new ContentValues();
		values.put(KEY_SYMBOL, portItem.getSymbol());
		values.put(KEY_OWNED, portItem.getOwned());
		values.put(KEY_PAID_PRICE, portItem.getPaidPrice());
		
		db.update(TABLE_SYMBOLS,values, KEY_ID + " = ?", new String[]{String.valueOf(portItem.getID())});
		
	}
	public void deletePortItem(PortItem portItem){
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		db.delete(TABLE_SYMBOLS, KEY_ID+" = ?", new String[]{String.valueOf(portItem.getID())});
	}

	public List<Integer> getPortIdList() {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String query="SELECT "+KEY_ID+" FROM "+TABLE_SYMBOLS;
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		List<Integer> intList=new ArrayList<Integer>();
		if (c.getCount()>0){
			for (int i=0;i<c.getCount();i++){
				intList.add(c.getInt(0));
				c.moveToNext();
			}
		}
		return intList;
	}

	public Float getRecentQuotePriceOnly(String symbol) {
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		long twenty_mins=20;
		Calendar now = Calendar.getInstance();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.US);
		String time=timeFormat.format(now.getTime());
		int inttime=Integer.parseInt(time);
		String TABLE=symbol.toUpperCase(Locale.US);
		if(tableExists(symbol)){
			Cursor c=db.rawQuery("SELECT "+KEY_ID+","+KEY_DATE+","+KEY_TIME+","+KEY_CURRENT_PRICE+" FROM "+TABLE+" WHERE "+KEY_TIME+" > ?",new String[] {String.valueOf(inttime-twenty_mins)});
			c.moveToFirst();
			boolean exists=(c.getCount()>0);
			if (exists){
				Log.d("Finances.StockDBHandler","Using existing quote");
				String lastupdated=c.getString(1)+c.getString(2);
				long numlastupdated=Long.parseLong(lastupdated);
				Stock quote = new Stock(Integer.parseInt(c.getString(0)),symbol,Float.parseFloat(c.getString(3)),null,null,null,null,null,null, numlastupdated);
				return quote.getCurrent();
			}
		}
		Log.d("Finances.StockDBHandler","Getting new quote");
		Stock quote = StockTicker.getInstance().getStockPrice(symbol);
		return quote.getCurrent();
	}
}
