package com.p2p.files.models;

public class FileRange {

    private long offset;
    private long size;

    public FileRange() {
    }

    public FileRange(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
