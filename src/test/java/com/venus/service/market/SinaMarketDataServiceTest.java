package com.venus.service.market;

import com.venus.base.AbstractTestCase;
import com.venus.domain.LiveData;
import com.venus.utils.HtmlUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaMarketDataServiceTest extends AbstractTestCase{

    @Autowired
    SinaMarketDataService sinaMarketDataService;

    @Test
    public void testGetAllMarketIndexs() throws Exception {

        Map<String,LiveData> indexs = sinaMarketDataService.getAllMarketIndexs();
        for (Map.Entry<String, LiveData> stock : indexs.entrySet()) {
            System.out.println(stock.getKey());
            System.out.println(stock.getValue());
        }

        Assert.assertNotNull(indexs);
        Assert.assertTrue(indexs.values().size() > 0);
    }

    @Test
    public void testGetMarketData() throws Exception {
        Map<String, LiveData> market = sinaMarketDataService.getMarketLiveData("sh600577,sh600030");

        for (Map.Entry<String, LiveData> stock : market.entrySet()) {
            System.out.println(stock.getKey());
            System.out.println(stock.getValue());
        }

        Assert.assertNotNull(market);
        Assert.assertTrue(market.values().size() > 0);

    }


    @Test
    public void testSinaParseHistoryData(){

        String req = "(?<=(\"center\">)).*?\\d{4}-\\d{2}-\\d{2}(?=(</div>))|(?<=(\"center\">))\\d{1}.*?(?=(</div>))";

        Pattern pattern = Pattern.compile(req);

        String context = "<tr class=\"gray\">\n" +
                "\t\t\t<td class=\"head\"><div align=\"center\">\n" +
                "\t\t\t2006-12-29\t\t\t</div></td>\n" +
                "\t\t\t<td><div align=\"center\">6.800</div></td>\n" +
                "\t\t\t<td><div align=\"center\">6.860</div></td>\n" +
                "\t\t\t<td><div align=\"center\">6.770</div></td>\n" +
                "\t\t\t<td class=\"tdr\"><div align=\"center\">6.700</div></td>\n" +
                "\t\t\t<td class=\"tdr\"><div align=\"center\">936791</div></td>\n" +
                "\t\t\t<td class=\"tdr\"><div align=\"center\">6328633</div></td>\n" +
                "\t\t  </tr>";

        Matcher matcher = pattern.matcher(HtmlUtils.removeStringTabReturn(context));
        while (matcher.find()) {
            System.out.println("Match: " + matcher.group());
        }

        System.out.println("Match End! ");

    }
}