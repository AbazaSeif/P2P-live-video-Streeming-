package com.p2p.guifx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.springframework.stereotype.Component;

@Component
public class SceneController {

    private Scene scene;

    public void changeNode(Parent node) {
        scene.setRoot(node);
    }

    public Scene createScene(Parent node) {
        scene = new Scene(node);
        return scene;
    }
}
