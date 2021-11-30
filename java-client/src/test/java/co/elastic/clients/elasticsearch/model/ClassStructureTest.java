/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package co.elastic.clients.elasticsearch.model;

import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.CardinalityAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.DateRangeAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.ValueCountAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch._types.query_dsl.IntervalsQuery;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.MissingRequiredPropertyException;
import co.elastic.clients.util.ApiTypeHelper;
import co.elastic.clients.util.ObjectBuilder;
import org.junit.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tests that verify common features of generated classes.
 */
public class ClassStructureTest extends ModelTestCase {

    /**
     * Tests optional and required primitive types
     */
    @Test
    public void testPrimitiveTypes() throws Exception {
        // Optional primitive should use the boxed type and be @Nullable
        {
            Method searchSize = SearchRequest.class.getMethod("size");
            assertNotNull(searchSize.getAnnotation(Nullable.class));
            assertEquals(Integer.class, searchSize.getReturnType());
        }

        // Builder setter for optional primitive should use the boxed type and be @Nullable
        {
            Method searchBuilderSize = SearchRequest.Builder.class.getMethod("size", Integer.class);
            assertTrue(Arrays.stream(searchBuilderSize.getParameterAnnotations()[0])
                .anyMatch(a -> a.annotationType() == Nullable.class));
        }

        // Required primitive should use the primitive type and not be @Nullable
        {
            Method totalHitsValue = TotalHits.class.getMethod("value");
            assertNull(totalHitsValue.getAnnotation(Nullable.class));
            assertEquals(long.class, totalHitsValue.getReturnType());
        }

        // Builder setter for required primitive should use the primitive type and not be @Nullable
        {
            Method totalHitsBuilderValue = TotalHits.Builder.class.getMethod("value", long.class);
            assertFalse(Arrays.stream(totalHitsBuilderValue.getParameterAnnotations()[0])
                .anyMatch(a -> a.annotationType() == Nullable.class));
        }

        // Not setting a required primitive should throw an exception
        {
            assertThrows(MissingRequiredPropertyException.class, () -> TotalHits.of(b -> b));
        }
    }

    /**
     * Tests correct initialization of inherited fields through builders and data class inheritance.
     */
    @Test
    public void testDataClassInheritedFieldAssignment() {
        // 1 ancestor
        {
            CardinalityAggregate card = CardinalityAggregate.of(b -> b
                .meta(Collections.singletonMap("foo", JsonData.of("bar")))
                .value(1)
            );

            assertAncestorCount(1, card);
            assertEquals("bar", card.meta().get("foo").to(String.class));
            assertEquals("{\"meta\":{\"foo\":\"bar\"},\"value\":1}", toJson(card));
        }

        // 3 ancestors
        {
            DateRangeAggregate date = DateRangeAggregate.of(_1 -> _1
                .meta(Collections.singletonMap("foo", JsonData.of("bar")))
                .buckets(Buckets.of(b -> b.array(Collections.singletonList(RangeBucket.of(_2 -> _2.docCount(1))))))
            );

            assertAncestorCount(3, date);
            assertEquals("bar", date.meta().get("foo").to(String.class));
            assertEquals("{\"meta\":{\"foo\":\"bar\"},\"buckets\":[{\"doc_count\":1}]}", toJson(date));
        }
    }

    /**
     * Tests correct initialization of inherited fields for union typed.
     */
    @Test
    public void testUnionInheritedFieldAssignment() {
        IntervalsQuery iq = IntervalsQuery.of(_1 -> _1
            .boost(2.0f)
            .field("foo")
            .allOf(b -> b.intervals(Collections.emptyList()))
        );
        assertAncestorCount(1, iq);
        assertEquals(2.0f, iq.boost(), 0.01);
        assertEquals("{\"foo\":{\"boost\":2.0,\"all_of\":{\"intervals\":[]}}}", toJson(iq));
    }

    @Test
    public void testUndefinedCollections() {
        // Not setting a required list should throw an exception
        {
            MissingRequiredPropertyException ex = assertThrows(MissingRequiredPropertyException.class,
                () -> HitsMetadata.of(_1 -> _1.total(_2 -> _2.value(0).relation(TotalHitsRelation.Eq))));
            assertTrue(ex.getMessage().contains(".hits"));
        }

        // Unset list should be non-null, empty but not serialized
        {
            SearchRequest search = SearchRequest.of(b -> b);
            assertNotNull(search.storedFields());
            assertEquals(0, search.storedFields().size());
            assertFalse(ApiTypeHelper.isDefined(search.storedFields()));
            assertEquals("{}", toJson(search));
        }

        // Setting an empty list defines it
        {
            SearchRequest search = SearchRequest.of(b -> b.storedFields(Collections.emptyList()));
            assertNotNull(search.storedFields());
            assertEquals(0, search.storedFields().size());
            assertTrue(ApiTypeHelper.isDefined(search.storedFields()));
            assertEquals("{\"stored_fields\":[]}", toJson(search));
        }

        // Unset map should be non-null, empty but not serialized
        {
            CardinalityAggregate card = CardinalityAggregate.of(b -> b.value(1));
            assertNotNull(card.meta());
            assertEquals(0, card.meta().size());
            assertFalse(ApiTypeHelper.isDefined(card.meta()));
            assertEquals("{\"value\":1}", toJson(card));
        }

        // Setting an empty map defines it
        {
            CardinalityAggregate card = CardinalityAggregate.of(b -> b
                .value(1)
                .meta(Collections.emptyMap())
            );
            assertNotNull(card.meta());
            assertEquals(0, card.meta().size());
            assertTrue(ApiTypeHelper.isDefined(card.meta()));
            assertEquals("{\"meta\":{},\"value\":1}", toJson(card));
        }
    }

    @Test
    public void testListSetters() {
        List<String> fields = Arrays.asList("a", "b");

        {
            // Appending doesn't modify the original collection
            SearchRequest search = SearchRequest.of(b -> b
                .storedFields(fields)
                .storedFields("c")
                .storedFields("d", "e", "f")
            );
            assertEquals(Arrays.asList("a", "b"), fields);
            assertEquals(Arrays.asList("a", "b", "c", "d", "e", "f"), search.storedFields());
        }

        {
            // Appending doesn't modify the original collection (appending the same list twice)
            SearchRequest search = SearchRequest.of(b -> b
                .storedFields(fields)
                .storedFields(fields)
            );
            assertEquals(Arrays.asList("a", "b"), fields);
            assertEquals(Arrays.asList("a", "b", "a", "b"), search.storedFields());
        }


        {
            // Reset builder property
            SearchRequest search = SearchRequest.of(b -> b
                .storedFields(fields)
                .storedFields(ApiTypeHelper.resetList())
            );
            assertFalse(ApiTypeHelper.isDefined(search.storedFields()));

            search = SearchRequest.of(b -> b
                .storedFields(fields)
                .storedFields(ApiTypeHelper.resetList())
                .storedFields("d", "e")
            );
            assertEquals(Arrays.asList("d", "e"), search.storedFields());
        }

        {
            // Combine value and builder
            FieldAndFormat fieldA = FieldAndFormat.of(f -> f.field("a"));
            SearchRequest search = SearchRequest.of(b -> b
                .docvalueFields(fieldA)
                .docvalueFields(f -> f.field("b"))
                .docvalueFields(f -> f.field("c"))
            );

            assertEquals(Arrays.asList("a", "b", "c"), search.docvalueFields().stream()
                .map(FieldAndFormat::field).collect(Collectors.toList()));
        }
    }

    @Test
    public void testMapSetters() {
        ValueCountAggregation countA = ValueCountAggregation.of(v -> v.field("a"));
        ValueCountAggregation countB = ValueCountAggregation.of(v -> v.field("b"));
        ValueCountAggregation countC = ValueCountAggregation.of(v -> v.field("c"));

        Map<String, Aggregation> aggs = new HashMap<>();
        aggs.put("aggA", countA._toAggregation());
        aggs.put("aggB", countB._toAggregation());

        {
            // Appending doesn't modify the original collection

            SearchRequest search = SearchRequest.of(b -> b
                .aggregations(aggs)
                .aggregations("aggC", countC._toAggregation())
                .aggregations("aggD", a -> a.valueCount(c -> c.field("d")))
            );

            // Original map wasn't modified
            assertEquals(2, aggs.size());

            assertEquals(4, search.aggregations().size());
            assertEquals("a", search.aggregations().get("aggA").valueCount().field());
            assertEquals("b", search.aggregations().get("aggB").valueCount().field());
            assertEquals("c", search.aggregations().get("aggC").valueCount().field());
            assertEquals("d", search.aggregations().get("aggD").valueCount().field());
        }

        {
            // Reset builder property
            SearchRequest search = SearchRequest.of(b -> b
                .aggregations(aggs)
                .aggregations(ApiTypeHelper.resetMap())
            );
            assertFalse(ApiTypeHelper.isDefined(search.storedFields()));

            search = SearchRequest.of(b -> b
                .aggregations(aggs)
                .aggregations(ApiTypeHelper.resetMap())
                .aggregations("aggC", countC._toAggregation())
            );
            assertEquals(1, search.aggregations().size());
            assertEquals("c", search.aggregations().get("aggC").valueCount().field());
        }
    }

    @Test
    public void testDataClassesSingleBuilderUse() {
        // no ancestor + ObjectBuilderBase
        checkSingleBuilderUse(1, new ErrorCause.Builder().type("foo").reason("bar"));

        // 1 ancestor + ObjectBuilderBase
        checkSingleBuilderUse(2, new CardinalityAggregate.Builder().value(0));

        // 3 ancestors + ObjectBuilderBase
        checkSingleBuilderUse(4, new DateRangeAggregate.Builder().buckets(Buckets.of(b -> b.array(Collections.emptyList()))));
    }

    @Test
    public void testUnionSingleBuilderUse() {
        // no ancestor + ObjectBuilderBase
        checkSingleBuilderUse(1, new Aggregate.Builder().cardinality(b -> b.value(0)));

        // 1 ancestor + ObjectBuilderBase
        checkSingleBuilderUse(2, new IntervalsQuery.Builder().field("foo").allOf(b -> b.intervals(Collections.emptyList())));
    }

    @Test
    public void testRequiredProperty() {
        // All required properties present
        GetRequest r = GetRequest.of(b -> b.index("foo").id("bar"));

        // Missing id property throws an exception
        MissingRequiredPropertyException ex = assertThrows(MissingRequiredPropertyException.class, () -> {
                GetRequest r1 = GetRequest.of(b -> b.index("foo"));
        });
        assertEquals("id", ex.getPropertyName());

        // Disable checks, missing id property is accepted.
        try (ApiTypeHelper.DisabledChecksHandle h = ApiTypeHelper.DANGEROUS_disableRequiredPropertiesCheck(true)) {
            GetRequest r1 = GetRequest.of(b -> b.index("foo"));
            assertNull(r1.id());
        }

        // Checks are enabled again after the try block
        ex = assertThrows(MissingRequiredPropertyException.class, () -> {
            GetRequest r1 = GetRequest.of(b -> b.index("foo"));
        });
        assertEquals("id", ex.getPropertyName());
    }

    private void assertAncestorCount(int count, Object obj) {
        Class<?> clazz = obj.getClass();
        while(count-- >= 0) {
            clazz = clazz.getSuperclass();
        }
        assertEquals(Object.class, clazz);
    }

    private void checkSingleBuilderUse(int count, ObjectBuilder<?> builder) {
        assertAncestorCount(count, builder);

        // Building once should succeed
        builder.build();

        // Building twice should fail
        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Object builders can only be used once", ex.getMessage());

        // One more for good measure
        ex = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Object builders can only be used once", ex.getMessage());
    }
}
