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
package edu.geneve.autosize.unit;

import android.util.DisplayMetrics;
import edu.geneve.autosize.utils.Preconditions;

/**
 * 管理 AutoSize 支持的所有单位， AutoSize 支持五种单位： dp, sp, pt, in, mm 其中 dp、sp 是常见单位，作为 AutoSize的主单位，默认被
 * AutoSize 支持 pt、in、mm 是比较少见的单位， 只能选择其中的一个作为 AutoSize 的副单位 副单位是用于规避修改 {@link
 * DisplayMetrics#density} 所造成的对于其他使用 dp 布局的系统控件或第三方库控件的不良影响 选择什么单位，就在 layout 文件中使用什么单位布局 <br/>
 * 两个主单位和一个副单位，可以随时使用下面的方法关闭和重新开启对它们对支持
 */

public class UnitsManager {

  /**
   * 是否支持 dp 单位，默认开启
   */
  private boolean isSupportDP = true;

  /**
   * 是否支持 sp 单位，默认开启
   */
  private boolean isSupportSP = true;

  /**
   * 是否支持副单位，以什么作为副单位，默认不支持
   */
  private Subunits mSupportSubunits = Subunits.NONE;

  /**
   * 是否支持 dp 单位，默认支持，详情看类文件注释 {@link UnitsManager}
   *
   * @return {@code true} 为支持， {@code false} 为不支持
   */
  public boolean isSupportDP() {
    return isSupportDP;
  }

  /**
   * 是否让 AutoSize 支持 dp 单位, 详情请看类文件的注释 {@link UnitsManager}
   *
   * @param supportDP {@code true} 为支持， {@code false} 为不支持
   * @return {@link UnitsManager} 当前实例
   */
  public UnitsManager setSupportDP(boolean supportDP) {
    isSupportDP = supportDP;
    return this;
  }

  /**
   * 是否支持 sp 单位，默认支持，详情看类文件注释 {@link UnitsManager}
   *
   * @return {@code true} 为支持， {@code false} 为不支持
   */
  public boolean isSupportSP() {
    return isSupportSP;
  }

  /**
   * 是否让 AutoSize 支持 sp 单位, 详情请看类文件的注释 {@link UnitsManager}
   *
   * @param supportSP {@code true} 为支持， {@code false} 为不支持
   * @return {@link UnitsManager} 当前实例
   */
  public UnitsManager setSupportSP(boolean supportSP) {
    isSupportSP = supportSP;
    return this;
  }

  /**
   * AutoSize 使用什么单位作为副单位 默认为 {@link Subunits#NONE}, 即不支持副单位, 详情请看类文件的注释 {@link UnitsManager}
   *
   * @return {@link Subunits}
   */
  public Subunits getSupportSubunits() {
    return mSupportSubunits;
  }

  /**
   * 设置 AutoSize 的副单位, 在 pt、in、mm 三个单位中选择一个即可, 三个效果都是一样的 默认为 {@link Subunits#NONE}, 即不支持副单位,
   * 详情请看类文件的注释 {@link UnitsManager}
   *
   * @param supportSubunits {@link Subunits}
   * @return {@link UnitsManager}
   */

  public UnitsManager setSupportSubunits(Subunits supportSubunits) {
    this.mSupportSubunits = Preconditions.checkNotNull(supportSubunits,
        "The supportSubunits can not be null, use Subunits.NONE instead");
    return this;
  }
}
