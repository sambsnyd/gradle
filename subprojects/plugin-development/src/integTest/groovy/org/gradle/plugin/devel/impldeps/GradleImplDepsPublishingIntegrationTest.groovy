/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.plugin.devel.impldeps

import org.gradle.integtests.fixtures.ToBeFixedForConfigurationCache

class GradleImplDepsPublishingIntegrationTest extends BaseGradleImplDepsIntegrationTest {

    def "module metadata generated by Maven publish plugin does not contain reference to Gradle modules for a published plugin"() {
        given:
        buildFile << testablePluginProject()
        buildFile << """
            apply plugin: 'maven-publish'

            publishing {
                publications {
                    mavenJava(MavenPublication) {
                        from components.java
                    }
                }
            }
        """

        file('src/main/groovy/MyPlugin.groovy') << customGroovyPlugin()

        when:
        succeeds 'generatePomFileForMavenJavaPublication'

        then:
        def xml = new groovy.xml.XmlSlurper().parse(file('build/publications/mavenJava/pom-default.xml'))
        xml.dependencies.size() == 0
    }

    @ToBeFixedForConfigurationCache(because = "publishing")
    def "module metadata generated by Ivy publish plugin does not contain reference to Gradle modules for a published plugin"() {
        given:
        buildFile << testablePluginProject()
        buildFile << """
            apply plugin: 'ivy-publish'

            publishing {
                publications {
                    ivyJava(IvyPublication) {
                        from components.java
                    }
                }
            }
        """

        file('src/main/groovy/MyPlugin.groovy') << customGroovyPlugin()

        when:
        succeeds 'generateDescriptorFileForIvyJavaPublication'

        then:
        def xml = new groovy.xml.XmlSlurper().parse(file('build/publications/ivyJava/ivy.xml'))
        xml.dependencies.children().size() == 0
    }
}
