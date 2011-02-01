package trunkly;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Link {
	private String lid;

	private Date dt_string;
	private Long uid;
	private String tags;
	private String url;
	private String link_hash;
	private String title;
	private String note;
	private List<Source> source = new ArrayList<Source>();
	private Long dt_epoch;

	/**
	 * @return the lid
	 */
	public String getLid() {
		return lid;
	}

	/**
	 * @param lid
	 *            the lid to set
	 */
	public void setLid(String lid) {
		this.lid = lid;
	}

	/**
	 * @return the dt_string
	 */
	public Date getDt_string() {
		return dt_string;
	}

	/**
	 * @param dt_string
	 *            the dt_string to set
	 */
	public void setDt_string(Date dt_string) {
		this.dt_string = dt_string;
	}

	/**
	 * @return the uid
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(Long uid) {
		this.uid = uid;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the link_hash
	 */
	public String getLink_hash() {
		return link_hash;
	}

	/**
	 * @param link_hash
	 *            the link_hash to set
	 */
	public void setLink_hash(String link_hash) {
		this.link_hash = link_hash;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note
	 *            the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the source
	 */
	public List<Source> getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(List<Source> source) {
		this.source = source;
	}

	/**
	 * @return the dt_epoch
	 */
	public Long getDt_epoch() {
		return dt_epoch;
	}

	/**
	 * @param dt_epoch
	 *            the dt_epoch to set
	 */
	public void setDt_epoch(Long dt_epoch) {
		this.dt_epoch = dt_epoch;
	}

	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

}
