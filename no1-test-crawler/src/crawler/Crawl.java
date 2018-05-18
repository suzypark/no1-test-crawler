package crawler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class Crawl {
	private static final Logger logger = Logger.getLogger(Crawl.class.getName());
	//Apache HttpClient
	private static CloseableHttpClient client;
	//URL
	private static final String URL = "https://no1s.biz/";
	//正規表現
	private static final Pattern REG_EXP_URL = Pattern.compile("<a[^>]*href=[\\\"']?([^>\\\"']+)[\\\"']?[^>]*>");
	private static final Pattern REG_EXP_TITLE = Pattern.compile("\\<title>(.*)\\</title>",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	/**
	 * 「URL」と「titleタグのテキスト」が一覧で表示される
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		client = HttpClientBuilder.create().build();
		Crawl jm = new Crawl();
		// URLにHTTPS通信を行い、HTMLを取得
		String htmlStringToUrl = jm.get(URL);

		LinkedHashSet<String> aTagUrlList = new LinkedHashSet<String>();
		LinkedHashMap<String, String> urlTitleList = new LinkedHashMap<>();
		// Aタグのリンク先を抽出
		Matcher matcher = REG_EXP_URL.matcher(htmlStringToUrl);
		while (matcher.find()) {
			aTagUrlList.add(matcher.group(1).trim());
		}
		// logger.info(aTagUrlList.toString());
		// 抽出したリンク先に再度HTTPSアクセスを行います
		for (String aTagUrl : aTagUrlList) {
			if (aTagUrl.equals("/") || aTagUrl.equals("#header")) {
				continue;
			}
			String htmlStringToTitle = jm.get(aTagUrl);
			Matcher matcherTitle = REG_EXP_TITLE.matcher(htmlStringToTitle);
			String title = null;
			while (matcherTitle.find()) {
				title = matcherTitle.group(1).trim();
			}
			urlTitleList.put(title, aTagUrl);
		}
		// logger.info(urlTitleList.toString());

		System.out.println("=========================================");
		System.out.println(Crawl.class.getName() + " 「URL」と「titleタグのテキスト」が一覧");
		System.out.println("=========================================");
		Iterator<String> iterator = urlTitleList.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			// 実行結果(「URL」と「titleタグのテキスト」が一覧)表示
			System.out.println(urlTitleList.get(key) + "  " + key);
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * GET
	 * 
	 * @param url
	 * @return HTML
	 */
	public String get(String url) {
		try {
			HttpGet get = new HttpGet(url);
			logger.info("GET : " + get.getURI());
			ResponseHandler<String> rh = new BasicResponseHandler();
			String execute = client.execute(get, rh);
			return execute;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
}
