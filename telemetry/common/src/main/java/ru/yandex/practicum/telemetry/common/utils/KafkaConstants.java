package ru.yandex.practicum.telemetry.common.utils;

public class KafkaConstants {

    public static final String TOPIC_TELEMETRY = "telemetry";

    // возможно лучше сделать шаблон с версией? но пока не требуется
    public static final String TOPIC_TELEMETRY_SENSORS_V1 = TOPIC_TELEMETRY + ".sensors.v1";
    public static final String TOPIC_TELEMETRY_HUBS_V1 = TOPIC_TELEMETRY + ".hubs.v1";
    public static final String TOPIC_TELEMETRY_SNAPSHOTS_V1 = TOPIC_TELEMETRY + ".snapshots.v1";

    public static final String CONSUMER_GROUP_AGGREGATOR = "group.aggregator";
    public static final String CONSUMER_GROUP_HUB_EVENTS = "group.hub-events";
    public static final String CONSUMER_GROUP_SNAPSHOTS = "group.snapshots";

}
