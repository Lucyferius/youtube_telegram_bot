package com.alevel.bot.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamProcessExtractorService extends Thread {

    private static final String GROUP_PERCENT = "percent";

    private static final String GROUP_MINUTES = "minutes";

    private static final String GROUP_SECONDS = "seconds";

    private final InputStream stream;

    private final StringBuffer buffer;

    private final Pattern p = Pattern.compile("\\[download]\\s+(?<percent>\\d+\\.\\d)% .* ETA (?<minutes>\\d+):(?<seconds>\\d+)");

    private boolean exit = false;

    public StreamProcessExtractorService(StringBuffer buffer, InputStream stream) {
        this.stream = stream;
        this.buffer = buffer;
    }

    public void run() {
        try {
            StringBuilder currentLine = new StringBuilder();
            int nextChar;
            while ((nextChar = stream.read()) != -1) {
                if (exit) {
                    break;
                }
                buffer.append((char) nextChar);
                if (nextChar == '\r') {
                    processOutputLine(currentLine.toString());
                    currentLine.setLength(0);
                    continue;
                }
                currentLine.append((char) nextChar);
            }
        } catch (IOException ignored) {
        }
    }

    public void stopStream() {
        exit = true;
    }

    private void processOutputLine(String line) {
        Matcher m = p.matcher(line);
        if (m.matches()) {
            float progress = Float.parseFloat(m.group(GROUP_PERCENT));
            long eta = convertToSeconds(m.group(GROUP_MINUTES), m.group(GROUP_SECONDS));

        }
    }

    private int convertToSeconds(String minutes, String seconds) {
        return Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
    }
}
