package com.p2p.gui.pages;

import com.p2p.peers.model.Peer;
import com.p2p.videos.model.VideoStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableStreamsPanel extends AbstractPanel {

    private static final String[] COLUMNS = {"Stream Name", "Peer"};
    private static final Class[] COLUMN_CLASSES = new Class[]{
            String.class, String.class
    };

    private JTable availableStreamsTable;
    private JLabel availableStreamsLabel;
    private JScrollPane scrollPane;

    private Map<Peer, List<VideoStream>> availableStreamsMap;

    public AvailableStreamsPanel(Map<Peer, List<VideoStream>> availableStreamMap) {
        super();
        this.availableStreamsMap = availableStreamMap;
        initializeAndAddElements();
    }

    @Override
    protected void initializeElements() {
        availableStreamsTable = new JTable(getTableModel(convertStreamsToStringArray(availableStreamsMap), COLUMNS));
        availableStreamsLabel = new JLabel("Available Streams");
        scrollPane = new JScrollPane(availableStreamsTable);
    }

    @Override
    protected void addElements() {
        addElement(availableStreamsLabel);
        addElement(scrollPane);
    }

    private String[] convertStreamToStringArray(Peer peer, VideoStream videoStream) {

        return new String[]{videoStream.getVideoStreamName(), Long.toString(videoStream.getDuration()), peer.getName()};
    }

    private Object[][] convertStreamsToStringArray(Map<Peer, List<VideoStream>> availableStreamsMap) {
        List<String[]> arrayList = new ArrayList<>();
        availableStreamsMap.forEach((peer, videoStreams) -> {
            videoStreams.forEach(stream -> {
                arrayList.add(convertStreamToStringArray(peer, stream));
            });
        });
        String[][] array = null;
        for (String[] item : arrayList) {
            array = ArrayUtils.add(array, item);
        }
        return array;
    }

    private DefaultTableModel getTableModel(Object[][] data, String[] columns) {
        return new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return COLUMN_CLASSES[columnIndex];
            }
        };
    }
}
