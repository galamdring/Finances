package com.luke.financesbridge;

public class Stock {
	//private variables
	int _id;
	String _symbol;
	Float _current;
	Float _dayopen;
	Float _daylow;
	Float _dayhigh;
	Float _owned;
	Float _pprice;
	Float _change;
	long _lastUpdated;
	
	//empty contructor
	public Stock(){
		
	}
	//Full contructor
	public Stock(int id,String symbol, Float current, Float dayopen, Float daylow, Float dayhigh, Float owned, Float pprice, Float change, long lastupdated){
		this._id=id;
		this._symbol=symbol;
		this._current=current;
		this._dayopen=dayopen;
		this._daylow=daylow;
		this._dayhigh=dayhigh;
		this._owned=owned;
		this._pprice=pprice;
		this._change=change;
		this._lastUpdated=lastupdated;
	}
	
	//gets
	public int getID(){
		return this._id;
	}
	public String getSymbol(){
		return this._symbol;
	}
	public Float getCurrent(){
		return this._current;
	}
	public Float getDayOpen(){
		return this._dayopen;
	}
	public Float getDayLow(){
		return this._daylow;
	}
	public Float getDayHigh(){
		return this._dayhigh;
	}
	public Float getOwned(){
		return this._owned;
	}
	
	public Float getChange(){
		return this._change;
	}
	public long getLastUpdated(){
		return this._lastUpdated;
	}
	
	
	//puts
	public void putID(int id){
		this._id=id;
	}
	public void putSymbol(String symbol){
		this._symbol=symbol;
	}
	public void putCurrent(Float current){
		this._current=current;
	}
	public void putDayOpen(Float open){
		this._dayopen=open;
	}
	public void putDayLow(Float low){
		this._daylow=low;
	}
	public void putDayHigh(Float high){
		this._dayhigh=high;
	}
	public void putOwned(Float owned){
		this._owned=owned;
	}
	
	public void putChange(Float change){
		this._change=change;
	}
	public void putLastUpdated(long lastupdated){
		this._lastUpdated=lastupdated;
	}
	
	
}
