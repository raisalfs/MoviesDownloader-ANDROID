package com.rafslab.movie.dl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.child.ChildData;
import com.rafslab.movie.dl.model.child.DownloadedDatabase;

import java.util.ArrayList;
import java.util.List;

public class DownloadedHistoryHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "history.db";

    private static final int DATABASE_VERSION = 1;

    public static final String DB_TAG = "History";

    private SQLiteOpenHelper dbhandler;
    private SQLiteDatabase db;

    public DownloadedHistoryHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void open(){
        Log.i(DB_TAG,"Database Opened");
        db = dbhandler.getWritableDatabase();
    }
    public void close(){
        Log.i(DB_TAG,"Database Closed");
        dbhandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + DatabaseContract.HistoryEntry.TABLE_NAME + " (" +
                DatabaseContract.HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.HistoryEntry.COLUMN_ID + " INTEGER, " +
                DatabaseContract.HistoryEntry.COLUMN_NAME + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DATE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_HOURS + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_POSTER + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_COVER + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_TRAILER + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_RATING + " REAL, " +
                DatabaseContract.HistoryEntry.COLUMN_DURATION + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_TYPE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_RELEASE + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_IMBD + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_PINNED + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER + " TEXT, " +
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION + " TEXT" +
                "); ";
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.HistoryEntry.TABLE_NAME);
        onCreate(db);
    }

//    public void addHistoryDownloaded(ChildData data, Episode episode, Batch batch, String date, String hours, DownloadedDatabase downloaded, String downloadedServer, String downloadedResolution){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Gson gson = new Gson();
//        ContentValues values = new ContentValues();
//        values.put(DatabaseContract.HistoryEntry.COLUMN_ID, data.getId());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_NAME, data.getTitle());
//        String bbb = gson.toJson(data.getCastList());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_CAST_LIST, bbb);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DATE, date);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_HOURS, hours);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE, data.getSecondTitle());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION, data.getDescription());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_CATEGORIES, data.getCategories());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_POSTER, data.getPoster());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_COVER, data.getCover());
////        values.put(FavoritesContract.HistoryEntry.COLUMN_COVER_ARRAY_IMAGE, data.getCover()); /* Need POJO */
//        values.put(DatabaseContract.HistoryEntry.COLUMN_SUBTITLE, data.getSubtitle());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION, data.getSubtitleRegion());
//
////        values.put(DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT, episode.getCount());
////        String episodeValue = gson.toJson(episode.getDownloadEpisodeItems());
////        String batchValue = gson.toJson(batch.getDownloadBatch());
//
////        values.put(DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE, episodeValue);
////        values.put(DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH, batchValue);
//
//        values.put(DatabaseContract.HistoryEntry.COLUMN_TRAILER, data.getTrailer());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_RATING, data.getRating());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DURATION, data.getDuration());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_TYPE, data.getType());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_RELEASE, data.getRelease());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_IMBD, data.getImdb());
//        values.put(DatabaseContract.HistoryEntry.COLUMN_PINNED, data.isPinned());
//
//        String downloaded2Json = gson.toJson(downloaded);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED, downloaded2Json);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER, downloadedServer);
//        values.put(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION, downloadedResolution);
//
//        db.insert(DatabaseContract.HistoryEntry.TABLE_NAME,null, values);
//        db.close();
//    }
    public String getTitle(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION
        };
        String title = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                title = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_NAME));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return title;
    }
    public String getDateAdded(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION
        };
        String date = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                date = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DATE));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return date;
    }
    public String getHoursAdded(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION
        };
        String hours = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                hours = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_HOURS));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hours;
    }
    public int getDownloadPosition(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION
        };
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return count;
    }
    public List<DownloadedDatabase> getDownloaded(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION

        };
        List<DownloadedDatabase> downloaded = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                DownloadedDatabase downloadedDatabase = new DownloadedDatabase();
                downloadedDatabase.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED)));
                downloaded.add(downloadedDatabase);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return downloaded;
    }
    public String getDownloadedServer(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION

        };
        String downloaded = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                downloaded = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return downloaded;
    }
    public String getDownloadedResolution(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION

        };
        String downloaded = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                downloaded = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return downloaded;
    }
    public List<ChildData> getAllHistory(){
        String[] columns = {
                DatabaseContract.HistoryEntry._ID,
                DatabaseContract.HistoryEntry.COLUMN_ID,
                DatabaseContract.HistoryEntry.COLUMN_CAST_LIST,
                DatabaseContract.HistoryEntry.COLUMN_NAME,
                DatabaseContract.HistoryEntry.COLUMN_DATE,
                DatabaseContract.HistoryEntry.COLUMN_HOURS,
                DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION,
                DatabaseContract.HistoryEntry.COLUMN_CATEGORIES,
                DatabaseContract.HistoryEntry.COLUMN_POSTER,
                DatabaseContract.HistoryEntry.COLUMN_COVER,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE,
                DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH,
                DatabaseContract.HistoryEntry.COLUMN_TRAILER,
                DatabaseContract.HistoryEntry.COLUMN_RATING,
                DatabaseContract.HistoryEntry.COLUMN_DURATION,
                DatabaseContract.HistoryEntry.COLUMN_TYPE,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.HistoryEntry.COLUMN_RELEASE,
                DatabaseContract.HistoryEntry.COLUMN_IMBD,
                DatabaseContract.HistoryEntry.COLUMN_PINNED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_SERVER,
                DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED_RESOLUTION
        };
        String sortByOrder = DatabaseContract.HistoryEntry._ID + " DESC";
        List<ChildData> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DatabaseContract.HistoryEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                ChildData data = new ChildData();
//                Batch batch = new Batch();
//                Episode episode = new Episode();
                data.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_ID)));
                data.setCastString(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_CAST_LIST)));
                data.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_NAME)));
                data.setSecondTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_SECOND_TITLE)));
                data.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DESCRIPTION)));
                data.setCategories(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_CATEGORIES)));
                data.setPoster(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_POSTER)));
//                data.setCover(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_COVER)));
                data.setSubtitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_SUBTITLE)));
                data.setSubtitleRegion(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_SUBTITLE_REGION)));
                data.setTrailer(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_TRAILER)));
                data.setRating(cursor.getInt(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_RATING)));
                data.setDuration(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DURATION)));
                data.setType(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_TYPE)));
                data.setDownloadable(cursor.getInt(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADABLE)));
                data.setRelease(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_RELEASE)));
                data.setMovieDetails(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_IMBD)));
                data.setPinned(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_PINNED))));
                String casts = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_CAST_LIST));
                List<Cast> castList = new Gson().fromJson(casts, new TypeToken<List<Cast>>() {}.getType());
                data.setCastList(castList);
//                episode.setCount(cursor.getInt(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_EPISODE_COUNT)));
//                String episodeDownload = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_EPISODE));
//                String batchDownload = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOAD_BATCH));
//                List<DownloadItems> episodeItems = new Gson().fromJson(episodeDownload, new TypeToken<List<DownloadItems>>() {}.getType());
//                List<DownloadItems> batchItems = new Gson().fromJson(batchDownload, new TypeToken<List<DownloadItems>>() {}.getType());
//                episode.setDownloadEpisodeItems(episodeItems);
//                batch.setDownloadBatch(batchItems);
//                data.setEpisode(episode);
//                data.setBatch(batch);
                String downloadItems = cursor.getString(cursor.getColumnIndex(DatabaseContract.HistoryEntry.COLUMN_DOWNLOADED));
                DownloadedDatabase downloadedDatabase = new DownloadedDatabase();
                downloadedDatabase.setTitle(downloadItems);
//                List<DownloadedDatabase> downloadedDatabaseList = new Gson().fromJson(downloadItems, new TypeToken<List<DownloadItems>>() {}.getType());
                List<DownloadedDatabase> downloadedDatabaseList = new ArrayList<>();
                downloadedDatabaseList.add(downloadedDatabase);
                data.setDownloadedDatabases(downloadedDatabaseList);
                historyList.add(data);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historyList;
    }
    public void removeHistoryDownloaded(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseContract.HistoryEntry.TABLE_NAME, DatabaseContract.HistoryEntry.COLUMN_ID + "=" + id, null);
    }
    public void removeAllHistoryDownloaded(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseContract.HistoryEntry.TABLE_NAME, null, null);
    }
}
