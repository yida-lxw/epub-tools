package com.yida.epub.bean;

public class RenameCoverImageResult {
    private String sourceCoverImageFilePath;
    private String sourceCoverImageFileName;
    private String targetCoverImageFilePath;
    private String targetCoverImageFileName;
    private boolean renameSuccess;

    public RenameCoverImageResult(String sourceCoverImageFilePath, String sourceCoverImageFileName, String targetCoverImageFilePath, String targetCoverImageFileName, boolean renameSuccess) {
        this.sourceCoverImageFilePath = sourceCoverImageFilePath;
        this.sourceCoverImageFileName = sourceCoverImageFileName;
        this.targetCoverImageFilePath = targetCoverImageFilePath;
        this.targetCoverImageFileName = targetCoverImageFileName;
        this.renameSuccess = renameSuccess;
    }

    public String getSourceCoverImageFilePath() {
        return sourceCoverImageFilePath;
    }

    public void setSourceCoverImageFilePath(String sourceCoverImageFilePath) {
        this.sourceCoverImageFilePath = sourceCoverImageFilePath;
    }

    public String getSourceCoverImageFileName() {
        return sourceCoverImageFileName;
    }

    public void setSourceCoverImageFileName(String sourceCoverImageFileName) {
        this.sourceCoverImageFileName = sourceCoverImageFileName;
    }

    public String getTargetCoverImageFilePath() {
        return targetCoverImageFilePath;
    }

    public void setTargetCoverImageFilePath(String targetCoverImageFilePath) {
        this.targetCoverImageFilePath = targetCoverImageFilePath;
    }

    public String getTargetCoverImageFileName() {
        return targetCoverImageFileName;
    }

    public void setTargetCoverImageFileName(String targetCoverImageFileName) {
        this.targetCoverImageFileName = targetCoverImageFileName;
    }

    public boolean isRenameSuccess() {
        return renameSuccess;
    }

    public void setRenameSuccess(boolean renameSuccess) {
        this.renameSuccess = renameSuccess;
    }
}
