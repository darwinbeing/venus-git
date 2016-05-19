package com.venus.service.container.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.domain.Stock;
import com.venus.domain.Trade;

public class TradeReport {
	public static void printTradeDetail(List<Trade> trades) {
		HashMap<Stock, List<Trade>> stockTrades = TradeReport.groupByStock(trades);
		
//		for()
//		DecimalFormat format = new DecimalFormat("0.##");
//		for (Trade t : trades) {
//			TradeProfit p = t.getProfit();
//			System.out.println("Stock: " + t.getStock().getCode() + " " + t.getStock().getName()
//					+ " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: "
//					+ format.format(p.getProfit()) + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: "
//					+ format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell())
//					+ " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
//		}
	}
	
	private static HashMap<Stock, List<Trade>> groupByStock(List<Trade> trades){
		HashMap<Stock, List<Trade>> tradeMap = Maps.newHashMap();
		
		for(Trade t : trades){
			if(tradeMap.get(t.getStock()) == null){
				List<Trade> stockTrade = Lists.newArrayList(t);
				tradeMap.put(t.getStock(), stockTrade);
			}else{
				tradeMap.get(t.getStock()).add(t);
			}
		}
		return tradeMap;
	}
}
