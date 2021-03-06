/* Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.api.codegen.configgen.mergers;

import com.google.api.codegen.config.ProtoApiModel;
import com.google.api.codegen.configgen.ConfigHelper;
import com.google.api.codegen.configgen.InterfaceTransformer;
import com.google.api.codegen.configgen.ProtoInterfaceTransformer;
import com.google.api.codegen.configgen.ProtoMethodTransformer;
import com.google.api.codegen.configgen.ProtoPageStreamingTransformer;
import com.google.api.codegen.configgen.nodes.ConfigNode;
import com.google.api.tools.framework.model.Interface;
import com.google.api.tools.framework.model.Model;
import com.google.protobuf.Api;
import java.io.File;

/** Merges the gapic config from a proto Model into a ConfigNode. */
public class ProtoConfigMerger {
  public ConfigNode mergeConfig(Model model, String fileName) {
    ConfigMerger configMerger = createMerger(model, fileName);
    if (configMerger == null) {
      return null;
    }

    return configMerger.mergeConfig(new ProtoApiModel(model));
  }

  public ConfigNode mergeConfig(Model model, File file) {
    ConfigMerger configMerger = createMerger(model, file.getName());
    if (configMerger == null) {
      return null;
    }

    return configMerger.mergeConfig(new ProtoApiModel(model), file);
  }

  private ConfigMerger createMerger(Model model, String fileName) {
    ConfigHelper helper = new ConfigHelper(model.getDiagCollector(), fileName);
    String packageName = getPackageName(model, helper);
    if (packageName == null) {
      return null;
    }

    CollectionMerger collectionMerger = new CollectionMerger();
    RetryMerger retryMerger = new RetryMerger();
    PageStreamingMerger pageStreamingMerger =
        new PageStreamingMerger(new ProtoPageStreamingTransformer(), helper);
    MethodMerger methodMerger =
        new MethodMerger(retryMerger, pageStreamingMerger, new ProtoMethodTransformer());
    LanguageSettingsMerger languageSettingsMerger = new LanguageSettingsMerger();
    InterfaceTransformer interfaceTranformer = new ProtoInterfaceTransformer();
    InterfaceMerger interfaceMerger =
        new InterfaceMerger(collectionMerger, retryMerger, methodMerger, interfaceTranformer);
    return new ConfigMerger(languageSettingsMerger, interfaceMerger, packageName, helper);
  }

  private String getPackageName(Model model, ConfigHelper helper) {
    if (model.getServiceConfig().getApisCount() > 0) {
      Api api = model.getServiceConfig().getApis(0);
      Interface apiInterface = model.getSymbolTable().lookupInterface(api.getName());
      if (apiInterface != null) {
        return apiInterface.getFile().getFullName();
      }
    }

    helper.error(model.getLocation(), "No interface found");
    return null;
  }
}
