package com.bulgogi.recipe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bulgogi.recipe.R;
import com.bulgogi.recipe.adapter.RecipePagerAdapter;
import com.bulgogi.recipe.config.Constants.Extra;
import com.bulgogi.recipe.loader.DisplayOptions;
import com.bulgogi.recipe.loader.ImageLoader;
import com.bulgogi.recipe.model.Post;

public class RecipeActivity extends SherlockActivity implements OnClickListener {
	private static final String TAG = RecipeActivity.class.getSimpleName();
	
	private ViewPager pager;
	private ImageView ivChef;
	private TextView tvContent;
	private TextView tvYoutube;
	private TextView tvComment;
	private TextView tvLike;	
	private ImageLoader imageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_recipe);		
	    
		Intent intent = getIntent();
		Post post = (Post)intent.getSerializableExtra(Extra.POST);

		setupView(post);
		
		String htmlComments = getHtmlComment("?p=7", "qnfrhrl", "http://14.63.219.181/");
		 
		WebView webDisqus = (WebView) findViewById(R.id.disqus);
		// set up disqus
		WebSettings webSettings2 = webDisqus.getSettings();
		webSettings2.setJavaScriptEnabled(true);
		webSettings2.setBuiltInZoomControls(true);
		webDisqus.requestFocusFromTouch();
		webDisqus.setWebViewClient(new WebViewClient());
		webDisqus.setWebChromeClient(new WebChromeClient());
		webDisqus.loadData(htmlComments, "text/html", null);
	}
	
	private void setupView(Post post) {		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(post.title);

		final ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(new RecipePagerAdapter(this, post.getRecipes()));

	    pager.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            v.getParent().requestDisallowInterceptTouchEvent(true);
	            return false;
	        }
	    });

	    pager.setOnPageChangeListener(new OnPageChangeListener() {
	        @Override
	        public void onPageSelected(int arg0) {}

	        @Override
	        public void onPageScrollStateChanged(int arg0) {}
	        
	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	           pager.getParent().requestDisallowInterceptTouchEvent(true);
	        }
	    });
	    
		DisplayOptions options = new DisplayOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.build();
		
		ivChef = (ImageView)findViewById(R.id.iv_chef);
		imageLoader = new ImageLoader(this);
		imageLoader.displayImage(post.getChef().url, ivChef, options);
		
		String content = post.content;
		content = content.replaceAll("<a.*?/a>","");
		tvContent = (TextView)findViewById(R.id.tv_content);		
		tvContent.setText(Html.fromHtml(content));
		
		tvYoutube = (TextView)findViewById(R.id.tv_youtube);
		tvYoutube.setOnClickListener(this);
		String youtubeId = post.tags.get(0).youtubeId();
		if (youtubeId.equals("null")) {
			tvYoutube.setVisibility(View.INVISIBLE);
		} else {
			tvYoutube.setTag(youtubeId);	
		}
		
//		tvLike = (TextView)findViewById(R.id.tv_comment);
//		tvLike.setText(post.commentCount);
//		
//		tvLike = (TextView)findViewById(R.id.tv_like);
//		tvLike.setText(post.likeCount);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_youtube:
			StringBuilder sb = new StringBuilder("vnd.youtube:"); 
			sb.append(v.getTag()); 
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString())));
			break;
		}
	}
	
	public String getHtmlComment(String idPost, String shortName, String baseUrl) {
		return "<div id='disqus_thread'></div>"
				+ "<script type='text/javascript'>"
				+ "var disqus_identifier = '"
				+ idPost
				+ "';"
				+ "var disqus_shortname = '"
				+ shortName
				+ "';"
				+ "var disqus_url = '"
				+ baseUrl
				+ "';"
				+ " (function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;"
				+ "dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';"
				+ "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"
				+ "</script>";
	}
}
