package fr.ktoublanc.config.management;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The test case for the config reader class
 * Created by kevin on 04/01/2016.
 */
public class ConfigReaderTestCase {

    private ConfigReader config;

    @Before
    public void before() throws IOException, URISyntaxException {
        config = new ConfigReader("config.properties");
    }

    @Test
    public void testReadConfig_FromMainFile() {
        Assertions.assertThat(config.retrieve("a.key")).isEqualTo("some.value");
        Assertions.assertThat(config.retrieve("this.key.does.not.exists")).isNull();
    }

    @Test
    public void testReadConfig_FromCompositeFile() {
        Assertions.assertThat(config.retrieve("file.specific.key")).isEqualTo("some.specific.value");
    }

    @Test
    public void testReadConfig_FromCompositeFile_OverridenValueFrom00File() {
        Assertions.assertThat(config.retrieve("a.second.key")).isEqualTo("00 file overridden value");
    }

    @Test
    public void testReadConfig_FromCompositeFile_OverridenValueFrom99File() {
        Assertions.assertThat(config.retrieve("overridden.key")).isEqualTo("some.overridden.value");
    }

    @Test
    public void testReadConfig_FromCompositeFile_NotLoadedBecauseNotBeginningWithNumerics() {
        Assertions.assertThat(config.retrieve("this.key")).isNull();
    }

    @Test
    public void testReadConfig_NoMainFile() throws IOException {
        Assertions.assertThat(new ConfigReader("nomainfile").retrieve("configuration.key")).isEqualTo("value");
    }

    @Test(expected = IOException.class)
    public void testReadConfig_FileNotFound() throws IOException {
        new ConfigReader("this.file.does.not.exist");
    }

    @Test
    public void testReadConfig_NoDirectory() throws IOException {
        Assertions.assertThat(new ConfigReader("nodirectory.properties").retrieve("configuration.key")).isEqualTo("value");
    }

    @Test(expected = NullPointerException.class)
    public void testReadConfig_NullResource() throws IOException {
        try {
            new ConfigReader(null);
        } catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Can not load configuration for null resource name");
            throw e;
        }
    }
}
