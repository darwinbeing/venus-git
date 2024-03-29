package com.venus.service.strategy.gann;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.domain.*;
import com.venus.domain.enums.*;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.analysis.ExtremeAnalyze;
import com.venus.service.market.MarketDataService;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import com.venus.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by erix-mac on 15/10/25.
 */
@Slf4j
@Component
public class TopBottomStrategy extends AbstractTradeStrategy implements Strategy {

    private final static int TIME_WINDOW = -7;

    private final static int TOP_RANK = 1;
    private final static int PEAK_DELTA_PERCENTAGE = 3;
    private final static int PEAK_CONFIRM_PERCENTAGE = 5;
    private final static int PEAK_CONFIRM_DATE = 7;
    private final static int PEAK_ANAYSIS_GAP = 7;
    @Autowired
    private ExtremeAnalyze extremeAnalyze;


    private final static int MARKET_UP_DOWN_COMPARE = -5;
    private final static int WEIGHT = 100;
    private int month = 0;

    private PeakMonitor peakMonitor;

    private Map<String, Map<TimeWindow, List<MarketPeak>>> topMap = Maps.newConcurrentMap();
    private Map<String, Map<TimeWindow, List<MarketPeak>>> bottomMap = Maps.newConcurrentMap();
    private Date peakAnalyzeDate;

    private void cleanUp() {
        this.peakMonitor = null;
        this.peakAnalyzeDate = null;
        this.month = 0;
    }

    private List<MarketPeak> getPeaks(String code, TimeWindow window, Peak peak) {
        Map<TimeWindow, List<MarketPeak>> map = peak.equals(Peak.TOP) ? topMap.get(code) : bottomMap.get(code);
        return map == null ? null : map.get(window);
    }

    private void setPeaks(String code, TimeWindow window, Peak peak, Date date, List<MarketPeak> peaks) {
        Map<TimeWindow, List<MarketPeak>> map = peak.equals(Peak.TOP) ? topMap.get(code) : bottomMap.get(code);

        if (map == null) {
            this.cleanUp();
            map = Maps.newConcurrentMap();
            map.put(window, peaks);
            if (peak.equals(Peak.TOP)) {
                topMap.put(code, map);
            } else {
                bottomMap.put(code, map);
            }
        } else {
            map.put(window, peaks);
        }

        this.peakAnalyzeDate = date;
    }


    private void initPeaksMap(Stock stock, TimeWindow window, Date analysisDate) {

        List<MarketPeak> topPeaks = this.getPeaks(stock.getCode(),window, Peak.TOP);
        List<MarketPeak> bomPeaks = this.getPeaks(stock.getCode(),window,Peak.BOTTOM);
        int dateGap = DateUtils.daysBetween(this.peakAnalyzeDate, analysisDate);

        if ((topPeaks == null || topPeaks.size() == 0) || (bomPeaks == null || bomPeaks.size() == 0) ) {
            extractUpdatePeaks(stock, window, analysisDate,this.month);
        }

        this.updateLatestTop(stock,window, analysisDate,this.getCurrentMarket(stock.getCode(),analysisDate));
    }

    private void extractUpdatePeaks(Stock stock, TimeWindow window, Date analysisDate, int month ){
        Map<Peak, List<MarketPeak>> peaks = extremeAnalyze.determinMarketPeaks(stock, TimeWindow.getTimeWindow(TimePeriod.DAILY, analysisDate, TIME_WINDOW, month, 0), TOP_RANK);

        this.setPeaks(stock.getCode(), window, Peak.TOP, analysisDate, MarketPeak.extractPeak(peaks.get(Peak.TOP), WEIGHT));
        this.setPeaks(stock.getCode(), window, Peak.BOTTOM,analysisDate, MarketPeak.extractPeak(peaks.get(Peak.BOTTOM), WEIGHT));
    }


    @Override
    public String getName() {
        return TradeStrategy.TopBottomStrategy.toString();
    }

    @Override
    public List<TradeAction> analysis(TradeAnalysisInfo info){
        List<TradeAction> result = Lists.newArrayList();

        Stock stock = info.getStock();
        Date date = info.getAnalysisDate();
        Trade trade = info.getTrade();
        TimeWindow window = info.getTimeWindow();

        HistoricalData market = this.getCurrentMarket(stock.getCode(),date);
        Date today = date;

        this.initPeaksMap(stock, window, date);

        double marketPrice = info.getMarketPrice();
        TradeAction action = new TradeAction(TradeStrategy.TopBottomStrategy, TradeDirection.NONE, stock, today, today, marketPrice, marketPrice);
        if (isTriggerTrade(stock, window, action, date, market)) {
            System.out.println("----------------------------------------->>>>> TopBottomStrategy : Today: " + today + "  Action: " + action.getTradeDirection().toString() + " market: " + marketPrice);
            result.add(action);
        }

        return result;
    }


    private void updateLatestTop(Stock stock, TimeWindow window, Date date, HistoricalData data){
        List<MarketPeak> top = this.getPeaks(stock.getCode(), window, Peak.TOP);
        List<MarketPeak> bom = this.getPeaks(stock.getCode(), window, Peak.BOTTOM);
        double market = data.getClose();

        boolean max = true;
        for ( MarketPeak p : top ){
            if ( p.getPeak() > market ){
                max = false;
                break;
            }
        }

        if ( max ){
            top.get(top.size()-1).setPeak(market);
            top.get(top.size()-1).setData(data);
            this.peakAnalyzeDate = date;
        }

        boolean min = true;
        for ( MarketPeak p : bom ){
            if ( p.getPeak() < market ){
                min = false;
                break;
            }
        }

        if ( min ){
            bom.get(bom.size()-1).setPeak(market);
            bom.get(bom.size()-1).setData(data);
            this.peakAnalyzeDate = date;
        }
    }


    private boolean isTriggerTrade(Stock stock, TimeWindow window, TradeAction action, Date analysisDate, HistoricalData market) {
        List<MarketPeak> top = this.getPeaks(stock.getCode(), window, Peak.TOP);
        List<MarketPeak> bom = this.getPeaks(stock.getCode(), window, Peak.BOTTOM);

        Trend trend = this.getMarketTrend(stock.getCode(), analysisDate, market.getClose());
        action.setTradeDirection(TradeDirection.NONE);
        PeakTradeResult result;
        //double marketPrice = (trend.equals(Trend.UP) ? market.getHigh() : market.getLow());
        double marketPrice = market.getClose();

        if (peakMonitor == null) {
            result = detectPeakTrade(market, (trend.equals(Trend.UP) ? top : bom), trend);
            if (result.isClosePeak) {
                peakMonitor = new PeakMonitor((trend.equals(Trend.UP) ? Peak.TOP : Peak.BOTTOM), analysisDate, result.peak, marketPrice , trend);
            }
        } else if (comfirmPeakTrade(peakMonitor, analysisDate, marketPrice)) {

            switch ( this.peakMonitor.trend ) {
                case UP:
                    action.setTradeDirection(TradeDirection.SELL);
                    break;
                case DOWN:
                    action.setTradeDirection(TradeDirection.BUY);
                    break;
            }
        }

        TradeDirection direction = action.getTradeDirection();
        boolean isTrigger = direction.equals(TradeDirection.BUY) || direction.equals(TradeDirection.SELL);

        if (isTrigger) {
            System.out.println("CONFIRMED --------");
            MarketPeak.printShortString(getPeaks(stock.getCode(),window, Peak.TOP));
            MarketPeak.printShortString(getPeaks(stock.getCode(),window, Peak.BOTTOM));
            System.out.println("\n*******************peakMonitor: " + this.peakMonitor.toString());
            peakMonitor = null;
        }

        return isTrigger;
    }

    private boolean comfirmPeakTrade(PeakMonitor peakMonitor, Date analysisDate, double market) {
        int dayGap = DateUtils.daysBetween(peakMonitor.getMonitorDate(), analysisDate);
        if (dayGap > PEAK_CONFIRM_DATE) {
            this.peakMonitor = null;
            return false;
        }

        boolean isConfrimed = false;
        this.peakMonitor.updatePeakMonitorIfRequired(analysisDate,market);
        double delta = market - peakMonitor.getMarketPrice();
        double percentage = ((market - peakMonitor.getMarketPrice()) / peakMonitor.getMarketPrice()) * 100;
        switch (peakMonitor.getTrend()) {
            case UP:
                isConfrimed = delta < 0 && percentage <= PEAK_CONFIRM_PERCENTAGE * -1;
                break;
            case DOWN:
                isConfrimed = delta > 0 && percentage >= PEAK_CONFIRM_PERCENTAGE * 1;
                break;
        }

        return isConfrimed;
    }


    private Trend getMarketTrend(String code, Date today, double market) {

        Trend trend = Trend.NONE;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, MARKET_UP_DOWN_COMPARE);

        HistoricalData m = this.marketService.getMarketHistoricalData(code, TimePeriod.DAILY, calendar.getTime());

        if (m != null) {
            trend = market > m.getClose() ? Trend.UP : Trend.DOWN;
        }

        return trend;
    }

    private PeakTradeResult detectPeakTrade(HistoricalData market, List<MarketPeak> peaks, Trend trend) {

        PeakTradeResult result = new PeakTradeResult(false, -1, trend);
        for (MarketPeak p : peaks) {
            result.setPeak(p.getPeak());
            //double marketPrice = trend.equals(Trend.UP) ? market.getHigh() : market.getLow();
            double marketPrice = market.getClose();
            double delta = ((marketPrice - p.getPeak()) / p.getPeak()) * 100;
            result.setClosePeak(Math.abs(delta) <= PEAK_DELTA_PERCENTAGE);

            if ( result.isClosePeak ){
              /*  System.out.println(">>>>>>>>>>  Current Market " + market.getDate().toString() + " Pice: " + marketPrice + " Close To: " + p.getData().getDate().toString() + " " + p.getPeak() + " Delta: " + delta);
                MarketPeak.printShortString(peaks);
                System.out.println("\n>>>>>>>>>>  Current Market End " + marketPrice);*/

                break;
            }
        }

        return result;
    }

}

@Data
@AllArgsConstructor
class PeakTradeResult {
    public boolean isClosePeak;
    public double peak;
    public Trend trend;
}

@Data
@AllArgsConstructor
class PeakMonitor {
    public Peak peakType;
    public Date monitorDate;
    public double peak;
    public double marketPrice;
    public Trend trend;

    private boolean isGreater(double market){
        return peakType.equals(Peak.TOP) ? market > marketPrice : market < marketPrice;
    }

    public void updatePeakMonitorIfRequired(Date date, double marketPrice){
        if ( isGreater(marketPrice) ){
            this.monitorDate = date;
            this.marketPrice = marketPrice;
        }
    }
}