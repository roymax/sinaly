package controllers;

import java.util.ArrayList;
import java.util.List;

import models.User;
import play.Logger;
import play.libs.OAuth;
import play.libs.OAuth.ServiceInfo;
import play.libs.OAuth.TokenPair;
import play.mvc.Controller;
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
		if (tp.token != null || !"".equals(tp.token)) {
			Weibo sina = new Weibo();
			sina.setToken(tp.token, tp.secret);
			List<Status> statuses = new ArrayList();
			try {
				statuses = sina.getUserTimeline();
			} catch (WeiboException e) {
				Logger.debug(e.getMessage());
			}
			render(statuses);
		}
		render();
	}

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

}
