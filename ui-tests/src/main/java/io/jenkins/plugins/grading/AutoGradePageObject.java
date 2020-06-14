package io.jenkins.plugins.grading;

import java.net.MalformedURLException;
import java.net.URL;
import org.jenkinsci.test.acceptance.po.PageObject;

/**
 * {@link PageObject} representing the details page of the static analysis tool results.
 *
 * @author Lukas Kirner
 */
public class AutoGradePageObject extends PageObject {

    /**
     * Creates an instance of the page displaying the details of the issues for a specific tool.
     *
     * @param pageObject
     *         a finished build configured with a static analysis tool and configured AutoGrading
     * @param url
     *         the base url of the job
     */
    public AutoGradePageObject(final PageObject pageObject, final URL url) {
        super(pageObject, url);
        this.open();
    }
}
