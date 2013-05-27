package com.bulgogi.recipe;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bulgogi.recipe.activity.RecipeActivity;
import com.bulgogi.recipe.activity.TransitionActivity;
import com.bulgogi.recipe.adapter.ThumbnailAdapter;
import com.bulgogi.recipe.config.Constants.Extra;
import com.bulgogi.recipe.http.WPRestApi;
import com.bulgogi.recipe.model.Post;
import com.bulgogi.recipe.model.Posts;
import com.bulgogi.recipe.model.Thumbnail;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnItemClickListener;

public class MainActivity extends TransitionActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private StaggeredGridView sgvThumbnail;	
	private ThumbnailAdapter adapter;
	private ArrayList<Thumbnail> urls = new ArrayList<Thumbnail>();				
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_main);		
		setupView();
		requestRecipe();
	}

	private void setupView() {
		sgvThumbnail = (StaggeredGridView)findViewById(R.id.sgv_thumbnail);
		sgvThumbnail.setItemMargin(getResources().getDimensionPixelSize(R.dimen.thumbnail_margin));
		sgvThumbnail.setSelector(R.drawable.list_selector_holo_light);
		sgvThumbnail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
		        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
		        Thumbnail thumbnail = urls.get(position);
		        intent.putExtra(Extra.POST, (Serializable)thumbnail.getPost());
		        startActivityTransition(intent, TransitionActivity.SLIDE_RIGHT_TO_LEFT);
			}
		});
		
		adapter = new ThumbnailAdapter(this, urls);
		sgvThumbnail.setAdapter(adapter);	
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.application, menu);
		return true;		
	}
		
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_refresh:
        	requestRecipe();
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void requestRecipe() {
    	new RecipeLoader().execute(WPRestApi.getPostsUrl(3, false));
    	//new RecipeLoader().execute(WPRestApi.getPostsUrl(20));
    	//new RecipeLoader().execute(WPRestApi.getPostsUrlById(7));
    }
    
	private class RecipeLoader extends AsyncTask {
		TextView tvError;
		ProgressBar pbLoading; 		
				
		RecipeLoader() {
			tvError = (TextView)findViewById(R.id.tv_error);
			pbLoading = (ProgressBar)findViewById(R.id.pb_loading);			
		}
		
		@Override
		protected Object doInBackground(Object... param) {
			ObjectMapper mapper = new ObjectMapper();
			Posts posts = null;
			String rowJson = null;
			
			try {
				posts = mapper.readValue(new URL((String)param[0]), Posts.class);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();				
			} catch (UnknownHostException e) { 				
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			return posts;
		}
		
		@Override
        protected void onPreExecute() {
			tvError.setVisibility(View.GONE);
			pbLoading.setVisibility(View.VISIBLE);			
		}
		
		@Override
		protected void onPostExecute(Object result) {			
			pbLoading.setVisibility(View.GONE);			
			
			if (result == null) {
				tvError.setVisibility(View.VISIBLE);
				return;
			} else {
				tvError.setVisibility(View.GONE);
				sgvThumbnail.setVisibility(View.VISIBLE);	
			}

			Posts posts = (Posts)result;
			Log.d(TAG, posts.toString());
			
			for (int i = 0; i < posts.posts.size(); i++) {
				Post post = posts.posts.get(i);
				Thumbnail thumbnail = new Thumbnail(post.title, post.getThumbnail().url, post);
				if (contain(post)) {
					remove(post);					
				}
				
				urls.add(thumbnail);
			}
			
			Collections.sort(urls, new Comparator<Thumbnail>(){
				public int compare(Thumbnail s1, Thumbnail s2) {
					if (s1.getId() > s2.getId()) return -1;
					else if (s1.getId() > s2.getId()) return 1;
					else return 0;
				}
			});
			
			adapter.notifyDataSetChanged();
		}
		
		private void remove(Post post) {
			Iterator<Thumbnail> iter = urls.iterator();
			while (iter.hasNext()) {
				Thumbnail thumbnail = (Thumbnail)iter.next(); 
				if (thumbnail.getId() == post.id) {
					urls.remove(thumbnail);
					return;
				}
			}			
		}
		
		private boolean contain(Post post) {
			Iterator<Thumbnail> iter = urls.iterator();
			while (iter.hasNext()) {
				if (((Thumbnail)iter.next()).getId() == post.id) {
					return true;
				}
			}			
			return false;
		}
	};
}
