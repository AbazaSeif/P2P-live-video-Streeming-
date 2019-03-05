package com.p2p.files.dao;

public class FileDBConstants {

    public static final String TABLE_FILES = "files";
    public static final String COLUMN_FILES_ID = "file_id";
    public static final String COLUMN_FILES_NAME = "file_name";
    public static final String COLUMN_FILES_PATH = "file_path";
    public static final String COLUMN_FILES_SIZE = "file_size";
    public static final String COLUMN_FILES_MD5_HASH = "file_hash";
    public static final String COLUMN_FILES_STATUS = "status";

    public static final String TABLE_FILE_CHUNKS = "file_chunks";
    public static final String COLUMN_FILE_CHUNKS_ID = "file_chunk_id";
    public static final String COLUMN_FILE_CHUNKS_FILE_ID = "file_id";
    public static final String COLUMN_FILE_CHUNKS_OFFSET = "file_offset";
    public static final String COLUMN_FILE_CHUNKS_LENGTH = "length";
    public static final String COLUMN_FILE_CHUNKS_TYPE = "chunk_type";
    public static final String COLUMN_FILE_CHUNKS_PATH = "chunk_path";
    public static final String COLUMN_FILE_CHUNKS_STATUS = "status";
    public static final String COLUMN_FILE_CHUNKS_MD5_HASH = "file_chunk_hash";
}
