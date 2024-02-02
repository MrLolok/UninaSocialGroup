package org.unina.project.view.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.unina.project.App;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * FXML Controller per l'interfaccia di login.
 * Validazione dell'email e password inserita dall'utente
 * e recupero dell'utente associato a questi due input.
 */
public class FXMLLoginController {
    private final static Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    @FXML
    private TextField email, password;

    @FXML
    private void login() {
        String email = this.email.getText(), password = this.password.getText();
        if (!EMAIL_REGEX_PATTERN.matcher(email).matches()) {
            warnLoginFailed("Email invalida!", "Inserisci un'email valida come username@domain.com.");
            return;
        }
        if (password.isBlank()) {
            warnLoginFailed("Password invalida!", "Inserisci una password valida.");
            return;
        }
        Optional<User> user = App.getInstance().getUsers().get(email, password);
        if (user.isEmpty())
            Platform.runLater(() -> warnLoginFailed("Utente non trovato!", "Non è stato trovato alcun utente, forse vuoi registrarti?"));
        else {
            Session.getGlobalSession().setUser(user.get());
            App.getInstance().setScene(SceneType.HOMEPAGE.getScene());
        }
    }

    private void warnLoginFailed(String reason, String content) {
        SceneAlertUtils.alert(Alert.AlertType.ERROR, "Login fallito", reason, content, false);
    }
}