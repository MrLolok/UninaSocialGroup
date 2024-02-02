package org.unina.project.view.nodes.custom;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.unina.project.App;

import java.util.Objects;

/**
 * Questa classe si occupa di creare il popup {@link Dialog} per
 * pubblicare un nuovo commento, inserendo il testo da inviare.
 */
public class CommentCreationDialog {
    private final static ImageView GRAPHIC;
    private final static String DIALOG_TITLE = "Scrivi un commento";
    private final static String DIALOG_HEADER = "Invia un nuovo commento\nInserisci tutti i campi richiesti";

    static {
        GRAPHIC = new ImageView(new Image(Objects.requireNonNull(App.class.getClassLoader().getResourceAsStream("imgs/comment.png"))));
        GRAPHIC.setFitHeight(80);
        GRAPHIC.setFitWidth(80);
    }

    public static Dialog<String> create() {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(App.getInstance().getStage());
        dialog.setGraphic(GRAPHIC);
        dialog.setTitle(DIALOG_TITLE);
        dialog.setHeaderText(DIALOG_HEADER);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(400);
        ButtonType createButtonType = new ButtonType("Commenta", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        TextArea textArea = new TextArea();
        textArea.setPromptText("Inserisci il commento");
        dialogPane.setContent(new VBox(8, textArea));

        Node createButton = dialogPane.lookupButton(createButtonType);
        createButton.setDisable(true);
        // Verifica che ci sia un commento valido
        textArea.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.isBlank()));

        dialog.setResultConverter(button -> button.getButtonData() == ButtonBar.ButtonData.OK_DONE ? textArea.getText() : null);
        return dialog;
    }
}
