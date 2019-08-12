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
import androidx.fragment.app.Fragment;
import edu.geneve.autosize.AutoSizeConfig;
import edu.geneve.autosize.utils.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ExternalAdaptManager} 管理第三方库的适配信息和状态，通过 {@link AutoSizeConfig#getExternalAdaptManager()}
 * 获取，不可自己 new 实例 AutoSize 通过实现接口的方式让每个 {@link Activity} 都具有自定义适配参数的功能，实现自定义适配效果
 * 但是远程以来的第三方库不能修改源代码，也不能让第三方的 {@link Activity} 实现接口 {@link ExternalAdaptManager}
 * 用于解决该问题，项目初始化的时将对应的第三方 {@link Activity} 传入其中即可
 *
 * @author Geneve
 * @version 1.0
 */

public class ExternalAdaptManager {

  private List<String> mCancelAdaptList;
  private Map<String, ExternalAdaptInfo> mExternalAdaptInfo;
  private boolean isRun;

  /**
   * 将不需要适配的第三方库(但不局限于三方库) {@link Activity} 添加进来 , 即可让该 {@link Activity} 的适配效果失效
   * <p>
   * 支持链式调用，如： {@link ExternalAdaptManager#addCancelAdaptOfActivity(Class)#addCancelAdaptOfActivity(Class)}
   *
   * @param targetClass {@link Activity} class, {@link Fragment} class
   * @return {@link ExternalAdaptManager} self
   */
  public synchronized ExternalAdaptManager addCancelAdaptOfActivity(Class<?> targetClass) {
    Preconditions.checkNotNull(targetClass, "targetClass == null");
    if (!isRun) {
      isRun = true;
    }
    if (mCancelAdaptList == null) {
      mCancelAdaptList = new ArrayList<>();
    }
    mCancelAdaptList.add(targetClass.getCanonicalName());
    return this;
  }

  /**
   * 将需要提供自定义适配参数的三方库(但不局限于三方库) {@link Activity} 添加进来 即可让该 {@link Activity} 根据自己提供的适配参数进行适配
   * 默认的全局适配参数不能满足您时可以使用此方法
   * <p>
   * 支持链式调用, 如: {@link ExternalAdaptManager#addExternalAdaptInfoOfActivity(Class,
   * ExternalAdaptInfo)#addExternalAdaptInfoOfActivity(Class, ExternalAdaptInfo)}
   *
   * @param targetClass {@link Activity} class, {@link Fragment} class
   * @param info {@link ExternalAdaptInfo} 适配参数
   */
  public synchronized ExternalAdaptManager addExternalAdaptInfoOfActivity(
      Class<?> targetClass, ExternalAdaptInfo info) {
    Preconditions.checkNotNull(targetClass, "targetClass == null");
    if (!isRun) {
      isRun = true;
    }
    if (mExternalAdaptInfo == null) {
      mExternalAdaptInfo = new HashMap<>(16);
    }
    mExternalAdaptInfo.put(targetClass.getCanonicalName(), info);
    return this;
  }

  /**
   * 这个 {@link Activity} 是否存在在取消适配的列表中, 如果在, 则该 {@link Activity} 适配失效
   *
   * @param targetClass {@link Activity} class, {@link Fragment} class
   * @return {@code true} 为存在, {@code false} 为不存在
   */
  public synchronized boolean isCancelAdapt(Class<?> targetClass) {
    Preconditions.checkNotNull(targetClass, "targetClass == null");
    if (mCancelAdaptList == null) {
      return false;
    }
    return mCancelAdaptList.contains(targetClass.getCanonicalName());
  }

  /**
   * 这个 {@link Activity} 是否提供有自定义的适配参数, 如果有则使用此适配参数进行适配
   *
   * @param targetClass {@link Activity} class, {@link Fragment} class
   * @return 如果返回 {@code null} 则说明该 {@link Activity} 没有提供自定义的适配参数
   */
  public synchronized ExternalAdaptInfo getExternalAdaptInfoOfActivity(Class<?> targetClass) {
    Preconditions.checkNotNull(targetClass, "targetClass == null");
    if (mExternalAdaptInfo == null) {
      return null;
    }
    return mExternalAdaptInfo.get(targetClass.getCanonicalName());
  }

  /**
   * 此管理器是否已经启动
   *
   * @return {@code true} 为已经启动, {@code false} 为没有启动
   */
  public boolean isRun() {
    return isRun;
  }

  /**
   * 设置管理器的运行状态
   *
   * @param run {@code true} 为让管理器启动运行, {@code false} 为让管理器停止运行
   */
  public void setRun(boolean run) {
    isRun = run;
  }
}
