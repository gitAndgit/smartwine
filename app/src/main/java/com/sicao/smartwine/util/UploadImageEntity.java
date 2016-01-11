/**
 * Administrator
 * 2015-1-28
 */
package com.sicao.smartwine.util;

import android.net.Uri;

import java.io.Serializable;

/**
 * @author li'mingqi
 * 
 */
public class UploadImageEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean uploaded;
	Uri uri;
	String title;
	String url;
	int progress;
	boolean isuploading;
	int index;
	
	
	
	public boolean isUploaded() {
		return uploaded;
	}
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}
	public boolean isIsuploading() {
		return isuploading;
	}
	public void setIsuploading(boolean isuploading) {
		this.isuploading = isuploading;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public Uri getUri() {
		return uri;
	}
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "UploadImageEntity [upload=" + uploaded + ", uri=" + uri
				+ ", title=" + title + ", url=" + url + ", progress="
				+ progress + ", isupload=" + isuploading + ", index=" + index
				+ "]";
	}
	
	
}
