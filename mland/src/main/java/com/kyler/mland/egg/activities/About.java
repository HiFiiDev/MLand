package com.kyler.mland.egg.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.samples.apps.iosched.ui.widget.CheckableFloatingActionButton;
import com.google.samples.apps.iosched.ui.widget.ObservableScrollView;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.utils.UIUtils;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

/** Created by kyler on 10/6/15. */
@SuppressWarnings({"ALL", "DefaultFileTemplate"})
public class About extends MLandBase implements ObservableScrollView.Callbacks {
  private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
  private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
  private ImageView mlandView;
  private int mPhotoHeightPixels;
  private View mAddScheduleButtonContainer;
  private CheckableFloatingActionButton mAddScheduleButton;
  private int mAddScheduleButtonContainerHeightPixels;
  private View mScrollViewChild;
  private int mHeaderHeightPixels;
  private View mDetailsContainer;
  private ObservableScrollView mScrollView;
  private View mPhotoViewContainer;
  private boolean mHasPhoto;
  private float mMaxHeaderElevation;
  private View mHeaderBox;
  private Handler mHandler;
  private float mFABElevation;
  private OnGlobalLayoutListener mGlobalLayoutListener = () -> recomputePhotoAndScrollingMetrics();

  @Override
  protected int getSelfNavDrawerItem() {
    return NAVDRAWER_ITEM_ABOUT;
  }

  @Override
  protected Context getContext() {
    return About.this;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_MLand_Home);
    super.onCreate(savedInstanceState);
    super.getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    boolean shouldBeFloatingWindow = false;
    if (shouldBeFloatingWindow) {
      setupFloatingWindow(
          R.dimen.session_details_floating_width, R.dimen.session_details_floating_height, 1, 0.5f);
    }
    setContentView(R.layout.about);
    getSupportActionBar().setTitle(null);
    activateLightNavigationBar();

    mHasPhoto = true;

    mlandView = (ImageView) findViewById(R.id.session_photo);

    mHandler = new Handler();
    initViews();
  }

  @TargetApi(Build.VERSION_CODES.O)
  private void activateLightNavigationBar() {
    int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
    int newSystemUiFlags = oldSystemUiFlags;
    newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
    getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
    if (newSystemUiFlags != oldSystemUiFlags) {
      final int systemUiFlags = newSystemUiFlags;
      runOnUiThread(() -> getWindow().getDecorView().setSystemUiVisibility(systemUiFlags));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mScrollView == null) {
      return;
    }
    ViewTreeObserver vto = mScrollView.getViewTreeObserver();
    if (vto.isAlive()) {
      vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
    }
  }

  private void initViews() {
    mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
    mMaxHeaderElevation =
        getResources().getDimensionPixelSize(R.dimen.session_detail_max_header_elevation);
    mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
    mScrollView.addCallbacks(this);
    ViewTreeObserver vto = mScrollView.getViewTreeObserver();
    if (vto.isAlive()) {
      vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
    }
    mScrollViewChild = findViewById(R.id.scroll_view_child);
    mScrollViewChild.setVisibility(View.VISIBLE);
    mDetailsContainer = findViewById(R.id.details_container);
    mHeaderBox = findViewById(R.id.header_session);
    mActionBarToolbar.setVisibility(View.VISIBLE);
    mPhotoViewContainer = findViewById(R.id.session_photo_container);
    mAddScheduleButtonContainer = findViewById(R.id.add_schedule_button_container);
    displayData();
  }

  private void recomputePhotoAndScrollingMetrics() {
    mHeaderHeightPixels = mHeaderBox.getHeight();

    mPhotoHeightPixels = 0;
    if (mHasPhoto) {
      mPhotoHeightPixels = (int) (mlandView.getWidth() / PHOTO_ASPECT_RATIO);
      mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 1 / 3);
    }

    ViewGroup.LayoutParams lp;
    lp = mPhotoViewContainer.getLayoutParams();
    if (lp.height != mPhotoHeightPixels) {
      lp.height = mPhotoHeightPixels;
      mPhotoViewContainer.setLayoutParams(lp);
    }

    ViewGroup.MarginLayoutParams mlp =
        (ViewGroup.MarginLayoutParams) mDetailsContainer.getLayoutParams();
    if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
      mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
      mDetailsContainer.setLayoutParams(mlp);
      mDetailsContainer.setPadding(16, 150, 16, 150);
    }

    onScrollChanged(0, 0); // trigger scroll handling
  }

  @TargetApi(Build.VERSION_CODES.M)
  @Override
  public void onScrollChanged(int deltaX, int deltaY) {
    // Reposition the header bar -- it's normally anchored to the top of the content,
    // but locks to the top of the screen on scroll
    int scrollY = mScrollView.getScrollY();

    float newTop = Math.max(mPhotoHeightPixels, scrollY);

    mHeaderBox.setTranslationY(newTop);
    mAddScheduleButtonContainer.setTranslationY(
        newTop + mHeaderHeightPixels - mAddScheduleButtonContainerHeightPixels / 2);

    float gapFillProgress = 1;
    if (mPhotoHeightPixels != 0) {
      gapFillProgress =
          Math.min(Math.max(UIUtils.getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);

      if (gapFillProgress == 1) {
        //    Toast.makeText(this, "Okay we're locked", Toast.LENGTH_LONG).show();
      } else if (gapFillProgress >= 1) {

      }
    }

    ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
    ViewCompat.setTranslationZ(mHeaderBox, gapFillProgress * mMaxHeaderElevation);

    // Move background photo (parallax effect)
    mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
  }

  public void displayData() {
    mHandler.post(() -> mScrollViewChild.setVisibility(View.VISIBLE));
  }
}
