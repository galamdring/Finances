package com.luke.financesbridge;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class PortDialogWrapper {
	    EditText symbolField=null;
	    EditText sharesField=null;
	    EditText ppriceField=null;
	    View base=null;
	    
	    PortDialogWrapper(View base) {
	      this.base=base;
	      sharesField=(EditText)base.findViewById(R.id.portShares);
	    }
	    void setSymbol(String text, View view){
//	    	Log.w("Finances", text);
	    	symbolField=(EditText)view.findViewById(R.id.portSymbol);
	    	symbolField.setText((CharSequence) text);
	    }
	    void setShares(Float amount, View view){
	    	sharesField=(EditText)view.findViewById(R.id.portShares);
	    	sharesField.setText((CharSequence) String.valueOf(amount));
	    }
	    void setPPrice(Float pprice, View view){
	    	ppriceField=(EditText)view.findViewById(R.id.portPPrice);
	    	ppriceField.setText((CharSequence) String.valueOf(pprice));
	    }
	    
	    String getSymbol() {
	      return(getSymbolField().getText().toString());
	    }
	    
	    float getShares() {
	    	try{
	    		if(getShareField().getText().toString()!=""){
		    		return(Float.valueOf(getShareField().getText().toString()));
		    	}
	    	}catch (Exception NumberFormatException){
	    		return 0;
	    	}
	    	return 0;
	    }
	    
	    float getPPrice(){
	    	try{
	    		if(getPPriceField().getText().toString()!=""){
		    		return(Float.valueOf(getPPriceField().getText().toString()));
		    	}
	    	}catch (Exception NumberFormatException){
	    		return 0;
	    	}
	    	
	    	return 0;
	    }
	    
	    public EditText getSymbolField() {
	      if (symbolField==null) {
	        symbolField=(EditText)base.findViewById(R.id.portSymbol);
	      }
	      
	      return(symbolField);
	    }
	    
	    public EditText getShareField() {
	      if (sharesField==null) {
	        sharesField=(EditText)base.findViewById(R.id.portShares);
	      }
	      
	      return(sharesField);
	    }
	    private EditText getPPriceField(){
	    	if (ppriceField==null){
	    		ppriceField=(EditText)base.findViewById(R.id.portPPrice);
	    	}
	    	return(ppriceField);
	    }
	  }

