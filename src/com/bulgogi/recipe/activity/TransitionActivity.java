package com.bulgogi.recipe.activity;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockActivity;
import com.bulgogi.recipe.R;

public class TransitionActivity extends SherlockActivity {
	public static final int SLIDE_RIGHT_TO_LEFT = 0x8000;
	public static final int SLIDE_LEFT_TO_RIGHT = 0x8001;
	public static final int SLIDE_BOTTOM_TO_TOP = 0x8002;
	public static final int SLIDE_TOP_TO_BOTTOM = 0x8003;
	
	public void startActivityTransition(Intent intent, int type) {
		startActivityForResult(intent, type);
		switch (type) {
		case SLIDE_RIGHT_TO_LEFT:
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			break;
		case SLIDE_LEFT_TO_RIGHT:
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			break;
		case SLIDE_BOTTOM_TO_TOP:
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
			break;
		case SLIDE_TOP_TO_BOTTOM:
			overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
			break;
		}
	}	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SLIDE_RIGHT_TO_LEFT:
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);			
			break;
		case SLIDE_LEFT_TO_RIGHT:
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			break;
		case SLIDE_BOTTOM_TO_TOP:
			overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
			break;
		case SLIDE_TOP_TO_BOTTOM:
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);			
			break;
		}
	}
}
