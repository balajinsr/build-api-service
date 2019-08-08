package com.broadcom.nbiapps.model;

public class PullRequest {
	private String patch_url;
	private Head head;
	private Base base;
	private String title; //DTNumber or Case number from title 
	private String body; // comments from body tag.
	private String merge_commit_sha;	

	public String getPatch_url() {
		return patch_url;
	}

	public void setPatch_url(String patch_url) {
		this.patch_url = patch_url;
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public String getMerge_commit_sha() {
		return merge_commit_sha;
	}

	public void setMerge_commit_sha(String merge_commit_sha) {
		this.merge_commit_sha = merge_commit_sha;
	}

	@Override
	public String toString() {
		return "PullRequest [patch_url=" + patch_url + ", head=" + head + ", base=" + base + ", title=" + title + ", body=" + body + ", merge_commit_sha=" + merge_commit_sha + "]";
	}
}
