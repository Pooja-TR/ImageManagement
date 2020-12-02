package sample;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.utils.BatchConverter;
import org.im4java.utils.ExtensionFilter;
import org.im4java.utils.FilenameLoader;
import org.im4java.utils.FilenamePatternResolver;

import javax.imageio.ImageIO;
import javax.swing.*;

import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Controller extends Main {
    @FXML
    private Button uploadButton;

    @FXML
    private Button downloadButton;

    @FXML
    private ListView<String> listView;

    @FXML
    private ImageView imageView;

    @FXML
    private HBox thumbnailBox;

    @FXML
    private ScrollPane gallery;

    /**
     * Helper function for deleteExt Create a function to get basename
     */
    public String getBaseName(File file) {
        String name = file.getName();
        // split with dot
        int dot = name.lastIndexOf('.');
        return (dot == -1) ? name : name.substring(0, dot);
    }

    /**
     * Helper function for deleteExt Create a function to eliminate format
     */
    public String deleteExt(File file) {
        String parentPath = file.getParent();
        String baseName = getBaseName(file);
        // output the path + filename
        return new String(parentPath + '\\' + baseName);
    }

    /**
     * Main function to upload the image and printout message Create a function to
     * browse multiple file on the disk upload to the indicated file
     */
    public void BrowseMultiFile(ActionEvent event) {
        // Browse multiple file
        FileChooser fc = new FileChooser();
        List<File> fileList = fc.showOpenMultipleDialog(null);

        try {
            for (File file : fileList) {
                // clear the previous image's information
                listView.getItems().clear();

                // Get file path and name, and present on canvas
                listView.getItems().add(file.getAbsolutePath());
                listView.getItems().add(new File(file.getParent()).getParent());
                listView.getItems().add(file.getName());

                // show image, port from the show image
                Image image = new Image("file:" + file.getAbsolutePath());
                ImageView img = new ImageView(image);

                // set the image to printout
                imageView.setImage(image);

                // add thumbnail to the image list
                img.setFitHeight(100);
                img.setFitWidth(100);
                thumbnailBox.getChildren().add(img);

                // upload chosen file to target img destination
                File destFile = new File(new File(file.getParent()).getParent() + "\\img\\" + file.getName());
                upload(file, destFile);

                // initial metadata for exif
                Metadata metadata = null;

                // determine the image's format to analyse the exif
                if (getExtension(file).equalsIgnoreCase("JPG")) {
                    metadata = JpegMetadataReader.readMetadata(file);
                } else if (getExtension(file).equalsIgnoreCase("png")) {
                    metadata = PngMetadataReader.readMetadata(file);
                }

                // print the exif information to the list view
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        listView.getItems().add(String.format("[%s] - %s = %s", directory.getName(), tag.getTagName(),
                                tag.getDescription()));
                    }
                    if (directory.hasErrors()) {
                        for (String error : directory.getErrors()) {
                            System.err.format("ERROR: %s", error);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a function to upload files
     * 
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void upload(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);
    }

    /**
     * Create a function to convert file to .png
     */
    public void convertToPNG(ActionEvent event) {
        // browse a file to convert
        BufferedImage bufferIn;
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        // Converter
        try {
            // buffer the image
            bufferIn = ImageIO.read(file);

            // create the buffer out image
            BufferedImage bufferOut = new BufferedImage(bufferIn.getWidth(), bufferIn.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            bufferOut.createGraphics().drawImage(bufferIn, 0, 0, Color.white, null);

            // output the buffer image to the png format to the same directory, with
            // _convert annotation
            ImageIO.write(bufferOut, "jpg", new File(deleteExt(file) + "_convert.png"));

            // show complete message
            JOptionPane.showMessageDialog(null, "Done");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Create a function to convert file to .jpg
     */
    public void convertToJPG(ActionEvent event) {
        // browse a file to convert
        BufferedImage bufferIn;
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        // Converter
        try {
            // buffer the image
            bufferIn = ImageIO.read(file);

            // create the buffer out image
            BufferedImage bufferOut = new BufferedImage(bufferIn.getWidth(), bufferIn.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            bufferOut.createGraphics().drawImage(bufferIn, 0, 0, Color.white, null);

            // output the buffer image to the jpg format to the same directory, with
            // _convert annotation
            ImageIO.write(bufferOut, "png", new File(deleteExt(file) + "_convert.jpg"));

            // show complete message
            JOptionPane.showMessageDialog(null, "Done");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Helper function to get the file's extension Create a function to get
     * extension, return the file's extension
     */
    public String getExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return (dot == -1) ? "" : name.substring(dot + 1);
    }

    /**
     * Helper function for multiple file upload Create a function to browse single
     * file on the disk
     */
    public void BrowseSingleFile(ActionEvent event) {
        // Browse single file
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        // Get file path and name, and present on canvas
        try {
            listView.getItems().add(file.getAbsolutePath());
            listView.getItems().add(file.getName());
            listView.getItems().add(getBaseName(file));
            listView.getItems().add(getExtension(file));
            listView.getItems().add(deleteExt(file));
        } catch (Exception e) {
            System.out.println("No file input (Single file).");
        }
    }

    /**
     * Helper function for testing thumbnail upload and detail printout Create a
     * function to show image chosen (Runtime exception: NullPointer Error when
     * directly use shift to select all without a starting file)
     *
     */
    public void showImage(ActionEvent event) {
        try {
            // Browse file
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(null);

            // create image and image view for tests
            Image image = new Image("file:" + file.getAbsolutePath());
            ImageView img = new ImageView(image);

            // image height restriction tester
            imageView.setImage(image);

            // thumbnail image tester
            img.setFitHeight(100);
            img.setFitWidth(100);
            thumbnailBox.getChildren().add(img);

            // file name tester
            listView.getItems().add(file.getName());

            // Metadata output tester
            // check the input stream
            FileInputStream inputStream = new FileInputStream("file:" + file.getAbsolutePath());
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            // printout the exif information
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    System.out.format("[%s] - %s = %s", directory.getName(), tag.getTagName(), tag.getDescription());
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.format("ERROR: %s", error);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No file input (Show image).");
        }
    }

    /**
     * Helper function Create a function to show exist uploaded images in thumbnail
     * gallery To check whether the image is in the thumbnail list To refresh the
     * thumbnail's image view
     */
    public void showGallery(ActionEvent event) {
        try {
            // refresh the image view
            thumbnailBox.getChildren().clear();

            // starter image test
            Image image = new Image("file:image/img/Shaka1.jpg");
            ImageView img = new ImageView(image);

            // set the thumbnail's size
            img.setFitHeight(100);
            img.setFitWidth(100);

            // add thumbnail to the image view
            thumbnailBox.getChildren().add(img);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No file in Gallery (Show image).");
        }
    }

    /**
     * Create a function to use IM4Java Intended to convert file format
     */
    public void thumbnail(ActionEvent event) throws InterruptedException, IOException, IM4JavaException {

        // Set initial path
        String imPath = "C:\\Programs\\ImageMagick-7.0.9-Q16";
        ConvertCmd cmd = new ConvertCmd();
        cmd.setSearchPath(imPath);

        // Object pGlobalSearchPath = "C:\\Programs\\ImageMagick-7.0.9-Q16";
        ProcessStarter.setGlobalSearchPath("C:\\Programs\\ImageMagick-7.0.9-Q16");

        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();
        op.addImage("Y:\\5100_Image_Management_Tool\\image\\img\\Shaka1.jpg");
        op.resize(100, 100);
        op.addImage("Y:\\5100_Image_Management_Tool\\image\\img\\Shaka1.1.jpg");

        cmd.run(op);

    }

}
