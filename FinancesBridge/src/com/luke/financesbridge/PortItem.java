package com.luke.financesbridge;

import android.util.Log;

public class PortItem {
	//private variables
	int _id;
	String _symbol;
	Float _owned;
	Float _pprice;
	StockDBHandler db;
	//empty constructor
	public PortItem(){
		
	}
	
	//full constructor
	public PortItem(int id, String symbol, Float owned, Float pprice){
		this._id=id;
		this._symbol=symbol;
		this._owned=owned;
		this._pprice=pprice;
	}
	public int getID(){
		return this._id;
	}
	public String getSymbol(){
		return this._symbol;
	}
	public Float getOwned(){
		return this._owned;
	}
	public Float getPaidPrice(){
		return this._pprice;
	}
	public void putID(int id){
		this._id=id;
	}
	public void putSymbol(String symbol){
		this._symbol=symbol;
	}
	public void putOwned(Float owned){
		this._owned=owned;
	}
	public void putPaidPrice(Float pprice){
		this._pprice=pprice;
	}

	public Float getChange(Float current, Float paid) {
		Float total=current+paid;
		return current-paid;
	}
}
