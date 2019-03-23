package com.kyler.mland.egg.activities;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.MLandOriginal;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.drawerMLandOriginal;
import java.util.Objects;

/** Created by kyler on 10/26/15. */
public class Home extends MLandBase {
  private static Bitmap sIcon = null;
  private drawerMLandOriginal mLand;

  @Override
  protected int getSelfNavDrawerItem() {
    return NAVDRAWER_ITEM_HOME;
  }

  @Override
  protected Context getContext() {
    return Home.this;
  }

  //    private final int darkText =
  // darkenColor((UIUtils.scaleSessionColorToDefaultBG(MLandModified.DAY)));

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.Theme_MLand_Home);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home);
    super.getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    activateLightNavigationBar();
    activateLightStatusBar();
    mLand = findViewById(R.id.world);
    mLand.setScoreFieldHolder(findViewById(R.id.scores));
    final View welcome = findViewById(R.id.welcome);
    mLand.setSplash(welcome);
    final int numControllers = mLand.getGameControllers().size();
    if (numControllers > 0) {
      mLand.setupPlayers(numControllers);
    }

    // I should perhaps do some research to see whether or not
    // setting the SupportActionBars title to an empty string is
    // more efficient than *.setTitle(null);
    Objects.requireNonNull(getSupportActionBar()).setTitle(null);

    Resources resources = this.getResources();
    String label = resources.getString(this.getApplicationInfo().labelRes);
    final int aboutPrimaryDark = resources.getColor(R.color.colorPrimaryDark);

    if (sIcon == null) {
      // Cache to avoid decoding the same bitmap on every Activity change
      sIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_mland_home);
    }

    this.setTaskDescription(new ActivityManager.TaskDescription(label, sIcon, aboutPrimaryDark));
  }

  @ColorInt
  private int darkenColor(@ColorInt int color) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[2] *= 0.8f;
    // hsv[2] = 0.2f + 0.8f * hsv[2];
    return Color.HSVToColor(hsv);
  }

  /** Sets the status bar to be light or not. Light status bar means dark icons. */
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

  private void activateLightStatusBar() {
    int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
    int newSystemUiFlags = oldSystemUiFlags;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
      getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.black__10_percent));
    }
    if (newSystemUiFlags != oldSystemUiFlags) {
      final int systemUiFlags = newSystemUiFlags;
      runOnUiThread(
          () -> {
            getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
            activateLightStatusBar();
          });
    }
  }

  private void updateSplashPlayers() {
    final int N = mLand.getNumPlayers();
    final View minus = findViewById(R.id.player_minus_button);
    final View plus = findViewById(R.id.player_plus_button);
    if (N == 1) {
      minus.setVisibility(View.INVISIBLE);
      plus.setVisibility(View.VISIBLE);
      plus.requestFocus();
    } else if (N == MLandOriginal.MAX_PLAYERS) {
      minus.setVisibility(View.VISIBLE);
      plus.setVisibility(View.INVISIBLE);
      minus.requestFocus();
    } else {
      minus.setVisibility(View.VISIBLE);
      plus.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onPause() {
    mLand.stop();
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();

    mLand.onAttachedToWindow(); // resets and starts animation
    updateSplashPlayers();
    mLand.showSplash();
  }

  public void playerMinus(View v) {
    mLand.removePlayer();
    updateSplashPlayers();
  }

  public void playerPlus(View v) {
    mLand.addPlayer();
    updateSplashPlayers();
  }

  public void startButtonPressed(View v) {
    findViewById(R.id.player_minus_button).setVisibility(View.INVISIBLE);
    findViewById(R.id.player_plus_button).setVisibility(View.INVISIBLE);
    mLand.start(true);
  }
}
