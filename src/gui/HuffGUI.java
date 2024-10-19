package gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import core.Compressor;
import data.Constants;
import io.OutStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dev1Art
 * @project HuffCompressor
 */

public class HuffGUI extends JFrame {
    private final JTextField filePathField;
    private final JTextArea outputArea;
    private final JButton selectDirButton;
    private final JButton compressButton;
    private final JButton decompressButton;
    private final JButton exitButton;
    private final JProgressBar progressBar;
    private final Logger LOGGER = Logger.getLogger(HuffGUI.class.getName());

    public HuffGUI() {
        // main frame
        setTitle("Huffman Compression");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Panel for file selection
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePathField = new JTextField(35);
        selectDirButton = new JButton("Select");
        selectDirButton.setBackground(Constants.BACKGROUND);
        selectDirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                selectDirButton.setBackground(Constants.BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                selectDirButton.setBackground(Constants.BACKGROUND);
            }
        });
        selectDirButton.setPreferredSize(new Dimension(100, 30));
        panel1.add(filePathField);
        panel1.add(selectDirButton);
        add(panel1);

        // progress bar
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER));
        progressBar = new JProgressBar(0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(470, 20));
        progressBar.setBackground(Constants.BACKGROUND);
        progressBar.setForeground(Constants.PROGRESS_BAR_FILLER);
        panel2.add(progressBar);
        add(panel2);

        // Output area
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(outputArea);
        panel3.add(jScrollPane);
        add(panel3, BorderLayout.CENTER);

        // Buttons for compressing, decompressing and exit
        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        compressButton = new JButton("Compress");
        compressButton.setBackground(Constants.BACKGROUND);
        compressButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                compressButton.setBackground(Constants.BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                compressButton.setBackground(Constants.BACKGROUND);
            }
        });
        compressButton.setPreferredSize(Constants.BUTTON_SIZE);
        decompressButton = new JButton("Decompress");
        decompressButton.setBackground(Constants.BACKGROUND);
        decompressButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                decompressButton.setBackground(Constants.BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                decompressButton.setBackground(Constants.BACKGROUND);
            }
        });
        decompressButton.setPreferredSize(Constants.BUTTON_SIZE);
        exitButton = new JButton("Exit");
        exitButton.setBackground(Constants.BACKGROUND);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(Constants.BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(Constants.BACKGROUND);
            }
        });
        exitButton.setPreferredSize(Constants.BUTTON_SIZE);
        panel4.add(compressButton);
        panel4.add(decompressButton);
        panel4.add(exitButton);
        add(panel4, BorderLayout.SOUTH);

        // Action listeners
        selectDirButton.addActionListener(new SelectFileAction());
        compressButton.addActionListener(new CompressAction());
        decompressButton.addActionListener(new DecompressAction());
        exitButton.addActionListener(new ExitAction());
    }

    private class SelectFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setDialogTitle("Choose a Directory or a File to compress");
            chooser.setAcceptAllFileFilterUsed(false); // Disable the "All Files" option
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only allow directories

            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = chooser.getSelectedFile();
                filePathField.setText(selectedDirectory.getAbsolutePath()); // Display selected directory path
            } else {
                filePathField.setText(null);
                outputArea.setText("Selection canceled.");
            }
        }
    }

    private class Task extends SwingWorker<Void, Integer> {
        private File packToCompress;
        private int fileAmount = 1;
        private File saveTo;
        private OutStream out;
        private Action action;
        private Task(Action action) {
            this.action = action;
        }

        @Override protected Void doInBackground() {
            if(action == Action.COMPRESS) {
                if(filePathField.getText().isEmpty()) {
                    return null;
                }
                // checks if button 'compress' pressed for no content in filePathField
                packToCompress = new File(filePathField.getText());
                // for progressbar increment
                fileAmount = packToCompress.listFiles().length;
                progressBar.setMaximum(fileAmount);
                // request for directory to save, where will be created file compressed.huff
                invokeChooser("Select a Directory to save compressed data");

                saveTo = new File( filePathField.getText() + "\\compressed.huff"); // Save to current directory
                out = new OutStream(saveTo);

                // perform compression
                try {
                    long startTime = System.currentTimeMillis();
                    Compressor.compress(packToCompress, out, this::updateProgress);
                    long endTime = System.currentTimeMillis();
                    long time = endTime - startTime;

                    outputArea.setText("Compression successfully completed!");
                    outputArea.append("\n" + "Compressed to: " + filePathField.getText());
                    outputArea.append("\n" + "Elapsed time: " + time + Constants.TIME_UNIT);
                    outputArea.append("\n" + "Compression ratio: " + ratio());
                    outputArea.setFont(Constants.FONT);

                } catch (Exception ex) {
                    outputArea.setText("Error during compression!");
                    outputArea.append("\n" + "Nothing to compress or no directory for saving data.");
                    outputArea.setFont(Constants.FONT);
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            } else if (action == Action.DECOMPRESS) {
                // request for a .huff file directory
                invokeChooser("Select a Directory of Huffman file (.huff) for decompressing");
                File compressedFile = new File(filePathField.getText() + "\\compressed.huff");
                try {
                    Compressor.decompress(compressedFile, this::updateProgress);
                    outputArea.append("\n" + "Decompression complete.");
                } catch (NoSuchElementException ex) {
                    outputArea.append("\n" + "End of decompression.");
                    outputArea.append("\n" + "Decompressed to: " + filePathField.getText());
                } catch (Exception ex) {
                    outputArea.append("\n" + "Error during decompression: " + ex.getMessage());
                }
            }
            return null;
        }

        private void updateProgress(int progress) {
            publish((progress/fileAmount)*100);
        }

        @Override protected void process(java.util.List<Integer> chunks) {
            for (int value : chunks) {
                progressBar.setValue(value);
            }
        }

        @Override protected void done() {
            if (action==Action.COMPRESS & filePathField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nothing to compress!");
            } else if (action == Action.COMPRESS) {
                JOptionPane.showMessageDialog(null, "Compression Completed!");
                progressBar.setValue(0);
            } else if (action == Action.DECOMPRESS & filePathField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nothing to decompress!");
            } else if (action == Action.DECOMPRESS) {
                JOptionPane.showMessageDialog(null, "Decompression Completed!");
                progressBar.setValue(0);
            }
        }

        private void invokeChooser(String dialogTitle) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setDialogTitle(dialogTitle);
            chooser.setAcceptAllFileFilterUsed(false); // Disable the "All Files" option
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only allow directories

            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = chooser.getSelectedFile();
                filePathField.setText(selectedDirectory.getAbsolutePath()); // Display selected directory path
            } else {
                filePathField.setText(null);
                outputArea.setText("Selection canceled.");
            }
        }

        private String ratio() {
            long newSize = saveTo.length();
            Double ratio = ((double) newSize / getDirLength(packToCompress)) * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(ratio) + "%";
        }
    }

    private class CompressAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            new Task(Action.COMPRESS).execute();
        }
    }

    private class DecompressAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            new Task(Action.DECOMPRESS).execute();
        }
    }

    private class ExitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private enum Action {
        COMPRESS, DECOMPRESS
    }

    private long getDirLength(File file) {
        if(file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            long length = 0;
            File[] list = file.listFiles();
            if (null == list)
                return 0;
            for (File content : list) {
                length += getDirLength(content);
            }
            return length;
        } else {
            throw new RuntimeException("Unknown file type");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(() -> {
            HuffGUI gui = new HuffGUI();
            gui.setVisible(true);
        });
    }
}
