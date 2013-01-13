package com.luke.financesbridge;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PortListAdapter extends ArrayAdapter<PortItem> {

	private ArrayList<PortItem> items;
	
	public PortListAdapter(Context context, int textViewResourceId, ArrayList<PortItem> objects) {
		super(context, textViewResourceId, objects);
		this.items=objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		StockDBHandler db=new StockDBHandler(getContext());
		if (v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v=vi.inflate(R.layout.portlist, null);
		}
		PortItem o =items.get(position);
		if (o != null){
			TextView tvid=(TextView)v.findViewById(R.id.portID);
			TextView tvSym=(TextView)v.findViewById(R.id.portSymbols);
			TextView tvShares=(TextView)v.findViewById(R.id.portOwned);
			TextView tvpprice=(TextView)v.findViewById(R.id.portPaid);
			TextView tvcprice=(TextView)v.findViewById(R.id.portCurrent);
			TextView tvchange=(TextView)v.findViewById(R.id.portDiff);
			String symbol=o.getSymbol();
//			Log.d("Finances", "Symbol: "+symbol);
			Float current=db.getRecentQuotePriceOnly(symbol);
//			Log.d("Finances", "Price: "+String.valueOf(current));
			Float paid=o.getPaidPrice();
//			Log.d("Finances", "Paid: "+String.valueOf(paid));
			if (tvid !=null){
				tvid.setText(String.valueOf(o.getID()));
			}
			if (tvSym !=null){
				tvSym.setText(symbol);
			}
			if (tvShares !=null){
				tvShares.setText(String.valueOf(o.getOwned()));
			}
			if (tvpprice !=null){
				tvpprice.setText(String.valueOf(paid));
			}
			if (tvcprice !=null){
				tvcprice.setText(String.valueOf(current));
			}
			if (tvchange !=null){
				Float change=o.getChange(current, paid);
				if (change<0){
					tvchange.setTextColor(Color.parseColor("#FFFF0000"));
				}
				tvchange.setText(String.valueOf(change));
			}	
		}
		return v;
	}
		
}
