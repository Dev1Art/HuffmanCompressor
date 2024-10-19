package core;

import data.Constants;
import huffman.Huffman;
import io.InStream;
import io.OutStream;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author DanielDFY, Dev1Art
 * @project HuffCompressor
 */
public class Compressor {

    public static void compress(File src, OutStream binaryOut, ProgressCallback callback) {
        if (null == src)
            throw new IllegalArgumentException("Null source file for compress");

        if (src.isFile()) {
            // compress single file
            compressFile(src, binaryOut);
        } else if (src.isDirectory()) {
            // compress directory
            compressDir(src, binaryOut, callback);
        } else
            throw new RuntimeException("Unknown kind of source");
    }

    private static void compressFile(File file, OutStream binaryOut) {
        assert file.isFile();

        String fileName = file.getName();
        boolean isEmptyFile = (file.length() == 0);

        // write file info
        writeFileHead(fileName, isEmptyFile, binaryOut);

        // only compress non-empty file
        if (!isEmptyFile) {
            InStream binaryIn = new InStream(file);
            Huffman.compress(binaryIn, binaryOut);
        }
    }

    private static void writeFileHead(String fileName, boolean isEmptyFile, OutStream binaryOut) {
        // to signal file header
        binaryOut.write(Constants.FILE_BIT);

        // write in file name info
        byte[] bytes = fileName.getBytes();
        binaryOut.write(bytes.length);
        binaryOut.write(bytes);

        // to signal whether its empty
        binaryOut.write(isEmptyFile);
    }

    private static void compressDir(File dir, OutStream binaryOut, ProgressCallback callback) {
        assert dir.isDirectory();

        File[] files = dir.listFiles();

        if (null == files)
            throw new RuntimeException("Null file list of dir");

        ArrayList<File> list = new ArrayList<>();
        for (File file : files) {

            // ignore unwanted files
            if (Constants.IGNORE_SET.contains(file.getName())) {
                continue;
            }
            list.add(file);
        }
        int length = list.size();
        if (length == 0) throw new RuntimeException("Nothing to compress");

        writeDirHead(dir.getName(), length, binaryOut);

        // compress each content respectively
        for (File file : list) {
            compress(file, binaryOut, callback);
        }

        // fill progress bar
        callback.onProgress((length / files.length) * 100);
    }

    private static void writeDirHead(String dirName, int length, OutStream binaryOut) {
        // to signal the directory header
        binaryOut.write(Constants.DIR_BIT);

        // write in directory name info
        byte[] bytes = dirName.getBytes();
        binaryOut.write(bytes.length);
        binaryOut.write(bytes);

        if (length == 0) {
            binaryOut.write(Constants.EMPTY_BIT);      // true for empty directory
        } else {
            binaryOut.write(Constants.NON_EMPTY_BIT);  // false for non-empty directory
            binaryOut.write(length);
        }
    }

    public static void decompress(File file, ProgressCallback callback) {
        if (null == file)
            throw new IllegalArgumentException("Null source file for decompress");

        // supports only .huff extension
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!suffix.equals(Constants.HUFF_SUFFIX))
            throw new RuntimeException("Unsupported file suffix");

        File parent = new File(file.getAbsoluteFile().getParent());
        decompress(parent, new InStream(file), callback);
    }

    private static void decompress(File parent, InStream binaryIn, ProgressCallback callback) {
        if (binaryIn.readBoolean() == Constants.FILE_BIT) {
            // expand single file
            decompressFile(parent, binaryIn);
            callback.onProgress(1);
        } else {
            // decompress directory
            // get name info
            int nameLength = binaryIn.readInt();
            byte[] bytes = new byte[nameLength];
            for (int i = 0; i < nameLength; ++i) {
                bytes[i] = binaryIn.readByte();
            }

            // deal with chinese
            String dirName = new String(bytes, StandardCharsets.UTF_8);
            File dir = new File(parent, dirName);
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to make dir: " + dirName);
            }

            if (binaryIn.readBoolean() != Constants.EMPTY_BIT) {
                // decompress each content respectively
                int length = binaryIn.readInt();
                for (int i = 0; i < length; ++i) {
                    decompress(dir, binaryIn, callback);
                }
            }
        }
    }
    private static void decompressFile(File parent, InStream binaryIn) {
        // get name info
        int nameLength = binaryIn.readInt();
        byte[] bytes = new byte[nameLength];
        for (int i = 0; i < nameLength; ++i) {
            bytes[i] = binaryIn.readByte();
        }

        // deal with chinese file name
        String fileName = new String(bytes, StandardCharsets.UTF_8);
        OutStream binaryOut = new OutStream(new File(parent, fileName));

        if (binaryIn.readBoolean() != Constants.EMPTY_BIT) {
            Huffman.decompress(binaryIn, binaryOut);
        }

        binaryOut.close();
    }

    public interface ProgressCallback {
        void onProgress(int progress);
    }
}