package com.sicao.smartwine.shop.entity;

import java.io.Serializable;
import java.util.ArrayList;

/***
 * 二级回复列表实体类
 * 
 * @author mingqi'li
 * 
 */
public class CommentList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5260157381440375093L;
	// 二层回复id
	private int commentListid;
	// 评论者id
	private String uida = "";
	// 评论者昵称
	private String unamea = "";
	// 评论内容
	private String content = "";
	// 被评论者id
	private String uidb = "";
	// 被评论者昵称
	private String unameb = "";
	//评论时间
	private String create_time="";
	// 三级回复 列表 (就是二级评论者之间的相互评论)
	private ArrayList<CommentList> thereComment;
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public void setCommentListid(int commentListid) {
		this.commentListid = commentListid;
	}

	public int getCommentListid() {
		return commentListid;
	}

	public void setThereComment(ArrayList<CommentList> thereComment) {
		this.thereComment = thereComment;
	}

	public ArrayList<CommentList> getThereComment() {
		return thereComment;
	}

	public CommentList() {
	}

	public CommentList(String uida, String unamea, String content, String uidb,
			String unameb,String create_time) {
		super();
		this.uida = uida;
		this.unamea = unamea;
		this.content = content;
		this.uidb = uidb;
		this.unameb = unameb;
		this.create_time=create_time;
		
	}

	public String getUida() {
		return uida;
	}

	public void setUida(String uida) {
		this.uida = uida;
	}

	public String getUnamea() {
		return unamea;
	}

	public void setUnamea(String unamea) {
		this.unamea = unamea;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUidb() {
		return uidb;
	}

	public void setUidb(String uidb) {
		this.uidb = uidb;
	}

	public String getUnameb() {
		return unameb;
	}

	public void setUnameb(String unameb) {
		this.unameb = unameb;
	}

	@Override
	public String toString() {
		return "CommentList [uida=" + uida + ", unamea=" + unamea
				+ ", content=" + content + ", uidb=" + uidb + ", unameb="
				+ unameb + "]";
	}

}
