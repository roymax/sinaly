package trunkly;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import play.Logger;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import common.Constants;

public class Trunk {
	private static final String POST_LINK = "http://trunk.ly/api/v1/link/";
	//private static final String GET_USER_TAGS = "http://trunk.ly/api/v1/user/tags/?api_key=5229-4bdad3ab-650d-4f6d-9e8e-8912cb841d76";

	/**
	 * 
	 * @param url
	 * @param title
	 * @param tags
	 * @param note
	 * @param text
	 * @return
	 */
	public String postLink(String url, String title, String tags, String note,
			String text) {             
		Logger.debug("url: '%s', title: '%s', tags: '%s', note: '%s', text: '%s'",url,title,tags,note,text);		
		AsyncHttpClient client = new AsyncHttpClient();
		Response response = null;
		try {
			// Play默认的WS.url("string")调用方式在传递params时会出错，改用Play的底层Http调用包进行调用。
			/*
			 * // Map<String, String> params = new HashMap<String, String>(); //
			 * // params.put("api_key", Constants.TRUNK_API_KEY); //
			 * params.put("url", url); // params.put("title", title); //
			 * params.put("tags", tags); // params.put("note", note); //
			 * params.put("text", text); // HttpResponse response = //
			 * WS.url(POST_LINK).setParameters(params).post(); // if
			 * (response.getStatus() == 200) { // JsonElement body =
			 * response.getJson(); // Logger.debug("response body : %s",
			 * body.getAsString()); // Gson gson = new Gson(); // return
			 * gson.fromJson(body, new TypeToken<Link>() { // }.getType()); // }
			 */
			response = client.preparePost(POST_LINK)
					.addQueryParameter("api_key", Constants.TRUNK_API_KEY)
					.addParameter("url", url).addParameter("title", title)
					.addParameter("tags", tags).addParameter("note", note)
					.addParameter("text", text).execute().get();
			//当前实现为同步返回，如提高效率可以通过异步方式
			if (response.getStatusCode() == 200) {
				String resBody = response.getResponseBody();
				Logger.debug("response body : %s", resBody);
				// Gson gson = new Gson();
				// return gson.fromJson(resBody, new TypeToken<Link>() {
				// }.getType());
				return resBody;
			}

			Logger.debug("response status code : %s", response.getStatusCode());
		} catch (IllegalArgumentException e) {
			Logger.warn(e.getMessage(), e);
		} catch (InterruptedException e) {
			Logger.warn(e.getMessage(), e);
		} catch (ExecutionException e) {
			Logger.warn(e.getMessage(), e);
		} catch (IOException e) {
			Logger.warn(e.getMessage(), e);
		} finally {
			if (null != client)
				client.close();
		}

		return null;
	}
}
