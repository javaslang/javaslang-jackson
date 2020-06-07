/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2017 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.jackson.datatype.serialize;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.vavr.Value;

import java.io.IOException;
import java.util.ArrayList;

class ArraySerializer<T extends Value<?>> extends ValueSerializer<T> implements ContextualSerializer {

    private static final long serialVersionUID = 1L;

    ArraySerializer(JavaType collectionType, BeanProperty property) {
        super(collectionType, property);
    }

    ArraySerializer(JavaType collectionType) {
        this(collectionType, null);
    }

    /**
     * Creates a new serializer from the original one.
     *
     * @param origin   the original serializer
     * @param property the new bean property
     */
    ArraySerializer(ArraySerializer<T> origin, BeanProperty property) {
        this(origin.type, property);
    }

    @Override
    Object toJavaObj(T value) throws IOException {
        return value.toJavaList();
    }

    @Override
    JavaType emulatedJavaType(JavaType type, TypeFactory typeFactory) {
        CollectionLikeType collectionLikeType = (CollectionLikeType) type;
        return typeFactory.constructCollectionType(ArrayList.class, collectionLikeType.getContentType());
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, T value) {
        return value.isEmpty();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
            throws JsonMappingException {
        if (property == beanProperty) {
            return this;
        }
        return new ArraySerializer<>(this, property);
    }
}
