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

import com.example.android.activityanim.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
public class FragmentAnimations extends Activity implements FirstFragment.OnFirstFragmentThumbnailClickedListener {

    private static final String PACKAGE = "com.example.android.activityanim";
    private static final String TAG_FIRST_FRAGMENT = PACKAGE + ".tagFirstFragment";
    private static final String TAG_PICTURE_DETAILS_FRAGMENT = ".tagPictureDetailsFragment";
    static float sAnimatorScale = 1;
    private PictureDetailsFragment pictureDetailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirstFragment firstFragment;
        if (savedInstanceState == null) {
        	firstFragment = new FirstFragment(); 
            getFragmentManager().beginTransaction()
                    .add(R.id.container, firstFragment, TAG_FIRST_FRAGMENT)
                    .commit();

        } else {
            pictureDetailsFragment = (PictureDetailsFragment) getFragmentManager().
                                    findFragmentByTag(TAG_PICTURE_DETAILS_FRAGMENT);
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
        	   getFragmentManager().beginTransaction().detach(pictureDetailsFragment).commit();
           }
       	});
	   }
	   else super.onBackPressed();
   }


    @Override
    public void onFirstFragmentThumbnailClickedListener(int orientation, int resourceId,
                                                        int screenLocationX, int screenLocationY,
                                                        int width, int height, String description) {

        pictureDetailsFragment = PictureDetailsFragment.newInstance(orientation, resourceId,
                screenLocationX, screenLocationY, width, height, description);

        getFragmentManager().beginTransaction().add(R.id.container, pictureDetailsFragment,
                TAG_PICTURE_DETAILS_FRAGMENT).commit();

    }
}
