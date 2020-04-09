package io.jenkins.plugins.grading;

import jline.internal.Nullable;

import hudson.model.Job;

import io.jenkins.plugins.util.JobAction;

/**
 * A job action displays a link on the side panel of a job that refers to the last build that contains autograding
 * results (i.e. a {@link AutoGradingBuildAction} with a {@link AggregatedScore} instance). This action also is responsible to
 * render the historical trend via its associated 'floatingBox.jelly' view.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGradingJobAction extends JobAction<AutoGradingBuildAction> {
    static final String SMALL_ICON = "/plugin/autograding/icons/autograding-24x24.png";

    static final String ID = "autograding";

    /**
     * Creates a new instance of {@link AutoGradingJobAction}.
     *
     * @param owner
     *         the job that owns this action
     */
    public AutoGradingJobAction(final Job<?, ?> owner) {
        super(owner, AutoGradingBuildAction.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.Action_Name();
    }

    @Override
    @Nullable
    public String getIconFileName() {
        return SMALL_ICON;
    }

    @Override
    public String getUrlName() {
        return ID;
    }
}
