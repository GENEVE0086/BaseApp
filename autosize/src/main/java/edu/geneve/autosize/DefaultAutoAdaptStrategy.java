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
import edu.geneve.autosize.external.ExternalAdaptInfo;
import edu.geneve.autosize.internal.CancelAdapt;
import edu.geneve.autosize.internal.CustomAdapt;
import edu.geneve.autosize.utils.LogUtils;
import java.util.Locale;

/**
 * 屏幕适配逻辑策略默认实现类 可通过 {@link AutoSizeConfig#init(Application, boolean, AutoAdaptStrategy)} 和{@link
 * AutoSizeConfig#setAutoAdaptStrategy(AutoAdaptStrategy)} 切换策略
 *
 * @author Geneve
 * @version 1.0
 */

public class DefaultAutoAdaptStrategy implements AutoAdaptStrategy {

  @Override
  public void applyAdapt(Object target, Activity activity) {
    if (AutoSizeConfig.getInstance().getExternalAdaptManager().isRun()) {
      if (AutoSizeConfig.getInstance().getExternalAdaptManager().isCancelAdapt(target.getClass())) {
        LogUtils.w(String
            .format(Locale.ENGLISH, "%s canceled the adaptation!", target.getClass().getName()));
        AutoSize.cancelAdapt(activity);
        return;
      } else {
        ExternalAdaptInfo info = AutoSizeConfig.getInstance().getExternalAdaptManager()
            .getExternalAdaptInfoOfActivity(target.getClass());
        if (info != null) {
          LogUtils.d(String
              .format(Locale.ENGLISH, "%s used %s for adaptation!", target.getClass().getName(),
                  ExternalAdaptInfo.class.getName()));
          AutoSize.autoConvertDensityOfExternalAdaptInfo(activity, info);
          return;
        }
      }
    }

    if (target instanceof CancelAdapt) {
      LogUtils.w(String
          .format(Locale.ENGLISH, "%s canceled the adaptation!", target.getClass().getName()));
      AutoSize.cancelAdapt(activity);
      return;
    }

    if (target instanceof CustomAdapt) {
      LogUtils.d(String.format(Locale.ENGLISH, "%s implemented by %s!", target.getClass().getName(),
          CustomAdapt.class.getName()));
      AutoSize.autoConvertDensityOfCustomAdapt(activity, (CustomAdapt) target);
    } else {
      LogUtils.d(String.format(Locale.ENGLISH, "%s used the global configuration.",
          target.getClass().getName()));
      AutoSize.autoConvertDensityOfGlobal(activity);
    }
  }
}
