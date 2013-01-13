package com.luke.financesbridge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	Button addExpense;
	Button addIncome;
	Button getStockQuote;
	Button submit;
	Button remExpense;
	Button remIncome;
	Button displayBudget;
	TextView nameDisplay;
	TextView amountDisplay;

	EditText Input1;
	EditText Input2;
/**	Notifier Notifier1;
	Web getQuote;
*/
	DatabaseHandler db =new DatabaseHandler(this);
	StockDBHandler stockdb = new StockDBHandler(this);
	List<String> incomeList=new ArrayList<String>();
	List<String> expenseList=new ArrayList<String>();
	List<budgetItem> budgetItemList=new ArrayList<budgetItem>();
	ListView listView;
	ListView listView2;
	TextView tvIncome;
	TextView tvExpenses;
	TextView tvBudgetBalance;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budget);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		listView=(ListView) findViewById(R.id.list);
		listView2=(ListView) findViewById(R.id.list2);
/**		CustomListViewAdapter adapter = new CustomListViewAdapter(this, R.layout.budgetitem,budgetItemList);
		listView.setAdapter(adapter);
*/		
		
		//Setup Buttons
	
		Button addExpense=(Button)findViewById(R.id.addExpense);
		addExpense.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				add("expense");
				updateLists();
				fillData();
				
			}});
		
		Button addIncome=(Button)findViewById(R.id.addIncome);
		addIncome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				add("income");
				
				
			}});
		
/*		Button displayBudget=(Button)findViewById(R.id.displayBudget);
		displayBudget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateLists();
				fillData();
				
				
				
		}});
*/		
/*		Button getStockQuote=(Button)findViewById(R.id.getStockQuote);
		getStockQuote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), StockActivity.class));
				updateLists();
				fillData();
				
		}});
		
*/		
		updateLists();
		fillData();

		
		
	
	}
	
	


	private void updateLists(){
		ArrayList<String> incomeList = new ArrayList<String>();
		ArrayList<String> expenseList = new ArrayList<String>();
		ArrayList<budgetItem> budgetItemList=new ArrayList<budgetItem>();
		Float balance = (float) 0.0;
		for (budgetItem item:db.getAllIncomeItems()){
			incomeList.add(String.valueOf(item.getID()));
			budgetItemList.add(item);
		//	Log.w("Finances",item.getName());
		}
		for (budgetItem item:db.getAllExpenseItems()){
			expenseList.add(String.valueOf(item.getID()));
			budgetItemList.add(item);
		//	Log.w("Finances",item.getName());
		}
		for (String item : expenseList){
		//	Log.w("Finances", item);
		}
		Float[] total=sumItems(budgetItemList);
		try {
			balance=total[0]-total[1];
		} catch (NullPointerException e) {
		
			balance=(float) 0.0;
		}
//		Float totalIncome=sumItems(incomeList,"income");
		tvIncome=(TextView) findViewById(R.id.totalincome);
		tvExpenses=(TextView) findViewById(R.id.totalexpenses);
		tvBudgetBalance=(TextView) findViewById(R.id.budgetbalance);
		tvIncome.setText(String.format("%.2f",total[0]));
		tvExpenses.setText(String.format("%.2f",total[1]));
		tvBudgetBalance.setText(String.format("%.2f", balance));
		
	}
	
	private Float[] sumItems(List<budgetItem> budgetItemList) {
		Float expsum=(float) 0.0;
		Float incsum=(float) 0.0;
		Float[] totalList=new Float[2];
		for (budgetItem item:budgetItemList){
			if (item.getType()=="expense"){
				Float value=item.getAmount();
			//	Log.w("Finances",String.valueOf(value));
				expsum=expsum+value;
			}
			if(item.getType()=="income"){
				Float value=item.getAmount();
			//	Log.w("Finances",String.valueOf(value));
				incsum=incsum+value;
			}
		
		totalList[0]=incsum;
		totalList[1]=expsum;
		}
//		Log.w("Finances", "summing"+String.valueOf(incsum));
//		Log.w("Finances", "summing"+String.valueOf(expsum));
		return totalList;
	}

	private void fillData(){
		Cursor expenseCur=db.fetchAllExpense();
		startManagingCursor(expenseCur);
		String[] from=new String[]{DatabaseHandler.KEY_ID, DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_AMT};
		int[] to = new int[]{R.id.itemid,R.id.name,R.id.amount};
		SimpleCursorAdapter expense=new SimpleCursorAdapter(this, R.layout.budgetitem, expenseCur, from, to);
//		CustomListViewAdapter expense=new CustomListViewAdapter(this,R.layout.budgetitem,expenseCur, from, to);
		listView.setAdapter(expense);
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Dialog dialog = editDialog(arg1,"expense");
				dialog.show();
				return true;
			}
			
		});
		Cursor incomeCur=db.fetchAllIncome();
		startManagingCursor(incomeCur);
		String[] incomefrom = new String[]{DatabaseHandler.KEY_ID, DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_AMT};
		int[] incometo = new int[]{R.id.itemid,R.id.name,R.id.amount};
		SimpleCursorAdapter income=new SimpleCursorAdapter(this, R.layout.budgetitem, incomeCur, incomefrom, incometo);
		listView2.setAdapter(income);
		listView2.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Dialog dialog = editDialog(arg1,"income");
				dialog.show();
				return true;
			}});
		updateLists();
	}
	public Dialog editDialog(View view, String type){
		int itemid;
		String itemName;
		Float itemAmount;
		
		TextView tvid = (TextView) view.findViewById(R.id.itemid);
		TextView tvName = (TextView) view.findViewById(R.id.name);
		TextView tvAmount=(TextView) view.findViewById(R.id.amount);
		itemid =  Integer.parseInt((String) tvid.getText());
		itemName = (String) tvName.getText();
		itemAmount = Float.parseFloat((String) tvAmount.getText());
		
		final budgetItem editItem=db.getBudgetItem(itemid, type);
		
		Toast.makeText(MainActivity.this, "Touched id is " + String.valueOf(itemid), Toast.LENGTH_LONG).show();
		
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(),R.style.AboutDialog));
		View editView=inflater.inflate(R.layout.alert,null);
		final DialogWrapper editwrapper=new DialogWrapper(editView);
		editwrapper.setTitle(itemName,editView);
		editwrapper.setAmount(itemAmount,editView);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AboutDialog));
		builder.setTitle("Edit Item");
		builder.setView(editView);
		builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editItem.setName(editwrapper.getTitle());
				editItem.setAmount(editwrapper.getValue());
				db.updateBudgetItem(editItem);
				updateLists();
				fillData();
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//do Nothing, Dismiss
				dialog.cancel();
			}});
		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				db.deleteBudgetItem(editItem);
				updateLists();
				fillData();
			}});		
			
		return builder.create();
	}

	private void add(final String type){
		String title="";
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(this,R.style.AboutDialog));
		View addView=inflater.inflate(R.layout.alert,null);
		final DialogWrapper wrapper=new DialogWrapper(addView);
		if (type=="expense") title="Add Expense";
		if (type=="income") title="Add Income";
		new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AboutDialog))
		  .setTitle(title)
		  .setView(addView)
		  .setPositiveButton("Add",new DialogInterface.OnClickListener() {
			 @Override
			public void onClick(DialogInterface dialog, int which) {
				 String title=wrapper.getTitle();
				 Float value=wrapper.getValue();
				 if (title.trim().length()>0 && value>0){
					 db.addBudgetItem(type,wrapper.getTitle(),wrapper.getValue());
					 updateLists();
					 fillData();
				}else{
					Toast.makeText(getApplicationContext(), "Adding item failed. Please populate all fields.", Toast.LENGTH_LONG).show();
				}
			 }
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				//Do nothing, dismiss
			}
		})
		.show();
			
	}
	
	
	private boolean doubleBacktoExitPressedOnce = false;
	public void onBackPressed() {
		if (doubleBacktoExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBacktoExitPressedOnce=true;
		Toast.makeText(this, "The cat doesn't want you to go!", Toast.LENGTH_SHORT).show();
	    return;
	   }
	 
	public void onResume(){
		super.onResume();
		updateLists();
		fillData();
	}
	public void onDestroy(){
		super.onDestroy();
		Log.v(getPackageName(), "Closing app.");
		db.close();
		
	}
	}

