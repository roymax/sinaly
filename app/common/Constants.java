package common;

import play.Play;

public class Constants extends Object {
	public static String SINA_REQUEST_TOKEN = Play.configuration.getProperty(
			"sina.request_token", "");
	public static String SINA_ACCESS_TOKEN = Play.configuration.getProperty(
			"sina.access_token", "");
	public static String SINA_AUTHORIZE = Play.configuration.getProperty(
			"sina.authorize", "");

	public static String SINA_CONSUMER_KEY = Play.configuration.getProperty(
			"sina.consumer_key", "");
	public static String SINA_CONSUMER_SECRET = Play.configuration.getProperty(
			"sina.consumer_secret", "");

}
