package ru.yandex.practicum.telemetry.common.entity.sensor.light;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.telemetry.common.entity.sensor.BaseDevice;
import ru.yandex.practicum.telemetry.common.entity.sensor.OnOffTrigger;


@NoArgsConstructor
@AllArgsConstructor
public class LightnessSensor extends BaseDevice implements OnOffTrigger {

    private boolean state;

    @Override
    public boolean isOn() {
        return state;
    }

    @Override
    public void setState(boolean newState) {
        this.state = newState;
    }

}
