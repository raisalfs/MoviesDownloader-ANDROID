package com.rafslab.movie.dl.database;

import android.provider.BaseColumns;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */
public class DatabaseContract {

    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CAST_LIST = "cast_list";
        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_SECOND_TITLE = "second_title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_COVER_LIST = "cover_list";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_CONTENT_RATING = "content_rating";
        public static final String COLUMN_SEASON_COUNT = "season_count";
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_DOWNLOAD_LIST = "download_list";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORIES = "categories";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_SUBTITLE = "subtitle";
        public static final String COLUMN_SUBTITLE_REGION = "subtitle_region";

        public static final String COLUMN_EPISODE_COUNT = "episode_count";

        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DOWNLOADABLE = "downloadable";
        public static final String COLUMN_RELEASE = "release";

        public static final String COLUMN_IMBD = "imdb";
        public static final String COLUMN_PINNED = "pinned";
    }
    public static final class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_ID = "history_id";
        public static final String COLUMN_NAME = "history_name";
        public static final String COLUMN_DATE = "history_date";
        public static final String COLUMN_HOURS = "history_hours";
        public static final String COLUMN_CAST_LIST = "history_cast_list";

        public static final String COLUMN_SECOND_TITLE = "history_second_title";
        public static final String COLUMN_DESCRIPTION = "history_description";
        public static final String COLUMN_CATEGORIES = "history_categories";

        public static final String COLUMN_POSTER = "history_poster";
        public static final String COLUMN_COVER = "history_cover";

        public static final String COLUMN_SUBTITLE = "history_subtitle";
        public static final String COLUMN_SUBTITLE_REGION = "history_subtitle_region";

        public static final String COLUMN_EPISODE_COUNT = "history_episode_count";

        public static final String COLUMN_DOWNLOAD_EPISODE = "history_download_episode";
        public static final String COLUMN_DOWNLOAD_BATCH = "history_download_batch";

        public static final String COLUMN_TRAILER = "history_trailer";
        public static final String COLUMN_RATING = "history_rating";
        public static final String COLUMN_DURATION = "history_duration";
        public static final String COLUMN_TYPE = "history_type";
        public static final String COLUMN_DOWNLOADABLE = "history_downloadable";
        public static final String COLUMN_RELEASE = "history_release";


        public static final String COLUMN_IMBD = "history_imdb";
        public static final String COLUMN_PINNED = "history_pinned";

        public static final String COLUMN_DOWNLOADED = "history_downloaded";
        public static final String COLUMN_DOWNLOADED_SERVER = "history_downloaded_server";
        public static final String COLUMN_DOWNLOADED_RESOLUTION = "history_downloaded_resolution";
    }
}
