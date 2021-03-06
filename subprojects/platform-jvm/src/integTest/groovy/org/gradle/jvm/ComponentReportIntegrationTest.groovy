/*
 * Copyright 2014 the original author or authors.
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
package org.gradle.jvm

import org.gradle.api.JavaVersion
import org.gradle.api.reporting.components.AbstractComponentReportIntegrationTest
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

class ComponentReportIntegrationTest extends AbstractComponentReportIntegrationTest {
    private JavaVersion currentJvm = JavaVersion.current()
    private String currentJava = "java" + currentJvm.majorVersion
    private String currentJdk = String.format("JDK %s (%s)", currentJvm.majorVersion, currentJvm);

    def "shows details of Java library"() {
        given:
        buildFile << """
plugins {
    id 'jvm-component'
    id 'java-lang'
}

model {
    components {
        someLib(JvmLibrarySpec)
    }
}
"""
        when:
        succeeds "components"

        then:
        outputMatches output, """
JVM library 'someLib'
---------------------

Source sets
    Java source 'someLib:java'
        src/someLib/java
    JVM resources 'someLib:resources'
        src/someLib/resources

Binaries
    Jar 'someLibJar'
        build using task: :someLibJar
        platform: $currentJava
        tool chain: $currentJdk
        Jar file: build/jars/someLibJar/someLib.jar
"""
    }

    @Requires(TestPrecondition.JDK7_OR_LATER)
    def "shows details of jvm library with multiple targets"() {
        given:
        buildFile << """
    apply plugin: 'jvm-component'
    apply plugin: 'java-lang'

    model {
        components {
            myLib(JvmLibrarySpec) {
                targetPlatform "java5"
                targetPlatform "java6"
                targetPlatform "java7"
            }
        }
    }
"""
        when:
        succeeds "components"

        then:
        outputMatches output, """
JVM library 'myLib'
-------------------

Source sets
    Java source 'myLib:java'
        src/myLib/java
    JVM resources 'myLib:resources'
        src/myLib/resources

Binaries
    Jar 'java5MyLibJar'
        build using task: :java5MyLibJar
        platform: java5
        tool chain: $currentJdk
        Jar file: build/jars/java5MyLibJar/myLib.jar
    Jar 'java6MyLibJar'
        build using task: :java6MyLibJar
        platform: java6
        tool chain: $currentJdk
        Jar file: build/jars/java6MyLibJar/myLib.jar
    Jar 'java7MyLibJar'
        build using task: :java7MyLibJar
        platform: java7
        tool chain: $currentJdk
        Jar file: build/jars/java7MyLibJar/myLib.jar
"""
    }

    @Requires(TestPrecondition.JDK8_OR_EARLIER)
    def "shows which jvm libraries are buildable"() {
        given:
        buildFile << """
    apply plugin: 'jvm-component'
    apply plugin: 'java-lang'

    model {
        components {
            myLib(JvmLibrarySpec) {
                targetPlatform "java5"
                targetPlatform "java6"
                targetPlatform "java9"
            }
        }
    }
"""
        when:
        succeeds "components"

        then:
        outputMatches output, """
JVM library 'myLib'
-------------------

Source sets
    Java source 'myLib:java'
        src/myLib/java
    JVM resources 'myLib:resources'
        src/myLib/resources

Binaries
    Jar 'java5MyLibJar'
        build using task: :java5MyLibJar
        platform: java5
        tool chain: $currentJdk
        Jar file: build/jars/java5MyLibJar/myLib.jar
    Jar 'java6MyLibJar'
        build using task: :java6MyLibJar
        platform: java6
        tool chain: $currentJdk
        Jar file: build/jars/java6MyLibJar/myLib.jar
    Jar 'java9MyLibJar' (not buildable)
        build using task: :java9MyLibJar
        platform: java9
        tool chain: $currentJdk
        Jar file: build/jars/java9MyLibJar/myLib.jar
"""
    }
}
