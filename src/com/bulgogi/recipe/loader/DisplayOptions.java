package com.bulgogi.recipe.loader;


public class DisplayOptions {
    private int stubImage;
    private int imageOnFail;
    private float radius;
    private boolean drawOnlyTopRound;
    private boolean thumbnail;
    
    DisplayOptions(Builder builder) {
    	stubImage = builder.stubImage;
		imageOnFail = builder.imageOnFail;
		radius = builder.radius;
		drawOnlyTopRound = builder.drawOnlyTopRound;
		thumbnail = builder.thumbnail;
    }
    	
    public int getStubImage() {
		return stubImage;
	}
    
    public int getImageOnFail() {
		return imageOnFail;
	}
    
    public float getRadius() {
    	return radius;
    }
    
    public boolean shouldShowStubImage() {
		return stubImage != 0;
	}
    
    public boolean shoudDrawRound() {
    	return radius != 0f;
    }
    
    public boolean shoudDrawOnlyTopRound() {
    	return drawOnlyTopRound;
    }
    
    public boolean isThumbnail() {
    	return thumbnail;
    }
    
    public static class Builder {
		private int stubImage = 0;
		private int imageOnFail = 0;
		private float radius = 0;
		private boolean drawOnlyTopRound = false;
		private boolean thumbnail = false;
		
		public Builder showStubImage(int stubImageRes) {
			stubImage = stubImageRes;
			return this;
		}
		
		public Builder showImageOnFail(int imageRes) {
			imageOnFail = imageRes;
			return this;
		}
		
		public Builder setRadius(float radius) {
			this.radius = radius;
			return this;
		}
		
		public Builder drawOnlyTopRound(boolean enable) {
			this.drawOnlyTopRound = enable;
			return this;
		}
		
		public Builder thumbnail(boolean isThumbnail) {
			this.thumbnail = isThumbnail;
			return this;
		}
		
		public DisplayOptions build() {
			return new DisplayOptions(this);
		}
    }
}
