package com.p2p.gui.pages;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends AbstractPanel {

    JButton buttonStartNewStream;
    JButton buttonJoinStream;
    JButton buttonExit;

    public MainPanel() throws HeadlessException {
        super();
        initializeAndAddElements();
    }

    protected void addElements() {
        addElement(buttonStartNewStream);
        addElement(buttonJoinStream);
        addElement(buttonExit);
    }

    @Override
    protected void initializeElements() {
        buttonStartNewStream = new JButton("Start New Stream");
        buttonJoinStream = new JButton("Join Stream");
        buttonExit = new JButton("Exit");
    }

    public JButton getButtonStartNewStream() {
        return buttonStartNewStream;
    }

    public void setButtonStartNewStream(JButton buttonStartNewStream) {
        this.buttonStartNewStream = buttonStartNewStream;
    }

    public JButton getButtonJoinStream() {
        return buttonJoinStream;
    }

    public void setButtonJoinStream(JButton buttonJoinStream) {
        this.buttonJoinStream = buttonJoinStream;
    }

    public JButton getButtonExit() {
        return buttonExit;
    }

    public void setButtonExit(JButton buttonExit) {
        this.buttonExit = buttonExit;
    }
}
