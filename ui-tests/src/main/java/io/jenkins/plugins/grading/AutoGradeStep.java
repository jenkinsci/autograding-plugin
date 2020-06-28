package io.jenkins.plugins.grading;

import org.jenkinsci.test.acceptance.po.AbstractStep;
import org.jenkinsci.test.acceptance.po.CodeMirror;
import org.jenkinsci.test.acceptance.po.Describable;
import org.jenkinsci.test.acceptance.po.Job;
import org.jenkinsci.test.acceptance.po.PostBuildStep;

/**
 * AutoGrader configuration UI.
 *
 * @author Lukas Kirner
 */
@Describable("Autograde project")
public class AutoGradeStep extends AbstractStep implements PostBuildStep {

    /**
     * Constructor.
     * @param parent parent.
     * @param path path.
     */
    public AutoGradeStep(final Job parent, final String path) {
        super(parent, path);
    }

    /**
     * Setting Autograde configuration.
     * @param configuration the JSON to configure the plugin.
     */
    public void setConfiguration(final String configuration) {
        new CodeMirror(this, "configuration").set(configuration);
    }
}
