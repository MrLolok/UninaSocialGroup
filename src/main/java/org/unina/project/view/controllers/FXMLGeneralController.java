package org.unina.project.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.Getter;
import org.unina.project.App;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.custom.GroupCreationDialog;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller generale.
 * Contiene componenti comuni tra le varie interfacce
 * come la sidebar e il titolo.
 */
@Getter
public abstract class FXMLGeneralController implements Initializable {
    @FXML
    private Text title;
    @FXML
    private ImageView avatar;
    @FXML
    private Button username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = Session.getGlobalSession().getUser().getGender().getImage();
        if (image != null)
            avatar.setImage(image);
        username.setText(Session.getGlobalSession().getUser().getFullName());
        setup();
    }

    /**
     * Ritorna alla homepage.
     */
    @FXML
    public void setHomepage() {
        Session.getGlobalSession().setGroup(null);
        App.getInstance().setScene(SceneType.HOMEPAGE.getScene());
    }

    /**
     * Naviga nella pagina con le notifiche.
     */
    @FXML
    public void setNotifications() {
        Session.getGlobalSession().setGroup(null);
        App.getInstance().setScene(SceneType.NOTIFICATIONS.getScene());
    }

    /**
     * Esegui il logout.
     */
    @FXML
    public void logout() {
        Session.getGlobalSession().reset();
        App.getInstance().setScene(SceneType.LOGIN.getScene());
    }

    /**
     * Mostra il popup per creare un nuovo gruppo.
     */
    @FXML
    public void showGroupCreationDialog() {
        Dialog<GroupCreationDialog.Results> dialog = GroupCreationDialog.create();
        Optional<GroupCreationDialog.Results> result = dialog.showAndWait();
        result.ifPresent(results -> {
            App.getInstance().getGroups().create(Session.getGlobalSession().getUser(), results.name(), results.description(), results.type());
            setHomepage();
            SceneAlertUtils.alert(Alert.AlertType.INFORMATION, "Gruppo creato", "Gruppo creato!", "Hai creato il gruppo chiamato " + results.name(), false);
        });
    }

    /**
     * Mostra un avviso per le funzionalità non implementate.
     */
    @FXML
    private void showNotImplementedAlert() {
        SceneAlertUtils.alert(Alert.AlertType.WARNING, "Non disponibile", "Funzionalità non disponibile", "Questa funzionalità non è stata ancora implementata!", false);
    }

    /**
     * Metodo chiamato all'inizializzazione dell'interfaccia.
     * @see Initializable#initialize(URL, ResourceBundle)
     */
    protected abstract void setup();
}
