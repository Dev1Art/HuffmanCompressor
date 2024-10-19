package io;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author DanielDFY, Dev1Art
 * @project HuffCompressor
 */
public final class InStream {

    private static final Logger LOGGER = Logger.getLogger(InStream.class.getName());
    private static final int EOF = -1;   // end of file

    private BufferedInputStream in;      // the input stream
    private int buffer;                  // one character buffer
    private int n;                       // number of bits left in buffer

    public InStream(File file) {

        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                in = new BufferedInputStream(fis);
                fillBuffer();
            }
        }
        catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error occurs while creating InputStream via File class", ioe);
        }
    }

    private void fillBuffer() {
        try {
            buffer = in.read();
            n = 8;
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error occurs while reading from buffer", e);
            buffer = EOF;
            n = -1;
        }
    }

    public boolean exists()  {
        return in != null;
    }

    public boolean isEmpty() {
        return buffer == EOF;
    }

    public boolean readBoolean() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n--;
        boolean bit = ((buffer >> n) & 1) == 1;
        if (n == 0) fillBuffer();
        return bit;
    }

    public char readChar() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        // special case when aligned byte
        if (n == 8) {
            int x = buffer;
            fillBuffer();
            return (char) (x & 0xff);
        }

        // combine last N bits of current buffer with first 8-N bits of new buffer
        int x = buffer;
        x <<= (8 - n);
        int oldN = n;
        fillBuffer();
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n = oldN;
        x |= (buffer >>> n);
        return (char) (x & 0xff);
    }

    public String readString() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        StringBuilder sb = new StringBuilder();
        while (!isEmpty()) {
            char c = readChar();
            sb.append(c);
        }
        return sb.toString();
    }

    public int readInt() {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    public byte readByte() {
        char c = readChar();
        return (byte) (c & 0xff);
    }
}
