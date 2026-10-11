package ru.yandex.practicum.telemetry.common.entity.sensor;

/**
 * Base interface for declaring sensor On/Off logic
 */
public interface OnOffTrigger {

    boolean isOn();

    void setState(boolean newState);

}
