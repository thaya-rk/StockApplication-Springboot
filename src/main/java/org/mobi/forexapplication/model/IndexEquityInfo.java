package org.mobi.forexapplication.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class IndexEquityInfo {

    private int priority;
    private String symbol;
    private String identifier;
    private String series;
    private double open;
    private double dayHigh;
    private double dayLow;
    private double lastPrice;
    private double previousClose;
    private double change;
    @JsonProperty("pChange")
    private double pChange;
    private long totalTradedVolume;
    private double totalTradedValue;
    private String lastUpdateTime;
    private double yearHigh;
    private double ffmc;
    private double yearLow;
    private double nearWKH;
    private double nearWKL;
    private String perChange365d;
    private String date365dAgo;
    private String chart365dPath;
    private String date30dAgo;
    private double perChange30d;
    private String chart30dPath;
    private String chartTodayPath;
    private double stockIndClosePrice;

    private Meta meta;

    // Getters and Setters for all fields
    // (can generate via IDE)

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public double getDayLow() {
        return dayLow;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPChange() {
        return pChange;
    }

    public void setPChange(double pChange) {
        this.pChange = pChange;
    }

    public long getTotalTradedVolume() {
        return totalTradedVolume;
    }

    public void setTotalTradedVolume(long totalTradedVolume) {
        this.totalTradedVolume = totalTradedVolume;
    }

    public double getTotalTradedValue() {
        return totalTradedValue;
    }

    public void setTotalTradedValue(double totalTradedValue) {
        this.totalTradedValue = totalTradedValue;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public double getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(double yearHigh) {
        this.yearHigh = yearHigh;
    }

    public double getFfmc() {
        return ffmc;
    }

    public void setFfmc(double ffmc) {
        this.ffmc = ffmc;
    }

    public double getYearLow() {
        return yearLow;
    }

    public void setYearLow(double yearLow) {
        this.yearLow = yearLow;
    }

    public double getNearWKH() {
        return nearWKH;
    }

    public void setNearWKH(double nearWKH) {
        this.nearWKH = nearWKH;
    }

    public double getNearWKL() {
        return nearWKL;
    }

    public void setNearWKL(double nearWKL) {
        this.nearWKL = nearWKL;
    }

    public String getPerChange365d() {
        return perChange365d;
    }

    public void setPerChange365d(String perChange365d) {
        this.perChange365d = perChange365d;
    }

    public String getDate365dAgo() {
        return date365dAgo;
    }

    public void setDate365dAgo(String date365dAgo) {
        this.date365dAgo = date365dAgo;
    }

    public String getChart365dPath() {
        return chart365dPath;
    }

    public void setChart365dPath(String chart365dPath) {
        this.chart365dPath = chart365dPath;
    }

    public String getDate30dAgo() {
        return date30dAgo;
    }

    public void setDate30dAgo(String date30dAgo) {
        this.date30dAgo = date30dAgo;
    }

    public double getPerChange30d() {
        return perChange30d;
    }

    public void setPerChange30d(double perChange30d) {
        this.perChange30d = perChange30d;
    }

    public String getChart30dPath() {
        return chart30dPath;
    }

    public void setChart30dPath(String chart30dPath) {
        this.chart30dPath = chart30dPath;
    }

    public String getChartTodayPath() {
        return chartTodayPath;
    }

    public void setChartTodayPath(String chartTodayPath) {
        this.chartTodayPath = chartTodayPath;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    public double getStockIndClosePrice() {
        return stockIndClosePrice;
    }

    public void setStockIndClosePrice(double stockIndClosePrice) {
        this.stockIndClosePrice = stockIndClosePrice;
    }

    // Nested Meta class
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private String symbol;
        private String companyName;
        private String industry;
        private List<String> activeSeries;
        private List<Object> debtSeries; // Use Object if you don't have a specific type
        private List<Object> tempSuspendedSeries;
        private boolean isFNOSec;
        private boolean isCASec;
        private boolean isSLBSec;
        private boolean isDebtSec;
        private boolean isSuspended;
        private boolean isETFSec;
        private boolean isDelisted;
        private String isin;

        // Getters and setters for Meta fields

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public List<String> getActiveSeries() {
            return activeSeries;
        }

        public void setActiveSeries(List<String> activeSeries) {
            this.activeSeries = activeSeries;
        }

        public List<Object> getDebtSeries() {
            return debtSeries;
        }

        public void setDebtSeries(List<Object> debtSeries) {
            this.debtSeries = debtSeries;
        }

        public List<Object> getTempSuspendedSeries() {
            return tempSuspendedSeries;
        }

        public void setTempSuspendedSeries(List<Object> tempSuspendedSeries) {
            this.tempSuspendedSeries = tempSuspendedSeries;
        }

        public boolean isFNOSec() {
            return isFNOSec;
        }

        public void setFNOSec(boolean FNOSec) {
            isFNOSec = FNOSec;
        }

        public boolean isCASec() {
            return isCASec;
        }

        public void setCASec(boolean CASec) {
            isCASec = CASec;
        }

        public boolean isSLBSec() {
            return isSLBSec;
        }

        public void setSLBSec(boolean SLBSec) {
            isSLBSec = SLBSec;
        }

        public boolean isDebtSec() {
            return isDebtSec;
        }

        public void setDebtSec(boolean debtSec) {
            isDebtSec = debtSec;
        }

        public boolean isSuspended() {
            return isSuspended;
        }

        public void setSuspended(boolean suspended) {
            isSuspended = suspended;
        }

        public boolean isETFSec() {
            return isETFSec;
        }

        public void setETFSec(boolean ETFSec) {
            isETFSec = ETFSec;
        }

        public boolean isDelisted() {
            return isDelisted;
        }

        public void setDelisted(boolean delisted) {
            isDelisted = delisted;
        }

        public String getIsin() {
            return isin;
        }

        public void setIsin(String isin) {
            this.isin = isin;
        }
    }
}