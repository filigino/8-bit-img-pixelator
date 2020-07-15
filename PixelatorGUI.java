import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelatorGUI implements ActionListener {
    JFrame frame;

    JPanel imagePanel;
    JPanel buttonPanel;
    JPanel colorsPanel;

    JLabel imageLabel;
    JLabel numberOfColorsLabel;

    JButton plusButton;
    JButton minusButton;
    JButton selectImageButton;
    JButton pixelateButton;
    JButton saveButton;

    Picture originalPicture;
    Picture pixelatedPicture;
    int numberOfColors;

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
                "Picture Files (*.png,*.jpg,*.jpeg)", "png", "jpg", "jpeg"
            );
            fileChooser.addChoosableFileFilter(restrict);

            // invoke the showsOpenDialog function to show the save dialog
            int r = fileChooser.showOpenDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // set the label to the path of the selected file
                imageLabel.setText(
                    fileChooser.getSelectedFile().getName()
                );
                originalPicture = new Picture(new File(
                    fileChooser.getSelectedFile().getAbsolutePath()
                ));
                originalPicture.show();
            }
        } else if (command.equals("Pixelate!")) {
            if (originalPicture != null) {
                pixelatedPicture = new Picture(originalPicture);
                Pixelator.pixelate(pixelatedPicture, numberOfColors);
                pixelatedPicture.show();
            }
        } else if (command.equals("Save")) {
            if (pixelatedPicture != null) {
                pixelatedPicture.save(imageLabel.getText());
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

    public static void main(String[] args) {
        new PixelatorGUI();
    }
}
