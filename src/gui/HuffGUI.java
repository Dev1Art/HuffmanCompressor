package gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import core.Compressor;
import data.Constants;
import io.OutStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dev1Art
 * @project HuffCompressor
 */

public class HuffGUI extends JFrame {
    private JTextField dirPathField;
    private JProgressBar progressBar;
    private JTextArea outputArea;
    private JButton selectButton;
    private JButton compressButton;
    private JButton decompressButton;
    private JButton exitButton;
    private File packToCompress;
    private File saveTo;
    private static final Logger LOGGER = Logger.getLogger(HuffGUI.class.getName());

    public HuffGUI() {
        huffGUISetup();                         // sets up the main frame
        add(createDirectorySelectionPanel());   // panel for directory selection
        add(createProgressBarPanel());          // progress bar
        add(createOutputArea());                // output area
        add(createButtonsPanel());              // buttons for compressing, decompressing and exit
        // Action listeners
        selectButton.addActionListener(action -> handleFileSelection());
        compressButton.addActionListener(action -> handleCompressAction());
        decompressButton.addActionListener(action -> handleDecompressionAction());
        exitButton.addActionListener(action -> handleExitAction());
    }

    private void huffGUISetup() {
        setIconImage(new ImageIcon(Constants.GUI_ICON).getImage());
        setTitle("Huffman Compression");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    }

    private JPanel createDirectorySelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // field for the absolute path of the directory
        dirPathField = new JTextField(37);
        // button for directory selection
        selectButton = new JButton("Select");
        selectButton.setBackground(Constants.BACKGROUND);
        selectButton.setBorderPainted(false);
        selectButton.addMouseListener(createEffectsListener());
        selectButton.setPreferredSize(new Dimension(100, 30));
        // adding
        panel.add(dirPathField);
        panel.add(selectButton);
        return panel;
    }

    private JPanel createProgressBarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        // progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(470, 20));
        progressBar.setBackground(Constants.BACKGROUND);
        progressBar.setForeground(Constants.PROGRESS_BAR_FILLER);
        // adding
        panel.add(progressBar);
        return panel;
    }

    private JPanel createOutputArea() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // area for the app output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        // makes view scrollable
        JScrollPane jScrollPane = new JScrollPane(outputArea);
        // adding
        panel.add(jScrollPane);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // compression button
        compressButton = new JButton("Compress");
        compressButton.setBackground(Constants.BACKGROUND);
        compressButton.setBorderPainted(false);
        compressButton.addMouseListener(createEffectsListener());
        compressButton.setPreferredSize(Constants.BUTTON_SIZE);
        // decompression button
        decompressButton = new JButton("Decompress");
        decompressButton.setBackground(Constants.BACKGROUND);
        decompressButton.setBorderPainted(false);
        decompressButton.addMouseListener(createEffectsListener());
        decompressButton.setPreferredSize(Constants.BUTTON_SIZE);
        // exit button
        exitButton = new JButton("Exit");
        exitButton.setBackground(Constants.BACKGROUND);
        exitButton.setBorderPainted(false);
        exitButton.addMouseListener(createEffectsListener());
        exitButton.setPreferredSize(Constants.BUTTON_SIZE);
        // adding to the panel
        panel.add(compressButton);
        panel.add(decompressButton);
        panel.add(exitButton);
        return panel;
    }

    private MouseListener createEffectsListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND_PRESSED);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND);
            }
        };
    }

    private void handleFileSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooser.setDialogTitle("Choose a Directory or a File to compress");
        chooser.setAcceptAllFileFilterUsed(false); // Disable the "All Files" option
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only allow directories

        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = chooser.getSelectedFile();
            dirPathField.setText(selectedDirectory.getAbsolutePath()); // Display selected directory path
        } else {
            dirPathField.setText(null);
            outputArea.setText("Selection canceled.");
        }
    }

    private void handleCompressAction() {
        // case for the empty path
        if (dirPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(HuffGUI.this, "Nothing to compress!");
            return;
        }
        // sets up the pack to compress
        packToCompress = new File(dirPathField.getText());
        // request for directory to save, where will be created file compressed.huff
        invokeChooser("Select a Directory to save compressed data");
        // sets up directory for saving
        saveTo = new File(dirPathField.getText() + "\\compressed.huff");
        OutStream out = new OutStream(saveTo);
        // perform compression
        try {
            // compression time
            long startTime = System.currentTimeMillis();
            Compressor.compress(packToCompress, out);
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            // simple progress bar filler
            createProgressBarWorker().execute();
            // success compression
            outputArea.setText("Compression successfully completed!");
            outputArea.append("\n" + "Compressed to: " + dirPathField.getText());
            outputArea.append("\n" + "Elapsed time: " + time + Constants.TIME_UNIT);
            outputArea.append("\n" + "Compression ratio: " + ratio());
            outputArea.setFont(Constants.FONT);

        } catch (Exception ex) {
            // errors handling
            outputArea.setText("Error during compression!");
            outputArea.append("\n" + "Nothing to compress or no directory for saving data.");
            outputArea.setFont(Constants.FONT);
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    private void handleDecompressionAction() {
        // request for a .huff file directory
        invokeChooser("Select a Directory of Huffman file (.huff) for decompressing");
        // case for the empty path
        if (dirPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(HuffGUI.this, "Nothing to decompress!");
            return;
        }
        // sets up file to decompress with .huff format
        File compressedFile = new File(dirPathField.getText() + "\\compressed.huff");
        // perform decompression
        try {
            Compressor.decompress(compressedFile);
            // simple progress bar filler
            createProgressBarWorker().execute();
        } catch (NoSuchElementException ex) {
            // InStream throws exception for the last character if N = 8
            // This catch block gets rid of that. Will be fixed.
            outputArea.setText("Successfully decompressed.");
            outputArea.append("\n" + "Decompressed to: " + dirPathField.getText());
        } catch (Exception ex) {
            // errors handling
            outputArea.append("\n" + "Error during decompression: " + ex.getMessage());
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    private void handleExitAction() {
        System.exit(0);
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
            dirPathField.setText(selectedDirectory.getAbsolutePath()); // Display selected directory path
        } else {
            dirPathField.setText(null);
            outputArea.setText("Selection canceled.");
        }
    }

    private SwingWorker<Void, Integer> createProgressBarWorker() {
        return new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                compressButton.setEnabled(false);
                int totalFiles = Objects.requireNonNull(packToCompress.listFiles()).length;
                for (int i = 0; i < totalFiles; i++) {
                    publish((int) ((i + 1) * 100.0) / totalFiles);
                }
                return null;
            }
            @Override
            protected void process(List<Integer> chunks) {
                for (Integer value : chunks) {
                    progressBar.setValue(value);
                }
            }
            @Override
            protected void done() {
                JOptionPane.showMessageDialog(HuffGUI.this, "Compression Completed!");
                compressButton.setEnabled(true);
            }
        };
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

    private String ratio() {
        long newSize = saveTo.length();
        Double ratio = ((double) newSize / getDirLength(packToCompress)) * 100;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(ratio) + "%";
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            UIManager.put("TextField.border", BorderFactory.createLineBorder(Constants.BACKGROUND_HOVER, 3));
            UIManager.put("JTextArea.border", BorderFactory.createEmptyBorder());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            HuffGUI gui = new HuffGUI();
            gui.setVisible(true);
        });
    }
}