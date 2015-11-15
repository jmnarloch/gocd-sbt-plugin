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

import io.jmnarloch.cd.go.plugin.api.executor.ExecutionConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.jmnarloch.cd.go.plugin.sbt.Sbt.sbt;

/**
 * The SBT task configuration parser.
 *
 * @author Jakub Narloch
 */
final class SbtTaskConfigParser {

    /**
     * The SBT home.
     */
    private static final String SBT_HOME = "SBT_HOME";

    /**
     * The SBT bin.
     */
    private static final String SBT_BIN = "bin";

    /**
     * The PATH environment variable.
     */
    private static final String PATH = "PATH";

    /**
     * The OS name.
     */
    private static final String OS_NAME = "os.name";

    /**
     * The task configuration.
     */
    private final ExecutionConfiguration configuration;

    /**
     * The SBT tasks.
     */
    private final List<String> tasks = new ArrayList<String>();

    /**
     * The SBT options.
     */
    private final List<String> options = new ArrayList<String>();

    /**
     * The execution environment.
     */
    private Map<String, String> environment = new HashMap<String, String>();

    /**
     * SBT HOME dir.
     */
    private String sbtHome;

    /**
     * SBT version.
     */
    private String sbtVersion;

    /**
     * Creates new instance of {@link SbtTaskConfigParser}.
     *
     * @param config the task configuration
     */
    private SbtTaskConfigParser(ExecutionConfiguration config) {
        this.configuration = config;
    }

    /**
     * Specifies the build environment.
     *
     * @param environment the environment
     * @return the config parser
     */
    SbtTaskConfigParser withEnvironment(Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Specifies the SBT home directory.
     *
     * @param propertyKey the name of the property that specifies this setting
     * @return the config parser
     */
    SbtTaskConfigParser withSbtHome(String propertyKey) {
        this.sbtHome = configuration.getProperty(propertyKey);
        return this;
    }

    /**
     * Specifies the SBT version.
     *
     * @param propertyKey the name of the property that specifies this setting
     * @return the config parser
     */
    SbtTaskConfigParser withSbtVersion(String propertyKey) {
        String version = configuration.getProperty(propertyKey);
        if (!StringUtils.isBlank(version)) {
            this.options.add(String.format("-Dsbt.version=%s", version));
        }
        return this;
    }

    /**
     * Specifies the SBT tasks to be executed.
     *
     * @param propertyKey the name of the property that specifies this setting
     * @return the config parser
     */
    SbtTaskConfigParser withTasks(String propertyKey) {
        final String tasks = configuration.getProperty(propertyKey);
        if (!StringUtils.isBlank(tasks)) {
            this.tasks.addAll(Arrays.asList(tasks.split("\\s+")));
        }
        return this;
    }

    /**
     * Specifies the additional SBT command line options to be passed to the build.
     *
     * @param propertyKey the name of the property that specifies this setting
     * @return the config parser
     */
    SbtTaskConfigParser withAdditionalOptions(String propertyKey) {
        final String additional = configuration.getProperty(propertyKey);
        if (!StringUtils.isBlank(additional)) {
            this.options.addAll(Arrays.asList(additional.split("\\s+")));
        }
        return this;
    }

    /**
     * Creates new instance of {@link SbtTaskConfigParser}.
     *
     * @param config the task configuration
     * @return the config parser
     */
    static SbtTaskConfigParser fromConfig(ExecutionConfiguration config) {
        return new SbtTaskConfigParser(config);
    }

    /**
     * Builds the SBT executable command.
     *
     * @return the SBT executable command
     */
    List<String> build() {
        final List<String> command = new ArrayList<String>();
        setSbtCommand(command);
        command.addAll(options);
        command.addAll(tasks);
        return command;
    }

    /**
     * Sets the SBT command.
     *
     * @param command the SBT command
     */
    private void setSbtCommand(List<String> command) {
        String sbtHome = getSbtHome();
        String sbt;

        if (isWindows()) {
            sbt = sbt().windows();
        } else {
            sbt = sbt().unix();
        }

        if (!StringUtils.isBlank(sbtHome)) {
            sbt = Paths.get(sbtHome, SBT_BIN, sbt).toAbsolutePath().normalize().toString();
        } else {
            sbt = getExecutablePath(sbt);
        }
        command.add(sbt);
    }

    /**
     * Finds first matching path to executable file by iterating over all system path entires.
     *
     * @param command the command
     * @return the absolute path to the executable file
     */
    private String getExecutablePath(String command) {
        final String systemPath = getEnvironmentVariable(PATH);
        if (StringUtils.isBlank(systemPath)) {
            return command;
        }
        final String[] paths = systemPath.split(File.pathSeparator);
        for (String path : paths) {
            if (Files.exists(Paths.get(path, command))) {
                return Paths.get(path, command).toAbsolutePath().normalize().toString();
            }
        }
        return command;
    }

    /**
     * Retrieves the SBT home directory, which might be either specified as environment variable or overridden for
     * specific task.
     *
     * @return the SBT home
     */
    private String getSbtHome() {
        if (!StringUtils.isBlank(sbtHome)) {
            return sbtHome;
        } else if (!StringUtils.isBlank(environment.get(SBT_HOME))) {
            return environment.get(SBT_HOME);
        }
        return null;
    }

    /**
     * Returns whether current OS family is Windows.
     *
     * @return true if current task is executed on Windows
     */
    private boolean isWindows() {
        final String os = getSystemProperty(OS_NAME);
        return !StringUtils.isBlank(os) && os.toLowerCase().contains("win");
    }

    /**
     * Retrieves the systemm property.
     *
     * @param property the property name
     * @return the system property
     */
    private String getSystemProperty(String property) {
        return environment.containsKey(property) ? environment.get(property) : System.getProperty(property);
    }

    /**
     * Retrieves the environment variable.
     *
     * @param property the property name
     * @return the environment variable
     */
    private String getEnvironmentVariable(String property) {

        return environment.containsKey(property) ? environment.get(property) : System.getenv(property);
    }
}
