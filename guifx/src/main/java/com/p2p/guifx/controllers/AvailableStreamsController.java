package com.p2p.guifx.controllers;

import com.p2p.exceptions.CoreException;
import com.p2p.files.models.UploadedFile;
import com.p2p.files.service.UploadedFileService;
import com.p2p.guifx.SceneController;
import com.p2p.guifx.loader.FXMLLoaderService;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.service.VideoPlayerWebServlet;
import com.p2p.videos.service.VideoStreamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
@Component
public class AvailableStreamsController implements Initializable {

    private static final Logger LOG = Logger.getLogger(AvailableStreamsController.class);


    private enum TableColumn {
        PEER_IP("Peer IP"),
        STREAM_NANME("Stream Name"),
        STREAM_STARTED("Started At"),
        DURATION("Duration");

        private String name;

        TableColumn(String name) {
            this.name = name;
        }
    }

    @Autowired
    private FXMLLoaderService fxmlLoaderService;
    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private VideoStreamService videoStreamService;
    @Autowired
    private VideoPlayerWebServlet videoPlayerWebServlet;
    @Autowired
    private MainPageFXController mainPageFXController;
    @Autowired
    private SceneController sceneController;


    @FXML
    public Button goToMainPageButton;
    @FXML
    public javafx.scene.control.TableColumn peerIp;
    @FXML
    public TableView tableView;
    @FXML
    public javafx.scene.control.TableColumn streamDuration;
    @FXML
    public javafx.scene.control.TableColumn startStreamButton;
    @FXML
    public javafx.scene.control.TableColumn streamName;
    @FXML
    public javafx.scene.control.TableColumn streamStartedAt;
    @FXML
    public javafx.scene.control.TableColumn actionButton;
    @FXML
    public Button joinStreamButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peerIp.setCellValueFactory(getValue(TableColumn.PEER_IP));
        streamDuration.setCellValueFactory(getValue(TableColumn.DURATION));
        streamName.setCellValueFactory(getValue(TableColumn.STREAM_NANME));
        streamStartedAt.setCellValueFactory(getValue(TableColumn.STREAM_STARTED));
        actionButton.setCellValueFactory(getValue(TableColumn.STREAM_NANME));
        actionButton.setCellFactory(getActionColumnCellFactory());
        tableView.getItems().setAll(getVideoStreams());
    }

    private Callback<javafx.scene.control.TableColumn.CellDataFeatures<Map.Entry<Peer, VideoStream>, String>, ObservableValue<String>> getValue(
            TableColumn tableColumn) {
        switch (tableColumn) {
            case PEER_IP:
                return param -> new SimpleStringProperty(param.getValue().getKey().getIp());
            case DURATION:
                return param -> new SimpleStringProperty(Long.toString(param.getValue().getValue().getDuration()));
            case STREAM_NANME:
                return param -> new SimpleStringProperty(param.getValue().getValue().getVideoStreamName());
            case STREAM_STARTED:
                return param -> new SimpleStringProperty(param.getValue().getValue().getCreatedTime().toString());
            default:
                throw new CoreException.NotValidException("Unknown value " + tableColumn);
        }

    }

    public Parent getParent() throws IOException {
        return fxmlLoaderService.load("availablestreams.fxml").load();
    }

    @FXML
    public void reloadStreams(ActionEvent actionEvent) {
        tableView.getItems().setAll(getVideoStreams());
    }

    private List<Map.Entry<Peer, VideoStream>> getVideoStreams() {
        Map<Peer, List<VideoStream>> videoStreamMap =
                videoStreamService.getStreamsFromNetwork(null, BooleanStatus.ACTIVE, BooleanStatus.ACTIVE, null);
        List<Map.Entry<Peer, VideoStream>> streamList = new ArrayList<>();
        videoStreamMap.forEach((peer, peerStreams) -> {
            peerStreams.forEach(stream -> {
                streamList.add(new AbstractMap.SimpleEntry<>(peer, stream));
            });

        });
        return streamList;
    }

    private Callback<javafx.scene.control.TableColumn, TableCell<Map.Entry<Peer, VideoStream>, String>> getActionColumnCellFactory
            () {
        return new Callback<javafx.scene.control.TableColumn, TableCell<Map.Entry<Peer, VideoStream>, String>>() {
            @Override
            public TableCell call(final javafx.scene.control.TableColumn param) {
                final TableCell<Map.Entry<Peer, VideoStream>, String> cell =
                        new TableCell<Map.Entry<Peer, VideoStream>, String>() {

                            final Button btn = new Button("Start Stream");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Map.Entry<Peer, VideoStream> peerVideoStreamEntry =
                                                getTableView().getItems().get(getIndex());
                                        Map<String, Object> requestParameters = new HashMap<>();
                                        requestParameters.put("file_hash",
                                                peerVideoStreamEntry.getValue().getUploadedFile().getFileHash());
                                        requestParameters.put("ip", peerVideoStreamEntry.getKey().getIp());
                                        requestParameters.put("port", peerVideoStreamEntry.getKey().getPort());
                                        requestParameters.put("status", BooleanStatus.ACTIVE.name());
                                        requestParameters.put("output", FilenameUtils
                                                .concat(uploadedFileService.getDownloadDirectory().getAbsolutePath(),
                                                        RandomStringUtils.randomAlphabetic(5) + ".part"));
                                        videoPlayerWebServlet.joinStream(requestParameters);
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

    @FXML
    public void goToMainPageButton(ActionEvent actionEvent) throws IOException {
        sceneController.changeNode(mainPageFXController.getParent());
    }

}
