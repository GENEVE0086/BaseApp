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
package edu.geneve.autosize.internal;

import android.app.Activity;

/**
 * 如果某些页面不想使用 AutoSize 初始化时设置的默认适配参数, 请让该页面 {@link Activity} 实现此接口 实现此接口即可自定义用于适配的一些参数, 从而影响最终的适配效果
 */

public interface CustomAdapt {

  /**
   * 是否按照屏幕宽度进行比例适配 ps:为保证宽高比不同的屏幕也能正常适配，所以只能用宽和高中的一个作为基准 {@code} 按照宽度进行适配 {@code} 按照高度进行适配
   */
  boolean isBaseOnWidth();

  /**
   * 返回设计图上的设计尺寸 {@link #getSizeInDp} 需要配合 {@link #isBaseOnWidth} 使用，规则如下: 如果 {@link #isBaseOnWidth}
   * 设置为 {@code true}, {@link #getSizeInDp} 应当上设计图的总宽度 如果 {@link #isBaseOnWidth} 设置为 {@code false},
   * {@link #getSizeInDp} 应当上设计图的总高度 若使用 AndroidManifest 设置设计图尺寸，{@link #getSizeInDp} 应设置为 {@code
   * 0}
   *
   * @return 设计图上的设计尺寸，单位 dp
   */
  float getSizeInDp();
}
