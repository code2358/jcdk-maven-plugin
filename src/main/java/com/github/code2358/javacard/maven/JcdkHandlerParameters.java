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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class JcdkHandlerParameters {

    public static JcdkHandlerParameterBuilder newBuilder() {
        return new JcdkHandlerParameterBuilder();
    }
}

class JcdkHandlerParameterBuilder {
    private String buildDirectory;
    private String classesDirectory;
    private String jcdkConfiguration;
    private String jcdkHome;
    private String jcdkVersion;
    private String appletClass;
    private String appletId;
    private String appletVersion;
    private boolean supportInt32;

    public JcdkHandlerParameterBuilder buildDirectory(String buildDirectory) {
        this.buildDirectory = Objects.requireNonNull(buildDirectory);
        return this;
    }

    public JcdkHandlerParameterBuilder classesDirectory(String classesDirectory) {
        this.classesDirectory = Objects.requireNonNull(classesDirectory);
        return this;
    }

    public JcdkHandlerParameterBuilder jcdkConfiguration(String jcdkConfiguration) {
        this.jcdkConfiguration = Objects.requireNonNull(jcdkConfiguration);
        return this;
    }

    public JcdkHandlerParameterBuilder jcdkHome(String jcdkHome) {
        this.jcdkHome = Objects.requireNonNull(jcdkHome);
        return this;
    }

    public JcdkHandlerParameterBuilder jcdkVersion(String jcdkVersion) {
        this.jcdkVersion = Objects.requireNonNull(jcdkVersion);
        return this;
    }

    public JcdkHandlerParameterBuilder appletClass(String appletClass) {
        this.appletClass = Objects.requireNonNull(appletClass);
        return this;
    }

    public JcdkHandlerParameterBuilder appletId(String appletId) {
        this.appletId = Objects.requireNonNull(appletId);
        return this;
    }

    public JcdkHandlerParameterBuilder appletVersion(String appletVersion) {
        this.appletVersion = Objects.requireNonNull(appletVersion);
        return this;
    }

    public JcdkHandlerParameterBuilder supportInt32(boolean supportInt32) {
        this.supportInt32 = supportInt32;
        return this;
    }

    public JcdkHandlerParameter build() {
        JcdkHandlerParameterImpl impl = new JcdkHandlerParameterImpl();

        impl.buildDirectory = Paths.get(buildDirectory);
        impl.classesDirectory = Paths.get(classesDirectory);
        impl.jcdkConfiguration = Paths.get(jcdkConfiguration);
        impl.jcdkHome = Paths.get(jcdkHome);
        impl.jcdkVersion = jcdkVersion;
        impl.appletClass = appletClass;
        impl.appletId = appletId;
        impl.appletVersion = appletVersion;
        impl.supportInt32 = supportInt32;

        return impl;
    }

    private class JcdkHandlerParameterImpl implements JcdkHandlerParameter {
        private Path buildDirectory;
        private Path classesDirectory;
        private Path jcdkConfiguration;
        private Path jcdkHome;
        private String jcdkVersion;
        private String appletClass;
        private String appletId;
        private String appletVersion;
        private boolean supportInt32;

        @Override
        public Path buildDirectory() {
            return buildDirectory;
        }

        @Override
        public Path classesDirectory() {
            return classesDirectory;
        }

        @Override
        public Path workDirectory() {
            return buildDirectory.resolve(JcdkHandler.WORK_DIRECTORY);
        }

        @Override
        public Path jcdkConfiguration() {
            return jcdkConfiguration;
        }

        @Override
        public Path jcdkHome() {
            return jcdkHome;
        }

        @Override
        public String jcdkVersion() {
            return jcdkVersion;
        }

        @Override
        public String appletClass() {
            return appletClass;
        }

        @Override
        public String appletId() {
            return appletId;
        }

        @Override
        public String appletVersion() {
            return appletVersion;
        }

        @Override
        public boolean supportInt32() {
            return supportInt32;
        }
    }
}
