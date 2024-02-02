package org.unina.project.view.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unina.project.App;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.enums.PostType;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.custom.PublishedContentCard;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

import java.util.Set;

/**
 * FXML Controller per l'interfaccia di un gruppo.
 * Mostra tutti i post e i commenti del gruppo.
 * Sono inoltre disponibili i bottoni per interagire con il gruppo e i post di esso.
 */
public class FXMLGroupController extends FXMLEntityGridController<Post> {
    @FXML
    private AnchorPane header;
    @FXML
    private Button report, leave;
    @FXML
    private TextField content;
    @FXML
    public Button silent, send;

    private boolean isSilentPublication = false;

    @Override
    protected void setup() {
        setReversedOrder(true);
        Session session = Session.getGlobalSession();
        getTitle().setText(session.getGroup().getName());
        header.getChildren().remove(session.getGroup().getCreator().equals(session.getUser()) ? leave : report);
        silent.setGraphic(new FontIcon("fas-volume-up"));
        refresh();
        show();
    }

    @Override
    protected Set<Post> getAllEntities() {
        return App.getInstance().getPosts().getAll(Session.getGlobalSession().getGroup());
    }

    @Override
    protected VBox getEntityCard(Post post) {
        return new PublishedContentCard<>(getGrid(), post).create();
    }

    @FXML
    private void report() {
        App.getInstance().setScene(SceneType.REPORT.getScene());
    }

    @FXML
    private void leave() {
        Session session = Session.getGlobalSession();
        Alert alert = SceneAlertUtils.alert(Alert.AlertType.WARNING, "Abbandono gruppo", "Abbandono gruppo", "Sicuro di voler abbandonare il gruppo?", true, new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE));
        if (alert.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            if (session.getGroup().getCreator() == session.getUser()) {
                SceneAlertUtils.alert(Alert.AlertType.ERROR, "Abbandono annullato", "Abbandono bloccato!", "Sei il creatore del gruppo, non puoi abbandonarlo!", false);
                return;
            }
            session.getGroup().getMembers().remove(session.getUser());
            App.getInstance().getGroups().update(session.getGroup());
            SceneAlertUtils.alert(Alert.AlertType.INFORMATION, "Abbandono completato", "Abbandono completato!", "Hai abbandonato il gruppo " + session.getGroup().getName(), false);
            setHomepage();
        }
    }

    @FXML
    private void publish() {
        String text = content.getText();
        if (text.isBlank()) return;
        content.clear();
        App.getInstance().getPosts().create(Session.getGlobalSession().getUser(), Session.getGlobalSession().getGroup(), PostType.TEXT, text, this.isSilentPublication).ifPresent(post -> {
            getCards().put(post, new PublishedContentCard<>(getGrid(), post).create());
            show(getCards().values().stream().toList());
        });
    }

    @FXML
    public void toggleSilentPostPublication(ActionEvent event) {
        if (!(event.getSource() instanceof Button button)) return;
        this.isSilentPublication = !this.isSilentPublication;
        button.setGraphic(new FontIcon(this.isSilentPublication ? "fas-volume-mute" : "fas-volume-up"));
    }

    @FXML
    private void pressPublishButton(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            publish();
    }
}