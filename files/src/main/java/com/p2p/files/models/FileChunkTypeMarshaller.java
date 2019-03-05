package com.p2p.files.models;

import com.p2p.model.BooleanStatus;

import javax.persistence.AttributeConverter;

public class FileChunkTypeMarshaller implements AttributeConverter<FileChunkType, String> {
    @Override
    public String convertToDatabaseColumn(FileChunkType fileChunkType) {
        return fileChunkType.name();
    }

    @Override
    public FileChunkType convertToEntityAttribute(String s) {
        return FileChunkType.valueOf(s);
    }
}
