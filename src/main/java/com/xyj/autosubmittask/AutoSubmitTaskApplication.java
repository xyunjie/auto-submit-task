package com.xyj.autosubmittask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author xuyunjie
 */
public class AutoSubmitTaskApplication extends Application {


    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        // 使用Spring注入控制器

        Parent root = loader.load();
        primaryStage.setTitle("日报日报！");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
