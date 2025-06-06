package controllers.dashBoard.artist;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import services.file.SongFileManager;
import utils.AlertUtil;

import java.io.File;
import java.io.IOException;

public class CreateAlbumController extends BaseArtistController {

    @FXML private Label welcomeLabel;
    @FXML private Button signOutButton;
    @FXML private TextField albumTitleField;
    @FXML private Button chooseImageButton;
    @FXML private ImageView imagePreview;
    @FXML private Button submitButton;

    private final SongFileManager songFileManager = new SongFileManager();
    private File selectedImageFile;

    @FXML
    private void initialize() {
        if (!validateSession(signOutButton)) return;
        initializeUI();
    }

    private void initializeUI() {
        setArtistInfo(welcomeLabel);
        updateImageView(imagePreview, null); // Set default image
    }

    @FXML
    private void chooseImage() {
        checkComponent(chooseImageButton, "chooseImageButton");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Album Art");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            checkComponent(imagePreview, "imagePreview");
            if (imagePreview != null) {
                updateImageView(imagePreview, file.toURI().toString()); // Show selected image
            }
        }
    }

    @FXML
    private void createAlbum() {
        checkComponent(albumTitleField, "albumTitleField");
        String albumTitle = albumTitleField != null ? albumTitleField.getText().trim() : "";
        if (albumTitle.isEmpty()) {
            AlertUtil.showError("Album title is required.");
            return;
        }

        try {
            String albumArtPath = selectedImageFile != null ? saveAlbumArt(albumTitle) : null;
            songFileManager.saveAlbum(artist.getNickName(), albumTitle, java.time.LocalDate.now().toString(), null, albumArtPath);

            // Checking the existence of the file
            String albumDir = songFileManager.getAlbumDir(artist.getNickName(), albumTitle) + "album.txt";
            File albumFile = new File(albumDir);
            if (!albumFile.exists()) {
                throw new IOException("Failed to create album file: " + albumDir);
            }

            // Show image in preview (if image exists)
            if (albumArtPath != null && imagePreview != null) {
                System.out.println("Updating image preview with path: " + albumArtPath);
                updateImageView(imagePreview, new File(albumArtPath).toURI().toString());
            }

            AlertUtil.showSuccess("Album '" + albumTitle + "' created successfully!");
            resetForm();
        } catch (IOException e) {
            AlertUtil.showError("Error creating album: " + e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Unexpected error creating album: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String saveAlbumArt(String albumTitle) throws IOException {
        checkComponent(selectedImageFile, "selectedImageFile");
        if (selectedImageFile == null) return null;

        return songFileManager.saveAlbumArt(artist.getNickName(), albumTitle, selectedImageFile);
    }

    private void resetForm() {
        clearFields();
        resetImage();
    }

    private void clearFields() {
        checkComponent(albumTitleField, "albumTitleField");
        if (albumTitleField != null) albumTitleField.clear();
    }

    private void resetImage() {
        checkComponent(imagePreview, "imagePreview");
        if (imagePreview != null) updateImageView(imagePreview, null);
        selectedImageFile = null;
    }

    @FXML public void goToProfile() { super.goToProfile(); }
    @FXML public void goToAddSong() { super.goToAddSong(); }
    @FXML public void goToDeleteSong() { super.goToDeleteSong(); }
    @FXML public void goToEditSong() { super.goToEditSong(); }
    @FXML public void goToCreateAlbum() { super.goToCreateAlbum(); }
    @FXML public void goToDeleteAlbum() { super.goToDeleteAlbum(); }
    @FXML public void goToEditAlbum() { super.goToEditAlbum(); }
    @FXML public void goToPendingRequests() { super.goToPendingRequests(); }
    @FXML public void goToApprovedRequests() { super.goToApprovedRequests(); }
    @FXML public void goToRejectedRequests() { super.goToRejectedRequests(); }
    @FXML public void signOut() { super.signOut(); }
}