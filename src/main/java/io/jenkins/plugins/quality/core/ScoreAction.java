package io.jenkins.plugins.quality.core;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep;

import javax.annotation.CheckForNull;
import java.util.Collection;

public class ScoreAction implements SimpleBuildStep.LastBuildAction, RunAction2 {
    @Override
    public void onAttached(Run<?, ?> run) {
        
    }

    @Override
    public void onLoad(Run<?, ?> run) {

    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return null;
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return null;
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return null;
    }
}
