package com.p2p.guifx.controllers;

import com.p2p.guifx.SceneController;
import com.p2p.guifx.loader.FXMLLoaderService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainPageFXController {

    @Autowired
    private AvailableFilesController availableFilesController;
    @Autowired
    private AvailableStreamsController availableStreamsController;

    @Autowired
    private SceneController sceneController;
    @Autowired
    private FXMLLoaderService fxmlLoaderService;

    private static final Logger LOG = Logger.getLogger(MainPageFXController.class);

    @FXML
    protected void handleStartStreamButton(ActionEvent event) throws IOException {
        sceneController.changeNode(availableFilesController.getParent());
    }

    @FXML
    protected void handleJoinStreamButton(ActionEvent event) throws IOException {
        sceneController.changeNode(availableStreamsController.getParent());
    }

    public Parent getParent() throws IOException {
        return fxmlLoaderService.load("mainpage.fxml").load();

    }
}
