package com.luke.financesbridge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.util.Log;


public class StockTicker {
	private static final StockTicker stockInstance = new StockTicker();
	private static HashMap<String, Stock> stocks = new HashMap<String, Stock>();
	private static final long TWENTY_MIN=1200000;
	private StockTicker(){}
	public static StockTicker getInstance(){
		return stockInstance;
	}
	public Stock getStockPrice(String symbol){
		Stock stock = null;
		long currentTime=(new Date()).getTime();
		if (stocks.containsKey(symbol)){
			stock=stocks.get(symbol);
			if (currentTime-stock.getLastUpdated()>TWENTY_MIN){
				stock = refreshStockInfo(symbol, currentTime);
			}}
			else{
				stock = refreshStockInfo(symbol, currentTime);
			}
		
		return stock;
	}
	//This is synched so we only do one request at a time
    //If yahoo doesn't return stock info we will try to return it from the map in memory
	private synchronized Stock refreshStockInfo(String symbol, long time){
		try{
			symbol=symbol.replace(" ", "");
			URL yahoofin=new URL("http://finance.yahoo.com/d/quotes.csv?s="+symbol+"&f=sl1c1ogh&e=.csv");
			URLConnection yc =yahoofin.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) !=null){
				String[] yahooStockInfo=inputLine.split(",");
				Stock stockInfo=new Stock();
				stockInfo.putSymbol(yahooStockInfo[0].replaceAll("\"", ""));
				String current=yahooStockInfo[1].replaceAll("N/A", "0");
				String change=yahooStockInfo[2].replaceAll("N/A", "0");
				String dayopen=yahooStockInfo[3].replaceAll("N/A", "0");
				String daylow=yahooStockInfo[4].replaceAll("N/A", "0");
				String dayhigh=yahooStockInfo[5].replaceAll("N/A", "0");
                stockInfo.putCurrent(Float.valueOf(current));
                stockInfo.putChange(Float.valueOf(change));
                stockInfo.putDayOpen(Float.valueOf(dayopen));
                stockInfo.putDayLow(Float.valueOf(daylow));
                stockInfo.putDayHigh(Float.valueOf(dayhigh));
                stockInfo.putLastUpdated(time);
                stocks.put(symbol, stockInfo);
                break;
			}
			in.close();
		}catch(Exception ex){
			Log.e("Finances.Stock", "Unable to get stockinfo for:"+symbol+ex);
		}
		Log.d("Finances.StockTicker", "Stock Retrieved "+stocks.get(symbol).getSymbol());
		return stocks.get(symbol);
	}
    public String checkStockSymbol(String symbol){
        String retSymbol=new String();
        try{
            symbol=symbol.replace(" ","");
            symbol=symbol.toUpperCase(Locale.US);
            URL yahoofin=new URL("http://finance.yahoo.com/d/quotes.csv?s="+symbol+"&f=s&e=.csv");
            URLConnection yc =yahoofin.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine())!= null){
                String[] yahooStockInfo=inputLine.split(",");
                retSymbol=yahooStockInfo[0].replaceAll("\"","");
                break;
            }

        }catch(Exception ex){
            Log.e("Finances.Stock", "Unable to get stockinfo for:"+symbol+ex);

        } finally{
            if (retSymbol.toUpperCase(Locale.US)!= symbol){
                retSymbol="";
            }
        }
        return retSymbol;
    }
	
}
