package com.wez.panda;

import com.wez.panda.servo.Servo;
import one.util.streamex.StreamEx;
import org.apache.commons.math3.util.FastMath;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

public class Main extends PApplet {

    List<Line> lines;

    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});
    }

    @Override
    public void settings() {
        size(500, 500);
        PandaDriver pandaDriver = new PandaDriver();
        pandaDriver.start();
        List<Servo> servos = pandaDriver.getServos();
        lines = StreamEx.of(servos).map(Line::new).toList();
    }

    @Override
    public void setup() {
        smooth();
    }

    @Override
    public void draw() {
        background(0);
        lines.forEach(Line::refresh);
    }

    class Line {
        Servo servo;
        PVector center;

        Line(Servo servo) {
            this.servo = servo;
            this.center = new PVector(width * (servo.getId() * 2 - 1) / 4, height / 2);
        }

        void refresh() {
            noStroke();
            double radians = FastMath.toRadians(StatusHolder.STATUS.get(servo));
            PVector dir = new PVector((float) FastMath.sin(radians), (float) FastMath.cos(radians));
            dir.mult(100);
            dir.add(center);
            stroke(255);
            strokeWeight(4);
            line(center.x, center.y, dir.x, dir.y);
        }
    }
}
