package com.luke.financesbridge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StockActivity extends Activity {

	StockDBHandler db=new StockDBHandler(this); 
	Button budget;
	Button getQuote;
	Button addPortfolio;
	EditText symbolInput;
	ListView portList;
	private ArrayList<PortItem> portItemList = null;
	private PortListAdapter m_adapter;
	private Runnable viewPort;
	private ProgressDialog m_ProgressDialog=null;
	Thread thread;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock);
		new initialListPopulate().execute();
//		Log.d("Finances","Starting up.");
		
//		Log.d(getLocalClassName(),"Creating Runnable");
//		viewPort=new Runnable(){
//			public void run(){
//				new listPopulate();
//				
//			}
//		};
////		Log.d(getPackageName(), "Starting ViewPort thread");
//		thread = new Thread(null,viewPort);
//		thread.start();
		
		
/*		Button budget=(Button)findViewById(R.id.budget);
		budget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				
			}
		});
*/
		Button getQuote=(Button)findViewById(R.id.getquote);
		getQuote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText symbolInput=(EditText)findViewById(R.id.symbol);
//				Log.d(getLocalClassName(), "setting symbol");
				String symbol = symbolInput.getText().toString();
//				Log.d(getPackageName(), "getting quote");
				Stock stock = retrieveQuote(symbol);
//				Log.d(getPackageName(), "Making Toast");
				// TODO: Replace Toast with alert.
				if (stock==null){
					Toast.makeText(StockActivity.this, "Unable to retreive quote", Toast.LENGTH_LONG).show();
				}
				if (stock.getCurrent()!=0.0){
					db.addQuote(stock);
					Toast.makeText(StockActivity.this, "Quote for: "+ stock.getSymbol()+" Currently trading at: "+String.valueOf(stock.getCurrent()), Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(StockActivity.this, "No price retreived. Is Symbol Valid? "+symbol, Toast.LENGTH_LONG).show();
				}
				
				symbolInput.setText("");
				
			}
		});
		
		Button addPortfolio=(Button)findViewById(R.id.addPortfolioItem);
		addPortfolio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText symbolInput =(EditText)findViewById(R.id.symbol);
				String symbol = symbolInput.getText().toString();
				addPort(symbol);
			}});
		
		Button updatePortfolio=(Button)findViewById(R.id.updateLists);
		updatePortfolio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new StockActivity.listPopulate().execute();
				
			}
		});
		
	}
	private OnItemLongClickListener listlistener=new OnItemLongClickListener(){
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3){
			Dialog dialog = portEditDialog(view);
			dialog.show();
			return true;
		}
	};
//	private Runnable returnRes = new Runnable(){
//
//		@Override
//		public void run() {
//			if (portItemList!=null && portItemList.size()>0){
//				m_adapter.notifyDataSetChanged();
//				for (int i=0; i<portItemList.size();i++)
//					m_adapter.add(portItemList.get(i));
//			}
//			m_ProgressDialog.dismiss();
//			m_adapter.notifyDataSetChanged();
//		}
//		
//	};
//	public void updateList(){
//		viewPort=new Runnable(){
//			public void run(){
//				getPortItemList();
//				
//			}
//		};
//		Log.d(getPackageName(), "Starting ViewPort thread");
//		thread = new Thread(null,viewPort);
//		thread.start();
//		m_ProgressDialog=ProgressDialog.show(StockActivity.this, "Please wait...", "Retreiving data ...",true);
//		
//	}
	protected void addPort(String symbol) {
		String title="Add Portfolio Item";
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(this, R.style.AboutDialog));
		View addView=inflater.inflate(R.layout.portalert, null);
		final PortDialogWrapper wrapper=new PortDialogWrapper(addView);
		if (symbol!=""){
			wrapper.setSymbol(symbol, addView);
		}
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AboutDialog))
		  .setTitle(title)
		  .setView(addView)
		  .setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO Error catching for empty/invalid fields
				String addsymbol=wrapper.getSymbol();
				Float shares=wrapper.getShares();
				Float pprice=wrapper.getPPrice();
                addsymbol=StockTicker.getInstance().checkStockSymbol(addsymbol);
				if(addsymbol!="" && shares >0 && pprice >0){
					Log.d(getCallingPackage(), "Adding "+addsymbol+" Owning "+shares+" shares at "+pprice+" per share.");
					Toast.makeText(StockActivity.this, "Adding "+addsymbol+" Owning "+shares+" shares at "+pprice+" per share.", Toast.LENGTH_LONG).show();
					db.addPortItem(addsymbol, shares, pprice);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
					new StockActivity.listPopulate().execute();
					//TODO dismiss keyboard
				}else{
					Toast.makeText(getApplicationContext(), "Adding item failed. Please populate all fields, Or check validity of symbol.", Toast.LENGTH_LONG).show();
				}
			}})
		  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing. Dismiss
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
				dialog.dismiss();
				
			}});
		
		builder.show();
		EditText et1 = (EditText)wrapper.getSymbolField();
		et1.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
		
	}
//	public void getPortItemList(){
//		
//		runOnUiThread(returnRes);
//	}
//	
	

	protected Dialog portEditDialog(View view) {
		int itemid;
		String itemSymbol;
		Float itemOwned;
		Float itemPPrice;
		
		TextView tvid= (TextView) view.findViewById(R.id.portID);
		TextView tvSymbol = (TextView) view.findViewById(R.id.portSymbols);
		TextView tvOwned = (TextView) view.findViewById(R.id.portOwned);
		TextView tvPPrice = (TextView) view.findViewById(R.id.portPaid);
		
		itemid=Integer.parseInt((String) tvid.getText());
		itemSymbol=(String) tvSymbol.getText();
		itemOwned=Float.parseFloat((String) tvOwned.getText());
		itemPPrice=Float.parseFloat((String) tvPPrice.getText());
		
		final PortItem editItem=db.getPortItem(itemid);
		LayoutInflater inflater=LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(),R.style.AboutDialog));
		View editView=inflater.inflate(R.layout.portalert, null);
		final PortDialogWrapper editWrapper=new PortDialogWrapper(editView);
		editWrapper.setSymbol(itemSymbol, editView);
		editWrapper.setShares(itemOwned, editView);
		editWrapper.setPPrice(itemPPrice, editView);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(StockActivity.this,R.style.AboutDialog));
		builder.setTitle("Edit Item");
		builder.setView(editView);
		builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editItem.putSymbol(editWrapper.getSymbol());
				editItem.putOwned(editWrapper.getShares());
				editItem.putPaidPrice(editWrapper.getPPrice());
				db.updatePortItem(editItem);
				new StockActivity.listPopulate().execute();
				
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//do Nothing, dismiss
				dialog.cancel();
			}});
		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				db.deletePortItem(editItem);
				new StockActivity.listPopulate().execute();
			}});
		return builder.create();
	}
	public class initialListPopulate extends AsyncTask<Void, Void, Void>{
		private ProgressDialog progressDialog;
		protected void onPreExecute(){
			progressDialog=ProgressDialog.show(StockActivity.this, "Please wait...", "Retreiving data ...",true);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				portItemList = new ArrayList<PortItem>();
				Log.d("Finances Bridge", "database count is "+String.valueOf(db.getPortCount()));
				ArrayList<Integer> portIds = new ArrayList<Integer>(db.getPortIdList());
				for(int i=0;i<portIds.size();i++){
					Log.d(getLocalClassName(),"processing item# "+String.valueOf(i)+" with id of "+portIds.get(i));
					PortItem item=db.getPortItem(portIds.get(i));
					if (item!=null){
						portItemList.add(item);
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
			Log.d(getLocalClassName(),"Initializing adapter with portItemList of "+portItemList.size()+" objects");
			m_adapter=new PortListAdapter(StockActivity.this, R.layout.portlist, portItemList);
			Log.d(getLocalClassName(),"Setting adapter");
			ListView portList=(ListView)findViewById(R.id.portItems);
			LayoutInflater inflater = LayoutInflater.from(StockActivity.this);
			View header = inflater.inflate(R.layout.portlistheader, (ViewGroup) findViewById(R.id.port_list_root));
			portList.addHeaderView(header,null,false);
			portList.setAdapter(StockActivity.this.m_adapter);
			portList.setOnItemLongClickListener(listlistener);
		}
		
	}
	public class listPopulate extends AsyncTask<Void, Void, Void>{
		private ProgressDialog progressDialog;
		protected void onPreExecute(){
			progressDialog=ProgressDialog.show(StockActivity.this, "Please wait...", "Retreiving data ...",true);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				portItemList = new ArrayList<PortItem>();
				Log.d("Finances Bridge", "database count is "+String.valueOf(db.getPortCount()));
				ArrayList<Integer> portIds = new ArrayList<Integer>(db.getPortIdList());
				for(int i=0;i<portIds.size();i++){
					Log.d(getLocalClassName(),"processing item# "+String.valueOf(i)+" with id of "+portIds.get(i));
					PortItem item=db.getPortItem(portIds.get(i));
					if (item!=null){
						portItemList.add(item);
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
			Log.d(getLocalClassName(),"Initializing adapter with portItemList of "+portItemList.size()+" objects");
			m_adapter=new PortListAdapter(StockActivity.this, R.layout.portlist, portItemList);
			Log.d(getLocalClassName(),"Setting adapter");
			ListView portList=(ListView)findViewById(R.id.portItems);
			portList.setAdapter(StockActivity.this.m_adapter);
			portList.setOnItemLongClickListener(listlistener);
		}
		
	}
	public Stock retrieveQuote(String symbol) {
		
		Stock stock = db.getRecentQuote(symbol);
//				StockTicker.getInstance().getStockPrice(symbol);
		return stock;
	}
	public void onDestroy(){
		super.onDestroy();
		Log.v(getPackageName(), "Closing app.");
		thread.interrupt();
		db.close();
		
	}
	
}
