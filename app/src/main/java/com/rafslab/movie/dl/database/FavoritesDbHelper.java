package com.rafslab.movie.dl.database;

import android.annotation.SuppressLint;
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
import com.rafslab.movie.dl.model.child.CoverArray;
import com.rafslab.movie.dl.model.child.Download;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite.db";

    private static final int DATABASE_VERSION = 1;

    public static final String DB_TAG = "FAVORITE";
    private static FavoritesDbHelper getInstance ;
    public static synchronized FavoritesDbHelper getHelper(Context context){
        if (getInstance == null) {
            getInstance = new FavoritesDbHelper(context);
        }
        return getInstance;
    }

    private SQLiteOpenHelper dbhandler;
    private SQLiteDatabase db;

    public FavoritesDbHelper(@Nullable Context context) {
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

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + DatabaseContract.FavoritesEntry.TABLE_NAME + " (" +
                DatabaseContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseContract.FavoritesEntry.COLUMN_ID + " INTEGER, " +
                DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_PROGRESS + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_TRAILER + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_RATING + " REAL NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_DURATION + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_IMBD + " TEXT NOT NULL, " +
                DatabaseContract.FavoritesEntry.COLUMN_PINNED + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
    public void addFavorites(ChildData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        String castList = gson.toJson(data.getCastList());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST, castList);
        values.put(DatabaseContract.FavoritesEntry.COLUMN_ID, data.getId());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_TITLE, data.getTitle());

        values.put(DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE, data.getSecondTitle());

        values.put(DatabaseContract.FavoritesEntry.COLUMN_STATUS, data.getStatus());
        String coverList = gson.toJson(data.getCoverArrays());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST, coverList);
        values.put(DatabaseContract.FavoritesEntry.COLUMN_COUNTRY, data.getCountry());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING, data.getContentRating());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT, data.getSeasonCount());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_PROGRESS, data.getProgress());
        String downloadList = gson.toJson(data.getDownloads());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST, downloadList);

        values.put(DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION, data.getDescription());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES, data.getCategories());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_POSTER, data.getPoster());
//        values.put(DatabaseContract.FavoritesEntry.COLUMN_COVER, data.getCover());
//        values.put(FavoritesContract.FavoritesEntry.COLUMN_COVER_ARRAY_IMAGE, data.getCover()); /* Need POJO */
        values.put(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE, data.getSubtitle());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION, data.getSubtitleRegion());

        values.put(DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT, data.getEpsCount());
//        String episodeValue = gson.toJson(episode.getDownloadEpisodeItems());
//        String batchValue = gson.toJson(batch.getDownloadBatch());

//        values.put(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_EPISODE, episodeValue);
//        values.put(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_BATCH, batchValue);

        values.put(DatabaseContract.FavoritesEntry.COLUMN_TRAILER, data.getTrailer());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_RATING, data.getRating());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_DURATION, data.getDuration());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_TYPE, data.getType());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE, data.getDownloadable());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_RELEASE, data.getRelease());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_IMBD, data.getMovieDetails());
        values.put(DatabaseContract.FavoritesEntry.COLUMN_PINNED, data.isPinned());

        db.insert(DatabaseContract.FavoritesEntry.TABLE_NAME,null, values);
        db.close();
    }
    public void deleteFavorites(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseContract.FavoritesEntry.TABLE_NAME, DatabaseContract.FavoritesEntry.COLUMN_ID + "=" + id, null);
    }
    public String getCastList(){
        String[] columns = {
                DatabaseContract.FavoritesEntry._ID,
                DatabaseContract.FavoritesEntry.COLUMN_ID,
                DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE,

                DatabaseContract.FavoritesEntry.COLUMN_STATUS,
                DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_COUNTRY,
                DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_PROGRESS,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST,

                DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION,
                DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES,
                DatabaseContract.FavoritesEntry.COLUMN_POSTER,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_TRAILER,
                DatabaseContract.FavoritesEntry.COLUMN_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_DURATION,
                DatabaseContract.FavoritesEntry.COLUMN_TYPE,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.FavoritesEntry.COLUMN_RELEASE,
                DatabaseContract.FavoritesEntry.COLUMN_IMBD,
                DatabaseContract.FavoritesEntry.COLUMN_PINNED
        };
        String favoriteList = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.FavoritesEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                favoriteList = cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
    public boolean isAvailable(){
        String[] columns = {
                DatabaseContract.FavoritesEntry._ID,
                DatabaseContract.FavoritesEntry.COLUMN_ID,
                DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_STATUS,
                DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_COUNTRY,
                DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_PROGRESS,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION,
                DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES,
                DatabaseContract.FavoritesEntry.COLUMN_POSTER,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_TRAILER,
                DatabaseContract.FavoritesEntry.COLUMN_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_DURATION,
                DatabaseContract.FavoritesEntry.COLUMN_TYPE,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.FavoritesEntry.COLUMN_RELEASE,
                DatabaseContract.FavoritesEntry.COLUMN_IMBD,
                DatabaseContract.FavoritesEntry.COLUMN_PINNED
        };
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query(DatabaseContract.FavoritesEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        return cursor.getCount() > 0;
    }
    public List<ChildData> getAllFavorites(){
        String[] columns = {
                DatabaseContract.FavoritesEntry._ID,
                DatabaseContract.FavoritesEntry.COLUMN_ID,
                DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE,
                DatabaseContract.FavoritesEntry.COLUMN_STATUS,
                DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_COUNTRY,
                DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_PROGRESS,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST,
                DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION,
                DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES,
                DatabaseContract.FavoritesEntry.COLUMN_POSTER,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE,
                DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION,
                DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT,
                DatabaseContract.FavoritesEntry.COLUMN_TRAILER,
                DatabaseContract.FavoritesEntry.COLUMN_RATING,
                DatabaseContract.FavoritesEntry.COLUMN_DURATION,
                DatabaseContract.FavoritesEntry.COLUMN_TYPE,
                DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE,
                DatabaseContract.FavoritesEntry.COLUMN_RELEASE,
                DatabaseContract.FavoritesEntry.COLUMN_IMBD,
                DatabaseContract.FavoritesEntry.COLUMN_PINNED
        };
        List<ChildData> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DatabaseContract.FavoritesEntry.TABLE_NAME,
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
                data.setId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_ID)));
                data.setCastString(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST)));
                data.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_TITLE)));
                data.setSecondTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_SECOND_TITLE)));

                data.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_STATUS)));
                String cover = cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_COVER_LIST));
                List<CoverArray> coverList = new Gson().fromJson(cover, new TypeToken<List<CoverArray>>() {}.getType());
                data.setCoverArrays(coverList);
                data.setCountry(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_COUNTRY)));
                data.setContentRating(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_CONTENT_RATING)));
                data.setSeasonCount(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_SEASON_COUNT)));
                data.setProgress(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_PROGRESS)));
                String download = cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOAD_LIST));
                List<Download> downloadList = new Gson().fromJson(download, new TypeToken<List<Download>>() {}.getType());
                data.setDownloads(downloadList);

                data.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_DESCRIPTION)));
                data.setCategories(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_CATEGORIES)));
                data.setPoster(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_POSTER)));
//                data.setCover(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_COVER)));
                data.setSubtitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE)));
                data.setSubtitleRegion(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_SUBTITLE_REGION)));
                data.setTrailer(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_TRAILER)));
                data.setRating(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_RATING)));
                data.setDuration(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_DURATION)));
                data.setType(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_TYPE)));
                data.setDownloadable(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_DOWNLOADABLE)));
                data.setRelease(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_RELEASE)));
                data.setMovieDetails(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_IMBD)));
                data.setPinned(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_PINNED))));
                String casts = cursor.getString(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_CAST_LIST));
                List<Cast> castList = new Gson().fromJson(casts, new TypeToken<List<Cast>>() {}.getType());
                data.setCastList(castList);
                data.setEpsCount(cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoritesEntry.COLUMN_EPISODE_COUNT)));
                favoriteList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}
