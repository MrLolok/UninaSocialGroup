package org.unina.project.view.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.unina.project.App;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.enums.ReportEngagementType;
import org.unina.project.social.enums.ReportEntityType;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.nodes.CommonNodesUtils;
import org.unina.project.view.nodes.TextBuilder;
import org.unina.project.view.scenes.enums.SceneType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * FXML Controller per l'interfaccia dei report di un gruppo.
 * Mostra tutti i report relativi ai contenuti di un gruppo,
 * come ad esempio il contenuti con più like, meno like, più
 * commenti, eccetera...
 */
public class FXMLReportController extends FXMLGeneralController {
    @FXML
    private DatePicker date;
    @FXML
    private VBox reports;

    @Override
    protected void setup() {
        date.setValue(LocalDate.now());
        getTitle().setText(Session.getGlobalSession().getGroup().getName());
        refresh();
    }

    @FXML
    private void back() {
        App.getInstance().setScene(SceneType.GROUP.getScene());
    }

    private VBox getReportCard(Group group, ReportEngagementType engagementType, ReportEntityType entityType) {
        LocalDate date = this.date.getValue();
        FontIcon icon = new FontIcon(entityType.getIconName());
        Text entity = TextBuilder.of(entityType.getDisplay()).color("#4e4e4e").size(24).weight(FontWeight.BOLD).build();
        HBox header = new HBox(icon, entity);
        header.setSpacing(12);
        String month = date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.ITALIAN);
        Text engagement = TextBuilder.of(String.format("%s %d", month.substring(0, 1).toUpperCase() + month.substring(1), date.getYear())).build();
        String content = engagementType.getExtraction().apply(entityType.getContents(group, date)).orElse("//");
        Text result = new Text(content);
        result.getStyleClass().addAll("report-result", "verdana");

        VBox card = new VBox(header, engagement, CommonNodesUtils.getSpacer(), result);
        card.setPrefWidth(340);
        card.setMinWidth(340);
        card.getStyleClass().addAll("report-card", "shadow");
        VBox.setMargin(card, new Insets(24, 0, 24, 0));
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    @FXML
    private void refresh() {
        Group group = Session.getGlobalSession().getGroup();
        this.reports.getChildren().removeAll(this.reports.getChildren().toArray(Node[]::new));
        Text averagesTitle = TextBuilder.of("Media pubblicazioni mensili").size(24).weight(FontWeight.BOLD).build();
        VBox.setMargin(averagesTitle, new Insets(24, 0, 12, 0));
        HBox averages = new HBox();
        averages.getStyleClass().add("report-row");
        for (ReportEntityType entityType : ReportEntityType.values()) {
            HBox average = getAverageMonthlyContentPublicationsCard(entityType);
            averages.getChildren().add(average);
        }
        this.reports.getChildren().addAll(averagesTitle, averages);
        for (ReportEngagementType engagementType : ReportEngagementType.values()) {
            Text title = TextBuilder.of(engagementType.getTitle()).weight(FontWeight.BOLD).size(24).build();
            HBox row = new HBox();
            row.getStyleClass().add("report-row");
            for (ReportEntityType entityType : ReportEntityType.values()) {
                VBox card = getReportCard(group, engagementType, entityType);
                row.getChildren().add(card);
            }
            VBox.setMargin(title, new Insets(24, 0, 12, 0));
            reports.getChildren().addAll(title, row);
        }
    }

    private HBox getAverageMonthlyContentPublicationsCard(ReportEntityType entityType) {
        FontIcon icon = new FontIcon(entityType.getIconName());
        Text entity = TextBuilder.of(entityType.getDisplay()).color("#4e4e4e").size(24).weight(FontWeight.BOLD).build();
        Text count = TextBuilder.of(String.valueOf(getAverageMonthlyContentPublications(entityType))).color("#ef6179").size(24).weight(FontWeight.BOLD).build();
        HBox card = new HBox(icon, entity, count);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(12);
        card.setPrefWidth(340);
        card.setMinWidth(340);
        card.getStyleClass().addAll("report-card", "shadow");
        VBox.setMargin(card, new Insets(24, 0, 24, 0));
        return card;
    }

    private double getAverageMonthlyContentPublications(ReportEntityType entityType) {
        int sum = 0, months = 0;
        Group group = Session.getGlobalSession().getGroup();
        LocalDateTime creation = group.getCreation().toLocalDateTime(), now = LocalDateTime.now();
        for (int year = creation.getYear(); year <= now.getYear(); year++) {
            int minMonth = year == creation.getYear() ? creation.getMonthValue() : 1;
            int maxMonth = year == now.getYear() ? now.getMonthValue() : 12;
            for (int month = minMonth; month <= maxMonth; month++) {
                sum += entityType.getContents(group, LocalDate.of(year, month, 1)).size();
                months++;
            }
        }
        return (double) sum / months;
    }
}