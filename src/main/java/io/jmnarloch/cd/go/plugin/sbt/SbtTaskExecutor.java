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

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionConfiguration;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionContext;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionResult;
import io.jmnarloch.cd.go.plugin.api.executor.TaskExecutor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The SBT task executor.
 *
 * @author Jakub Narloch
 */
public class SbtTaskExecutor implements TaskExecutor {

    /**
     * The logger instance used by this class.
     */
    private static final Logger logger = Logger.getLoggerFor(SbtTaskExecutor.class);

    /**
     * The build success message.
     */
    private static final String SUCCESS = "Build success";

    /**
     * The build failure message.
     */
    private static final String FAILURE = "Build failure";

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionResult execute(ExecutionContext context, ExecutionConfiguration config, JobConsoleLogger console) {

        try {
            final ProcessBuilder sbt = buildSbtProcess(context, config);

            int result = execute(sbt, console);

            if (!isSuccess(result)) {
                return ExecutionResult.failure(FAILURE);
            }

            return ExecutionResult.success(SUCCESS);
        } catch (Exception e) {
            logger.error("Build failed with error", e);

            console.printLine(e.getMessage());
            console.printLine(ExceptionUtils.getStackTrace(e));

            return ExecutionResult.failure(FAILURE, e);
        }
    }

    /**
     * Builds the SBT process to be executed
     *
     * @param environment the build environment
     * @param config      the build configuration
     * @return the SBT process
     */
    private ProcessBuilder buildSbtProcess(ExecutionContext environment, ExecutionConfiguration config) {

        final Map<String, String> env = environment.getEnvironmentVariables();

        final List<String> command = parse(config, env);

        logger.debug("Executing command: " + command);

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.environment().putAll(env);
        builder.directory(new File(environment.getWorkingDirectory()));
        return builder;
    }

    /**
     * Parses the task configuration.
     *
     * @param config the task configuration
     * @param env    the task environment
     * @return the command to execute
     */
    private List<String> parse(ExecutionConfiguration config, Map<String, String> env) {

        return SbtTaskConfigParser.fromConfig(config)
                .withEnvironment(env)
                .withSbtHome(SbtTaskConfig.SBT_HOME.getName())
                .withSbtVersion(SbtTaskConfig.SBT_VERSION.getName())
                .withTasks(SbtTaskConfig.TASKS.getName())
                .withAdditionalOptions(SbtTaskConfig.ADDITIONAL_OPTIONS.getName())
                .build();
    }

    private int execute(ProcessBuilder builder, JobConsoleLogger console) throws IOException, InterruptedException {

        Process process = null;
        try {
            process = builder.start();

            console.readOutputOf(process.getInputStream());
            console.readErrorOf(process.getErrorStream());
            return process.waitFor();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private boolean isSuccess(int result) {
        return result == 0;
    }
}
