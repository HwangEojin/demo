package com.baseline.dto;

public class FileVO {
    private int fileSeq;
    private int boardSeq;
    private String originalFileName;
    private String savedFileName;

    public FileVO() {}

    public FileVO(int boardSeq, String originalFileName, String savedFileName) {
        this.boardSeq = boardSeq;
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
    }

    public int getFileSeq() {
        return fileSeq;
    }
    public void setFileSeq(int fileSeq) {
        this.fileSeq = fileSeq;
    }
    public int getBoardSeq() {
        return boardSeq;
    }
    public void setBoardSeq(int boardSeq) {
        this.boardSeq = boardSeq;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public String getSavedFileName() {
        return savedFileName;
    }
    public void setSavedFileName(String savedFileName) {
        this.savedFileName = savedFileName;
    }
}