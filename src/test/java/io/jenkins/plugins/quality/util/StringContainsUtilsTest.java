package io.jenkins.plugins.quality.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the class {@link StringContainsUtils}.
 *
 * @author Ullrich Hafner
 */
class StringContainsUtilsTest {
    @Test
    void shouldHandleNull() {
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", (String[]) null)).isFalse();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", (String) null)).isFalse();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.")).isFalse();

        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase(null)).isFalse();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase(null, (String) null)).isFalse();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase(null, (String[]) null)).isFalse();
    }

    @Test
    void shouldSearchStrings() {
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", "something")).isFalse();

        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", "This")).isTrue();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", "this")).isTrue();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", "wrong", "is")).isTrue();
        Assertions.assertThat(StringContainsUtils.containsAnyIgnoreCase("This is a string text.", "wrong", "IS")).isTrue();
    }
}
