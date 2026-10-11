package ru.yandex.practicum.telemetry.common.utils;

public class KafkaConstants {

    public static String TOPIC_TELEMETRY = "telemetry";

    // возможно лучше сделать шаблон с версией? но пока не требуется
    public static String TOPIC_TELEMETRY_SENSORS_V1 = TOPIC_TELEMETRY + ".sensors.v1";
    public static String TOPIC_TELEMETRY_HUBS_V1 = TOPIC_TELEMETRY + ".hubs.v1";

}
