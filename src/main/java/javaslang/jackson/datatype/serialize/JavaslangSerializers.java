/**
 * Copyright 2015 The Javaslang Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javaslang.jackson.datatype.serialize;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import javaslang.Lazy;
import javaslang.Tuple;
import javaslang.collection.*;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.jackson.datatype.JavaslangModule;

public class JavaslangSerializers extends Serializers.Base {

    private final JavaslangModule.Settings settings;

    public JavaslangSerializers(JavaslangModule.Settings settings) {
        this.settings = settings;
    }

    @SuppressWarnings("deprecation")
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config,
                                            JavaType type, BeanDescription beanDesc) {

        Class<?> raw = type.getRawClass();
        if (Either.class.isAssignableFrom(raw)) {
            return new EitherSerializer(type);
        }
        if (Tuple.class.isAssignableFrom(raw)) {
            return new TupleSerializer(type);
        }

        if (javaslang.λ.class.isAssignableFrom(raw)) {
            return new SerializableSerializer<>(type);
        }

        return super.findSerializer(config, type, beanDesc);
    }

    @Override
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config,
                                                     ReferenceType type, BeanDescription beanDesc,
                                                     TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
        Class<?> raw = type.getRawClass();
        if (Lazy.class.isAssignableFrom(raw)) {
            return new LazySerializer(type);
        }
        if (Option.class.isAssignableFrom(raw)) {
            return new OptionSerializer(type, settings.useOptionInPlainFormat());
        }
        return super.findReferenceSerializer(config, type, beanDesc, contentTypeSerializer, contentValueSerializer);
    }

    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
                                                          CollectionLikeType type, BeanDescription beanDesc,
                                                          TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        Class<?> raw = type.getRawClass();
        if (raw == CharSeq.class) {
            return new CharSeqSerializer(type);
        }
        if (Seq.class.isAssignableFrom(raw)) {
            return new ArraySerializer<>(type);
        }
        if (Set.class.isAssignableFrom(raw)) {
            return new ArraySerializer<>(type);
        }
        if (PriorityQueue.class.isAssignableFrom(raw)) {
            return new ArraySerializer<>(type);
        }
        return super.findCollectionLikeSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
    }

    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
                                                   MapLikeType type, BeanDescription beanDesc,
                                                   JsonSerializer<Object> keySerializer,
                                                   TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        Class<?> raw = type.getRawClass();
        if (Map.class.isAssignableFrom(raw)) {
            return new MapSerializer(type);
        }
        if (Multimap.class.isAssignableFrom(raw)) {
            return new MultimapSerializer(type);
        }
        return super.findMapLikeSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
    }
}
