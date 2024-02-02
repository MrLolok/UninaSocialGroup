package org.unina.project.view.nodes.custom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.App;
import org.unina.project.social.enums.GroupType;

import java.util.Arrays;
import java.util.Objects;

/**
 * Questa classe si occupa di creare il popup {@link Dialog} per creare
 * un nuovo gruppo, inserendo il nome, la descrizione e il tipo.
 */
public class GroupCreationDialog {
    private final static ImageView GRAPHIC;
    private final static String DIALOG_TITLE = "Creazione gruppo";
    private final static String DIALOG_HEADER = "Crea un nuovo gruppo\nInserisci tutti i campi richiesti";

    static {
        GRAPHIC = new ImageView(new Image(Objects.requireNonNull(App.class.getClassLoader().getResourceAsStream("imgs/group.png"))));
        GRAPHIC.setFitHeight(80);
        GRAPHIC.setFitWidth(80);
    }

    public static Dialog<Results> create() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.initOwner(App.getInstance().getStage());
        dialog.setGraphic(GRAPHIC);
        dialog.setTitle(DIALOG_TITLE);
        dialog.setHeaderText(DIALOG_HEADER);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(400);
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        TextField name = new TextField();
        name.setPromptText("Nome");
        TextField description = new TextField();
        description.setPromptText("Descrizione");
        ObservableList<String> options = FXCollections.observableList(Arrays.stream(GroupType.values()).map(GroupType::getDisplay).toList());
        ComboBox<String> type = new ComboBox<>(options);
        type.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, name, description, type));

        Node createButton = dialogPane.lookupButton(createButtonType);
        createButton.setDisable(true);
        // Verifica che ci sia un nome valido
        name.textProperty().addListener((observable, oldValue, newValue) -> createButton.setDisable(newValue.isBlank()));

        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                return new Results(name.getText(), description.getText().isBlank() ? null : description.getText(), GroupType.getFromDisplay(type.getValue()).get());
            return null;
        });
        return dialog;
    }

    /**
     * Record rappresentativo del risultato di creazione di un nuovo gruppo.
     * @param name del gruppo
     * @param description del gruppo
     * @param type del gruppo
     */
    public record Results(@NotNull String name, @Nullable String description, GroupType type) {}
}
