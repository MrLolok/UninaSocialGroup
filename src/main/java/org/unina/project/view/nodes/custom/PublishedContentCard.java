package org.unina.project.view.nodes.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unina.project.social.entities.PublishedContent;
import org.unina.project.social.entities.comments.Comment;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.CommonNodesUtils;
import org.unina.project.view.nodes.TextBuilder;
import org.unina.project.view.nodes.handlers.PublishedContentCommentButtonHandler;
import org.unina.project.view.nodes.handlers.PublishedContentDeleteButtonHandler;
import org.unina.project.view.nodes.handlers.PublishedContentLikeButtonHandler;

import java.text.SimpleDateFormat;

/**
 * Questa classe si occupa di creare la "card" che rappresenta
 * un contenuto pubblicato (post o commento), contente tutte le
 * informazioni su di esso.
 * @param <T> tipo di contenuto
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class PublishedContentCard<T extends PublishedContent<?>> {
    private final static SimpleDateFormat POST_CREATION_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm");

    private final Pane parent;
    @Getter
    private final T content;
    @Getter @Setter
    private boolean insideGrid = true, commentsShown = true;

    public VBox create() {
        VBox card = new VBox();
        VBox.setVgrow(card, Priority.ALWAYS);
        // Setup della card del contenuto
        HBox header = getHeader();
        Text content = TextBuilder.of(this.content.getContent()).build();
        VBox.setMargin(content, new Insets(8, 0, 8, 0));
        HBox footer = getFooter(card);

        card.getChildren().addAll(header, content, CommonNodesUtils.getSpacer(), footer);
        card.setMaxHeight(140);
        card.getStyleClass().addAll("card", "shadow");
        card.setId(String.format("card-%s", this.content.getId().toString()));
        card.getProperties().put("date", this.content.getTimestamp().getTime());
        Insets margin = new Insets(16, 0, 16, 0);
        if (insideGrid)
            GridPane.setMargin(card, margin);
        else
            VBox.setMargin(card, margin);

        if (this.commentsShown) {
            // Setup dei commenti del contenuto
            if (!getContent().getComments().isEmpty()) {
                Text comments = TextBuilder.of("Commenti").color("#4e4e4e").weight(FontWeight.BOLD).size(20).build();
                VBox.setMargin(comments, new Insets(16, 0, 0, 0));
                card.getChildren().add(comments);
            }
            for (Comment comment : getContent().getComments()) {
                VBox commentCard = new PublishedContentCard<>(card, comment, false, true).create();
                card.getChildren().add(commentCard);
            }
        }
        return card;
    }

    private HBox getHeader() {
        ImageView avatar = new ImageView();
        avatar.setFitWidth(24);
        avatar.setFitHeight(24);
        avatar.setPreserveRatio(true);
        avatar.setImage(this.content.getAuthor().getGender().getImage());
        Text name = TextBuilder.of(this.content.getAuthor().getFullName()).color("#ef6179").size(20).weight(FontWeight.BOLD).build();
        Text date = TextBuilder.of(POST_CREATION_DATE_FORMAT.format(this.content.getTimestamp())).color("#d4d4d4").build();
        HBox header = new HBox(avatar, name, date);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(8);
        return header;
    }

    private HBox getFooter(VBox card) {
        HBox footer = new HBox();
        footer.setSpacing(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(footer, new Insets(12, 0, 12, 0));
        if (content.getAuthor().equals(Session.getGlobalSession().getUser())) {
            Button delete = new Button("Elimina");
            FontIcon trash = new FontIcon("far-trash-alt");
            delete.setOnAction(new PublishedContentDeleteButtonHandler<>(this.parent, card, this.content));
            delete.setGraphic(trash);
            delete.getStyleClass().addAll("delete-button");
            footer.getChildren().add(delete);
        }

        Button like = new Button();
        like.setOnAction(new PublishedContentLikeButtonHandler<>(this.content, Session.getGlobalSession().getUser()));
        like.getStyleClass().addAll("like-button", "rounded");
        CommonNodesUtils.setLikeButton(like, this.content, Session.getGlobalSession().getUser());
        Button comment = new Button("Commenta");
        comment.setOnAction(new PublishedContentCommentButtonHandler<>(this.content, Session.getGlobalSession().getUser()));
        comment.getStyleClass().addAll("comment-button", "rounded");
        footer.getChildren().addAll(like, comment);
        return footer;
    }
}
