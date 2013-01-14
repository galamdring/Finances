package com.luke.financesbridge;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
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
	private ArrayList<budgetItem> expenseItemList = null;
	private ExpenseListAdapter m_adapter;
	ListView listView;
	ListView listView2;
	TextView tvIncome;
	TextView tvExpenses;
	TextView tvBudgetBalance;
	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budget);
		new initiallistPopulate().execute();
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
				addexpense();
				updateLists();
				fillData();
				
			}});
		
		Button addIncome=(Button)findViewById(R.id.addIncome);
		addIncome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addincome();
				
				
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
//		Cursor expenseCur=db.fetchAllExpense();
//		startManagingCursor(expenseCur);
//		String[] from=new String[]{DatabaseHandler.KEY_ID, DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_AMT};
//		int[] to = new int[]{R.id.itemid,R.id.name,R.id.amount};
//		SimpleCursorAdapter expense=new SimpleCursorAdapter(this, R.layout.expense, expenseCur, from, to);
////		CustomListViewAdapter expense=new CustomListViewAdapter(this,R.layout.budgetitem,expenseCur, from, to);
//		listView.setAdapter(expense);
//		listView.setOnItemLongClickListener(new OnItemLongClickListener(){
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				Dialog dialog = editDialog(arg1,"expense");
//				dialog.show();
//				return true;
//			}
//			
//		});
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
				//do Nothing, Disprivate ArrayListmiss
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
	
	public Dialog expeditDialog(View view){
		int itemid;
		String itemName;
		Float itemAmount;
		String itemDue;
		boolean itempaid;
		
		TextView tvid = (TextView) view.findViewById(R.id.exp_itemid);
		TextView tvName = (TextView) view.findViewById(R.id.exp_name);
		TextView tvAmount=(TextView) view.findViewById(R.id.exp_amount);
		TextView tvdue=(TextView) view.findViewById(R.id.expense_due);
		CheckBox cbpaid=(CheckBox) view.findViewById(R.id.checkbox_exp);
		itemid =  Integer.parseInt((String) tvid.getText());
		itemName = (String) tvName.getText();
		itemAmount = Float.parseFloat((String) tvAmount.getText());
		itemDue=String.valueOf(tvdue.getText());
		itempaid=cbpaid.isChecked();
		final budgetItem editItem=db.getBudgetItem(itemid, "expense");
		
		Toast.makeText(MainActivity.this, "Touched id is " + String.valueOf(itemid), Toast.LENGTH_LONG).show();
		
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(),R.style.AboutDialog));
		View editView=inflater.inflate(R.layout.expedit,null);
		final ExpDialogWrapper editwrapper=new ExpDialogWrapper(editView);
		editwrapper.setTitle(itemName,editView);
		editwrapper.setAmount(itemAmount,editView);
		editwrapper.setDue(itemDue, editView);
		editwrapper.setPaid(itempaid, editView);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AboutDialog));
		builder.setTitle("Edit Item");
		builder.setView(editView);
		builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editItem.setName(editwrapper.getTitle());
				editItem.setAmount(editwrapper.getValue());
				editItem.setDue(editwrapper.getDue());
				editItem.setSelected(editwrapper.getPaid());
				db.updateBudgetItem(editItem);
				updateLists();
				fillData();
				new MainActivity.listPopulate().execute();
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
				new MainActivity.listPopulate().execute();
			}});		
			
		return builder.create();
	}

	private void addincome(){
		String type="income";
		String title="Add Income";
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(this,R.style.AboutDialog));
		View addView=inflater.inflate(R.layout.alert,null);
		final DialogWrapper wrapper=new DialogWrapper(addView);
		new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AboutDialog))
		  .setTitle(title)
		  .setView(addView)
		  .setPositiveButton("Add",new DialogInterface.OnClickListener() {
			 @Override
			public void onClick(DialogInterface dialog, int which) {
				 String title=wrapper.getTitle();
				 Float value=wrapper.getValue();
				 if (title.trim().length()>0 && value>0){
					 db.addBudgetItem("income",wrapper.getTitle(),wrapper.getValue(),"1",false);
					 updateLists();
					 fillData();
					 new MainActivity.listPopulate().execute();
				}else{
					Toast.makeText(getApplicationContext(), "Adding item failed. Please populate all fields.", Toast.LENGTH_LONG).show();
				}
			 }
		})
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				//Do nothing, dismiss
				dialog.dismiss();
			}
		})
		.show();
			
	}
	private void addexpense(){
		String title="Add Expense";
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(this,R.style.AboutDialog));
		View addView=inflater.inflate(R.layout.expedit,null);
		final ExpDialogWrapper wrapper=new ExpDialogWrapper(addView);
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AboutDialog));
		builder.setTitle(title);
		builder.setView(addView);
		builder.setPositiveButton("Add",new DialogInterface.OnClickListener() {
			 @Override
			public void onClick(DialogInterface dialog, int which) {
				 String title=wrapper.getTitle();
				 Float value=wrapper.getValue();
				 if (title.trim().length()>0 && value>0){
					 db.addBudgetItem("expense",wrapper.getTitle(),wrapper.getValue(),wrapper.getDue(),wrapper.getPaid());
					 new MainActivity.listPopulate().execute();
				}else{
					Toast.makeText(getApplicationContext(), "Adding item failed. Please populate all fields.", Toast.LENGTH_LONG).show();
				}
			 }
		});
		builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which){
				//Do nothing, dismiss
			}
		});
		builder.show();
			
	}

	public class listPopulate extends AsyncTask<Void, Void, Void>{
		private ProgressDialog progressDialog;
		protected void onPreExecute(){
			progressDialog=ProgressDialog.show(MainActivity.this, "Please wait...", "Retreiving data ...",true);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				String type="expense";
				expenseItemList = new ArrayList<budgetItem>();
				Log.d("Finances Bridge", "database count is "+String.valueOf(db.getItemCount(type)));
				ArrayList<Integer> expenseIds = new ArrayList<Integer>(db.getIdList(type));
				for(int i=0;i<expenseIds.size();i++){
					Log.d(getLocalClassName(),"processing item# "+String.valueOf(i)+" with id of "+expenseIds.get(i));
					budgetItem item=db.getBudgetItem(expenseIds.get(i),type);
					if (item!=null){
						expenseItemList.add(item);
					}
				}
				
				Thread.sleep(2000);
			}catch(Exception e){
				Log.e("Background_proc", "Crashed: "+e.getMessage());
			}			
			return null;
		}
		protected void onPostExecute(Void result){
			progressDialog.dismiss();
			Log.d(getLocalClassName(),"Initializing adapter with portItemList of "+expenseItemList.size()+" objects");
			m_adapter=new ExpenseListAdapter(MainActivity.this, R.layout.expense, expenseItemList);
			Log.d(getLocalClassName(),"Setting adapter");
			ListView expenseList=(ListView)findViewById(R.id.list);
			expenseList.setAdapter(MainActivity.this.m_adapter);
			expenseList.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Dialog dialog = expeditDialog(arg1);
					dialog.show();
					return true;
				}
				
			});
		}
		
	}
	public class initiallistPopulate extends AsyncTask<Void, Void, Void>{
		private ProgressDialog progressDialog;
		protected void onPreExecute(){
			progressDialog=ProgressDialog.show(MainActivity.this, "Please wait...", "Retreiving data ...",true);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				String type="expense";
				expenseItemList = new ArrayList<budgetItem>();
				Log.d("Finances Bridge", "database count is "+String.valueOf(db.getItemCount(type)));
				ArrayList<Integer> expenseIds = new ArrayList<Integer>(db.getIdList(type));
				for(int i=0;i<expenseIds.size();i++){
					Log.d(getLocalClassName(),"processing item# "+String.valueOf(i)+" with id of "+expenseIds.get(i));
					budgetItem item=db.getBudgetItem(expenseIds.get(i),type);
					if (item!=null){
						expenseItemList.add(item);
					}
				}
				
				Thread.sleep(2000);
			}catch(Exception e){
				Log.e("Background_proc", "Crashed: "+e.getMessage());
			}			
			return null;
		}
		protected void onPostExecute(Void result){
			progressDialog.dismiss();
			Log.d(getLocalClassName(),"Initializing adapter with portItemList of "+expenseItemList.size()+" objects");
			m_adapter=new ExpenseListAdapter(MainActivity.this, R.layout.expense, expenseItemList);
			Log.d(getLocalClassName(),"Setting adapter");
			ListView expenseList=(ListView)findViewById(R.id.list);
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
			View header = inflater.inflate(R.layout.expenseheader, (ViewGroup) findViewById(R.id.expense_root));
			expenseList.addHeaderView(header,null, false);
			expenseList.setAdapter(MainActivity.this.m_adapter);
			expenseList.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Dialog dialog = editDialog(arg1,"expense");
					dialog.show();
					return true;
				}
				
			});
		}
		
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

