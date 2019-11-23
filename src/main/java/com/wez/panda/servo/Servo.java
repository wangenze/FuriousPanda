package com.wez.panda.servo;

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
    private Serial serial;

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {
        private String name;
        private String dataFilePath;
        private double offsetDegrees = 0d;
        private double transmissionRatio = 1d;
        private Serial serial = null;

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

        public Builder serial(Serial serial) {
            this.serial = serial;
            return this;
        }

        public Servo build() {
            Validate.notEmpty(name);
            Validate.notEmpty(dataFilePath);
            return new Servo(name, dataFilePath, offsetDegrees, transmissionRatio, serial);
        }
    }
}
