/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.fragmentanim;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.android.activityanim.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

/**
 * This example shows how to create a custom activity animation when you want something more
 * than window animations can provide. The idea is to disable window animations for the
 * activities and to instead launch or return from the sub-activity immediately, but use
 * property animations inside the activities to customize the transition.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the androiddevelopers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class FragmentAnimations extends Activity {

    private static final String PACKAGE = "com.example.android.activityanim";
    static float sAnimatorScale = 1;
    PictureDetailsFragment pictureDetailsFragment;
    GridLayout mGridLayout;
    FirstFragment firstFragment; 
    HashMap<ImageView, PictureData> mPicturesData = new HashMap<ImageView, PictureData>();
    BitmapUtils mBitmapUtils = new BitmapUtils();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
        	firstFragment = new FirstFragment(); 
            getFragmentManager().beginTransaction()
                    .add(R.id.container, firstFragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_better_window_animations, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_slow) {
            sAnimatorScale = item.isChecked() ? 1 : 5;
            item.setChecked(!item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When the user clicks a thumbnail, bundle up information about it and launch the
     * details activity.
     */
    private View.OnClickListener thumbnailClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // Interesting data to pass across are the thumbnail size/location, the
            // resourceId of the source bitmap, the picture description, and the
            // orientation (to avoid returning back to an obsolete configuration if
            // the device rotates again in the meantime)
            int[] screenLocation = new int[2];
            v.getLocationOnScreen(screenLocation);
            PictureData info = mPicturesData.get(v);
            pictureDetailsFragment = new PictureDetailsFragment();

            int orientation = getResources().getConfiguration().orientation;
            Bundle extras = new Bundle(); 
            extras.putInt(PACKAGE + ".orientation", orientation); 
            extras.putInt(PACKAGE + ".resourceId", info.resourceId); 
            extras.putInt(PACKAGE + ".left", screenLocation[0]); 
            extras.putInt(PACKAGE + ".top", screenLocation[1]); 
            extras.putInt(PACKAGE + ".width", v.getWidth()); 
            extras.putInt(PACKAGE + ".height", v.getHeight()); 
            extras.putString(PACKAGE + ".description", info.description);
            pictureDetailsFragment.setArguments(extras); 
            
            getFragmentManager().beginTransaction().replace(R.id.container,pictureDetailsFragment, null).commit();
            
            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
//            overridePendingTransition(0, 0);
        }
    };
    
    
   class FirstFragment extends Fragment {
        public FirstFragment() {}
    	
        @Override 
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	    View rootView = inflater.inflate(R.layout.fragment_main, container,false); 
    	    // Grayscale filter used on all thumbnails
            ColorMatrix grayMatrix = new ColorMatrix();
            grayMatrix.setSaturation(0);
            ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
            
            mGridLayout = (GridLayout) rootView.findViewById(R.id.gridLayout);
            mGridLayout.setColumnCount(3);
            mGridLayout.setUseDefaultMargins(true);
            
            // add all photo thumbnails to layout
            Resources resources = getResources();
            ArrayList<PictureData> pictures = mBitmapUtils.loadPhotos(resources);
            for (int i = 0; i < pictures.size(); ++i) {
                PictureData pictureData = pictures.get(i);
                BitmapDrawable thumbnailDrawable =
                        new BitmapDrawable(resources, pictureData.thumbnail);
                thumbnailDrawable.setColorFilter(grayscaleFilter);
                ImageView imageView = new ImageView(getActivity());
                imageView.setOnClickListener(thumbnailClickListener);
                imageView.setImageDrawable(thumbnailDrawable);
                mPicturesData.put(imageView, pictureData);
                mGridLayout.addView(imageView);
            }
    		return rootView;
    	}
    	
    }
    
   @Override
   public void finish() {
       super.finish();
       // override transitions to skip the standard window animations
       overridePendingTransition(0, 0);
   }
   
   /**
    * Overriding this method allows us to run our exit animation first, then exiting
    * the activity when it is complete.
    */
   @Override
   public void onBackPressed() {
	   if (pictureDetailsFragment != null && pictureDetailsFragment.isVisible()) {
       pictureDetailsFragment.runExitAnimation(new Runnable() {
           public void run() {
        	   getFragmentManager().beginTransaction().replace(R.id.container,firstFragment).commit(); 
           }
       	});
	   }
	   else super.onBackPressed();
   }
    

}
