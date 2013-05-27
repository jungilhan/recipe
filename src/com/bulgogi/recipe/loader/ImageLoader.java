package com.bulgogi.recipe.loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bulgogi.recipe.config.Constants;
import com.bulgogi.recipe.util.ImageUtils;

/**
 * Using LazyList via https://github.com/thest1/LazyList/tree/master/src/com/fedorvlasov/lazylist
 * for the example since its super lightweight
 * I barely modified this file
 */
public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
//    final int stub_id= android.R.drawable.alert_dark_frame;
    
    public void displayImage(String url, ImageView imageView, DisplayOptions options)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=null;        
        if (options.isThumbnail()) {
        	bitmap=memoryCache.get(url + Constants.THUMBNAIL_POSTFIX);	
        } else {
        	bitmap=memoryCache.get(url);	
        }
        
        if(bitmap!=null) {
    		imageView.setImageBitmap(bitmap);
        }
        else
        {
            queuePhoto(url, imageView, options);
            if (options.shouldShowStubImage()) {
            	imageView.setImageResource(options.getStubImage());
            } else {
            	imageView.setImageDrawable(null);	
            }            
        }
    }
        
    private void queuePhoto(String url, ImageView imageView, DisplayOptions options)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, options);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url, int width, int height) 
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f, width, height);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f, width, height);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, int width, int height){
        try {
            //decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,options);
            stream1.close();            
            int scale=ImageUtils.calculateInSampleSize(options, width, height);
            
            //decode with inSampleSize
            options=new BitmapFactory.Options();
            options.inSampleSize=ImageUtils.nextPowerOf2(scale);
            options.inPreferredConfig=Bitmap.Config.RGB_565;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, options);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
        
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public DisplayOptions options;
        public PhotoToLoad(String u, ImageView i, DisplayOptions opts){
            url=u; 
            imageView=i;
            options=opts;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                
                int width, height;
                if (photoToLoad.options.isThumbnail()) {
                	DisplayMetrics displayMetrics = photoToLoad.imageView.getContext().getResources().getDisplayMetrics();
                	width = displayMetrics.widthPixels / 2;
                	height = displayMetrics.heightPixels / 2;
                } else {
                	DisplayMetrics displayMetrics = photoToLoad.imageView.getContext().getResources().getDisplayMetrics();
                	width = displayMetrics.widthPixels;
                	height = displayMetrics.heightPixels;
                }
                
                Bitmap bmp=getBitmap(photoToLoad.url, width, height);
                if (photoToLoad.options.shoudDrawRound()) {
            		if (photoToLoad.options.shoudDrawOnlyTopRound()) {
            			bmp = ImageUtils.roundTopCorners(bmp, photoToLoad.options.getRadius());        			
            		} else {
            			bmp = ImageUtils.roundCorners(bmp, photoToLoad.options.getRadius());
            		}
                }
                if (photoToLoad.options.isThumbnail()) {
                	memoryCache.put(photoToLoad.url + Constants.THUMBNAIL_POSTFIX, bmp);	
                } else {
                	memoryCache.put(photoToLoad.url, bmp);
                }
                
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            
            if(bitmap!=null) {
            	photoToLoad.imageView.setImageBitmap(bitmap);                
            } else {
                photoToLoad.imageView.setImageDrawable(null);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}
