package org.apache.logging.log4j.kafka.appender;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.junit.LoggerContextSource;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that restarting the {@link LoggerContext} doesn't cause
 * {@link KafkaManager} to leak threads.
 *
 * @see <a href="https://issues.apache.org/jira/browse/LOG4J2-2916">LOG4J2-2916</a>
 */
@LoggerContextSource("KafkaAppenderTest.xml")
class KafkaManagerProducerThreadLeakTest {

    @Test
    void context_restart_shouldnt_leak_producer_threads(final LoggerContext context) {

        // Determine the initial number of threads.
        final int initialThreadCount = kafkaProducerThreadCount();

        // Perform context restarts.
        final int contextRestartCount = 3;
        for (int i = 0; i < contextRestartCount; i++) {
            context.reconfigure();
        }

        // Verify the final thread count.
        final int lastThreadCount = kafkaProducerThreadCount();
        assertEquals(initialThreadCount, lastThreadCount);

    }

    private static int kafkaProducerThreadCount() {
        final long threadCount = Thread
                .getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().startsWith("kafka-producer"))
                .count();
        return Math.toIntExact(threadCount);
    }

}
