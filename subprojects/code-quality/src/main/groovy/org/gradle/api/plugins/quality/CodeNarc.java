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
package org.gradle.api.plugins.quality;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.project.IsolatedAntBuilder;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.quality.internal.CodeNarcInvoker;
import org.gradle.api.plugins.quality.internal.CodeNarcReportsImpl;
import org.gradle.api.reporting.Reporting;
import org.gradle.api.resources.TextResource;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.VerificationTask;
import org.gradle.util.internal.ClosureBackedAction;

import javax.inject.Inject;
import java.io.File;

/**
 * Runs CodeNarc against some source files.
 */
@CacheableTask
public class CodeNarc extends SourceTask implements VerificationTask, Reporting<CodeNarcReports> {

    private FileCollection codenarcClasspath;

    private FileCollection compilationClasspath;

    private TextResource config;

    private int maxPriority1Violations;

    private int maxPriority2Violations;

    private int maxPriority3Violations;

    private final CodeNarcReports reports;

    private boolean ignoreFailures;

    public CodeNarc() {
        reports = getObjectFactory().newInstance(CodeNarcReportsImpl.class, this);
        compilationClasspath = getProject().files();
    }

    /**
     * The CodeNarc configuration file to use.
     */
    @Internal
    public File getConfigFile() {
        return getConfig() == null ? null : getConfig().asFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PathSensitive(PathSensitivity.RELATIVE)
    public FileTree getSource() {
        return super.getSource();
    }

    /**
     * The CodeNarc configuration file to use.
     */
    public void setConfigFile(File configFile) {
        setConfig(getProject().getResources().getText().fromFile(configFile));
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException();
    }

    @Inject
    public IsolatedAntBuilder getAntBuilder() {
        throw new UnsupportedOperationException();
    }

    @TaskAction
    public void run() {
        CodeNarcInvoker.invoke(this);
    }

    /**
     * Configures the reports to be generated by this task.
     */
    @Override
    public CodeNarcReports reports(@DelegatesTo(CodeNarcReports.class) Closure closure) {
        return reports(new ClosureBackedAction<CodeNarcReports>(closure));
    }

    /**
     * Configures the reports to be generated by this task.
     */
    @Override
    public CodeNarcReports reports(Action<? super CodeNarcReports> configureAction) {
        configureAction.execute(reports);
        return reports;
    }

    /**
     * The class path containing the CodeNarc library to be used.
     */
    @Classpath
    public FileCollection getCodenarcClasspath() {
        return codenarcClasspath;
    }

    /**
     * The class path containing the CodeNarc library to be used.
     */
    public void setCodenarcClasspath(FileCollection codenarcClasspath) {
        this.codenarcClasspath = codenarcClasspath;
    }

    /**
     * The class path to be used by CodeNarc when compiling classes during analysis.
     *
     * @since 4.2
     */
    @Classpath
    public FileCollection getCompilationClasspath() {
        return compilationClasspath;
    }

    /**
     * The class path to be used by CodeNarc when compiling classes during analysis.
     *
     * @since 4.2
     */
    public void setCompilationClasspath(FileCollection compilationClasspath) {
        this.compilationClasspath = compilationClasspath;
    }

    /**
     * The CodeNarc configuration to use. Replaces the {@code configFile} property.
     *
     * @since 2.2
     */
    @Nested
    public TextResource getConfig() {
        return config;
    }

    /**
     * The CodeNarc configuration to use. Replaces the {@code configFile} property.
     *
     * @since 2.2
     */
    public void setConfig(TextResource config) {
        this.config = config;
    }

    /**
     * The maximum number of priority 1 violations allowed before failing the build.
     */
    @Input
    public int getMaxPriority1Violations() {
        return maxPriority1Violations;
    }

    /**
     * The maximum number of priority 1 violations allowed before failing the build.
     */
    public void setMaxPriority1Violations(int maxPriority1Violations) {
        this.maxPriority1Violations = maxPriority1Violations;
    }

    /**
     * The maximum number of priority 2 violations allowed before failing the build.
     */
    @Input
    public int getMaxPriority2Violations() {
        return maxPriority2Violations;
    }

    /**
     * The maximum number of priority 2 violations allowed before failing the build.
     */
    public void setMaxPriority2Violations(int maxPriority2Violations) {
        this.maxPriority2Violations = maxPriority2Violations;
    }

    /**
     * The maximum number of priority 3 violations allowed before failing the build.
     */
    @Input
    public int getMaxPriority3Violations() {
        return maxPriority3Violations;
    }

    /**
     * The maximum number of priority 3 violations allowed before failing the build.
     */
    public void setMaxPriority3Violations(int maxPriority3Violations) {
        this.maxPriority3Violations = maxPriority3Violations;
    }

    /**
     * The reports to be generated by this task.
     */
    @Override
    @Nested
    public CodeNarcReports getReports() {
        return reports;
    }

    /**
     * Whether the build should break when the verifications performed by this task fail.
     */
    @Override
    public boolean getIgnoreFailures() {
        return ignoreFailures;
    }

    /**
     * Whether the build should break when the verifications performed by this task fail.
     */
    @Override
    public void setIgnoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures;
    }
}
