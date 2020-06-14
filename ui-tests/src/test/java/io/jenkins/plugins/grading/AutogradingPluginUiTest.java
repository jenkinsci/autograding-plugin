package io.jenkins.plugins.grading;

import org.junit.Test;

import org.jenkinsci.test.acceptance.junit.AbstractJUnitTest;
import org.jenkinsci.test.acceptance.junit.WithPlugins;

import static org.assertj.core.api.Assertions.*;

/**
 * Acceptance tests for the Autograding Plugin.
 *
 * @author Lukas Kirner
 */
@WithPlugins("autograding")
public class AutogradingPluginUiTest extends AbstractJUnitTest {

    /**
     * Some Test Java Doc.
     */
    @Test
    public void test() {
        assertThat(true).isTrue();
    }
}
