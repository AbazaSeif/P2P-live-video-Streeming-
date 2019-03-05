package com.p2p.gui.controller;

import com.p2p.gui.pages.MainPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Component
public class MainPageController {

    @Autowired
    private MainPanelController mainPanelController;
    @Autowired
    private ListFilesPanelController listFilesPanelController;

    private MainPage mainPage;

    public MainPage getMainPage() {
        return mainPage;
    }


}
