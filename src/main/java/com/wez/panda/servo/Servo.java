package com.wez.panda.servo;

import com.wez.panda.serial.SerialMode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import processing.serial.Serial;

@Getter
@AllArgsConstructor
public class Servo {

    private final String name;
    private final String dataFilePath;

    private double offsetDegrees;
    private double transmissionRatio;

    private ControlMode controlMode;

    private Serial serial;
    private SerialMode serialMode;

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {
        private String name;
        private String dataFilePath;
        private double offsetDegrees = 0d;
        private double transmissionRatio = 1d;
        private ControlMode controlMode = ControlMode.RELATIVE;

        private Serial serial = null;
        private SerialMode serialMode = SerialMode.DISPLACEMENT;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder dataFilePath(String dataFilePath) {
            this.dataFilePath = dataFilePath;
            return this;
        }

        public Builder offsetDegrees(double offsetDegrees) {
            this.offsetDegrees = offsetDegrees;
            return this;
        }

        public Builder transmissionRatio(double transmissionRatio) {
            this.transmissionRatio = transmissionRatio;
            return this;
        }


        public Builder controlMode(ControlMode controlMode) {
            this.controlMode = controlMode;
            return this;
        }

        public Builder serial(Serial serial) {
            this.serial = serial;
            return this;
        }

        public Builder serialMode(SerialMode serialMode) {
            this.serialMode = serialMode;
            return this;
        }

        public Servo build() {
            Validate.notEmpty(name);
            Validate.notEmpty(dataFilePath);
            return new Servo(name, dataFilePath, offsetDegrees, transmissionRatio, controlMode, serial, serialMode);
        }
    }
}
