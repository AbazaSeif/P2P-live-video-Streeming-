package com.p2p.gui.pages;


import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {

    private String title;
    private CardLayout cardLayout;
    private JPanel container;

    private static final int SIZE_WIDTH = 600;
    private static final int SIZE_HEIGHT = 600;

    public MainPage() throws HeadlessException {
        cardLayout = new CardLayout();
        setTitle(title);
        setVisible(true);
        setSize(SIZE_WIDTH, SIZE_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = new JPanel(cardLayout);
        add(container);
    }

    public static MainPage buildPage(String title) {
        MainPage page = new MainPage();
        return page;
    }

    public void addPanel(Component panel, String name) {
        container.add(panel, name);
    }

    public void replacePanel(Component panel1, String name) {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(container, name);
            revalidate();
            repaint();
        });

    }
}
