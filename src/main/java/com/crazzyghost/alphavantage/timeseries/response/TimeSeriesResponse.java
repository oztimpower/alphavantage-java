package com.crazzyghost.alphavantage.timeseries.response;

import com.crazzyghost.alphavantage.AlphaVantageException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TimeSeriesResponse {


    private MetaData metaData;
    private List<StockUnit> stockUnits;
    private String errorMessage;

    private TimeSeriesResponse(MetaData metaData, List<StockUnit> stockUnits) {
        this.metaData = metaData;
        this.stockUnits = stockUnits;
        this.errorMessage = null;
    }

    private TimeSeriesResponse(String errorMessage){
        this.errorMessage = errorMessage;
        this.stockUnits = new ArrayList<>();
        this.metaData = MetaData.empty();
    }

    public static TimeSeriesResponse of(Map<String, Object> raw, boolean adjusted){
        Parser parser = new Parser(adjusted);
        return parser.parse(raw);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public List<StockUnit> getStockUnits() {
        return stockUnits;
    }

    public static class Parser {

        private final DateTimeFormatter DATE_WITH_FULL_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        private boolean adjusted;

        Parser(boolean adjusted){
            this.adjusted = adjusted;
        }

        TimeSeriesResponse parse(Map<String, Object> stringObjectMap) {

            //get the keys
            List<String> keys = new ArrayList<>(stringObjectMap.keySet());

            Map<String, String> md;
            Map<String, Map<String, String>> stockData;

            try{
                md = (Map<String, String>) stringObjectMap.get(keys.get(0));
                stockData = (Map<String, Map<String,String>>) stringObjectMap.get(keys.get(1));

            }catch (ClassCastException ex){
                return new TimeSeriesResponse((String)stringObjectMap.get(keys.get(0)));
            }

            MetaData metaData;
            if(md.get("4. Interval") == null){

                metaData = new MetaData(
                        md.get("1. Information"),
                        md.get("2. Symbol"),
                        md.get("3. Last Refreshed"),
                        md.get("4. Output Size"),
                        md.get("5. Time Zone")
                );
            }else{

                metaData = new MetaData(
                        md.get("1. Information"),
                        md.get("2. Symbol"),
                        md.get("3. Last Refreshed"),
                        md.get("4. Interval"),
                        md.get("5. Output Size"),
                        md.get("6. Time Zone")
                );
            }

            List<StockUnit> stockUnits =  new ArrayList<>();


            for (Map.Entry<String, Map<String, String>> e : stockData.entrySet()) {


                String date = e.getKey();
                if(date.length() == 10){
                    date = date + " 00:00:00";
                }else if(date.length() == 16){
                    date = date + ":00";
                }
                LocalDateTime d =  LocalDateTime.parse(date, DATE_WITH_FULL_TIME_FORMAT);


                Map<String, String> m = e.getValue();
                StockUnit.Builder stockUnit = StockUnit.builder();
                stockUnit.time(d);
                stockUnit.open(Double.parseDouble(m.get("1. open")));
                stockUnit.high(Double.parseDouble(m.get("2. high")));
                stockUnit.low(Double.parseDouble(m.get("3. low")));
                stockUnit.close(Double.parseDouble(m.get("4. close")));
                if (!adjusted) {
                    stockUnit.volume(Long.parseLong(m.get("5. volume")));
                } else {
                    stockUnit.adjustedClose(Double.parseDouble(m.get("5. adjusted close")));
                    stockUnit.volume(Long.parseLong(m.get("6. volume")));
                    stockUnit.dividendAmount(Double.parseDouble(m.get("7. dividend amount")));
                    if (m.get("8. split coefficient") != null)
                        stockUnit.splitCoefficient(Double.parseDouble(m.get("8. split coefficient")));
                }
                stockUnits.add(stockUnit.build());

            }

            return  new TimeSeriesResponse(metaData, stockUnits);
        }
    }


    @Override
    public String toString() {
        return "ForexResponse{" +
                "metaData=" + metaData +
                ", forexUnits=" + stockUnits +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

