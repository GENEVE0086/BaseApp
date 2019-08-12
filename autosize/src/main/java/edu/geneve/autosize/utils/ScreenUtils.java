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
package edu.geneve.autosize.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕工具
 *
 * @author Geneve
 * @version 1.0
 */

public class ScreenUtils {

  private ScreenUtils() {
    throw new IllegalStateException("ScreenUtils can not be instantiated!");
  }

  /**
   * 获取状态栏高度
   *
   * @return 状态栏高度，单位 px
   */
  public static int getStatusBarHeight() {
    int result = 0;
    try {
      int resourceId = Resources.getSystem().getIdentifier(
          "status_bar_height", "dimen", "android");
      if (resourceId > 0) {
        result = Resources.getSystem().getDimensionPixelSize(resourceId);
      }
    } catch (Resources.NotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 获取屏幕的宽高
   *
   * @return 屏幕的宽高，单位 px
   */

  public static int[] getScreenSize(Context context) {
    int[] size = new int[2];

    WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    Display d = w.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    d.getMetrics(metrics);

    int widthPixels = metrics.widthPixels;
    int heightPixels = metrics.heightPixels;

    if (VERSION.SDK_INT < 17) {
      try {
        widthPixels = (int) Display.class.getMethod("getRawWidth").invoke(d);
        heightPixels = (int) Display.class.getMethod("getRawHeight").invoke(d);
      } catch (Exception ignored) {
      }
    } else {
      try {
        Point realSize = new Point();
        Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
        widthPixels = realSize.x;
        heightPixels = realSize.y;
      } catch (Exception ignored) {
      }
    }

    size[0] = widthPixels;
    size[1] = heightPixels;
    return size;
  }

  /**
   * 获取导航栏的高度 若开启全面屏手势，返回高度为0
   *
   * @return 导航栏的高度，单位 px
   */
  public static int getNavigationBarHeight(Context context) {
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
      if (Settings.Global.getInt(
          context.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
        return 0;
      }
    }

    Display d = ((WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

    int realHeight = getScreenSize(context)[1];

    DisplayMetrics metrics = new DisplayMetrics();
    d.getMetrics(metrics);

    int displayHeight = metrics.heightPixels;

    return realHeight - displayHeight;
  }
}
