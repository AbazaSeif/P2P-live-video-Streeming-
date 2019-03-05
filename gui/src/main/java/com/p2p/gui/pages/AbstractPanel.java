package com.p2p.gui.pages;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPanel extends JPanel {

    private static final Logger LOG=Logger.getLogger(AbstractPanel.class);

    private List<Component> components = new ArrayList<>();

    public AbstractPanel() {
        setLayout(getLayoutManager());
    }

    protected LayoutManager getLayoutManager() {
        return new GridLayout(0,1);
    }

    protected void initializeAndAddElements() {
        initializeElements();
        addElements();
        addElementsToPanel();
    }

    protected abstract void initializeElements();

    protected void addElementsToPanel() {

        if (CollectionUtils.isNotEmpty(components)) {
            components.forEach(this::add);
        }
    }

    protected abstract void addElements();

    protected void addElement(Component component) {
        components.add(component);
    }
}
