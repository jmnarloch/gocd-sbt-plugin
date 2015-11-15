/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.cd.go.plugin.sbt;

import io.jmnarloch.cd.go.plugin.api.config.ConfigProperty;
import io.jmnarloch.cd.go.plugin.api.config.PropertyName;

/**
 * The SBT task configuration.
 *
 * @author Jakub Narloch
 */
public enum SbtTaskConfig {

    /**
     * The SBT home directory.
     */
    @ConfigProperty
    SBT_HOME("SbtHome"),

    /**
     * The SBT version.
     */
    @ConfigProperty
    SBT_VERSION("SbtVersion"),

    /**
     * The tasks (goals) to execute.
     */
    @ConfigProperty(required = true)
    TASKS("Tasks"),

    /**
     * Additional options to be passed to SBT process.
     */
    @ConfigProperty
    ADDITIONAL_OPTIONS("AdditionalOptions");

    /**
     * The property name.
     */
    @PropertyName
    private String name;

    /**
     * Creates new instance of {@link SbtTaskConfig} with property name.
     *
     * @param name the property name
     */
    SbtTaskConfig(String name) {
        this.name = name;
    }

    /**
     * Retrieves the configuration property name.
     *
     * @return the configuration property name
     */
    public String getName() {
        return name;
    }
}
