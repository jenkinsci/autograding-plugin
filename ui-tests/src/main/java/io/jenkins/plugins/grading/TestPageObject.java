package io.jenkins.plugins.grading;

import java.net.URL;
import com.google.inject.Injector;
import org.jenkinsci.test.acceptance.po.PageObject;

/**
 * {@link PageObject} representing the details page of the static analysis tool results.
 *
 * @author Lukas Kirner
 */
public class TestPageObject extends PageObject {

    private String test;

    /**
     * Creates an instance of the page displaying the details of the issues for a specific tool.
     *
     * @param injector
     *         a finished build configured with a static analysis tool
     * @param url
     *         the type of the result page (e.g. simian, checkstyle, cpd, etc.)
     */
    public TestPageObject(final Injector injector, final URL url) {
        super(injector, url);
        this.test = url.toString();
    }

    public String getTest() {
        return test;
    }
}
