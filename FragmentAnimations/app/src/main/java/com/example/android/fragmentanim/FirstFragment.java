package com.example.android.fragmentanim;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.example.android.activityanim.R;
import java.util.ArrayList;
import java.util.HashMap;


public class FirstFragment extends Fragment {

    private final HashMap<ImageView, PictureData> mPicturesData = new HashMap<>();
    private OnFirstFragmentThumbnailClickedListener mListener;


    public FirstFragment() {

    }

    /**
     * When the user clicks a thumbnail, bundle up information about it and launch the
     * details activity.
     */
    private final View.OnClickListener thumbnailClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // Interesting data to pass across are the thumbnail size/location, the
            // resourceId of the source bitmap, the picture description, and the
            // orientation (to avoid returning back to an obsolete configuration if
            // the device rotates again in the meantime)
            int[] screenLocation = new int[2];
            v.getLocationOnScreen(screenLocation);
            //noinspection SuspiciousMethodCalls - we know for sure v is an ImageView
            PictureData info = mPicturesData.get(v);

            int orientation = getResources().getConfiguration().orientation;


            mListener.onFirstFragmentThumbnailClickedListener(orientation, info.resourceId, screenLocation[0],
                    screenLocation[1], v.getWidth(), v.getHeight(), info.description);

            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
//            overridePendingTransition(0, 0);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = ((OnFirstFragmentThumbnailClickedListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "Must implement OnFirstFragmentThumbnailClickedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Grayscale filter used on all thumbnails
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);

        GridLayout mGridLayout;
        mGridLayout = (GridLayout) rootView.findViewById(R.id.gridLayout);
        mGridLayout.setColumnCount(3);
        mGridLayout.setUseDefaultMargins(true);

        // add all photo thumbnails to layout
        Resources resources = getResources();
        ArrayList<PictureData> pictures = BitmapUtils.loadPhotos(resources);
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

    /**
     * Interface to be implemented by Activities which will respond to a click in a thumbnail
     * in an instance of FirstFragment.
     *
     */
    public interface OnFirstFragmentThumbnailClickedListener {

        void onFirstFragmentThumbnailClickedListener(int orientation, int resourceId,
                                                     int screenLocationX, int screenLocationY,
                                                     int width, int height, String description);

    }

}

