package dev.arubik.craftengine.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.momirealms.craftengine.core.util.Key;

/**
 * The reason {@link JsonView} exists is error quality: a typo in a data file used
 * to surface as an NPE from deep inside Gson with no hint of which file or field
 * was wrong. Every failure here is asserted to name its path.
 */
class JsonViewTest {

    private static JsonView view(String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        return JsonView.of(object, "test.json");
    }

    @Test
    void readsScalars() {
        JsonView v = view("{\"s\":\"hi\",\"i\":7,\"f\":1.5,\"b\":true}");
        assertEquals("hi", v.string("s"));
        assertEquals(7, v.integer("i"));
        assertEquals(1.5f, v.floating("f"));
        assertTrue(v.bool("b", false));
    }

    @Test
    void missingOptionalFieldsFallBack() {
        JsonView v = view("{}");
        assertEquals("d", v.string("nope", "d"));
        assertEquals(3, v.integer("nope", 3));
        assertFalse(v.bool("nope", false));
        assertEquals(List.of(), v.stringList("nope"));
        assertEquals(List.of(), v.objectList("nope"));
    }

    @Test
    void missingRequiredFieldNamesThePath() {
        JsonView v = view("{}");
        var error = assertThrows(JsonView.MalformedDataException.class, () -> v.string("id"));
        assertTrue(error.getMessage().contains("test.json > id"), error.getMessage());
    }

    @Test
    void nestedErrorsCarryTheFullBreadcrumb() {
        JsonView v = view("{\"outputs\":[{\"amount\":1},{\"amount\":2}]}");
        List<JsonView> outputs = v.objectList("outputs");
        assertEquals(2, outputs.size());

        var error = assertThrows(JsonView.MalformedDataException.class, () -> outputs.get(1).string("id"));
        assertTrue(error.getMessage().contains("outputs[1] > id"), error.getMessage());
    }

    @Test
    void rangeViolationsAreRejectedWithBounds() {
        JsonView v = view("{\"n\":99}");
        var error = assertThrows(JsonView.MalformedDataException.class, () -> v.rangedInt("n", 0, 0, 10));
        assertTrue(error.getMessage().contains("[0, 10]"), error.getMessage());
        assertEquals(5, v.rangedInt("missing", 5, 0, 10));
    }

    @Test
    void nonIntegerWhereIntegerExpectedIsRejected() {
        JsonView v = view("{\"n\":1.5}");
        assertThrows(JsonView.MalformedDataException.class, () -> v.integer("n"));
    }

    @Test
    void enumsAreCaseInsensitiveAndListValidValuesOnFailure() {
        JsonView v = view("{\"e\":\"BeTa\",\"bad\":\"nope\"}");
        assertEquals(Sample.BETA, v.enumValue("e", Sample.class));

        var error = assertThrows(JsonView.MalformedDataException.class, () -> v.enumValue("bad", Sample.class));
        assertTrue(error.getMessage().contains("alpha"), error.getMessage());
        assertTrue(error.getMessage().contains("beta"), error.getMessage());
    }

    @Test
    void keysDefaultTheirNamespace() {
        JsonView v = view("{\"bare\":\"water\",\"full\":\"cml:copper_pipe\"}");
        assertEquals(Key.of("polyfills", "water"), v.key("bare", "polyfills"));
        assertEquals(Key.of("cml", "copper_pipe"), v.key("full", "polyfills"));
    }

    @Test
    void malformedKeysAreRejected() {
        assertThrows(JsonView.MalformedDataException.class,
                () -> JsonView.parseKey(":x", "minecraft", "where"));
        assertThrows(JsonView.MalformedDataException.class,
                () -> JsonView.parseKey("x:", "minecraft", "where"));
        assertThrows(JsonView.MalformedDataException.class,
                () -> JsonView.parseKey("  ", "minecraft", "where"));
    }

    @Test
    void wrongShapeIsReportedRatherThanCoerced() {
        JsonView v = view("{\"obj\":[1,2],\"arr\":{\"a\":1}}");
        assertThrows(JsonView.MalformedDataException.class, () -> v.object("obj"));
        assertThrows(JsonView.MalformedDataException.class, () -> v.objectList("arr"));
    }

    @Test
    void singleValueIsAcceptedWhereAListIsExpected() {
        // Data files routinely write one entry without brackets; accepting it avoids a
        // pointless class of authoring error.
        JsonView v = view("{\"faces\":\"down\",\"slots\":4}");
        assertEquals(List.of("down"), v.stringList("faces"));
        assertEquals(List.of(4), v.intList("slots"));
    }

    @Test
    void nullValuedFieldCountsAsAbsent() {
        JsonView v = view("{\"x\":null}");
        assertFalse(v.has("x"));
        assertEquals("fallback", v.string("x", "fallback"));
    }

    private enum Sample {
        ALPHA, BETA
    }
}
