package io.github.ibrahimbayramli.cronos.starter.discovery;

import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

final class NextRunCalculator {

    private NextRunCalculator() {
    }

    static Optional<Instant> calculate(String triggerInfo) {
        if (triggerInfo == null || triggerInfo.isBlank()) {
            return Optional.empty();
        }
        if (triggerInfo.startsWith("cron=")) {
            String expression = triggerInfo.substring("cron=".length());
            try {
                CronExpression cron = CronExpression.parse(expression);
                ZonedDateTime next = cron.next(ZonedDateTime.now(ZoneId.systemDefault()));
                return next != null ? Optional.of(next.toInstant()) : Optional.empty();
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        }
        if (triggerInfo.startsWith("fixedRate=") || triggerInfo.startsWith("fixedDelay=")) {
            long millis = parseDurationMillis(triggerInfo);
            if (millis > 0) {
                return Optional.of(Instant.now().plusMillis(millis));
            }
        }
        return Optional.empty();
    }

    private static long parseDurationMillis(String triggerInfo) {
        int separator = triggerInfo.indexOf('=');
        if (separator < 0) {
            return -1;
        }
        String value = triggerInfo.substring(separator + 1).trim();
        try {
            if (value.endsWith("ms")) {
                return Long.parseLong(value.substring(0, value.length() - 2));
            }
            if (value.endsWith("s")) {
                return Duration.ofSeconds(Long.parseLong(value.substring(0, value.length() - 1))).toMillis();
            }
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
