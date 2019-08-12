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
import androidx.fragment.app.Fragment;

/**
 * 屏幕适配策略类，通过
 */

public interface AutoAdaptStrategy {

  /**
   * 执行屏幕适配
   *
   * @param target 需要适配的对象（可能是 {@link Activity} 或者 {@link Fragment}）
   * @param activity 需要拿到当前的 {@link Activity} 才能修改 {@link android.util.DisplayMetrics#density}
   */
  void applyAdapt(Object target, Activity activity);
}
