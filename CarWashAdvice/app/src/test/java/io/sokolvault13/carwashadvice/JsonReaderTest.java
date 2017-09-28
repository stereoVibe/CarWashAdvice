package io.sokolvault13.carwashadvice;

import android.util.JsonReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class JsonReaderTest {
    @Test
    public void shouldWork() throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader("{\"name\": \"Berlin\"}"));
        jsonReader.beginObject();
        assertThat(jsonReader.nextName()).isEqualTo("name");
        assertThat(jsonReader.nextString()).isEqualTo("Berlin");
        jsonReader.endObject();
    }
}
