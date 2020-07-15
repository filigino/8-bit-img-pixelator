import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.filechooser.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelatorGUI implements ActionListener {
    JFrame frame;
    JPanel panel;

    JButton selectImageButton;
    JButton plusButton;
    JButton minusButton;
    JButton pixelateButton;

    JLabel numberOfColorsLabel;
    JLabel selectedImageLabel;

    Picture originalPicture;
    int numberOfColors;

    public PixelatorGUI() {
        numberOfColors = 16;
        selectImageButton = new JButton("Select File");
        plusButton = new JButton("+");
        minusButton = new JButton("-");
        pixelateButton = new JButton("Pixelate!");

        selectImageButton.addActionListener(this);
        plusButton.addActionListener(this);
        minusButton.addActionListener(this);
        pixelateButton.addActionListener(this);

        numberOfColorsLabel = new JLabel("Number of Colors: "
            + numberOfColors);
        selectedImageLabel = new JLabel();

        panel = new JPanel();
        // top left bottom right
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(selectedImageLabel);
        panel.add(selectImageButton);
        panel.add(numberOfColorsLabel);
        panel.add(plusButton);
        panel.add(minusButton);
        panel.add(pixelateButton);

        frame = new JFrame();
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Pixelator");
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("+")) {
            if (numberOfColors < 1024) {
                numberOfColors *= 2;
                numberOfColorsLabel.setText("Number of Colors: "
                    + numberOfColors);
            }
        } else if (command.equals("-")) {
            if (numberOfColors > 2) {
                numberOfColors /= 2;
                numberOfColorsLabel.setText("Number of Colors: "
                    + numberOfColors);
            }
        } else if (command.equals("Select File")) {
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
                selectedImageLabel.setText(
                    fileChooser.getSelectedFile().getName()
                );
                originalPicture = new Picture(new File(
                    fileChooser.getSelectedFile().getAbsolutePath()
                ));
                originalPicture.show();
            }
        } else if (command.equals("Pixelate!")) {
            Picture picture = new Picture(originalPicture);
            Pixelator.pixelate(picture, numberOfColors);
            picture.show();
        }
    }

    public static void main(String[] args) {
        new PixelatorGUI();
    }
}
