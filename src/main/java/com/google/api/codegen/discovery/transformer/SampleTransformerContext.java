/* Copyright 2016 Google Inc
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
package com.google.api.codegen.discovery.transformer;

import com.google.api.codegen.discovery.config.SampleConfig;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SampleTransformerContext {

  public static SampleTransformerContext create(
      SampleConfig sampleConfig,
      SampleTypeTable typeTable,
      SampleNamer sampleNamer,
      String methodName) {
    return new AutoValue_SampleTransformerContext(sampleConfig, typeTable, sampleNamer, methodName);
  }

  public abstract SampleConfig getSampleConfig();

  public abstract SampleTypeTable getTypeTable();

  public abstract SampleNamer getSampleNamer();

  public abstract String getMethodName();
}