package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import play.Logger;
import play.libs.OAuth;
import play.libs.OAuth.ServiceInfo;
import play.libs.OAuth.TokenPair;
import play.mvc.Controller;
import trunkly.Trunk;
import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import common.Constants;

public class Application extends Controller {

	public static void index() {
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);
		
		User user = User.getGuess();
		TokenPair tp = user.getTokenPair();
		Logger.debug("token : %s", tp.token);
		Logger.debug("secret : %s", tp.secret);
		if (tp.token != null && !"".equals(tp.token)) {
			Weibo sina = new Weibo();

			sina.setToken(tp.token, tp.secret);
//			List<Status> statuses = new ArrayList();
			weibo4j.User tuser = null;
			try {
//				statuses = sina.getUserTimeline();               
				tuser = sina.verifyCredentials();
				renderArgs.put("me", tuser);
			} catch (WeiboException e) {
				Logger.debug(e.getMessage());
			}
		}
		render();
	}

	/**
	 * 连接新浪授权
	 * 
	 * @param callback
	 * @throws Exception
	 */
	public static void authenticate(String callback) throws Exception {
		ServiceInfo connectService = new ServiceInfo(
				Constants.SINA_REQUEST_TOKEN, Constants.SINA_ACCESS_TOKEN,
				Constants.SINA_AUTHORIZE, Constants.SINA_CONSUMER_KEY,
				Constants.SINA_CONSUMER_SECRET);
		if (OAuth.isVerifierResponse()) {
			// We got the verifier; now get the access token, store it and back
			// to index
			TokenPair tokens = OAuth.service(connectService)
					.requestAccessToken(User.getGuess().getTokenPair());
			Logger.debug("token %s", tokens.token);
			User.getGuess().setTokenPair(tokens);
			index();
		}
		OAuth sinaOAuth = OAuth.service(connectService);
		TokenPair tokens = sinaOAuth.requestUnauthorizedToken();
		// We received the unauthorized tokens in the OAuth object - store it
		// before we proceed
		Logger.debug("token %s", tokens.token);
		User.getGuess().setTokenPair(tokens);
		redirect(sinaOAuth.redirectUrl(tokens));
	}

	private static final Pattern p = Pattern.compile("http://[\\w\\.\\-\\/]+",
			Pattern.CASE_INSENSITIVE);

	/**
	 * 基于GAE的定时器，通过/war/WEB-INF/cron.xml配置
	 * 
	 */
	public static void sinaTimelineCron() {
		Logger.debug("sina timeline");
		// 获取当天的timeline
		// 分析是否含URL，如有获取分享URL中的title和原链接, note和text为微博内容
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);

		User user = User.getGuess();
		TokenPair tp = user.getTokenPair();
		Weibo sina = new Weibo();
		sina.setToken(tp.token, tp.secret);
		List<Status> statuses = new ArrayList();
		Trunk t = new Trunk();
		try {
			statuses = sina.getUserTimeline();
			for (Status status : statuses) {
				StringBuffer text = new StringBuffer(status.getText());
				// 如果是转发，将两个文本合并 “aaaaaa// @xxxxx: 原文”
				if (null != status.getRetweetDetails()) {
					text.append(" //@")
							.append(status.getRetweetDetails().getUser()
									.getName()).append(":")
							.append(status.getRetweetDetails().getText());
				}
				Matcher m = p.matcher(text);
				while (m.find()) {
					String url = text.substring(m.start(), m.end());
					try {
						Document doc = Jsoup.connect(url).get();
						String title = doc.title();
						String baseURI = doc.baseUri();
						t.postLink(baseURI, title, null, text.toString(),
								text.toString());
					} catch (IOException e) {
						Logger.debug(e.getMessage(), e);
					}
				}
			}

		} catch (WeiboException e) {
			Logger.debug(e.getMessage());
		}

		render();
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, ExecutionException {
		String s = "adfsafdsasafdsa http://sinaurl.cn/hGBcc9 a http://sinaurl.cn/GUdgX";

		Matcher m = p.matcher(s);
		// AsyncHttpClient client = new AsyncHttpClient();
		while (m.find()) {
			String url = s.substring(m.start(), m.end());
			// System.out.println(url);
			// Response r = client.prepareGet(url).execute().get();
			// System.out.println(r.getStatusCode());
			// System.out.println(r.getStatusText());
			// System.out.println(r.getHeader("Location"));
			// Response endPoint =
			// client.prepareGet(r.getHeader("Location")).execute().get();
			// System.out.println(endPoint.getStatusCode());
			// System.out.println(endPoint.getStatusText());
			// System.out.println(endPoint.getHeaders());
			Document doc = Jsoup.connect(url).get();
			String title = doc.title();
			String baseURI = doc.baseUri();
			System.out.println(title);
			System.out.println(baseURI);

		}

	}
}
