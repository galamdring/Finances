package com.luke.financesbridge;

public class budgetItem{
	//private variables
	int _id;
	String _type;
	String _name;
	float _amount;
	boolean _selected;
	String _due;
	
	//Empty contructor
	public budgetItem(){
		
	}
	
	//contructor
	public budgetItem(int id, String type, String name, float amount, boolean selected, String due){
		this._id=id;
		this._type=type;
		this._name=name;
		this._amount=amount;
		this._selected=selected;
		this._due=due;
	}
	
	//constructor
	public budgetItem(String type, String name, float amount){
		this._type=type;
		this._name=name;
		this._amount=amount;
	}
	//getting id
	public int getID(){
		return this._id;
	}
	//set id
	public void setID(int id){
		this._id=id;
	}
	//get type
	public String getType(){
		return this._type;
	}
	//set type
	public void setType(String type){
		this._type=type;
	}
	//get name
	public String getName(){
		return this._name;
	}
	//set name
	public void setName(String name){
		this._name=name;
	}
	//get amount
	public float getAmount(){
		return this._amount;
	}
	//set amount
	public void setAmount(float amount){
		this._amount=amount;
	}
	public boolean isSelected(){
		return this._selected;
	}
	public void setSelected(boolean selected){
		this._selected=selected;
	}
	public String getDue(){
		return this._due;
	}
	public void setDue(String due){
		this._due=due;
	}
}