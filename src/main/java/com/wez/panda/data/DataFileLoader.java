package com.wez.panda.data;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DataFileLoader {

    private final ClassLoader classLoader = getClass().getClassLoader();

    public List<Pair<Double, Double>> loadRawData(String dataFilePath) {
        File dataFile = getFile(dataFilePath);
        final List<Pair<Double, Double>> data = new ArrayList<>();
        final LineIterator lineIterator;
        try {
            lineIterator = FileUtils.lineIterator(dataFile);
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

    private File getFile(String filePath) {
        return ObjectUtils.firstNonNull(getLocalFile(filePath), getResourceFile(filePath));
    }

    private File getLocalFile(String filePath) {
        Path path = Paths.get(filePath);
        File file = path.toFile();
        return file.exists() ? file : null;
    }

    private File getResourceFile(String filePath) {
        URL resource = getClass().getClassLoader().getResource(filePath);
        return resource != null ? new File(resource.getFile()) : null;
    }
}
