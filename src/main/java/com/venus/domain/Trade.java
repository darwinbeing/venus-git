package com.venus.domain;

import com.google.common.collect.Lists;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.vo.CSVSupport;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.strategy.PositionStrategy;
import com.venus.utils.CSVUtils;
import com.venus.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/8/16.
 */
@Data
@Builder
@AllArgsConstructor
@Slf4j
public class Trade implements CSVSupport {

    private final static String FILE_SIMULATE_LOCATION = "trade.simulate.location";
    private final static String FILE_TRADE_LOCATOIN = Constants.MARKET_FILE_BACK_TEST_CASE_LOCATION + "trade.csv";
    private final static String FILE_TRANSACTION_LOCATOIN = Constants.MARKET_FILE_BACK_TEST_CASE_LOCATION + "transaction.csv";

    private Stock stock;
    private double marketPrice;
    private List<Transaction> transactions = Lists.newArrayList();
    private double capital = 0;
    @Getter
    private double totalCapital = 0;
    private PyramidPosition pyramidPosistion = new PyramidPosition();
    private double highestProfit;

    private static TimeWindow timeWindow;

    public Trade() {
    }

    public Trade(String code, double marketPrice) {
        this(new Stock(code), marketPrice, 0);
    }

    public Trade(Stock stock, double marketPrice, double capital) {
        this.stock = stock;
        this.marketPrice = marketPrice;
        this.capital = capital;
        this.totalCapital = capital;
    }

    public double getMarketValue() {
        return this.marketPrice * this.getPositions();
    }

    public long getPositions() {

        long positions = 0;

        for (Transaction trans : transactions) {
            if (trans.getDirection() == TradeDirection.BUY) {
                positions += trans.getPositions();
            } else if (trans.getDirection() == TradeDirection.SELL) {
                positions -= trans.getPositions();
            }
        }

        return positions;
    }

    public boolean hasTransaction() {
        return this.transactions != null && this.transactions.size() > 0;
    }

    public TradeProfit getProfit() {
        double buy = 0, sell = 0, commission = 0, stampTax = 0;

        for (Transaction trans : this.transactions) {
            if (trans.getDirection() == TradeDirection.BUY) {
                buy += (trans.getAmount());
            } else if (trans.getDirection() == TradeDirection.SELL) {
                sell += (trans.getAmount());
            }

            commission += trans.getCommission();
            stampTax += trans.getStampTax();
        }

        double profit = (this.getMarketValue() + sell) - buy;
        double netProfit = profit - (commission + stampTax);
        double totalProfitPercentage = 0;

        if ( buy < this.totalCapital )
            totalProfitPercentage = (netProfit / buy) * 100;
        else
            totalProfitPercentage = (this.totalCapital == 0 ? 0 : netProfit / this.totalCapital) * 100;

        return new TradeProfit(buy, sell, profit, commission, stampTax, netProfit, totalProfitPercentage);
    }

    public Trade trade(Transaction transaction) {
        return trade(transaction, false);
    }

    public Trade trade(Transaction transaction, boolean nettingHandled) {
        this.transactions.add(transaction);
        this.setCapital(this.calculateCapital(transaction));

        if (!nettingHandled && transaction.getDirection().equals(TradeDirection.SELL)) {
            this.nettingPositionsUpdate(transaction);
        }

        return this;
    }

    private void nettingPositionsUpdate(Transaction tran) {
        List<Transaction> trans = this.getTransactions();
        long positions = tran.getPositions();
        for (int i = trans.size() - 1; i >= 0; i--) {
            if (positions <= 0) {
                //log.info("####### Close Transaction" + tran.toString());
                tran.setClosed(true);
                break;
            }

            Transaction t = trans.get(i);
            if (!t.getDirection().equals(TradeDirection.BUY) || t.isClosed() || t.getNetPositions() == 0)
                continue;

            long delta = t.getNetPositions() - positions;
            positions -= t.getNetPositions();

            t.setNetPositions(delta > 0 ? delta : 0);
            t.setClosed(delta <= 0);

            if (delta <= 0) {
                //log.info("####### Close Transaction" + tran.toString());
            }
        }
    }

    public Trade trade(List<TradeAction> actions, PositionStrategy strategy) {
        for (TradeAction action : actions) {
            this.pyramidPosistion = strategy.analysis(this, action);
            if ( pyramidPosistion.getPositions() <= 0) {
                continue;
            }
            this.addTradeAction(action, (action.isSettingTradePositions() ? action.getPositions() : this.pyramidPosistion.getPositions()) );
        }

        return this;
    }


    private void addTradeAction(TradeAction action, long positions) {
        if (!action.getTradeDirection().equals(TradeDirection.BUY) && !action.getTradeDirection().equals(TradeDirection.SELL))
            return;

        Transaction tran = new Transaction(action.getTradeDate(), action.getTradeDirection(), stock, action.getStrategy(), action.getTradePrice(), positions);

        DecimalFormat f = new DecimalFormat("0.##");

        if (validate(tran)) {
            //log.info("---- " + action.getStock().getCode() + " Make Transaction{ Date: " + HistoricalData.toMarketDate(action.getTradeDate()) + " Strategy: " + action.getStrategy() + " Direction: " + action.getTradeDirection().toString() + " Positions: " + positions + " Cost:" + f.format(action.getTradePrice() * positions) + " Profit: " + f.format(action.getProfit()) + " Trade Price:" + action.getTradePrice() + "  Trans Price: " + action.getOrignalTransPrice() + " Total Capital: " + f.format(this.getCapital()) + "}");
            log.info("---- " + action.getStock().getCode() + " Make Transaction{ Date: " + HistoricalData.toMarketDate(action.getTradeDate()) + " Strategy: N/A Direction: " + action.getTradeDirection().toString() + " Positions: " + positions + " Cost:" + f.format(action.getTradePrice() * positions) + " Profit: " + f.format(action.getProfit()) + " Trade Price:" + action.getTradePrice() + "  Trans Price: " + action.getOrignalTransPrice() + " Total Capital: " + f.format(this.getCapital()) + "}");

            this.trade(tran, false);
        }
    }

    private double calculateCapital(Transaction tran) {

        double ajdCapital = this.getCapital();

        if (tran.getDirection().equals(TradeDirection.BUY)) {
            ajdCapital = this.getCapital() - tran.getAmount();
        } else if (tran.getDirection().equals(TradeDirection.SELL)) {
            ajdCapital = this.getCapital() + tran.getAmount();
        }

        return ajdCapital;
    }

    private boolean validate(Transaction trans) {

        boolean result = true;

        switch (trans.getDirection()) {
            case BUY:
                double adjCapital = this.calculateCapital(trans);

                if (adjCapital < 0) {
                    log.info("REJECT! Long positions > current totalCapital! current capital: " + this.getCapital() + " long: " + trans.getAmount());
                    result = false;
                }
                break;
            case SELL:
                if (trans.getPositions() > this.getPositions()) {
                    log.info("REJECT! Short positions > current holding positions! current: " + this.getPositions() + " short: " + trans.getPositions());
                    result = false;
                }
                break;
            default:
                result = true;
                break;
        }

        return result;
    }


    public static void toCSVFile(String fileName, TimeWindow window, List<Trade> trades, boolean isTransaction) {
        String fullName = Constants.MARKET_FILE_BACK_TEST_CASE_LOCATION + fileName;
        timeWindow = window;
        String dateFileName = "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv";
        CSVUtils.writeCSV(fullName + dateFileName, trades, true);

        if (isTransaction) {
            for (Trade trade : trades) {
                CSVUtils.writeCSV(fullName + "_Transactions" + dateFileName, trade.getTransactions(), true);
            }
        }
    }

    public void toCSVFile(boolean append, boolean isTransaction) {
        CSVUtils.writeCSV(FILE_TRADE_LOCATOIN, Lists.newArrayList(this), append);

        if (isTransaction) {
            CSVUtils.writeCSV(FILE_TRANSACTION_LOCATOIN, this.getTransactions(), append);
        }
    }

    @Override
    public String[] getCSVHeader() {
        return new String[]{"Anaysis Date", "Time Window", "Stock", "Total Capital", "Total Capital Profit%", "Profit%", "Profit", "Total Positions", "Market Price", "Transaction Count"};
    }

    @Override
    public String[] getCSVRecord() {
        DecimalFormat format = new DecimalFormat("0.##");
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");


        return new String[]{
                sFormat.format(new Date()),
                timeWindow == null ? "" : timeWindow.toShortString(),
                this.getStock().getCode(),
                String.valueOf(format.format(this.capital)),
                String.valueOf(format.format(this.getProfit().getTotalProfitPercentage())),
                String.valueOf(format.format(this.getProfit().getProfit())),
                String.valueOf(this.getPositions()),
                String.valueOf(format.format(this.getMarketPrice())),
                String.valueOf(this.getTransactions().size())
        };
    }


    public static void printTradeResult(TimeWindow window, List<Trade> trades) {

        if ( trades == null || trades.size() == 0 )
            return;

        System.out.println("\n >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade " + window.toString() + ">>>>>>>>>>>>>> >>>>>>>>>>>>>>: ");
        DecimalFormat format = new DecimalFormat("0.##");

        for ( Trade t : trades ){
            TradeProfit p = t.getProfit();
            System.out.println("Stock: " + t.getStock().getCode() + " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: " + format.format(p.getProfit()) + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: " + format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell()) + " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
        }
        System.out.println(" >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade >>>>>>>>>>>>>> >>>>>>>>>>>>>>");

    }
}
