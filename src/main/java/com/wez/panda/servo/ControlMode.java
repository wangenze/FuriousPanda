package com.wez.panda.servo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum ControlMode {
    RELATIVE(Position::getStep),
    ABSOLUTE(Position::getPos);

    private Function<Position, Integer> position2SignalConverter;
}
