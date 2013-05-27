package com.bulgogi.recipe.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bulgogi.recipe.R;
import com.bulgogi.recipe.loader.DisplayOptions;
import com.bulgogi.recipe.loader.ImageLoader;
import com.bulgogi.recipe.model.Image;

public class RecipePagerAdapter extends PagerAdapter {
	private LayoutInflater inflater;
	private ArrayList<Image> images = new ArrayList<Image>();
	
	public RecipePagerAdapter(Context c, ArrayList<Image> images) {
		inflater = LayoutInflater.from(c);
		this.images = images;
     }
	
	@Override
	public Object instantiateItem(View pager, int position) {
		DisplayOptions options = new DisplayOptions.Builder()
		.thumbnail(false)
		.build();
		
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.ll_recipe_pager_item, null);
		ImageView ivRecipe = (ImageView)layout.findViewById(R.id.iv_recipe);
		ImageLoader imageLoader = new ImageLoader(pager.getContext());
		imageLoader.displayImage(images.get(position).url, ivRecipe, options);
		
		TextView tvPosition = (TextView)layout.findViewById(R.id.tv_position);
		tvPosition.setText((position + 1) + "/" + images.size());
		
		((ViewPager)pager).addView(layout, 0);
		return layout;
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		
		ViewPager pager = (ViewPager)container;
		
		int srcWidth = images.get(position).width;
		int srcHeight = images.get(position).height;
		
		if (srcWidth > srcHeight) {
			int displayWidth = pager.getResources().getDisplayMetrics().widthPixels; 
			float scale = (float)displayWidth / srcWidth;
			float scaledHeight = srcHeight * scale;
			pager.setLayoutParams(new LinearLayout.LayoutParams(displayWidth, Math.max((int)scaledHeight, 1)));
		}
	}
	
	@Override
    public void destroyItem(View pager, int position, Object view) {    
		((ViewPager)pager).removeView((View)view);
    }
	
	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public boolean isViewFromObject(View pager, Object obj) {
		return pager == obj;
	}

}
