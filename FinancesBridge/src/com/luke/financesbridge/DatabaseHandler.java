package com.luke.financesbridge;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION=1;
	
	private static final String DATABASE_NAME="myDB";
	
	static final String TABLE_EXPENSE="expense";
	static final String TABLE_INCOME="income";
	
	static final String KEY_ID="_id";
	static final String KEY_NAME="name";
	static final String KEY_AMT="amount";
	static final String KEY_DUE="due";
	static final String KEY_PAID="paid";
	SQLiteDatabase db;
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db){
		String CREATE_EXPENSE_TABLE="CREATE TABLE " +TABLE_EXPENSE + "(" + KEY_ID + " INTEGER,"+KEY_NAME+" TEXT,"+KEY_AMT+" REAL,"+KEY_DUE+" TEXT,"+KEY_PAID+" INTERGER,PRIMARY KEY("+KEY_ID+" ASC) )";
		db.execSQL(CREATE_EXPENSE_TABLE);
		String CREATE_INCOME_TABLE="CREATE TABLE " +TABLE_INCOME + "(" + KEY_ID + " INTEGER,"+KEY_NAME+" TEXT,"+KEY_AMT+" REAL,PRIMARY KEY("+KEY_ID+" ASC)"+")";
		db.execSQL(CREATE_INCOME_TABLE);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_INCOME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_EXPENSE);
		
		onCreate(db);
	}
	
	/**
	 * This is where the CRUD happens.
	 */
	//Add new expense
	void resetIds(){
		db = this.getReadableDatabase();
		List<budgetItem> listExpense=getAllExpenseItems();
		List<budgetItem> listIncome=getAllIncomeItems();
		int numIncome=getItemCount(TABLE_INCOME);
		int numExpense=getItemCount(TABLE_EXPENSE);
		if (listExpense.size()==numExpense){
			for (int i=0;i<numExpense;++i){
				
			}
		}
	}
	public List<Integer> getIdList(String type) {
		String TABLE = null;
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		TABLE=setTable(type);			
		String query="SELECT "+KEY_ID+" FROM "+TABLE;
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
	
	void addBudgetItem(String type, String name, float amt, String due, boolean paid){
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		
		ContentValues values=new ContentValues();
		
		
		if (type == "expense")
		{
			String TABLE=TABLE_EXPENSE;
			values.put(KEY_ID, getNewId(TABLE));
			values.put(KEY_NAME, name);
			values.put(KEY_AMT, amt);
			values.put(KEY_DUE, due);
			values.put(KEY_PAID, paid);
			db.insert(TABLE, null, values);	
		}
		if (type == "income")
		{
			String TABLE=TABLE_INCOME;
			values.put(KEY_ID, getNewId(TABLE));
			values.put(KEY_NAME, name);
			values.put(KEY_AMT, amt);
			db.insert(TABLE, null, values);
		}
		db.close();
	}
	
	public budgetItem getBudgetItem(int id,String type){
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String TABLE=setTable(type);
		Cursor cursor=db.query(TABLE, new String[] {KEY_ID, KEY_NAME, KEY_AMT},KEY_ID+"=?", new String[]{String.valueOf(id)},null,null,null,null);
		cursor.moveToFirst();
		budgetItem budgetItem=new budgetItem(Integer.parseInt(cursor.getString(0)),type,cursor.getString(1),Float.parseFloat(cursor.getString(2)),false, "1");
		return budgetItem;
	}
	private String setTable(String type) {
		String SELECT_TABLE = null;
		if (type=="expense"){
			SELECT_TABLE = TABLE_EXPENSE;
		}
		if (type=="income"){
			SELECT_TABLE=TABLE_INCOME;
		}
		return SELECT_TABLE;
	}

	public int updateBudgetItem(budgetItem budgetItem){
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		String TABLE=setTable(budgetItem.getType());
		ContentValues values=new ContentValues();
		values.put(KEY_NAME, budgetItem.getName());
	    values.put(KEY_AMT, budgetItem.getAmount());
	    
	    // updating row
	    return db.update(TABLE, values, KEY_ID + " = ?", new String[] { String.valueOf(budgetItem.getID()) });
	}
	public List<budgetItem> getAllExpenseItems(){
		List<budgetItem> budgetItemList = new ArrayList<budgetItem>();
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		String type ="expense";
		String selectQuery="SELECT * FROM "+TABLE_EXPENSE;
		Cursor cursor=db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()){
			do{
				budgetItem budgetItem = new budgetItem();
				budgetItem.setID(Integer.parseInt(cursor.getString(0)));
				budgetItem.setType(type);
				budgetItem.setName(cursor.getString(1));
				budgetItem.setAmount(Float.parseFloat(cursor.getString(2)));
//				StringBuilder sb=new StringBuilder();
//				int columsQty=cursor.getColumnCount();
//				for (int idx=0;idx<columsQty; ++idx){
//					sb.append(cursor.getString(idx));
//					if(idx<columsQty-1) sb.append("; ");
//				}
//				Log.v("Finances", String.format("Row: %d, Values: %s", cursor.getPosition(), sb.toString()));
				budgetItemList.add(budgetItem);
			}while (cursor.moveToNext());
		}
		return budgetItemList;
	}
	
	public List<budgetItem> getAllIncomeItems(){
		List<budgetItem> budgetItemList = new ArrayList<budgetItem>();
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String type ="income";
		String selectQuery="SELECT * FROM "+TABLE_INCOME;
		Cursor cursor=db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()){
			do{
				budgetItem budgetItem = new budgetItem();
				budgetItem.setID(Integer.parseInt(cursor.getString(0)));
				budgetItem.setType(type);
				budgetItem.setName(cursor.getString(1));
				budgetItem.setAmount(Float.parseFloat(cursor.getString(2)));
				budgetItemList.add(budgetItem);
			}while (cursor.moveToNext());
		}
		
		return budgetItemList;
	}
    // Deleting single contact
	public void deleteBudgetItem(budgetItem budgetItem) {
		if(db == null || !db.isOpen()){
			db=getWritableDatabase();
		}
		if(db.isReadOnly()){
			db.close();
			db=getWritableDatabase();
		}
		String TABLE=setTable(budgetItem.getType());
		db.delete(TABLE, KEY_ID + " = ?", new String[] { String.valueOf(budgetItem.getID()) });
		db.close();
	}
	public int getItemCount(String TABLE){
		int total=0;
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String countQuery="SELECT * FROM "+TABLE;
		Cursor cursor=db.rawQuery(countQuery, null);
		total = total+cursor.getCount();
		
		return total;
	}
	public Cursor fetchAllExpense(){
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		return db.query(TABLE_EXPENSE, new String[] {KEY_ID,KEY_NAME,KEY_AMT}, null, null, null, null, null);
	}
	public Cursor fetchAllIncome(){
		SQLiteDatabase db=this.getReadableDatabase();
		return db.query(TABLE_INCOME, new String[] {KEY_ID,KEY_NAME,KEY_AMT}, null, null, null, null, null);
	}
	public int getNewId(String TABLE){
		if(db == null || !db.isOpen()){
			db=getReadableDatabase();
		}
		if(!db.isReadOnly()){
			db.close();
			db=getReadableDatabase();
		}
		String getMaxId="SELECT MAX("+KEY_ID+") FROM "+TABLE;
		Cursor cursor=db.rawQuery(getMaxId, null);
		cursor.moveToFirst();
		int maxId;
		try {
			maxId = cursor.getInt(0);
		} catch (NumberFormatException e) {
			maxId=0;
		}
		
		return maxId+1;
	}
}


