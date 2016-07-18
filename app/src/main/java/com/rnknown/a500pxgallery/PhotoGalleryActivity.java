package com.rnknown.a500pxgallery;

import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment () {

        return PhotoGalleryFragment.newInstance();
    }
}
