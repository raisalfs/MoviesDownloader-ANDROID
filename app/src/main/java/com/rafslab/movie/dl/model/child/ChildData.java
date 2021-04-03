package com.rafslab.movie.dl.model.child;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ChildData implements Serializable {
    private int id;
    private String title;
    private String secondTitle;
    private int status;
    private List<CoverArray> coverArrays;
    private String description;
    private String categories;
    private String tags;
    private String country;
    private String contentRating;
    private int epsCount;
    private int seasonCount;
    private String progress;
    private String poster;
    private String subtitle;
    private List<Download> downloads;
    private String trailer;
    private double rating;
    private String duration;
    private String type;
    private String release;
    private List<Cast> castList;
    private boolean pinned;
    private String subtitleRegion;
    private String movieDetails;
    private String castDetails;
    private Cast castData;
    private String castString;
    private List<DownloadedDatabase> downloadedDatabases;
    private int idDownload;
    private int itemCount;
    private int downloadable;

//    private List<DownloadItems> episodeItems;
//    private List<DownloadItems> batchItems;
    public ChildData(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public int getEpsCount() {
        return epsCount;
    }

    public void setEpsCount(int epsCount) {
        this.epsCount = epsCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Download> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Download> downloads) {
        this.downloads = downloads;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public List<Cast> getCastList() {
        return castList;
    }

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public List<CoverArray> getCoverArrays() {
        return coverArrays;
    }

    public void setCoverArrays(List<CoverArray> coverArrays) {
        this.coverArrays = coverArrays;
    }

    public String getSubtitleRegion() {
        return subtitleRegion;
    }

    public void setSubtitleRegion(String subtitleRegion) {
        this.subtitleRegion = subtitleRegion;
    }

    public Cast getCastData() {
        return castData;
    }

    public void setCastData(Cast castData) {
        this.castData = castData;
    }

    public String getCastString() {
        return castString;
    }

    public void setCastString(String castString) {
        this.castString = castString;
    }

    public List<DownloadedDatabase> getDownloadedDatabases() {
        return downloadedDatabases;
    }

    public void setDownloadedDatabases(List<DownloadedDatabase> downloadedDatabases) {
        this.downloadedDatabases = downloadedDatabases;
    }

    public int getIdDownload() {
        return idDownload;
    }

    public void setIdDownload(int idDownload) {
        this.idDownload = idDownload;
    }

    public String getMovieDetails() {
        return movieDetails;
    }

    public void setMovieDetails(String movieDetails) {
        this.movieDetails = movieDetails;
    }

    public String getCastDetails() {
        return castDetails;
    }

    public void setCastDetails(String castDetails) {
        this.castDetails = castDetails;
    }

    public int isItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(int downloadable) {
        this.downloadable = downloadable;
    }
}
