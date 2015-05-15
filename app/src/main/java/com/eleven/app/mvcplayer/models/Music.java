package com.eleven.app.mvcplayer.models;

/**
 * Created by eleven on 15/5/15.
 */
public class Music {
    // 音乐id
    private int mMusicId;
    // 路径
    private String mFileName;
    // 名字
    private String mMusicName;
    // 时间
    private int mMusicDuration;
    // Artist
    private String mMusicArtist;
    // Album
    private String mMusicAlbum;
    // year
    private String mMusicYear;
    // file type
    private String mFileType;
    // fize size
    private String mFileSize;
    // path
    private String mFilePath;

    public int getMusicId() {
        return mMusicId;
    }

    public void setMusicId(int musicId) {
        mMusicId = musicId;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getMusicName() {
        if (mMusicName == null) {
            return "Unknown";
        }
        return mMusicName;
    }

    public void setMusicName(String musicName) {
        mMusicName = musicName;
    }

    public int getMusicDuration() {
        return mMusicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        mMusicDuration = musicDuration;
    }

    public String getMusicArtist() {
        if (mMusicArtist == null) {
            return "Unknown";
        }
        return mMusicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        mMusicArtist = musicArtist;
    }

    public String getMusicAlbum() {
        if (mMusicAlbum == null) {
            return "Unknown";
        }
        return mMusicAlbum;
    }

    public void setMusicAlbum(String musicAlbum) {
        mMusicAlbum = musicAlbum;
    }

    public String getMusicYear() {
        if (mMusicYear == null) {
            return "Unknown";
        }
        return mMusicYear;
    }

    public void setMusicYear(String musicYear) {
        mMusicYear = musicYear;
    }

    public String getFileType() {
        if (mFileType == null) {
            return  "Unknown";
        }
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = fileType;
    }

    public String getFileSize() {
        if (mFileSize == null) {
            return "Unknown";
        }
        return mFileSize;
    }

    public void setFileSize(String fileSize) {
        mFileSize = fileSize;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }
}
