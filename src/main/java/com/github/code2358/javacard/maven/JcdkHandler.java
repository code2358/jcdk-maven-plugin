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

import com.github.code2358.javacard.jcdk.*;
import com.github.code2358.javacard.jcdk.config.JcdkConfigurationBuilder;
import com.github.code2358.javacard.jcdk.config.JcdkConfigurations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class JcdkHandler {

    public static final String WORK_DIRECTORY = "generated-jcdk/";

    private final JcdkInstallation jcdkInstallation;
    private final JcdkHandlerParameter jcdkHandlerParameter;

    public JcdkHandler(JcdkHandlerParameter jcdkHandlerParameter) throws InitializationException {
        Objects.requireNonNull(jcdkHandlerParameter);

        this.jcdkInstallation = JcdkInstallationFactory.getInstance(jcdkHandlerParameter.jcdkVersion(), jcdkHandlerParameter.jcdkHome());
        this.jcdkHandlerParameter = jcdkHandlerParameter;
    }

    public void createWorkDirectory() throws IOException {
        Files.createDirectories(jcdkHandlerParameter.workDirectory());
    }

    public String prepareJcdkConfiguration() throws ConfigurationException {
        StringBuffer messages = new StringBuffer();
        if (!Files.exists(jcdkHandlerParameter.jcdkConfiguration())) {
            messages.append("Generating JCDK configuration: " + jcdkHandlerParameter.jcdkConfiguration());
            JcdkConfigurationBuilder builder = JcdkConfigurations.newBuilder()
                    .jcdkInstallation(jcdkInstallation)
                    .classesDirectory(jcdkHandlerParameter.classesDirectory())
                    .outputDirectory(jcdkHandlerParameter.workDirectory())
                    .appletClass(jcdkHandlerParameter.appletClass())
                    .appletId(jcdkHandlerParameter.appletId())
                    .appletVersion(jcdkHandlerParameter.appletVersion())
                    .supportInt32(jcdkHandlerParameter.supportInt32())
                    .exportPath(jcdkHandlerParameter.exportPaths());

            try (PrintWriter writer = new PrintWriter(new FileWriter(jcdkHandlerParameter.jcdkConfiguration().toFile()))) {
                writer.print(builder.build());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            messages.append("Using existing JCDK configuration: " + jcdkHandlerParameter.jcdkConfiguration());
        }
        return messages.toString();
    }

    public String convertArtifacts() throws UnsupportedEncodingException {
        StringBuffer messages = new StringBuffer();
        JcdkWrapperImpl wrapper = new JcdkWrapperImpl(jcdkInstallation, jcdkHandlerParameter.jcdkConfiguration());

        try {
            wrapper.convert();
        } catch (ConvertionException e) {
            messages.append(e.getMessage());
            messages.append("\n");
        }

        String output = wrapper.getJcdkOutput();

        if (output != null) {
            messages.append("Output JCDK Converter:\n");
            messages.append(output);
        } else {
            messages.append("No convertion output");
        }

        return messages.toString();
    }

    public String moveArtifactsToTarget() throws IOException {
        StringBuffer messages = new StringBuffer();
        JavacardArtifactFinder javacardArtifactFinder = new JavacardArtifactFinder();
        Files.walkFileTree(jcdkHandlerParameter.workDirectory(), javacardArtifactFinder);

        if (javacardArtifactFinder.getMatches().size() < 1) {
            throw new IOException("No Java Card artifacts found.");
        }

        for (Path match : javacardArtifactFinder.getMatches()) {
            Path target = jcdkHandlerParameter.buildDirectory()
                    .resolve(formatAppletIdForFilesystem(jcdkHandlerParameter.appletId()) + getFileExtension(match.getFileName().toString()));

            if (Files.exists(target)) {
                messages.append("Artifact does already exist. No update. Path: " + target);
            } else {
                messages.append(target);
                messages.append("\n");
                Files.copy(match, target);
            }
        }

        return messages.toString();
    }

    private static String formatAppletIdForFilesystem(String appletId) {
        return appletId.replace(":", "");
    }

    private static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
