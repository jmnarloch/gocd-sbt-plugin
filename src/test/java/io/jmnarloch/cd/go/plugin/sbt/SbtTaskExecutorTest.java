package io.jmnarloch.cd.go.plugin.sbt;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionConfiguration;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionContext;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionResult;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests the {@link SbtTaskExecutor} class.
 *
 * @author Jakub Narloch
 */
public class SbtTaskExecutorTest {

    /**
     * Instance of the tested class.
     */
    private SbtTaskExecutor instance;

    /**
     * Sets up the tests environment.
     *
     * @throws Exception if any error occurs
     */
    @Before
    public void setUp() throws Exception {

        instance = new SbtTaskExecutor();
    }

    @Test
    public void shouldBuildSbtProject() throws Exception {

        // given
        final ExecutionContext executionContext = createExecutionContext();
        final ExecutionConfiguration executionConfiguration = createExecutionConfig(Collections.<String, String>emptyMap());
        final JobConsoleLogger jobConsoleLogger = createConsoleLogger();

        // when
        final ExecutionResult result = instance.execute(executionContext, executionConfiguration, jobConsoleLogger);

        // then
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @SuppressWarnings("unchecked")
    private ExecutionContext createExecutionContext() {
        final Map<String, Object> config = new HashMap<>();
        final Map<String, String> env = new LinkedHashMap<>((Map)System.getProperties());
        config.put("workingDirectory", Paths.get("src/test/resources/sbt").toAbsolutePath().toString());
        config.put("environmentVariables", env);
        return new ExecutionContext(config);
    }

    private ExecutionConfiguration createExecutionConfig(Map<String, String> overrides) {
        final Map<String, Object> config = new HashMap<>();
        addConfigProperty(config, SbtTaskConfig.TASKS.getName(), "clean compile");
        for(Map.Entry<String, String> property : overrides.entrySet()) {
            addConfigProperty(config, property.getKey(), property.getValue());
        }
        return new ExecutionConfiguration(config);
    }

    private void addConfigProperty(Map<String, Object> config, String name, String value) {
        config.put(name, singletonMap("value", value));
    }

    private JobConsoleLogger createConsoleLogger() {
        return mock(JobConsoleLogger.class);
    }
}