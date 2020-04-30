package com.crazzyghost.alphavantage.indicator.request;

import com.crazzyghost.alphavantage.parameters.MAType;
import com.crazzyghost.alphavantage.parameters.SeriesType;

public class STOCHRSIRequest extends IndicatorRequest {

    private int time_period;
    private SeriesType series_type;
    private int fastKPeriod;
    private int fastDPeriod;
    private MAType fastDMaType;

    protected STOCHRSIRequest(Builder builder) {
        super(builder);
        this.fastKPeriod = builder.fastKPeriod == 0 ? 5 : builder.fastKPeriod;
        this.fastDPeriod = builder.fastDPeriod == 0 ? 3 : builder.fastDPeriod;
        this.fastDMaType = builder.fastDMaType == null ? MAType.SMA : builder.fastDMaType;
        this.time_period = builder.timePeriod;
        this.series_type = builder.seriesType;
    }
    
    public static class Builder extends IndicatorRequest.Builder<Builder> {

        public int timePeriod;
        public SeriesType seriesType;
        public int fastKPeriod;
        public int fastDPeriod;
        public MAType fastDMaType;
        
    
        public Builder fastKPeriod(int fastKPeriod){
            this.fastKPeriod = fastKPeriod;
            return this;
        }

        public Builder fastDPeriod(int fastDPeriod){
            this.fastDPeriod = fastDPeriod;
            return this;
        }

        public Builder fastDMaType(MAType type){
            this.fastDMaType = type;
            return this;
        }

        public Builder timePeriod(int timePeriod){
            this.timePeriod = timePeriod;
            return this;
        }

        public Builder seriesType(SeriesType seriesType){
            this.seriesType = seriesType;
            return this;
        }

        @Override
        public IndicatorRequest build() {
            return new STOCHRSIRequest(this);
        }
        
    }   
}