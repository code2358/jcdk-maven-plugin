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

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class JavacardArtifactFinder extends SimpleFileVisitor<Path> {
    private final PathMatcher matcher = FileSystems.getDefault()
            .getPathMatcher("glob:*.{cap,exp,jca}");

    private Set<Path> matches = new HashSet<>();

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {

        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            matches.add(file);
        }

        return FileVisitResult.CONTINUE;
    }

    public Set<Path> getMatches() {
        return matches;
    }

}
