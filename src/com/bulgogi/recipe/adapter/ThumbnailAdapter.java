package com.bulgogi.recipe.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulgogi.recipe.R;
import com.bulgogi.recipe.config.Constants.Config;
import com.bulgogi.recipe.loader.DisplayOptions;
import com.bulgogi.recipe.loader.ImageLoader;
import com.bulgogi.recipe.model.Thumbnail;
import com.bulgogi.recipe.view.ScaleImageView;

public class ThumbnailAdapter extends BaseAdapter {
	Context context;	
	private ArrayList<Thumbnail> thumbnails;
	private ImageLoader imageLoader;
	private DisplayOptions options;
	
	static class ViewHolder {
		ScaleImageView ivImageView;
		ImageView ivChef;
		TextView tvEpisode;
		TextView tvEpisodePostfix;
		TextView tvLike;
		TextView tvFood;
		TextView tvChef;	
	}
	
	public ThumbnailAdapter(Context context, ArrayList<Thumbnail> thumbnails) {
		this.context = context;
		this.thumbnails = thumbnails;	
		
		imageLoader = new ImageLoader(context);		
		options = new DisplayOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.thumbnail(true)
		.build();
	}

	@Override
	public int getCount() {
		return thumbnails.size();
	}

	@Override
	public Object getItem(int position) {
		return thumbnails.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}	 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(context);
			convertView = inflator.inflate(R.layout.rl_thumbnail, null);
			
			holder = new ViewHolder();
			holder.ivImageView = (ScaleImageView)convertView.findViewById(R.id.iv_image);
			holder.ivChef = (ImageView)convertView.findViewById(R.id.iv_chef);
			holder.tvEpisode = (TextView)convertView.findViewById(R.id.tv_episode);
			holder.tvEpisodePostfix = (TextView)convertView.findViewById(R.id.tv_episode_postfix);
			holder.tvLike = (TextView)convertView.findViewById(R.id.tv_like);
			holder.tvFood = (TextView)convertView.findViewById(R.id.tv_food);
			holder.tvChef = (TextView)convertView.findViewById(R.id.tv_chef);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder)convertView.getTag();	
		}
				
		Thumbnail thumbnail = (Thumbnail)thumbnails.get(position);
		holder.tvEpisode.setText(thumbnail.getEpisode());
		holder.tvEpisodePostfix.setText(context.getResources().getString(R.string.episode_postfix));
		holder.tvLike.setText(thumbnail.getLike());
		holder.tvFood.setText(thumbnail.getFood());
		holder.tvChef.setText(thumbnail.getChef());
		imageLoader.displayImage(thumbnail.getImageUrl(), holder.ivImageView, options);
		imageLoader.displayImage(thumbnail.getChefImageUrl(), holder.ivChef, new DisplayOptions.Builder().build());
		return convertView;
	}
}
