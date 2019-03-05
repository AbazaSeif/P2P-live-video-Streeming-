package com.p2p.gui.controller;

import com.p2p.files.models.UploadedFile;
import com.p2p.files.service.UploadedFileService;
import com.p2p.gui.pages.AbstractPanel;
import com.p2p.gui.pages.ListFilesPanel;
import com.p2p.model.BooleanStatus;
import com.p2p.videos.service.VideoPlayerWebServlet;
import com.p2p.videos.service.VideoStreamService;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

@Component
public class ListFilesPanelController extends AbstractPanelController {

    private static final Logger LOG = Logger.getLogger(ListFilesPanelController.class);

    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private VideoStreamService videoStreamService;
    @Autowired
    private VideoPlayerWebServlet videoPlayerWebServlet;
    @Autowired
    private MainPageController mainPageController;

    private ListFilesPanel listFilesPanel;


    public AbstractPanel getPanel() {
        this.listFilesPanel = new ListFilesPanel(uploadedFileService.getAllUploadedFiles(BooleanStatus.ACTIVE));
        this.listFilesPanel.getFilesTable().addMouseListener(getMouseAdapterLister());

        this.listFilesPanel.getRefreshTableDataButton().addActionListener(updateTableListener());
        return listFilesPanel;
    }

    public String getName() {
        return "List files panel";
    }

    public MouseAdapter getMouseAdapterLister() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    UploadedFile file = IterableUtils.get(listFilesPanel.getUploadedFiles(), row);
                    Map<String, Object> requestParameters = new HashMap<>();
                    requestParameters.put("name", RandomStringUtils.randomAlphabetic(3) + file.getFileName());
                    requestParameters.put("hash", file.getFileHash());
                    videoPlayerWebServlet.createAndStartVideoStream(requestParameters);
                }

            }
        };
    }

    public ActionListener updateTableListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                LOG.error(listFilesPanel.getFilesTable().getModel().getRowCount());
//                listFilesPanel.updateTableModel(uploadedFileService.getAllUploadedFiles(BooleanStatus.ACTIVE));
//                listFilesPanel.getFilesTable().repaint();
                SwingUtilities.invokeLater(()->{
                    LOG.error(listFilesPanel.getRefreshTableDataButton().getText());
                    listFilesPanel.getRefreshTableDataButton().setText("Clicked");
                    listFilesPanel.revalidate();
                    listFilesPanel.repaint();
                    LOG.error(listFilesPanel.getRefreshTableDataButton().getText());
                    mainPageController.getMainPage().revalidate();

                });
            }
        };
    }
}
