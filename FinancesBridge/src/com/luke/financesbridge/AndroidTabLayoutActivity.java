package com.luke.financesbridge;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class AndroidTabLayoutActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabHost tabHost=getTabHost();
		
		TabSpec budgetSpec=tabHost.newTabSpec("Budget");
		budgetSpec.setIndicator("Budget");
		Intent budgetIntent= new Intent (this, MainActivity.class);
		budgetSpec.setContent(budgetIntent);
		
		TabSpec portfolioSpec=tabHost.newTabSpec("Portfolio");
		portfolioSpec.setIndicator("Portfolio");
		Intent portfolioIntent = new Intent(this, StockActivity.class);
		portfolioSpec.setContent(portfolioIntent);
		
		tabHost.addTab(budgetSpec);
		tabHost.addTab(portfolioSpec);
	}
}
