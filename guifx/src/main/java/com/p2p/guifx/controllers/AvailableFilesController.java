package com.p2p.guifx.controllers;

import com.p2p.files.models.UploadedFile;
import com.p2p.files.service.UploadedFileService;
import com.p2p.guifx.SceneController;
import com.p2p.guifx.loader.FXMLLoaderService;
import com.p2p.model.BooleanStatus;
import com.p2p.videos.service.VideoPlayerWebServlet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
@Component
public class AvailableFilesController implements Initializable {

    private static final Logger LOG = Logger.getLogger(AvailableFilesController.class);

    @Autowired
    private FXMLLoaderService fxmlLoaderService;
    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private VideoPlayerWebServlet videoPlayerWebServlet;
    @Autowired
    private MainPageFXController mainPageFXController;
    @Autowired
    private SceneController sceneController;

    @FXML
    public TableColumn fileName;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn fileSize;
    @FXML
    public TableColumn startStreamButton;
    @FXML
    public Button joinStreamButton;
    @FXML

    public Button goToMainPageButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileName.setCellValueFactory(new PropertyValueFactory<UploadedFile, String>("fileName"));
        fileSize.setCellValueFactory(new PropertyValueFactory<UploadedFile, String>("fileSize"));
        startStreamButton.setCellValueFactory(new PropertyValueFactory<UploadedFile, String>("peerIp"));
        startStreamButton.setCellFactory(getActionColumnCellFactory());
        tableView.getItems().setAll(getFiles());
    }

    public Parent getParent() throws IOException {
        return fxmlLoaderService.load("availablefiles.fxml").load();
    }

    @FXML
    public void reloadFiles(ActionEvent actionEvent) {
        tableView.getItems().setAll(getFiles());
    }

    @FXML
    public void goToMainPageButton(ActionEvent actionEvent) throws IOException {
        sceneController.changeNode(mainPageFXController.getParent());
    }

    private List<UploadedFile> getFiles() {
        return uploadedFileService.getAllUploadedFiles(BooleanStatus.ACTIVE);
    }

    private Callback<TableColumn<UploadedFile, String>, TableCell<UploadedFile, String>> getActionColumnCellFactory() {
        return new Callback<TableColumn<UploadedFile, String>, TableCell<UploadedFile, String>>() {
            @Override
            public TableCell call(final TableColumn<UploadedFile, String> param) {
                final TableCell<UploadedFile, String> cell = new TableCell<UploadedFile, String>() {

                    final Button btn = new Button("Start Stream");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                UploadedFile uploadedFile = getTableView().getItems().get(getIndex());
                                Map<String, Object> requestParameters = new HashMap<>();
                                requestParameters.put("name",
                                        RandomStringUtils.randomAlphabetic(3) + uploadedFile.getFileName());
                                requestParameters.put("hash", uploadedFile.getFileHash());
                                videoPlayerWebServlet.createAndStartVideoStream(requestParameters);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
    }

}
