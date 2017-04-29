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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "packageApplet")
public class JcdkMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}")
    private String buildDirectory;

    @Parameter(defaultValue = "${project.build.directory}/classes")
    private String classesDirectory;

    @Parameter(defaultValue = "${project.build.directory}/" + JcdkHandler.WORK_DIRECTORY + "jcdk.config")
    private String jcdkConfiguration;

    @Parameter
    private String jcdkHome;

    @Parameter
    private String jcdkVersion;

    @Parameter
    private String appletClass;

    @Parameter
    private String appletId;

    @Parameter
    private String appletVersion;

    @Parameter(defaultValue = "false")
    private boolean supportInt32;

    @Parameter
    private List<String> exportPaths;

    @Override
    public void execute() throws MojoExecutionException {
        JcdkHandlerParameterBuilder jcdkHandlerParameterBuilder = JcdkHandlerParameters.newBuilder();

        jcdkHandlerParameterBuilder.buildDirectory(buildDirectory);
        jcdkHandlerParameterBuilder.classesDirectory(classesDirectory);

        jcdkHandlerParameterBuilder.jcdkConfiguration(jcdkConfiguration);
        jcdkHandlerParameterBuilder.jcdkHome(jcdkHome);
        jcdkHandlerParameterBuilder.jcdkVersion(jcdkVersion);

        jcdkHandlerParameterBuilder.appletClass(appletClass);
        jcdkHandlerParameterBuilder.appletId(appletId);
        jcdkHandlerParameterBuilder.appletVersion(appletVersion);
        jcdkHandlerParameterBuilder.supportInt32(supportInt32);

        if (exportPaths != null) {
            List<Path> exportPathsList = new ArrayList<>();
            for (String exportPath : exportPaths) {
                exportPathsList.add(Paths.get(exportPath));
                getLog().info("PATH: " + exportPath);
            }
            jcdkHandlerParameterBuilder.exportPaths(exportPathsList);
        }

        JcdkHandlerParameter jcdkHandlerParameter = jcdkHandlerParameterBuilder.build();

        try {
            JcdkHandler jcdkHandler = new JcdkHandler(jcdkHandlerParameter);
            String output;

            getLog().debug("Prepare work directory");
            jcdkHandler.createWorkDirectory();

            getLog().debug("Prepare JCDK configuration");
            output = jcdkHandler.prepareJcdkConfiguration();
            getLog().info(output);

            getLog().debug("Convert artifacts");
            output = jcdkHandler.convertArtifacts();
            if (!output.contains("0 errors")) {
                getLog().error(output);
                throw new MojoExecutionException("JCDK conversation failed.");
            } else if (!output.contains("0 warnings")) {
                getLog().warn(output);
            } else {
                getLog().debug(output);
            }

            getLog().debug("Move artifacts from converter to target directory");
            output = jcdkHandler.moveArtifactsToTarget();
            getLog().info("Java Card artifacts: \n" + output);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
