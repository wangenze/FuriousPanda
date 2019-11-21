package com.wez.panda.data;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DataFileLoader {

    private final ClassLoader classLoader = getClass().getClassLoader();

    public List<Pair<Double, Double>> loadRawData(String dataFilePath) {
        URL resource = Validate.notNull(classLoader.getResource(dataFilePath));
        List<Pair<Double, Double>> data = new ArrayList<>();
        final LineIterator lineIterator;
        try {
            lineIterator = FileUtils.lineIterator(new File(resource.getFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        try {
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                String[] fields = StringUtils.split(line, ',');
                double time = Double.parseDouble(fields[0]);
                double pos = Double.parseDouble(fields[1]);
                data.add(Pair.create(time, pos));
            }
        } finally {
            LineIterator.closeQuietly(lineIterator);
        }
        return data;
    }
}
