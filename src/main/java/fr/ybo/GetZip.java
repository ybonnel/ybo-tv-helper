package fr.ybo;


import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

public class GetZip {


    public final static String FILE_URL = "http://www.kazer.org/tvguide.zip?u=a5ewx4ne5whk8";

    public static Reader getFile() throws IOException {



        InputSupplier<InputStream> supplier = new InputSupplier<InputStream>() {

            ZipInputStream stream = null;

            @Override
            public InputStream getInput() throws IOException {
                if (stream == null) {
                    URL url = new URL(FILE_URL);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(0);
                    connection.setReadTimeout(0);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    stream = new ZipInputStream(inputStream);
                    stream.getNextEntry();
                }
                return stream;
            }
        };

        InputSupplier<InputStreamReader> reader = CharStreams.newReaderSupplier(supplier, Charset.forName("utf-8"));

        StringBuilder contentXml = new StringBuilder();
        for (String line : CharStreams.readLines(reader)) {
            if (!line.startsWith("<!DOCTYPE")) {
                contentXml.append(line);
                contentXml.append('\n');
            }
        }

        reader.getInput().close();

        return new StringReader(contentXml.toString());
    }
}
