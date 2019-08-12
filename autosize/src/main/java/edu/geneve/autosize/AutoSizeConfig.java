/*
 * Copyright 2018 JessYan
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
package edu.geneve.autosize;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import androidx.fragment.app.Fragment;
import edu.geneve.autosize.external.ExternalAdaptManager;
import edu.geneve.autosize.unit.UnitsManager;
import edu.geneve.autosize.utils.LogUtils;
import edu.geneve.autosize.utils.Preconditions;
import edu.geneve.autosize.utils.ScreenUtils;

/**
 * AutoSize 参数配置类，给AutoSize 配置一些必要的自定义参数
 *
 * @author Geneve
 * @version 1.0
 */

public class AutoSizeConfig {

  private static volatile AutoSizeConfig sInstance;
  private static final String KEY_DESIGN_WIDTH_IN_DP = "design_width_in_dp";
  private static final String KEY_DESIGN_HEIGHT_IN_DP = "design_height_in_dp";
  private Application mApplication;

  /**
   * 用于管理第三方库 {@link Activity} 的适配
   */
  private ExternalAdaptManager mExternalAdaptManager = new ExternalAdaptManager();

  /**
   * 用于管理AutoSize 中所支持的单位
   */
  private UnitsManager mUnitsManager = new UnitsManager();

  /**
   * 最初的 {@link DisplayMetrics#density}
   */
  private float mInitDensity = -1;

  /**
   * 最初的 {@link DisplayMetrics#densityDpi}
   */
  private int mInitDensityDpi;

  /**
   * 最初的 {@link DisplayMetrics#scaledDensity}
   */
  private float mInitScaledDensity;

  /**
   * 最初的 {@link DisplayMetrics#xdpi}
   */
  private float mInitXdpi;

  /**
   * 设计图纸上的总宽度，单位 dp
   */
  private int mDesignWidthInDp;

  /**
   * 设计图纸上的总宽度，单位 dp
   */
  private int mDesignHeightInDp;

  /**
   * 设备屏幕的总宽度，单位 px
   */
  private int mScreenWidth;

  /**
   * 设备屏幕的总高度，单位 px 如果{@link #isUseDeviceSize} 为{@code false} 时，屏幕高度会减去状态栏的高度 如果有导航栏也会减去导航栏的高度
   */
  private int mScreenHeight;

  /**
   * 为了保证在不同宽高比的屏幕上的显示效果能够保持一致， 本方案适配时，以设计图宽度与设备实际宽度的比例或者设计图高度与设备实际高度的比例应用到每一个View上
   * ps：只能在宽度与高度中选择一个作为基准 从而使每个 View 的高和宽用相同的比例缩放，避免在与设计图宽高比例不一致的设备上出现适配的 View 变形的问题 {@link
   * #isBaseOnWidth} 为 {@code true} 时,代表以宽度进行等比例缩放 {@link #isBaseOnWidth} 为 {@code false}
   * 时,代表以高度进行等比例缩放 {@link #isBaseOnWidth} 为全局配置, 默认为 {@code true}, 每个 {@link Activity}
   * 也可以单独选择使用高或者宽做等比例缩放
   */
  private boolean isBaseOnWidth = true;

  /**
   * 表示是否使用设备的实际尺寸进行适配 {@link #isUseDeviceSize} 为 {@code true} 表示屏幕高度 {@link #mScreenHeight}
   * 包含状态栏的高度 {@link #isUseDeviceSize} 为 {@code false} 表示屏幕高度 {@link #mScreenHeight} 不包含状态栏的高度
   * 如果有导航栏，也会相应的加上或减去导航栏的高度 默认为 {@code true}
   */
  private boolean isUseDeviceSize = true;

  /**
   * {@link #mActivityLifecycleCallbacks} 可用来代替在 BaseActivity 中加入适配代码的传统方式 这种方案类似于 AOP, 面向接口, 侵入性低,
   * 方便统一管理, 扩展性强, 并且也支持适配三方库的 {@link Activity}
   */
  private ActivityLifecycleCallbacksImpl mActivityLifecycleCallbacks;

  /**
   * 框架具有 热插拔 特性, 支持在项目运行中动态停止和重新启动适配功能
   *
   * @see #stop(Activity)
   * @see #restart()
   */
  private boolean isStop;

  /**
   * 是否让框架支持自定义 {@link Fragment} 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
   */
  private boolean isCustomFragment;

  /**
   * 屏幕方向, {@code true} 为纵向, {@code false} 为横向
   */
  private boolean isVertical;

  public static AutoSizeConfig getInstance() {
    if (sInstance == null) {
      synchronized (AutoSizeConfig.class) {
        if (sInstance == null) {
          sInstance = new AutoSizeConfig();
        }
      }
    }
    return sInstance;
  }

  private AutoSizeConfig() {
  }

  public Application getApplication() {
    Preconditions.checkNotNull(mApplication,
        "Please call the AutoSizeConfig#init() first");
    return mApplication;
  }

  /**
   * 框架会在 APP 启动时调用此方法进行初始化，使用者不需要手动初始化， 初始化方法只能调用一次，否则报错 此方法默认以宽度进行等比例适配，如想使用以高度进行等比例适配，请调用 {@link
   * #init(Application, boolean)}
   */
  AutoSizeConfig init(Application application) {
    return init(application, true, null);
  }

  /**
   * 框架会在 APP 启动时调用此方法进行初始化，使用者不需要手动初始化， 初始化方法只能调用一次，否则报错 此方法使用默认的 {@link AutoAdaptStrategy}
   * 策略，如想使用自定义的 {@link AutoAdaptStrategy} 策略 请调用 {@link #init(Application, boolean,
   * AutoAdaptStrategy)}
   */
  AutoSizeConfig init(Application application, boolean isBaseOnWidth) {
    return init(application, isBaseOnWidth, null);
  }

  AutoSizeConfig init(final Application application, boolean isBaseOnWidth,
      AutoAdaptStrategy strategy) {
    Preconditions
        .checkArgument(mInitDensity == -1, "AutoSizeConfig#init() can only be called once");
    Preconditions.checkNotNull(application, "application is NULL");
    this.mApplication = application;
    this.isBaseOnWidth = isBaseOnWidth;
    final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
    getMetaData(application);
    isVertical = application.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT;
    int[] screenSize = ScreenUtils.getScreenSize(application);
    mScreenWidth = screenSize[0];
    mScreenHeight = screenSize[1];
    LogUtils.d(
        "designWidthInDp = " + mDesignWidthInDp +
            ", designHeightInDp = " + mDesignHeightInDp +
            ", screenWidth = " + mScreenWidth +
            ", screenHeight = " + mScreenHeight);

    mInitDensity = displayMetrics.density;
    mInitDensityDpi = displayMetrics.densityDpi;
    mInitScaledDensity = displayMetrics.scaledDensity;
    mInitXdpi = displayMetrics.xdpi;
    application.registerComponentCallbacks(new ComponentCallbacks() {
      @Override
      public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig != null) {
          if (newConfig.fontScale > 0) {
            mInitScaledDensity =
                Resources.getSystem().getDisplayMetrics().scaledDensity;
            LogUtils.d("initScaledDensity = " + mInitScaledDensity + " on ConfigurationChanged");
          }
          isVertical = application.getResources().getConfiguration().orientation
              == Configuration.ORIENTATION_PORTRAIT;
          int[] screenSize = ScreenUtils.getScreenSize(application);
          mScreenWidth = screenSize[0];
          mScreenHeight = screenSize[1];
        }
      }

      @Override
      public void onLowMemory() {
      }
    });
    LogUtils.d(
        "initDensity = " + mInitDensity +
            ", initScaledDensity = " + mInitScaledDensity);
    mActivityLifecycleCallbacks = new ActivityLifecycleCallbacksImpl(
        strategy == null ? new DefaultAutoAdaptStrategy() : strategy);
    application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    return this;
  }

  /**
   * 重新开始框架的运行 框架具有 热插拔 特性, 支持在项目运行中动态停止和重新启动适配功能
   */
  public void restart() {
    Preconditions
        .checkNotNull(mActivityLifecycleCallbacks, "Please call the AutoSizeConfig#init() first");
    synchronized (AutoSizeConfig.class) {
      if (isStop) {
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        isStop = false;
      }
    }
  }

  /**
   * 停止框架的运行 框架具有 热插拔 特性, 支持在项目运行中动态停止和重新启动适配功能
   */
  public void stop(Activity activity) {
    Preconditions
        .checkNotNull(mActivityLifecycleCallbacks, "Please call the AutoSizeConfig#init() first");
    synchronized (AutoSizeConfig.class) {
      if (!isStop) {
        mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        AutoSize.cancelAdapt(activity);
        isStop = true;
      }
    }
  }

  /**
   * 设置屏幕适配逻辑策略类
   */
  public AutoSizeConfig setAutoAdaptStrategy(AutoAdaptStrategy autoAdaptStrategy) {
    Preconditions.checkNotNull(autoAdaptStrategy, "autoAdaptStrategy is NULL");
    Preconditions
        .checkNotNull(mActivityLifecycleCallbacks, "Please call the AutoSizeConfig#init() first");
    mActivityLifecycleCallbacks.setAutoAdaptStrategy(autoAdaptStrategy);
    return this;
  }

  /**
   * 设置是否全局按照宽度进行等比例适配
   *
   * @see #isBaseOnWidth
   */
  public AutoSizeConfig setBaseOnWidth(boolean baseOnWidth) {
    isBaseOnWidth = baseOnWidth;
    return this;
  }

  /**
   * 是否使用设备的实际尺寸做适配
   *
   * @see #isUseDeviceSize
   */
  public AutoSizeConfig setUseDeviceSize(boolean useDeviceSize) {
    isUseDeviceSize = useDeviceSize;
    return this;
  }

  /**
   * 是否打印 Log
   *
   * @param log {@code true} 为打印
   */
  public AutoSizeConfig setLog(boolean log) {
    LogUtils.setDebug(log);
    return this;
  }

  /**
   * 是否让框架支持自定义 {@link Fragment} 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
   *
   * @param customFragment {@code true} 为支持
   */
  public AutoSizeConfig setCustomFragment(boolean customFragment) {
    isCustomFragment = customFragment;
    return this;
  }

  /**
   * 框架是否已经开启支持自定义 {@link Fragment} 的适配参数
   *
   * @return {@code true} 为支持
   */
  public boolean isCustomFragment() {
    return isCustomFragment;
  }

  /**
   * 框架是否已经停止运行
   *
   * @return {@code false} 框架正在运行, {@code true} 框架已经停止运行
   */
  public boolean isStop() {
    return isStop;
  }

  /**
   * {@link ExternalAdaptManager} 用来管理外部三方库 {@link Activity} 的适配
   *
   * @return {@link ExternalAdaptManager}
   */
  public ExternalAdaptManager getExternalAdaptManager() {
    return mExternalAdaptManager;
  }

  /**
   * {@link UnitsManager} 用来管理 AndroidAutoSize 支持的所有单位, AndroidAutoSize 支持五种单位 (dp、sp、pt、in、mm)
   *
   * @return {@link UnitsManager}
   */
  public UnitsManager getUnitsManager() {
    return mUnitsManager;
  }

  /**
   * 返回 {@link #isBaseOnWidth}
   *
   * @return {@link #isBaseOnWidth}
   */
  public boolean isBaseOnWidth() {
    return isBaseOnWidth;
  }

  /**
   * 返回 {@link #isUseDeviceSize}
   *
   * @return {@link #isUseDeviceSize}
   */
  public boolean isUseDeviceSize() {
    return isUseDeviceSize;
  }

  /**
   * 返回 {@link #mScreenWidth}
   *
   * @return {@link #mScreenWidth}
   */
  public int getScreenWidth() {
    return mScreenWidth;
  }

  /**
   * 返回 {@link #mScreenHeight}
   *
   * @return {@link #mScreenHeight}
   */
  public int getScreenHeight() {
    return isUseDeviceSize() ? mScreenHeight
        : mScreenHeight - ScreenUtils.getStatusBarHeight() - ScreenUtils
            .getNavigationBarHeight(getApplication());
  }

  /**
   * 获取 {@link #mDesignWidthInDp}
   *
   * @return {@link #mDesignWidthInDp}
   */
  public int getDesignWidthInDp() {
    Preconditions.checkArgument(mDesignWidthInDp > 0,
        "you must set " + KEY_DESIGN_WIDTH_IN_DP + "  in your AndroidManifest file");
    return mDesignWidthInDp;
  }

  /**
   * 获取 {@link #mDesignHeightInDp}
   *
   * @return {@link #mDesignHeightInDp}
   */
  public int getDesignHeightInDp() {
    Preconditions.checkArgument(mDesignHeightInDp > 0,
        "you must set " + KEY_DESIGN_HEIGHT_IN_DP + "  in your AndroidManifest file");
    return mDesignHeightInDp;
  }

  /**
   * 获取 {@link #mInitDensity}
   *
   * @return {@link #mInitDensity}
   */
  public float getInitDensity() {
    return mInitDensity;
  }

  /**
   * 获取 {@link #mInitDensityDpi}
   *
   * @return {@link #mInitDensityDpi}
   */
  public int getInitDensityDpi() {
    return mInitDensityDpi;
  }

  /**
   * 获取 {@link #mInitScaledDensity}
   *
   * @return {@link #mInitScaledDensity}
   */
  public float getInitScaledDensity() {
    return mInitScaledDensity;
  }

  /**
   * 获取 {@link #mInitXdpi}
   *
   * @return {@link #mInitXdpi}
   */
  public float getInitXdpi() {
    return mInitXdpi;
  }

  /**
   * 获取屏幕方向
   *
   * @return {@code true} 为纵向, {@code false} 为横向
   */
  public boolean isVertical() {
    return isVertical;
  }

  /**
   * 设置屏幕方向
   *
   * @param vertical {@code true} 为纵向, {@code false} 为横向
   */
  public void setVertical(boolean vertical) {
    isVertical = vertical;
  }

  /**
   * 设置屏幕宽度
   *
   * @param screenWidth 屏幕宽度
   */
  public void setScreenWidth(int screenWidth) {
    mScreenWidth = screenWidth;
  }

  /**
   * 设置屏幕高度
   *
   * @param screenHeight 屏幕高度 (包含状态栏和导航栏)
   */
  public void setScreenHeight(int screenHeight) {
    mScreenHeight = screenHeight;
  }

  /**
   * 获取使用者在 AndroidManifest 中填写的 Meta 信息
   * <p>
   * Example usage:
   * <pre>
   * <meta-data android:name="design_width_in_dp"
   *            android:value="360"/>
   * <meta-data android:name="design_height_in_dp"
   *            android:value="640"/>
   * </pre>
   */
  private void getMetaData(final Context context) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
          applicationInfo = packageManager.getApplicationInfo(
              context.getPackageName(), PackageManager.GET_META_DATA);
          if (applicationInfo != null && applicationInfo.metaData != null) {
            if (applicationInfo.metaData.containsKey(KEY_DESIGN_HEIGHT_IN_DP)) {
              mDesignHeightInDp = (int) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT_IN_DP);
            }
            if (applicationInfo.metaData.containsKey(KEY_DESIGN_WIDTH_IN_DP)) {
              mDesignWidthInDp = (int) applicationInfo.metaData.get(KEY_DESIGN_WIDTH_IN_DP);
            }
          }
        } catch (NameNotFoundException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public AutoSizeConfig setDesignHeightInDp(int designHeightInDp) {
    this.mDesignHeightInDp = designHeightInDp;
    return this;
  }

  public AutoSizeConfig setDesignWidthInDp(int designWidthInDp) {
    this.mDesignWidthInDp = designWidthInDp;
    return this;
  }
}
