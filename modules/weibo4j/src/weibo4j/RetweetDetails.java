/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package weibo4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * A data class representing one single retweet details.
 * 
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Weibo4J 2.0.10
 */
public class RetweetDetails extends WeiboResponse implements
		java.io.Serializable {
	private Date createdAt;
	private long id;
	private String text;
	private String source;
	private boolean isTruncated;
	private long inReplyToStatusId;
	private int inReplyToUserId;
	private boolean isFavorited;
	private String inReplyToScreenName;
	private double latitude = -1;
	private double longitude = -1;
	private String thumbnail_pic;
	private String bmiddle_pic;
	private String original_pic;
	private User user = null;
	static final long serialVersionUID = 1957982268696560598L;

	/* package */RetweetDetails(Response res, Weibo weibo)
			throws WeiboException {
		super(res);
		Element elem = res.asDocument().getDocumentElement();
		init(res, elem, weibo);
	}

	RetweetDetails(JSONObject json) throws WeiboException {
		super();
		init(json);
	}

	private void init(JSONObject json) throws WeiboException {
		try {
			id = json.getLong("id");
			text = json.getString("text");
			source = json.getString("source");
			createdAt = parseDate(json.getString("created_at"),
					"EEE MMM dd HH:mm:ss z yyyy");

			inReplyToStatusId = getLong("in_reply_to_status_id", json);
			inReplyToUserId = getInt("in_reply_to_user_id", json);
			isFavorited = getBoolean("favorited", json);
			thumbnail_pic = json.getString("thumbnail_pic");
			bmiddle_pic = json.getString("bmiddle_pic");
			original_pic = json.getString("original_pic");
			user = new User(json.getJSONObject("user"));

		} catch (JSONException jsone) {
			throw new WeiboException(
					jsone.getMessage() + ":" + json.toString(), jsone);
		}
	}

	/* package */RetweetDetails(Response res, Element elem, Weibo weibo)
			throws WeiboException {
		super(res);
		init(res, elem, weibo);
	}

	private void init(Response res, Element elem, Weibo weibo)
			throws WeiboException {
		ensureRootNodeNameIs("retweet_details", elem);
		user = new User(res, (Element) elem.getElementsByTagName("user")
				.item(0), weibo);
		id = getChildLong("id", elem);
		text = getChildText("text", elem);
		source = getChildText("source", elem);
		createdAt = getChildDate("created_at", elem);
		isTruncated = getChildBoolean("truncated", elem);
		inReplyToStatusId = getChildLong("in_reply_to_status_id", elem);
		inReplyToUserId = getChildInt("in_reply_to_user_id", elem);
		isFavorited = getChildBoolean("favorited", elem);
		inReplyToScreenName = getChildText("in_reply_to_screen_name", elem);
		NodeList georssPoint = elem.getElementsByTagName("georss:point");

		if (1 == georssPoint.getLength()) {
			String[] point = georssPoint.item(0).getFirstChild().getNodeValue()
					.split(" ");
			if (!"null".equals(point[0]))
				latitude = Double.parseDouble(point[0]);
			if (!"null".equals(point[1]))
				longitude = Double.parseDouble(point[1]);
		}
	}

	/* modify by sycheng add json */
	/* package */
	static List<RetweetDetails> createRetweetDetails(Response res)
			throws WeiboException {
		try {
			JSONArray list = res.asJSONArray();
			int size = list.length();
			List<RetweetDetails> retweets = new ArrayList<RetweetDetails>(size);
			for (int i = 0; i < size; i++) {
				retweets.add(new RetweetDetails(list.getJSONObject(i)));
			}
			return retweets;
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		} catch (WeiboException te) {
			throw te;
		}
	}

	/* package */
	static List<RetweetDetails> createRetweetDetails(Response res, Weibo weibo)
			throws WeiboException {
		Document doc = res.asDocument();
		if (isRootNodeNilClasses(doc)) {
			return new ArrayList<RetweetDetails>(0);
		} else {
			try {
				ensureRootNodeNameIs("retweets", doc);
				NodeList list = doc.getDocumentElement().getElementsByTagName(
						"retweet_details");
				int size = list.getLength();
				List<RetweetDetails> statuses = new ArrayList<RetweetDetails>(
						size);
				for (int i = 0; i < size; i++) {
					Element status = (Element) list.item(i);
					statuses.add(new RetweetDetails(res, status, weibo));
				}
				return statuses;
			} catch (WeiboException te) {
				ensureRootNodeNameIs("nil-classes", doc);
				return new ArrayList<RetweetDetails>(0);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RetweetDetails))
			return false;

		RetweetDetails that = (RetweetDetails) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + createdAt.hashCode();
		result = 31 * result + user.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "RetweetDetails{" + "retweetId=" + id + ", retweetedAt="
				+ createdAt + ", retweetingUser=" + user + '}';
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the isTruncated
	 */
	public boolean isTruncated() {
		return isTruncated;
	}

	/**
	 * @param isTruncated
	 *            the isTruncated to set
	 */
	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	/**
	 * @return the inReplyToStatusId
	 */
	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	/**
	 * @param inReplyToStatusId
	 *            the inReplyToStatusId to set
	 */
	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	/**
	 * @return the inReplyToUserId
	 */
	public int getInReplyToUserId() {
		return inReplyToUserId;
	}

	/**
	 * @param inReplyToUserId
	 *            the inReplyToUserId to set
	 */
	public void setInReplyToUserId(int inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	/**
	 * @return the isFavorited
	 */
	public boolean isFavorited() {
		return isFavorited;
	}

	/**
	 * @param isFavorited
	 *            the isFavorited to set
	 */
	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}

	/**
	 * @return the inReplyToScreenName
	 */
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	/**
	 * @param inReplyToScreenName
	 *            the inReplyToScreenName to set
	 */
	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the thumbnail_pic
	 */
	public String getThumbnail_pic() {
		return thumbnail_pic;
	}

	/**
	 * @param thumbnail_pic
	 *            the thumbnail_pic to set
	 */
	public void setThumbnail_pic(String thumbnail_pic) {
		this.thumbnail_pic = thumbnail_pic;
	}

	/**
	 * @return the bmiddle_pic
	 */
	public String getBmiddle_pic() {
		return bmiddle_pic;
	}

	/**
	 * @param bmiddle_pic
	 *            the bmiddle_pic to set
	 */
	public void setBmiddle_pic(String bmiddle_pic) {
		this.bmiddle_pic = bmiddle_pic;
	}

	/**
	 * @return the original_pic
	 */
	public String getOriginal_pic() {
		return original_pic;
	}

	/**
	 * @param original_pic
	 *            the original_pic to set
	 */
	public void setOriginal_pic(String original_pic) {
		this.original_pic = original_pic;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
