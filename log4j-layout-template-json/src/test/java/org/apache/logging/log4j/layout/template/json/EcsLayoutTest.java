package org.apache.logging.log4j.layout.template.json;

import co.elastic.logging.log4j2.EcsLayout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.layout.template.json.JsonTemplateLayout.EventTemplateAdditionalField;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.layout.template.json.LayoutComparisonHelpers.renderUsing;

public class EcsLayoutTest {

    private static final Configuration CONFIGURATION = new DefaultConfiguration();

    private static final String SERVICE_NAME = "test";

    private static final String EVENT_DATASET = SERVICE_NAME + ".log";

    private static final JsonTemplateLayout JSON_TEMPLATE_LAYOUT = JsonTemplateLayout
            .newBuilder()
            .setConfiguration(CONFIGURATION)
            .setEventTemplateUri("classpath:EcsLayout.json")
            .setEventTemplateAdditionalFields(
                    JsonTemplateLayout
                            .EventTemplateAdditionalFields
                            .newBuilder()
                            .setAdditionalFields(
                                    new EventTemplateAdditionalField[]{
                                            EventTemplateAdditionalField
                                                    .newBuilder()
                                                    .setKey("service.name")
                                                    .setValue(SERVICE_NAME)
                                                    .build(),
                                            EventTemplateAdditionalField
                                                    .newBuilder()
                                                    .setKey("event.dataset")
                                                    .setValue(EVENT_DATASET)
                                                    .build()
                                    })
                            .build())
            .build();

    private static final EcsLayout ECS_LAYOUT = EcsLayout
            .newBuilder()
            .setConfiguration(CONFIGURATION)
            .setServiceName(SERVICE_NAME)
            .setEventDataset(EVENT_DATASET)
            .build();

    @Test
    public void test_lite_log_events() {
        final List<LogEvent> logEvents = LogEventFixture.createLiteLogEvents(1_000);
        test(logEvents);
    }

    @Test
    public void test_full_log_events() {
        final List<LogEvent> logEvents = LogEventFixture.createFullLogEvents(1_000);
        test(logEvents);
    }

    private static void test(final Collection<LogEvent> logEvents) {
        for (final LogEvent logEvent : logEvents) {
            test(logEvent);
        }
    }

    private static void test(final LogEvent logEvent) {
        final Map<String, Object> jsonTemplateLayoutMap = renderUsingJsonTemplateLayout(logEvent);
        final Map<String, Object> ecsLayoutMap = renderUsingEcsLayout(logEvent);
        Assertions.assertThat(jsonTemplateLayoutMap).isEqualTo(ecsLayoutMap);
    }

    private static Map<String, Object> renderUsingJsonTemplateLayout(
            final LogEvent logEvent) {
        return renderUsing(logEvent, JSON_TEMPLATE_LAYOUT);
    }

    private static Map<String, Object> renderUsingEcsLayout(
            final LogEvent logEvent) {
        return renderUsing(logEvent, ECS_LAYOUT);
    }

}
