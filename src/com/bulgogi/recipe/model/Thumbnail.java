package com.bulgogi.recipe.model;

public class Thumbnail {
	private String title;
	private String imageUrl;	
	private Post post;
	
	public Thumbnail(String title, String imageUrl, Post post) {
		this.title = title;
		this.imageUrl = imageUrl;
		this.post = post;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public Post getPost() {
		return post;
	}
	
	public String getLike() {
		return "0"; //post.likeCount;
	}
	
	public int getId() {
		return post.id;
	}

	public String getEpisode() {
		return post.tags.get(0).episode();
	}
	
	public String getFood() {
		return post.tags.get(0).food();
	}
	
	public String getDate() {
		return post.tags.get(0).date();
	}
	
	public String getChef() {
		return post.tags.get(0).chef();
	}
	
	public String getChefImageUrl() {
		return post.getChef().url;
	}
}
