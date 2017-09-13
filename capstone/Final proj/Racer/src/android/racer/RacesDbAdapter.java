package android.racer;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RacesDbAdapter {
//////////////////////////////////////////////
	public static final String KEY_CAR1 = "player1";
	public static final String KEY_CAR2 = "player2";
	public static final String KEY_CAR3 = "player3";
	public static final String KEY_CAR4 = "player4";
    public static final String KEY_RACEID = "_id";

    private static final String TAG = "TopTenDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE racer (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "player1 INTEGER NOT NULL, "
                    + "player2 INTEGER NOT NULL, "
                    + "player3 INTEGER NOT NULL, "
                    + "player4 INTEGER NOT NULL, "
                    + "name TEXT);";

    public static final String DATABASE_NAME = "racer";
    private static final String DATABASE_TABLE = "topten";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS racer");
            onCreate(db);
        }
    }
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public RacesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the topten database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RacesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new clicks record using the number provided. If the row is
     * successfully created return true, otherwise return false
     * 
     * @param currCount number of clicks to store
     * @return true if successful, false if not.
     */
    public long createRow(BigInteger currCount) {
    	GregorianCalendar gCal = new GregorianCalendar();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CAR1, gCal.get(Calendar.DAY_OF_MONTH));
        initialValues.put(KEY_MONTH, gCal.get(Calendar.MONTH)+1);
        initialValues.put(KEY_YEAR, gCal.get(Calendar.YEAR));
        initialValues.put(KEY_TIME, gCal.get(Calendar.HOUR_OF_DAY) * 3600000
        							+ gCal.get(Calendar.MINUTE) * 60000
        							+ gCal.get(Calendar.SECOND) * 1000
        							+ gCal.get(Calendar.MILLISECOND));
        initialValues.put(KEY_CLICKS, currCount.toString());
        
        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[] {"_id", KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_TIME, KEY_CLICKS},
        		KEY_CLICKS + "=" + currCount.toString(), null, null, null, null);
        long ret;
        if (mCursor == null || !mCursor.moveToFirst())
        	ret = mDb.insert(DATABASE_TABLE, null, initialValues);
        else
            ret = mCursor.getLong(mCursor.getColumnIndex("_id"));
        //mCursor.close();
        return ret;
    }

    /**
     * Delete the clicks with the given time
     * 
     * @param time time to use to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRow(long rowID) {
        return mDb.delete(DATABASE_TABLE, "_id=" + rowID, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAll() {
    	return this.fetchTopTenList(LIST_ALL);
    }

    /**
     * Return a Cursor positioned at the row that matches the given time
     * 
     * @param time tie of record to retrieve
     * @return Cursor positioned to matching record, if found
     * @throws SQLException if time record could not be found/retrieved
     */
    public Cursor fetchNote(long time) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_TIME,
                    KEY_CLICKS}, KEY_TIME + "=" + time, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public static final int LIST_ALL = 0;
    public static final int LIST_DAY = 1;
    public static final int LIST_WEEK = 2;
    public static final int LIST_MONTH = 3;
    public static final int LIST_YEAR = 4;

    public Cursor fetchTopTenList(int type) {
//    	long endRange = System.currentTimeMillis();
//    	long startRange = 0; // default LIST_ALL
//    	long day = 1 * 24 * 3600 * 1000;
    	String wanted = "_id";
    	switch (type) {
			case LIST_DAY:
				wanted = KEY_DAY;
				break;
			case LIST_WEEK:
				wanted = KEY_DAY;
				break;
			case LIST_MONTH:
				wanted = KEY_MONTH;
				break;
			case LIST_YEAR:
				wanted = KEY_YEAR;
				break;
    	}
    	Cursor mCursor;
    	
    	//In order to get the required data, we need to make a left join here.
    	//The way this works is we first get only the max number of clicks in each wanted group.
    	//Next, we use that information and join it to the info we get from the entire array.
    	//In doing so, BOTH the clicks and wanted field have to match.
    	mCursor = mDb.rawQuery("SELECT * " // Quick-and-dirty way to get things to work.
    						 + "FROM " + "(SELECT "+wanted+", MAX(clicks) AS max "
    						 		   + "FROM topten "
    						 		   + "GROUP by "+wanted+")" + " AS top " // Get our wanted info, use "top" to access
							 + "LEFT OUTER JOIN (SELECT * FROM topten) AS found " //We left join to entire database (Size will be same as wanted set.)
							 + "ON top."+wanted+"=found."+wanted+" AND top.max=found.clicks " // BOTH fields must match, here.
							 + "ORDER BY clicks", null); // And order by clicks.
    	
        if (mCursor != null) {
            mCursor.moveToFirst(); // Ensure that the cursor is on the first row of our data
        }
        return mCursor;
    }

//    /**
//     * Update the note using the details provided. The note to be updated is
//     * specified using the rowId, and it is altered to use the title and body
//     * values passed in
//     * 
//     * @param rowId id of note to update
//     * @param title value to set note title to
//     * @param body value to set note body to
//     * @return true if the note was successfully updated, false otherwise
//     */
//    public boolean updateNote(long rowId, String title, String body) {
//        ContentValues args = new ContentValues();
//        args.put(KEY_TITLE, title);
//        args.put(KEY_BODY, body);
//
//        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
//    }
}
