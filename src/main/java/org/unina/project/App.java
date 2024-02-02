package org.unina.project;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.unina.project.config.Config;
import org.unina.project.config.defaults.ConfigDatabaseSettings;
import org.unina.project.database.operations.Database;
import org.unina.project.database.provider.DatabaseProvider;
import org.unina.project.social.entities.posts.Post;
import org.unina.project.social.managers.extractors.notifications.PostNotificationsResultSetExtractor;
import org.unina.project.social.managers.impl.*;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.Scene;

import java.util.Objects;

@Getter
public class App extends Application {
    @Getter
    private static App instance;
    // Configurazioni dell'app
    private final double width = 1280, height = 720;
    private final String name = "UninaSocialGroup";
    private final Image icon = new Image(Objects.requireNonNull(App.class.getClassLoader().getResourceAsStream("imgs/icon.png")));
    // Database e managers
    private final Database database;
    private final UsersDAO users;
    private final GroupsDAO groups;
    private final PostsDAO posts;
    private final CommentsDAO comments;
    private final TagsDAO tags;
    private final NotificationsDAO<Post> postNotifications;
    // JavaFX Stage
    private @Nullable Stage stage;

    public App() {
        instance = this;
        ConfigDatabaseSettings settings = Config.load("database", ConfigDatabaseSettings.class).config();
        this.database = DatabaseProvider.getProvider().newDatabase(settings);
        this.users = new UsersDAO(database);
        this.groups = new GroupsDAO(database);
        this.comments = new CommentsDAO(database);
        this.posts = new PostsDAO(database);
        this.tags = new TagsDAO(database);
        this.postNotifications = new NotificationsDAO<>(database, new PostNotificationsResultSetExtractor(), "notifica", "fk_post");
    }

    public static void launchApplication(String... args) {
        App.launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.getIcons().add(icon);
        setScene(SceneType.LOGIN.getScene());
    }

    public void setScene(Scene scene) {
        if (this.stage == null)
            throw new RuntimeException("Lo stage dell'applicazione non è stato ancora inizializzato.");
        scene.show(this.stage);
    }
}
