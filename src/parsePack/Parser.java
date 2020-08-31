package parsePack;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.CashVO;
import domain.GraphVO;

public class Parser {
	// 환율을 계산할때
	public List<CashVO> getCashData() {
		String menu = null;
		String url = "https://finance.naver.com/marketindex/exchangeList.nhn";
		List<CashVO> mList = new ArrayList<>();
			
		try {
			Document doc = Jsoup.connect(url).get();
			Elements list = doc.select(".tbl_area tbody tr");
			
			list.forEach(value -> { 
				
				String name = value.selectFirst("td:nth-child(1) > a").text();
				String bias = value.selectFirst("td:nth-child(2)").text();
				String salePrice = value.selectFirst("td:nth-child(3)").text();
				String buyPrice = value.selectFirst("td:nth-child(4)").text();
				
				String urlLink = value.selectFirst("td:nth-child(1) > a").attr("href");
				
				CashVO temp = new CashVO(name, bias, salePrice, buyPrice, urlLink);
				mList.add(temp);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mList;
	}
	
	public List<GraphVO> getGraphData(String graphUrl) {
		List<GraphVO> mList = new ArrayList<>();
		try {
			Document doc2 = Jsoup.connect(graphUrl).get();
			Elements list = doc2.select(".tbl_exchange > tbody > tr");
			
			System.out.println(list.size());
			list.forEach(value -> {
				String date = value.selectFirst(".date").text(); //날짜
				String bias = value.selectFirst("td:nth-child(2)").text(); //매매기준율
				String persent = value.selectFirst("td:nth-child(3)").text(); //전일 대비
				
				GraphVO temp = new GraphVO(date, bias, persent);
				mList.add(temp);
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mList;
	}
}
