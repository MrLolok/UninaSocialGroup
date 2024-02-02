package org.unina.project.view.nodes.custom;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.enums.GroupType;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.CommonNodesUtils;
import org.unina.project.view.nodes.TextBuilder;
import org.unina.project.view.nodes.handlers.GroupAccessButtonHandler;

import java.text.SimpleDateFormat;

/**
 * Questa classe si occupa di creare la "card" che rappresenta
 * un gruppo, contente tutte le informazioni su di esso.
 */
@RequiredArgsConstructor
public class GroupCard {
    private final static SimpleDateFormat GROUP_CREATION_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    private final static String NO_DESCRIPTION_TEXT = "Nessuna descrizione";
    private final Group group;

    public VBox create() {
        Text title = TextBuilder.of(group.getName()).color("#4e4e4e").size(20).weight(FontWeight.BOLD).build();
        Text description = TextBuilder.of(group.getDescription() == null ? NO_DESCRIPTION_TEXT : group.getDescription()).build();

        HBox tags = getTags();
        HBox properties = getProperties();
        AnchorPane bottom = getBottomBar();

        VBox card = new VBox(title, description, CommonNodesUtils.getSpacer(), tags, properties, CommonNodesUtils.getSpacer(), bottom);
        VBox.setMargin(card, new Insets(24, 0, 24, 0));
        card.setMaxHeight(200);
        card.getStyleClass().addAll("group-card", "shadow");
        card.setId(String.format("group-card-%d", group.getId()));
        card.getProperties().put("date", group.getCreation().getTime());
        GridPane.setMargin(card, new Insets(16, 0, 16, 0));
        return card;
    }

    private HBox getTags() {
        FontIcon icon = new FontIcon("fas-hashtag");
        icon.getStyleClass().add("group-tags-icon");
        Text text = TextBuilder.of("Tags:").color("#f39772").weight(FontWeight.BOLD).build();
        HBox section = new HBox(icon, text);
        section.setSpacing(8);

        if (this.group.getTags().isEmpty()) {
            section.getChildren().add(TextBuilder.of("No tags").build());
        } else
            for (Tag tag : this.group.getTags()) {
                Text keyword = TextBuilder.of(tag.getCapitalizedKeyword()).color("#ef6179").weight(FontWeight.BOLD).build();
                StackPane tagContainer = new StackPane(keyword);
                tagContainer.getStyleClass().addAll("group-tag", "rounded", "shadow");
                section.getChildren().add(tagContainer);
            }
        return section;
    }

    private HBox getProperties() {
        HBox properties = new HBox(
                getProperty("fas-user", "Creatore:", this.group.getCreator().getFullName()),
                getProperty("fas-users", "Membri:", this.group.getMembers().size()),
                getProperty("fas-door-open", "Tipo:", this.group.getType().getDisplay())
        );
        properties.getStyleClass().add("group-properties");
        VBox.setMargin(properties, new Insets(8, 0, 0, 0));
        return properties;
    }

    private HBox getProperty(String iconName, String name, Object value) {
        FontIcon icon = new FontIcon(iconName);
        icon.getStyleClass().add("group-property-icon");
        Text text = TextBuilder.of(name).color("#f39772").weight(FontWeight.BOLD).build();
        Text property = TextBuilder.of(value.toString()).color("#4e4e4e").build();
        HBox wrapper = new HBox(icon, text, property);
        wrapper.setSpacing(8);
        return wrapper;
    }

    private AnchorPane getBottomBar() {
        // Icona calendario
        FontIcon calendar = new FontIcon("far-calendar-alt");
        AnchorPane.setTopAnchor(calendar, 12D);
        calendar.getStyleClass().add("calendar");
        // Data di creazione del gruppo
        Text date = TextBuilder.of(GROUP_CREATION_DATE_FORMAT.format(group.getCreation())).color("#c9c9c9").build();
        AnchorPane.setTopAnchor(date, 12D);
        AnchorPane.setLeftAnchor(date, 24D);
        // Bottone di accesso al gruppo
        boolean member = group.isMember(Session.getGlobalSession().getUser());
        Button access = new Button(member ? "Visualizza" : "Accedi");
        access.setId("group-button-" + group.getId());
        access.setOnAction(new GroupAccessButtonHandler(group, Session.getGlobalSession().getUser()));
        access.getStyleClass().addAll("group-access-button", "rounded", "verdana", "bold");
        if (!member && group.getType() == GroupType.CLOSED)
            access.setDisable(true);
        AnchorPane.setTopAnchor(access, 12D);
        AnchorPane.setRightAnchor(access, 0D);
        // Barra inferiore
        return new AnchorPane(calendar, date, access);
    }
}
