package com.p2p.gui.pages;

import com.p2p.files.models.UploadedFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListFilesPanel extends AbstractPanel {

    private static final Logger LOG = Logger.getLogger(ListFilesPanel.class);

    public static final String[] COLUMNS = {"File Name", "File Size"};
    public static final String[] COLUMNS_2 = {"File Name", "File Size 1"};
    public static final Class[] COLUMN_CLASSES = new Class[]{
            String.class, String.class
    };

    List<UploadedFile> uploadedFiles;
    private JTable filesTable;
    private JScrollPane scrollPane;
    private JLabel labelAvailableFiles;
    private DefaultTableModel tableModel;
    private JButton refreshTableDataButton;

    public ListFilesPanel(List<UploadedFile> uploadedFiles) {
        super();
        this.uploadedFiles = uploadedFiles;
        initializeAndAddElements();
    }

    protected void initializeElements() {
        tableModel = getTableModel(convertFilesToStringArray(uploadedFiles), COLUMNS_2);
        filesTable = new JTable(tableModel);
        labelAvailableFiles = new JLabel("Available Files");
        scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.getViewport().add(filesTable);
        refreshTableDataButton = new JButton("Refresh table" +RandomStringUtils.randomAlphabetic(2));
    }

    @Override
    protected void addElements() {
        addElement(labelAvailableFiles);
        addElement(refreshTableDataButton);
        addElement(scrollPane);
//        addElement(filesTable);
    }

    public void updateTableModel(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;

        removeAll();
        LOG.error(CollectionUtils.size(uploadedFiles));
        LOG.error(tableModel.getRowCount());
        uploadedFiles.forEach(file -> {
            tableModel.addRow(convertFileToStringArray(file));
        });
        addElements();
        revalidate();
        repaint();

        LOG.error(tableModel.getTableModelListeners()[0]);
        LOG.error(filesTable);
        ((DefaultTableModel) filesTable.getModel()).fireTableDataChanged();
        filesTable.setModel(tableModel);
        tableModel.fireTableStructureChanged();
    }

    public DefaultTableModel getTableModel(String[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return COLUMN_CLASSES[columnIndex];
            }
        };
        return model;
    }

    private String[] convertFileToStringArray(UploadedFile uploadedFile) {
        return ArrayUtils.toArray(uploadedFile.getFileName(), Long.toString(uploadedFile.getFileSize()));
    }

    public String[][] convertFilesToStringArray(List<UploadedFile> uploadedFiles) {
        String[][] array = null;
        if (CollectionUtils.isNotEmpty(uploadedFiles)) {
            for (UploadedFile file : uploadedFiles) {
                array = ArrayUtils.add(array, convertFileToStringArray(file));
            }
        }
        return array;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public JTable getFilesTable() {
        return filesTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getRefreshTableDataButton() {
        return refreshTableDataButton;
    }
}
