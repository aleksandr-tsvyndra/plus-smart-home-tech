package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

@Getter @Setter @ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    @NotNull
    private Integer linkQuality;

    private boolean motion;

    @NotNull
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
