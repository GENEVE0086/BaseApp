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
package edu.geneve.autosize.external;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link ExternalAdaptInfo} 用于存储第三方库的适配参数，由于 AutoSize 默认会对项目中对所有模块进行适配 第三方库的 {@link Activity}
 * 也不例外，但是第三方库的适配参数可能会与项目的适配参数不一致 倒是第三方库的适配效果与理想效果相差很大，所以需要向 AutoSize 提供第三方库的适配参数 来完成对第三方库对屏幕适配
 *
 * @author Geneve
 * @version 1.0
 */

public class ExternalAdaptInfo implements Parcelable {

  /**
   * 是否按照屏幕宽度进行比例适配 ps:为保证宽高比不同的屏幕也能正常适配，所以只能用宽和高中的一个作为基准 {@code} 按照宽度进行适配 {@code} 按照高度进行适配
   */
  private boolean isBaseOnWidth;

  /**
   * 设计图上的设计尺寸，单位 dp {@link #sizeInDp} 需要配合 {@link #isBaseOnWidth} 使用，规则如下: 如果 {@link
   * #isBaseOnWidth} 设置为 {@code true}, {@link #sizeInDp} 应当上设计图的总宽度 如果 {@link #isBaseOnWidth} 设置为
   * {@code false}, {@link #sizeInDp} 应当上设计图的总高度 若使用 AndroidManifest 设置设计图尺寸，{@link #sizeInDp} 应设置为
   * {@code 0}
   */
  private float sizeInDp;

  public ExternalAdaptInfo(boolean isBaseOnWidth) {
    this.isBaseOnWidth = isBaseOnWidth;
  }

  public ExternalAdaptInfo(boolean isBaseOnWidth, float sizeInDp) {
    this.isBaseOnWidth = isBaseOnWidth;
    this.sizeInDp = sizeInDp;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeByte(this.isBaseOnWidth ? (byte) 1 : (byte) 0);
    parcel.writeFloat(this.sizeInDp);
  }

  public static final Creator<ExternalAdaptInfo> CREATOR =
      new Creator<ExternalAdaptInfo>() {
        @Override
        public ExternalAdaptInfo createFromParcel(Parcel parcel) {
          return new ExternalAdaptInfo(parcel);
        }

        @Override
        public ExternalAdaptInfo[] newArray(int i) {
          return new ExternalAdaptInfo[i];
        }
      };

  private ExternalAdaptInfo(Parcel in) {
    this.isBaseOnWidth = in.readByte() != 0;
    this.sizeInDp = in.readFloat();
  }

  public boolean isBaseOnWidth() {
    return isBaseOnWidth;
  }

  public void setBaseOnWidth(boolean baseOnWidth) {
    isBaseOnWidth = baseOnWidth;
  }

  public float getSizeInDp() {
    return sizeInDp;
  }

  public void setSizeInDp(float sizeInDp) {
    this.sizeInDp = sizeInDp;
  }

  @Override
  public String toString() {
    return
        "ExternalAdaptInfo{" +
            "isBaseOnWidth=" + isBaseOnWidth +
            ", sizeInDp=" + sizeInDp +
            "}";
  }
}
