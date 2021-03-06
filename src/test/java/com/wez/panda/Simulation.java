package com.wez.panda;

import com.wez.panda.servo.Servo;
import com.wez.panda.servo.driver.AServoDriver;
import com.wez.panda.servo.driver.DriverParameters;
import com.wez.panda.servo.driver.ServoDriverFactory;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.util.FastMath;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Simulation extends PApplet {

    final ConcurrentMap<Servo, Double> STATUS = new ConcurrentHashMap<>();

    final List<Line> lines = new ArrayList<>();

    private PandaDriver pandaDriver;

    public static void main(String[] args) {
        PApplet.main(new String[]{Simulation.class.getName()});
    }

    @Override
    public void settings() {
        size(1000, 800);
        pandaDriver = PandaDriver.builder()
                .servos(Resources.getAllServosForTesting())
                .servoDriverFactory(new ServoDriverFactory() {
                    @Override
                    public AServoDriver getServoDriver(Servo servo, StopWatch sw, DriverParameters parameters) {
                        return new MockingServoDriver(servo, sw);
                    }
                })
                .build();
        pandaDriver.start();
        lines.addAll(StreamEx.of(pandaDriver.getServos()).map(Line::new).toList());
    }

    @Override
    public void setup() {
        smooth();
    }

    @Override
    public void draw() {
        background(0);
        noStroke();
        lines.forEach(Line::display);
    }

    @Override
    public void mousePressed() {
        if (pandaDriver.isPaused()) {
            pandaDriver.resume();
        } else {
            pandaDriver.pause();
        }
    }

    class Line {
        Servo servo;
        PVector center;

        Line(Servo servo) {
            this.servo = servo;
            String[] ids = StringUtils.split(servo.getName(), '_');
            int x = ids[0].startsWith("L") ? -1 : 1;
            int y = ids[0].endsWith("B") ? -1 : 1;
            int xx = ids[1].startsWith("K") ? -1 : 1;
            this.center = new PVector(width * (2 * x + xx + 4) / 8, height * (y + 2) / 4);
        }

        void display() {
            float radians = STATUS.get(servo).floatValue();
            PVector dir = new PVector(sin(radians), cos(radians));
            dir.mult(100);
            dir.add(center);
            stroke(255);
            strokeWeight(4);
            line(center.x, center.y, dir.x, dir.y);
        }
    }

    class MockingServoDriver extends AServoDriver {
        public MockingServoDriver(Servo servo, StopWatch stopWatch) {
            super(servo, stopWatch);
        }

        @Override
        protected void operate(double timeSec) throws InterruptedException {
            STATUS.put(getServo(), FastMath.toRadians(getPosAfterApplyingOffset(timeSec)));
        }
    }
}
