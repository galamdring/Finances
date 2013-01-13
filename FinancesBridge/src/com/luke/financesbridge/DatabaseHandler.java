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
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db){
		String CREATE_EXPENSE_TABLE="CREATE TABLE " +TABLE_EXPENSE + "(" + KEY_ID + " INTEGER,"+KEY_NAME+" TEXT,"+KEY_AMT+" REAL"+",PRIMARY KEY("+KEY_ID+" ASC) )";
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
		SQLiteDatabase db = this.getReadableDatabase();
		List<budgetItem> listExpense=getAllExpenseItems();
		List<budgetItem> listIncome=getAllIncomeItems();
		int numIncome=getItemCount(TABLE_INCOME);
		int numExpense=getItemCount(TABLE_EXPENSE);
		if (listExpense.size()==numExpense){
			for (int i=0;i<numExpense;++i){
				
			}
		}
	}
	void addBudgetItem(String type, String name, float amt){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values=new ContentValues();
		
		
		if (type == "expense")
		{
			String TABLE=TABLE_EXPENSE;
			values.put(KEY_ID, getNewId(TABLE));
			values.put(KEY_NAME, name);
			values.put(KEY_AMT, amt);
			db.insert(TABLE, null, values);	
		}
		if (type == "income")
		{
			String TABLE=TABLE_INCOME;
			values.put(KEY_ID, getNewId(TABLE));
			values.put(KEY_NAME, name);
			values.put(KEY_AMT, amt);
			db.insert(TABLE, null, values);;
		}
		db.close();
	}
	
	public budgetItem getBudgetItem(int id,String type){
		SQLiteDatabase db = this.getReadableDatabase();
		String SELECT_TABLE="";
		if (type=="expense"){
			SELECT_TABLE = TABLE_EXPENSE;
		}
		if (type=="income"){
			SELECT_TABLE=TABLE_INCOME;
		}
			Cursor cursor=db.query(SELECT_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_AMT},KEY_ID+"=?", new String[]{String.valueOf(id)},null,null,null,null);
			cursor.moveToFirst();
			budgetItem budgetItem=new budgetItem(Integer.parseInt(cursor.getString(0)),type,cursor.getString(1),Float.parseFloat(cursor.getString(2)));
		return budgetItem;
	}
	public int updateBudgetItem(budgetItem budgetItem){
		SQLiteDatabase db =this.getWritableDatabase();
		String TABLE="";
		if (budgetItem.getType()=="income") TABLE=TABLE_INCOME;
		if (budgetItem.getType()=="expense") TABLE=TABLE_EXPENSE;
		ContentValues values=new ContentValues();
		values.put(KEY_NAME, budgetItem.getName());
	    values.put(KEY_AMT, budgetItem.getAmount());
	    
	    // updating row
	    return db.update(TABLE, values, KEY_ID + " = ?", new String[] { String.valueOf(budgetItem.getID()) });
	}
	public List<budgetItem> getAllExpenseItems(){
		List<budgetItem> budgetItemList = new ArrayList<budgetItem>();
		SQLiteDatabase db=this.getWritableDatabase();
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
		db.close();
		return budgetItemList;
	}
	
	public List<budgetItem> getAllIncomeItems(){
		List<budgetItem> budgetItemList = new ArrayList<budgetItem>();
		SQLiteDatabase db=this.getWritableDatabase();
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
		db.close();
		return budgetItemList;
	}
    // Deleting single contact
	public void deleteBudgetItem(budgetItem budgetItem) {
		SQLiteDatabase db = this.getWritableDatabase();
		String TABLE="";
		if (budgetItem.getType()=="income") TABLE=TABLE_INCOME;
		if (budgetItem.getType()=="expense") TABLE=TABLE_EXPENSE;
		db.delete(TABLE, KEY_ID + " = ?", new String[] { String.valueOf(budgetItem.getID()) });
		db.close();
	}
	public int getItemCount(String TABLE){
		int total=0;
		SQLiteDatabase db=this.getReadableDatabase();
		String countQuery="SELECT * FROM "+TABLE;
		Cursor cursor=db.rawQuery(countQuery, null);
		total = total+cursor.getCount();
		
		return total;
	}
	public Cursor fetchAllExpense(){
		SQLiteDatabase db=this.getReadableDatabase();
		return db.query(TABLE_EXPENSE, new String[] {KEY_ID,KEY_NAME,KEY_AMT}, null, null, null, null, null);
	}
	public Cursor fetchAllIncome(){
		SQLiteDatabase db=this.getReadableDatabase();
		return db.query(TABLE_INCOME, new String[] {KEY_ID,KEY_NAME,KEY_AMT}, null, null, null, null, null);
	}
	public int getNewId(String TABLE){
		SQLiteDatabase db=this.getReadableDatabase();
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


