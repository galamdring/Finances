package com.luke.financesbridge;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class DialogWrapper {
	    EditText titleField=null;
	    EditText valueField=null;
	    View base=null;
	    
	    DialogWrapper(View base) {
	      this.base=base;
	      valueField=(EditText)base.findViewById(R.id.value);
	    }
	    void setTitle(String text, View view){
	    	Log.w("Finances", text);
	    	titleField=(EditText)view.findViewById(R.id.title);
	    	titleField.setText((CharSequence) text);
	    }
	    void setAmount(Float amount, View view){
	    	valueField=(EditText)view.findViewById(R.id.value);
	    	valueField.setText((CharSequence) String.valueOf(amount));
	    }
	    
	    String getTitle() {
	      return(getTitleField().getText().toString());
	    }
	    
	    float getValue() {
	    	try{
	    		return(Float.valueOf(getValueField().getText().toString()).floatValue());
	    	}catch(Exception NumberFormatException){
	    		return 0;
	    	}
	    }
	    
	    private EditText getTitleField() {
	      if (titleField==null) {
	        titleField=(EditText)base.findViewById(R.id.title);
	      }
	      
	      return(titleField);
	    }
	    
	    private EditText getValueField() {
	      if (valueField==null) {
	        valueField=(EditText)base.findViewById(R.id.value);
	      }
	      
	      return(valueField);
	    }
}

