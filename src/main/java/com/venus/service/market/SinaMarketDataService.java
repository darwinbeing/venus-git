package com.venus.service.market;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.domain.HistoricalData;
import com.venus.domain.enums.StockIndex;
import com.venus.domain.LiveData;
import com.venus.domain.enums.MarketDataProvider;
import com.venus.domain.enums.TimePeriod;
import com.venus.utils.HtmlUtils;
import com.venus.utils.URLUtils;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.venus.utils.DateUtils.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by erix-mac on 15/8/2.
 */
@Service
@Slf4j
public class SinaMarketDataService {

    public static final String SINA_FINANCE_URL_TODAY = "http://hq.sinajs.cn/list=";
    public static final String SINA_FINANCE_URL_HISTORY = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/{0}.phtml?year={1}&jidu={2}";
    public static final String SINA_FINANCE_INDEX_URL_HISTORY = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/{0}/type/S.phtml?year={1}&jidu={2}";

    public static final String SINA_REPONSE_CHAR = "\"";
    public static final String SINA_SPLIT = ",";
    public static final int SINA_MARKET_DATA_COLUMNS_LEN = 7;


    public Map<String, LiveData> getAllMarketIndexs() {
        return getMarketLiveData(StockIndex.values());
    }

    public LiveData getMarketData(StockIndex index) {
        return getMarketLiveData(new StockIndex[]{index}).get(0);
    }


    public Map<String, LiveData> getMarketLiveData(StockIndex[] indexs) {

        if (indexs == null || indexs.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (StockIndex index : indexs) {
            sb.append(index.getCode(MarketDataProvider.SINA)).append(SINA_SPLIT);
        }

        return getMarketLiveData(sb.toString());
    }

    public Map<String, LiveData> getMarketLiveData(String stockNames) {
        return getMarketLiveData(stockNames.split(SINA_SPLIT));

    }

    @SneakyThrows(IOException.class)
    public Map<String, LiveData> getMarketLiveData(String[] stockNames) {
        Map<String, LiveData> map = new HashMap<>();

        String url = extractURL(MarketDataProvider.toSINACode(stockNames));

        URL sinaURL = new URL(url);
        URLConnection con = sinaURL.openConnection();
        @Cleanup
        InputStreamReader ins = new InputStreamReader(con.getInputStream(), "GB2312");
        @Cleanup
        BufferedReader in = new BufferedReader(ins);
        String newLine = in.readLine();

        int i = 0;
        String name = stockNames[i];
        map.put(name, extractLiveMarketData(name, newLine));
        while ((newLine = in.readLine()) != null) {
            name = stockNames[++i];
            map.put(name, extractLiveMarketData(name, newLine));
        }

        return map;
    }

    public List<HistoricalData> getMarketDataIncremental(String code, Date fromDate){
        List<HistoricalData> datas = this.getMarketData(code, String.valueOf(extractYear(fromDate)), String.valueOf(extractQuarter(fromDate)));

        Date today = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while ( calendar.getTime().before(today) ){
            calendar.add(Calendar.MONTH, 3);

            List<HistoricalData> next = this.getMarketData(code,String.valueOf(extractYear(calendar.getTime())),String.valueOf(extractQuarter(calendar.getTime())));
            if ( next != null && next.size() > 0 ){
                datas.addAll(next);
            }
        }

        return datas;
    }

    public List<HistoricalData> getMarketData(String code) {
        List<HistoricalData> datas = Lists.newLinkedList();

        int year = getCurrentYear();
        int quarter  = getCurrentQuarter();

        List<HistoricalData> quarterData = this.getMarketData(code,String.valueOf(year),String.valueOf(quarter));

        while ( quarterData != null && quarterData.size() > 0 ){
            datas.addAll(quarterData);

            quarter --;
            if ( quarter == 0 ){
                year --;
                quarter = 4;
            }
            //System.out.println("Current Year: " + year + " quarter: " + quarter);
            quarterData = this.getMarketData(code,String.valueOf(year),String.valueOf(quarter));
        }

        return datas;
    }


    private String extractStockName(String context){
        String name = "";
        String req = "<title>(.*?)</title>";

        Pattern pattern = Pattern.compile(req);
        Matcher matcher = pattern.matcher(context);

        if (matcher.find()) {
            String title = matcher.group();
            name = title.substring(7, title.indexOf("("));
        }
        return name;
    }

    public List<HistoricalData> getMarketData(String code, String year, String quarter) {

        List<HistoricalData> datas = Lists.newArrayList();
        String url = code.indexOf("s") > 0 ? MessageFormat.format(SINA_FINANCE_INDEX_URL_HISTORY, code.replace("s",""), year, quarter) :  MessageFormat.format(SINA_FINANCE_URL_HISTORY, code, year, quarter);
        System.out.println("URL **********: " + url);

        String context = HtmlUtils.removeStringTabReturn(URLUtils.extractURLConext(url, "gb2312"));
        String req = "(?<=(date=)).*?(?=('>))|(?<=(\"<div align=\"center\">)).*?\\d{4}-\\d{2}-\\d{2}(?=(</div>))|(?<=(\"center\">))\\d{1}.*?(?=(</div>))";

        Pattern pattern = Pattern.compile(req);
        Matcher matcher = pattern.matcher(context);

        List<Map<Integer, String>> list = Lists.newLinkedList();
        Map<Integer, String> row = Maps.newHashMap();

        String stockName = this.extractStockName(context);
        int colIndex = 0;
        while (matcher.find()) {
            colIndex++;
            row.put(colIndex, matcher.group());

            if (colIndex == SINA_MARKET_DATA_COLUMNS_LEN) {
                list.add(row);
                colIndex = 0;

                HistoricalData market = extractHistroricalMarketData(code, stockName, row);
                //System.out.println("HistoricalData: " + market.toString());
                datas.add(market);

                row = Maps.newHashMap();
            }
        }

        return datas;
    }


    @SneakyThrows(ParseException.class)
    private HistoricalData extractHistroricalMarketData(String code, String name, Map<Integer, String> row) {
        HistoricalData m = new HistoricalData();

        m.setCode(code);
        m.setName(name);
        m.setPeriod(TimePeriod.DAILY);

       // System.out.println("HistoricalData: " + row.toString());

        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        m.setDate(format.parse(row.get(1)));
        m.setOpen(Double.parseDouble(row.get(2)));
        m.setHigh(Double.parseDouble(row.get(3)));
        m.setClose(Double.parseDouble(row.get(4)));
        m.setLow(Double.parseDouble(row.get(5)));
        m.setVolume(Long.parseLong(row.get(6)));
        m.setVolumeAmount(Double.parseDouble(row.get(7)));

        m.setAdjClose(m.getClose());

        return m;
    }

    @SneakyThrows(ParseException.class)
    private LiveData extractLiveMarketData(String code, String response) {

        if (response == null || response.length() == 0 || response.indexOf("FAILED") > 0)
            return null;

        LiveData m = new LiveData();

        int beginIndex = response.indexOf(SINA_REPONSE_CHAR);
        int endIndex = response.lastIndexOf(SINA_REPONSE_CHAR);
        String context = response.substring(beginIndex + 1, endIndex);
        String[] data = context.split(SINA_SPLIT);


        m.setCode(code);
        m.setName(data[0]);
        m.setOpen(Double.parseDouble(data[1]));
        m.setYesterdayClose(Double.parseDouble(data[2]));
        m.setCurrentPrice(Double.parseDouble(data[3]));
        m.setHigh(Double.parseDouble(data[4]));
        m.setLow(Double.parseDouble(data[5]));
        m.setCurrentPrice(Double.parseDouble(data[6]));
        m.setCurrentSellPrice(Double.parseDouble(data[7]));
        m.setVolume(Long.parseLong(data[8]));
        m.setVolumeAmount(Double.parseDouble(data[9]));

        m.setBuyVolume1(Long.parseLong(data[10]));
        m.setBuyPrice1(Double.parseDouble(data[11]));
        m.setBuyVolume2(Long.parseLong(data[12]));
        m.setBuyPrice2(Double.parseDouble(data[13]));
        m.setBuyVolume3(Long.parseLong(data[14]));
        m.setBuyPrice3(Double.parseDouble(data[15]));
        m.setBuyVolume4(Long.parseLong(data[16]));
        m.setBuyPrice4(Double.parseDouble(data[17]));
        m.setBuyVolume5(Long.parseLong(data[18]));
        m.setBuyPrice5(Double.parseDouble(data[19]));

        m.setSellVolume1(Long.parseLong(data[20]));
        m.setSellPrice1(Double.parseDouble(data[21]));
        m.setSellVolume2(Long.parseLong(data[22]));
        m.setSellPrice2(Double.parseDouble(data[23]));
        m.setSellVolume3(Long.parseLong(data[24]));
        m.setSellPrice3(Double.parseDouble(data[25]));
        m.setSellVolume4(Long.parseLong(data[26]));
        m.setSellPrice4(Double.parseDouble(data[27]));
        m.setSellVolume5(Long.parseLong(data[28]));
        m.setSellPrice5(Double.parseDouble(data[29]));

        SimpleDateFormat format = new SimpleDateFormat(LiveData.MARKET_DATE_FORMAT);
        m.setDate(format.parse(data[30]));
        m.setCurrentTime(data[31]);

        return m;

    }

    public String extractURL(String[] stockNames) {
        StringBuilder sb = new StringBuilder(SINA_FINANCE_URL_TODAY);

        for (String s : stockNames) {
            sb.append(s).append(SINA_SPLIT);
        }

        return sb.toString();
    }

    public String extractURL(StockIndex index) {
        return SINA_FINANCE_URL_TODAY + index.getCode(MarketDataProvider.SINA);
    }


    public static void main(String args[]) throws Exception {
        //List<HistoricalData> market = new SinaMarketDataService().getMarketData("600577");
       // List<HistoricalData> market = new SinaMarketDataService().getMarketData("600577","2006","4");
        //List<HistoricalData> market2 = new SinaMarketDataService().getMarketData("600577","2015","4");



        String code = "600577";

        String url = MessageFormat.format(SINA_FINANCE_URL_HISTORY, code, "2016", "1");
        System.out.println("URL **********: " + url);

        String context = HtmlUtils.removeStringTabReturn(URLUtils.extractURLConext(url,"gb2312"));
        String req = "<title>(.*?)</title>";

        System.out.println("Context " + context);
        Pattern pattern = Pattern.compile(req);
        Matcher matcher = pattern.matcher(context);

        if (matcher.find()) {
            String title = matcher.group();
            String name = title.substring(7, title.indexOf("("));
            System.out.println(name);
        }
    }



    public static void main1(String args[]) throws Exception {
        Map<String, LiveData> market = new SinaMarketDataService().getMarketLiveData("sh600577,sh600030");


        for (Map.Entry<String, LiveData> stock : market.entrySet()) {
            System.out.println(stock.getKey());
            System.out.println(stock.getValue());
        }

        Map<String, LiveData> indexs = new SinaMarketDataService().getAllMarketIndexs();
        for (Map.Entry<String, LiveData> stock : indexs.entrySet()) {
            System.out.println(stock.getKey());
            System.out.println(stock.getValue());
        }

    }
}
