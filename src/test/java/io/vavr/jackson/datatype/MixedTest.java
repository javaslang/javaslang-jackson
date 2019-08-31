package io.vavr.jackson.datatype;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class MixedTest extends BaseTest {

    @Test
    void test1() throws IOException {
        Object src = HashMap.empty().put("key1", List.of(1, 2)).put("key2", List.of(3, 4));
        String json = mapper().writer().writeValueAsString(src);
        Assertions.assertEquals(mapper().readValue(json, new TypeReference<HashMap<?,List<?>>>() {}), src);
    }

    @Test
    void test2() throws IOException {
        Object src = List.of(HashMap.empty().put("key1", 1), HashMap.empty().put("key2", 2));
        String json = mapper().writer().writeValueAsString(src);
        Assertions.assertEquals(mapper().readValue(json, new TypeReference<List<HashMap<?,?>>>() {}), src);
    }

    @Test
    void test3() throws IOException {
        java.util.Map<String, String> javaHMap = new java.util.HashMap<>();
        javaHMap.put("1", "2");
        List<?> src = List.of(javaHMap);
        List<?> restored = mapper().readValue(mapper().writer().writeValueAsString(src), List.class);
        Assertions.assertEquals(restored, src);
    }

    @Test
    void test4() {
        Assertions.assertThrows(JsonMappingException.class, () -> {
            mapper().readValue("[\"s\"]", new TypeReference<List<Integer>>() {});
        });
    }

    @Test
    void test5() throws IOException {
        Object src = List.of(List.of(1));
        String json = mapper().writer().writeValueAsString(src);
        Assertions.assertEquals(mapper().readValue(json, new TypeReference<List<List<?>>>() {}), src);
    }

    @Test
    void test6() throws IOException {
        Object src = HashMap.empty().put("1", HashMap.empty().put("X", "Y"));
        String json = mapper().writer().writeValueAsString(src);
        Assertions.assertEquals(mapper().readValue(json, new TypeReference<HashMap<?, HashMap<?, ?>>>() {}), src);
    }

    @Test
    void test7() throws IOException {
        Object src = List.of(Arrays.asList(1, 2));
        String json = mapper().writer().writeValueAsString(src);
        Assertions.assertEquals(mapper().readValue(json, List.class), src);
        Assertions.assertEquals(mapper().readValue(json, new TypeReference<List<java.util.List<?>>>() {}), src);
    }
}
