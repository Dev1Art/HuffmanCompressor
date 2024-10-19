package data;

import java.awt.*;
import java.util.HashSet;

/**
 * @author Artem
 * @project HuffCompressor
 */
public class Constants {
    private Constants () {}

    // compressing
    public final static String HUFF_SUFFIX = ".huff";
    public final static boolean FILE_BIT = true; // bit marks the case when it is a file
    public final static boolean DIR_BIT = false; // bit marks the case when it is a dir
    public final static boolean EMPTY_BIT = true; // bit marks empty directory
    public final static boolean NON_EMPTY_BIT = false; // bit marks non-empty directory

    public final static HashSet<String> IGNORE_SET = new HashSet<>(); // directory of files which should be ignored

    static {
        IGNORE_SET.add(".huffignore");
    }

    // in-place tests
    /*
     * Via https://github.com/alexschiller/file-format-commons. 66 files with various extensions like present below.
     * .3gp, .7z, .aac, .adoc, .avi, .bat, .bin, .bmp, .bz2, .c, .class, .cpp, .csv, .doc, .docx, .epub, .flac, .flv,
     * .gif, .gz, .h, .html, .ico, .iff, .iso, .jar, .jpeg, .jpg, .json, .log, .md, .midi, .mov, .mp3, .mp4, .Rdata,
     * .mpg, .odg, .ods, .odt, .ogg, .pdf, .png, .ppt, .pptx, .properties, .psd, .rar, .raw, .svg, .tar, .tif, .tiff,
     * .txt, .wav, .webm, .webp, .wma, .wmv, .xls, .xlsx, .xml, .yaml, .yml, .zip, .xhtml, .xlt, .xltx, .xul, .zsh
     *
     * SIZE: 8_049_889 bytes
     */
    public final static String BIG_PACK = "src\\test_cases\\BIG_PACK";

    /*
     * 15 files with various extensions like present below.
     * .ott, .pbm, .pct, .pcx, .ppt, .png, .pptx, .pcx, .pdf, .gif, .tiff, .mp3, .xlsx, .pdf, .ogg, .avi
     *
     * SIZE: 7_422_184 bytes
     */
    public final static String MIDDLE_PACK = "src\\test_cases\\MIDDLE_PACK";

    /*
     * 5 files with various extensions like present below.
     * .jpg, .mp4, .wav, .xls, .docx
     *
     * SIZE: 4_828_198 bytes
     */
    public final static String SMALL_PACK = "src\\test_cases\\SMALL_PACK";

    /*
     * 1 file .wav.
     *
     * SIZE: 2_104_474 bytes
     */
    public final static String ONE_FILE_PACK = "src\\test_cases\\ONE_FILE_PACK";
    public final static String SAVES = "src\\test_cases\\SAVES"; // path to compressed and decompressed data

    // GUI
    public final static Color BACKGROUND = new Color(66, 66, 66);
    public final static Color BACKGROUND_HOVER = new Color(47, 79, 79);
    public final static Color PROGRESS_BAR_FILLER = new Color(0, 128, 0);
    public final static Dimension BUTTON_SIZE = new Dimension(150, 30);
    public final static Font FONT = new Font(Font.DIALOG_INPUT, Font.BOLD, 14);
    public final static String TIME_UNIT = " ms.";

}
