package com.github.code2358.javacard.maven;

/*-
 * #%L
 * Java Card SDK Maven Plugin
 * %%
 * Copyright (C) 2017 code2358
 * %%
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
 * #L%
 */

import com.github.code2358.javacard.jcdk.ConfigurationException;
import com.github.code2358.javacard.jcdk.InitializationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JcdkHandlerTest {

    private final static Path JCDK3 = Paths.get(JcdkHandlerTest.class.getResource("/jcdk3").getFile());

    private JcdkHandlerParameterBuilder jcdkHandlerParameterBuilder;

    private Path buildDirectory;
    private Path classesDirectory;
    private Path jcdkConfiguration;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws IOException, InitializationException {
        buildDirectory = Files.createTempDirectory("");
        classesDirectory = Files.createTempDirectory("");
        jcdkConfiguration = buildDirectory.resolve(JcdkHandler.WORK_DIRECTORY).resolve("jcdk.config");

        jcdkHandlerParameterBuilder = JcdkHandlerParameters.newBuilder()
                .buildDirectory(buildDirectory.toString())
                .classesDirectory(classesDirectory.toString())
                .jcdkConfiguration(jcdkConfiguration.toString())
                .jcdkHome(JCDK3.toString())
                .jcdkVersion("3.0.x");
    }

    @Test
    public void createWorkDirectorySuccess() throws IOException, InitializationException {
        JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameterBuilder.build());

        jcdkHandler.createWorkDirectory();

        Assert.assertTrue(Files.exists(buildDirectory.resolve(JcdkHandler.WORK_DIRECTORY)));
    }

    @Test
    public void prepareJcdkConfigurationSuccess() throws IOException, ConfigurationException, InitializationException {
        jcdkHandlerParameterBuilder.appletClass("my.Class").appletId("01:02:03:04:05:06").appletVersion("1.0");

        JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameterBuilder.build());

        jcdkHandler.createWorkDirectory();
        jcdkHandler.prepareJcdkConfiguration();

        Assert.assertTrue(Files.exists(jcdkConfiguration));
        MatcherAssert.assertThat(Files.size(jcdkConfiguration), Matchers.greaterThan(100L));
    }

    @Test
    public void convertArtifactsFailure() throws IOException, ConfigurationException, InitializationException {
        jcdkHandlerParameterBuilder.appletClass("my.Class").appletId("01:02:03:04:05:06").appletVersion("1.0");

        JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameterBuilder.build());

        jcdkHandler.createWorkDirectory();
        jcdkHandler.prepareJcdkConfiguration();
        String output = jcdkHandler.convertArtifacts();

        MatcherAssert.assertThat(output, CoreMatchers.containsString("Error during execution of JCDK converter (see output for details)."));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Output JCDK Converter:"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Error: Could not find or load main class com.sun.javacard.converter.Main"));
    }

    @Test
    public void moveArtifactsSuccess() throws IOException, ConfigurationException, InitializationException {
        jcdkHandlerParameterBuilder.appletClass("my.Class").appletId("01:02:03:04:05:06").appletVersion("1.0");

        JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameterBuilder.build());

        jcdkHandler.createWorkDirectory();
        Path testCap = buildDirectory.resolve(JcdkHandler.WORK_DIRECTORY).resolve("test.cap");
        Files.createFile(testCap);

        String output = jcdkHandler.moveArtifactsToTarget();

        MatcherAssert.assertThat(output, CoreMatchers.equalTo(buildDirectory.resolve("010203040506.cap").toString() + "\n"));
    }

    @Test
    public void moveArtifactsFailure() throws IOException, ConfigurationException, InitializationException {
        exception.expect(IOException.class);
        exception.expectMessage("No Java Card artifacts found.");

        jcdkHandlerParameterBuilder.appletClass("my.Class").appletId("01:02:03:04:05:06").appletVersion("1.0");

        JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameterBuilder.build());

        jcdkHandler.createWorkDirectory();
        String output = jcdkHandler.moveArtifactsToTarget();

        MatcherAssert.assertThat(output, CoreMatchers.containsString("Error during execution of JCDK converter (see output for details)."));
    }
}
