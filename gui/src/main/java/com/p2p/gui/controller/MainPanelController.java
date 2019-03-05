package com.p2p.gui.controller;

import com.p2p.gui.pages.MainPage;
import com.p2p.gui.pages.MainPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class MainPanelController {

    @Autowired
    private MainPageController mainPageController;
    @Autowired
    private ListFilesPanelController listFilesPanelController;

    private ActionListener getButtonListFilesActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainPage mainPage = mainPageController.getMainPage();
                mainPage.replacePanel(listFilesPanelController.getPanel(),listFilesPanelController.getName());
            }
        };
    }

    public MainPanel getMainPanel() {
        MainPanel mainPanel = new MainPanel();
        mainPanel.getButtonStartNewStream().addActionListener(getButtonListFilesActionListener());
        return mainPanel;
    }

    public String getName() {
        return "Main Panel";
    }

}
