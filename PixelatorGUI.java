import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelatorGUI implements ActionListener {
    private final JFrame frame;

    private final JPanel imagePanel;
    private final JPanel buttonPanel;
    private final JPanel colorsPanel;

    private final JLabel imageLabel;
    private final JLabel numberOfColorsLabel;

    private final JButton plusButton;
    private final JButton minusButton;
    private final JButton selectImageButton;
    private final JButton pixelateButton;
    private final JButton saveButton;

    private BufferedImage originalImage;
    private BufferedImage pixelatedImage;
    private int numberOfColors;

    public PixelatorGUI() {
        numberOfColors = 16;

        numberOfColorsLabel = new JLabel(Integer.toString(numberOfColors));
        imageLabel = new JLabel(" ");

        plusButton = new JButton("+");
        minusButton = new JButton("-");
        selectImageButton = new JButton("Select File");
        pixelateButton = new JButton("Pixelate!");
        saveButton = new JButton("Save");

        plusButton.addActionListener(this);
        minusButton.addActionListener(this);
        selectImageButton.addActionListener(this);
        pixelateButton.addActionListener(this);
        saveButton.addActionListener(this);

        imagePanel = new JPanel();
        imagePanel.add(imageLabel);

        buttonPanel = new JPanel();
        buttonPanel.add(selectImageButton);
        buttonPanel.add(pixelateButton);
        buttonPanel.add(saveButton);

        colorsPanel = new JPanel();
        colorsPanel.add(new JLabel("Number of Colors"));
        colorsPanel.add(minusButton);
        colorsPanel.add(numberOfColorsLabel);
        colorsPanel.add(plusButton);

        frame = new JFrame();
        frame.add(imagePanel, BorderLayout.PAGE_START);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(colorsPanel, BorderLayout.PAGE_END);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Pixelator");
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Select File")) {
            JFileChooser fileChooser = new JFileChooser(
                FileSystemView.getFileSystemView().getHomeDirectory()
            );
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle("Select an image");
            
            FileNameExtensionFilter restrict = new FileNameExtensionFilter(
                "Picture Files (*.png,*.jpg,*.jpeg,*.jfif)", "png", "jpg", "jpeg", "jfif"
            );
            fileChooser.addChoosableFileFilter(restrict);

            // invoke the showsOpenDialog function to show the save dialog
            int r = fileChooser.showOpenDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                try {
                    originalImage = ImageIO.read(
                        new File(fileChooser.getSelectedFile().getAbsolutePath()));
                    if (originalImage.getWidth() != originalImage.getHeight()) {
                        imageLabel.setText("Selected image is not a square");
                        originalImage = null;
                    } else {
                        imageLabel.setText(
                            fileChooser.getSelectedFile().getName()
                        );
                        Image image = originalImage;
                        image = image.getScaledInstance(256, 256, Image.SCALE_DEFAULT);
                        originalImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB_PRE);

                        Graphics2D graphics = originalImage.createGraphics();                        
                        graphics.drawImage(image, 0, 0, null);
                        graphics.dispose();
                        show(originalImage);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } else if (command.equals("Pixelate!")) {
            if (originalImage != null) {
                int width  = originalImage.getWidth();
                int height = originalImage.getHeight();
                pixelatedImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB_PRE);

                for (int col = 0; col < width; col++)
                    for (int row = 0; row < height; row++)
                        pixelatedImage.setRGB(col, row, 
                            originalImage.getRGB(col, row));

                Pixelator.pixelate(pixelatedImage, numberOfColors);
                show(pixelatedImage);
            }
        } else if (command.equals("Save")) {
            if (pixelatedImage != null) {
                save(pixelatedImage);
            }
        } else if (command.equals("+")) {
            if (numberOfColors < 1024) {
                numberOfColors *= 2;
                numberOfColorsLabel.setText(Integer.toString(numberOfColors));
            }
        } else if (command.equals("-")) {
            if (numberOfColors > 2) {
                numberOfColors /= 2;
                numberOfColorsLabel.setText(Integer.toString(numberOfColors));
            }
        }
    }

    private void show(BufferedImage image) {
        JFrame frame = new JFrame();

        frame.setContentPane(new JLabel(new ImageIcon(image)));
        // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.repaint();
    }

    private void save(BufferedImage image) {
        File file = new File(imageLabel.getText());
        String filename = file.getName();

        String suffix = filename.substring(filename.lastIndexOf('.') + 1);
        if ("jpg".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(image, suffix, file);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            System.out.println("Error: filename must end in .jpg or .png");
        }
    }

    public static void main(String[] args) {
        new PixelatorGUI();
    }
}
