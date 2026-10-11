package com.example.campusfix.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusfix.model.HelpContact;
import com.example.campusfix.model.Report;
import com.example.campusfix.model.StatusHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_fix.db";
    private static final int DATABASE_VERSION = 2;

    // Table Reports
    public static final String TABLE_REPORTS = "reports";
    public static final String COL_REPORT_ID = "id";
    public static final String COL_REPORT_TITLE = "title";
    public static final String COL_REPORT_DESC = "description";
    public static final String COL_REPORT_CATEGORY = "category";
    public static final String COL_REPORT_LOCATION = "location";
    public static final String COL_REPORT_PRIORITY = "priority";
    public static final String COL_REPORT_STATUS = "status";
    public static final String COL_REPORT_IMAGE_PATH = "image_path";
    public static final String COL_REPORT_CREATED_AT = "created_at";
    public static final String COL_REPORT_UPDATED_AT = "updated_at";
    public static final String COL_REPORT_IS_HAZARD = "is_hazard";
    public static final String COL_REPORT_CONTACT_EMAIL = "contact_email";
    public static final String COL_REPORT_IS_DEMO = "is_demo";

    // Table Status History
    public static final String TABLE_STATUS_HISTORY = "status_history";
    public static final String COL_HISTORY_ID = "id";
    public static final String COL_HISTORY_REPORT_ID = "report_id";
    public static final String COL_HISTORY_STATUS = "status";
    public static final String COL_HISTORY_NOTES = "notes";
    public static final String COL_HISTORY_TIMESTAMP = "timestamp";
    public static final String COL_HISTORY_UPDATED_BY = "updated_by";

    // Table Help Contacts
    public static final String TABLE_HELP_CONTACTS = "help_contacts";
    public static final String COL_CONTACT_ID = "id";
    public static final String COL_CONTACT_NAME = "name";
    public static final String COL_CONTACT_ROLE = "role";
    public static final String COL_CONTACT_PHONE = "phone";
    public static final String COL_CONTACT_EMAIL = "email";
    public static final String COL_CONTACT_LOCATION = "location";
    public static final String COL_CONTACT_DESC = "description";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REPORTS_TABLE = "CREATE TABLE " + TABLE_REPORTS + " (" +
                COL_REPORT_ID + " TEXT PRIMARY KEY, " +
                COL_REPORT_TITLE + " TEXT NOT NULL, " +
                COL_REPORT_DESC + " TEXT, " +
                COL_REPORT_CATEGORY + " TEXT, " +
                COL_REPORT_LOCATION + " TEXT, " +
                COL_REPORT_PRIORITY + " TEXT, " +
                COL_REPORT_STATUS + " TEXT, " +
                COL_REPORT_IMAGE_PATH + " TEXT, " +
                COL_REPORT_CREATED_AT + " INTEGER, " +
                COL_REPORT_UPDATED_AT + " INTEGER, " +
                COL_REPORT_IS_HAZARD + " INTEGER, " +
                COL_REPORT_CONTACT_EMAIL + " TEXT, " +
                COL_REPORT_IS_DEMO + " INTEGER DEFAULT 0" +
                ")";

        String CREATE_STATUS_HISTORY_TABLE = "CREATE TABLE " + TABLE_STATUS_HISTORY + " (" +
                COL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HISTORY_REPORT_ID + " TEXT NOT NULL, " +
                COL_HISTORY_STATUS + " TEXT NOT NULL, " +
                COL_HISTORY_NOTES + " TEXT, " +
                COL_HISTORY_TIMESTAMP + " INTEGER NOT NULL, " +
                COL_HISTORY_UPDATED_BY + " TEXT, " +
                "FOREIGN KEY(" + COL_HISTORY_REPORT_ID + ") REFERENCES " + TABLE_REPORTS + "(" + COL_REPORT_ID + ") ON DELETE CASCADE" +
                ")";

        String CREATE_HELP_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HELP_CONTACTS + " (" +
                COL_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CONTACT_NAME + " TEXT NOT NULL, " +
                COL_CONTACT_ROLE + " TEXT, " +
                COL_CONTACT_PHONE + " TEXT, " +
                COL_CONTACT_EMAIL + " TEXT, " +
                COL_CONTACT_LOCATION + " TEXT, " +
                COL_CONTACT_DESC + " TEXT" +
                ")";

        db.execSQL(CREATE_REPORTS_TABLE);
        db.execSQL(CREATE_STATUS_HISTORY_TABLE);
        db.execSQL(CREATE_HELP_CONTACTS_TABLE);

        seedHelpContacts(db);
        seedSampleReports(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_REPORTS + " ADD COLUMN " + COL_REPORT_IS_DEMO + " INTEGER DEFAULT 0");
            db.execSQL("UPDATE " + TABLE_REPORTS + " SET " + COL_REPORT_IS_DEMO + " = 1 WHERE " + COL_REPORT_ID + " LIKE 'CF-20250520-10%'");
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HELP_CONTACTS);
            onCreate(db);
        }
    }

    private void seedHelpContacts(SQLiteDatabase db) {
        insertHelpContactRaw(db, "Campus Safety & Security", "Emergency Response & Security Dispatch", "555-0199", "security@campus.edu", "Building A, Ground Floor", "24/7 Campus safety, medical emergency dispatch, and escort service.");
        insertHelpContactRaw(db, "Facilities Maintenance Helpdesk", "Infrastructure, Repairs & Work Orders", "555-0122", "facilities@campus.edu", "Service Depot Room 102", "Handles plumbing, electrical, heating/cooling, and structural repairs.");
        insertHelpContactRaw(db, "IT & Network Operations Center", "WiFi, Computers & AV Equipment", "555-0144", "ithelp@campus.edu", "Library Commons 1st Floor", "Assists with classroom AV, lab computers, and campus network connectivity.");
        insertHelpContactRaw(db, "Environmental Health & Safety (EHS)", "Hazardous Materials & Fire Safety", "555-0188", "ehs@campus.edu", "Science Complex Annex 2", "Investigates chemical spills, gas smells, biohazards, and fire safety issues.");
        insertHelpContactRaw(db, "Student Housing & Residence Life", "Dorm Maintenance & Student Services", "555-0177", "housing@campus.edu", "Student Center Room 210", "Coordinates residence hall issues, key locks, and room repairs.");
    }

    private void insertHelpContactRaw(SQLiteDatabase db, String name, String role, String phone, String email, String location, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CONTACT_NAME, name);
        cv.put(COL_CONTACT_ROLE, role);
        cv.put(COL_CONTACT_PHONE, phone);
        cv.put(COL_CONTACT_EMAIL, email);
        cv.put(COL_CONTACT_LOCATION, location);
        cv.put(COL_CONTACT_DESC, desc);
        db.insert(TABLE_HELP_CONTACTS, null, cv);
    }

    private void seedSampleReports(SQLiteDatabase db) {
        long now = System.currentTimeMillis();
        long dayInMs = 24 * 60 * 60 * 1000L;

        // Sample Report 1
        String id1 = "CF-20250520-101";
        insertReportRaw(db, id1, "Flickering Lights in Library Hall", "Main overhead lights in the 2nd floor reading area are flickering constantly.", "Electrical", "Main Library 2nd Floor", "MEDIUM", "OPEN", null, now - (2 * dayInMs), now - (2 * dayInMs), false, "student@campus.edu", 1);
        insertStatusHistoryRaw(db, id1, "OPEN", "Report submitted by user.", now - (2 * dayInMs), "Student User");

        // Sample Report 2
        String id2 = "CF-20250520-102";
        insertReportRaw(db, id2, "Water Leak Near Chemistry Lab", "Pipe leaking under sink in Lab 304, creating slip hazard.", "Plumbing", "Science Building Room 304", "HIGH", "IN_PROGRESS", null, now - dayInMs, now - (dayInMs / 2), true, "lab_tech@campus.edu", 1);
        insertStatusHistoryRaw(db, id2, "OPEN", "Report submitted by lab technician.", now - dayInMs, "Lab Tech");
        insertStatusHistoryRaw(db, id2, "IN_PROGRESS", "Facilities team dispatched plumber to inspect pipe.", now - (dayInMs / 2), "Facilities Admin");

        // Sample Report 3
        String id3 = "CF-20250520-103";
        insertReportRaw(db, id3, "Broken Desk Chair in Auditorium", "Chair row 4 seat B is missing armrest and unstable.", "Furniture", "Auditorium A", "LOW", "RESOLVED", null, now - (3 * dayInMs), now - (dayInMs / 4), false, "student@campus.edu", 1);
        insertStatusHistoryRaw(db, id3, "OPEN", "Report submitted.", now - (3 * dayInMs), "Student User");
        insertStatusHistoryRaw(db, id3, "IN_PROGRESS", "Replacement chair ordered from warehouse.", now - (2 * dayInMs), "Facilities Admin");
        insertStatusHistoryRaw(db, id3, "RESOLVED", "Chair replaced with new ergonomics chair.", now - (dayInMs / 4), "Facilities Admin");
    }

    private void insertReportRaw(SQLiteDatabase db, String id, String title, String desc, String category, String location, String priority, String status, String imagePath, long createdAt, long updatedAt, boolean isHazard, String email, int isDemo) {
        ContentValues cv = new ContentValues();
        cv.put(COL_REPORT_ID, id);
        cv.put(COL_REPORT_TITLE, title);
        cv.put(COL_REPORT_DESC, desc);
        cv.put(COL_REPORT_CATEGORY, category);
        cv.put(COL_REPORT_LOCATION, location);
        cv.put(COL_REPORT_PRIORITY, priority);
        cv.put(COL_REPORT_STATUS, status);
        cv.put(COL_REPORT_IMAGE_PATH, imagePath);
        cv.put(COL_REPORT_CREATED_AT, createdAt);
        cv.put(COL_REPORT_UPDATED_AT, updatedAt);
        cv.put(COL_REPORT_IS_HAZARD, isHazard ? 1 : 0);
        cv.put(COL_REPORT_CONTACT_EMAIL, email);
        cv.put(COL_REPORT_IS_DEMO, isDemo);
        db.insertWithOnConflict(TABLE_REPORTS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertStatusHistoryRaw(SQLiteDatabase db, String reportId, String status, String notes, long timestamp, String updatedBy) {
        ContentValues cv = new ContentValues();
        cv.put(COL_HISTORY_REPORT_ID, reportId);
        cv.put(COL_HISTORY_STATUS, status);
        cv.put(COL_HISTORY_NOTES, notes);
        cv.put(COL_HISTORY_TIMESTAMP, timestamp);
        cv.put(COL_HISTORY_UPDATED_BY, updatedBy);
        db.insert(TABLE_STATUS_HISTORY, null, cv);
    }

    public void resetDemoData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_STATUS_HISTORY + " WHERE " + COL_HISTORY_REPORT_ID + " IN (SELECT " + COL_REPORT_ID + " FROM " + TABLE_REPORTS + " WHERE " + COL_REPORT_IS_DEMO + " = 1)");
            db.execSQL("DELETE FROM " + TABLE_REPORTS + " WHERE " + COL_REPORT_IS_DEMO + " = 1");
            seedSampleReports(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // --- CRUD OPERATIONS ---

    public boolean insertReport(Report report) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_REPORT_ID, report.getId());
            cv.put(COL_REPORT_TITLE, report.getTitle());
            cv.put(COL_REPORT_DESC, report.getDescription());
            cv.put(COL_REPORT_CATEGORY, report.getCategory());
            cv.put(COL_REPORT_LOCATION, report.getLocation());
            cv.put(COL_REPORT_PRIORITY, report.getPriority());
            cv.put(COL_REPORT_STATUS, report.getStatus());
            cv.put(COL_REPORT_IMAGE_PATH, report.getImagePath());
            cv.put(COL_REPORT_CREATED_AT, report.getCreatedAt());
            cv.put(COL_REPORT_UPDATED_AT, report.getUpdatedAt());
            cv.put(COL_REPORT_IS_HAZARD, report.isHazard() ? 1 : 0);
            cv.put(COL_REPORT_CONTACT_EMAIL, report.getContactEmail());
            cv.put(COL_REPORT_IS_DEMO, report.isDemo() ? 1 : 0);

            long result = db.insert(TABLE_REPORTS, null, cv);
            if (result == -1) {
                return false;
            }

            ContentValues hcv = new ContentValues();
            hcv.put(COL_HISTORY_REPORT_ID, report.getId());
            hcv.put(COL_HISTORY_STATUS, report.getStatus());
            hcv.put(COL_HISTORY_NOTES, "Report submitted.");
            hcv.put(COL_HISTORY_TIMESTAMP, report.getCreatedAt());
            hcv.put(COL_HISTORY_UPDATED_BY, "User");

            long hResult = db.insert(TABLE_STATUS_HISTORY, null, hcv);
            if (hResult == -1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateReportStatus(String reportId, String newStatus, String notes, String updatedBy, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_REPORT_STATUS, newStatus);
            cv.put(COL_REPORT_UPDATED_AT, timestamp);

            int rows = db.update(TABLE_REPORTS, cv, COL_REPORT_ID + "=?", new String[]{reportId});
            if (rows <= 0) {
                return false;
            }

            ContentValues hcv = new ContentValues();
            hcv.put(COL_HISTORY_REPORT_ID, reportId);
            hcv.put(COL_HISTORY_STATUS, newStatus);
            hcv.put(COL_HISTORY_NOTES, notes);
            hcv.put(COL_HISTORY_TIMESTAMP, timestamp);
            hcv.put(COL_HISTORY_UPDATED_BY, updatedBy);

            long hResult = db.insert(TABLE_STATUS_HISTORY, null, hcv);
            if (hResult == -1) {
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean addStatusHistory(String reportId, String status, String notes, long timestamp, String updatedBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HISTORY_REPORT_ID, reportId);
        cv.put(COL_HISTORY_STATUS, status);
        cv.put(COL_HISTORY_NOTES, notes);
        cv.put(COL_HISTORY_TIMESTAMP, timestamp);
        cv.put(COL_HISTORY_UPDATED_BY, updatedBy);
        long result = db.insert(TABLE_STATUS_HISTORY, null, cv);
        return result != -1;
    }

    public Report getReportById(String reportId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REPORTS, null, COL_REPORT_ID + "=?", new String[]{reportId}, null, null, null);
        Report report = null;
        if (cursor != null && cursor.moveToFirst()) {
            report = cursorToReport(cursor);
            cursor.close();
        }
        return report;
    }

    public List<Report> getAllReports() {
        return getFilteredReports(null, "ALL", "ALL", "ALL");
    }

    public List<Report> getFilteredReports(String searchQuery, String categoryFilter, String statusFilter, String priorityFilter) {
        List<Report> reports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            selection.append("(").append(COL_REPORT_TITLE).append(" LIKE ? OR ")
                    .append(COL_REPORT_DESC).append(" LIKE ? OR ")
                    .append(COL_REPORT_LOCATION).append(" LIKE ? OR ")
                    .append(COL_REPORT_ID).append(" LIKE ?)");
            String queryArg = "%" + searchQuery.trim() + "%";
            selectionArgs.add(queryArg);
            selectionArgs.add(queryArg);
            selectionArgs.add(queryArg);
            selectionArgs.add(queryArg);
        }

        if (categoryFilter != null && !categoryFilter.equalsIgnoreCase("ALL")) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COL_REPORT_CATEGORY).append("=?");
            selectionArgs.add(categoryFilter);
        }

        if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COL_REPORT_STATUS).append("=?");
            selectionArgs.add(statusFilter);
        }

        if (priorityFilter != null && !priorityFilter.equalsIgnoreCase("ALL")) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COL_REPORT_PRIORITY).append("=?");
            selectionArgs.add(priorityFilter);
        }

        String whereClause = selection.length() > 0 ? selection.toString() : null;
        String[] whereArgs = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[0]);

        Cursor cursor = db.query(TABLE_REPORTS, null, whereClause, whereArgs, null, null, COL_REPORT_CREATED_AT + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                reports.add(cursorToReport(cursor));
            }
            cursor.close();
        }
        return reports;
    }

    public List<StatusHistory> getStatusHistoryForReport(String reportId) {
        List<StatusHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STATUS_HISTORY, null, COL_HISTORY_REPORT_ID + "=?", new String[]{reportId}, null, null, COL_HISTORY_TIMESTAMP + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StatusHistory sh = new StatusHistory();
                sh.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HISTORY_ID)));
                sh.setReportId(cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_REPORT_ID)));
                sh.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_STATUS)));
                sh.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_NOTES)));
                sh.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HISTORY_TIMESTAMP)));
                sh.setUpdatedBy(cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_UPDATED_BY)));
                historyList.add(sh);
            }
            cursor.close();
        }
        return historyList;
    }

    public List<HelpContact> getAllHelpContacts() {
        List<HelpContact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HELP_CONTACTS, null, null, null, null, null, COL_CONTACT_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HelpContact hc = new HelpContact();
                hc.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CONTACT_ID)));
                hc.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_NAME)));
                hc.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_ROLE)));
                hc.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_PHONE)));
                hc.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_EMAIL)));
                hc.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_LOCATION)));
                hc.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_DESC)));
                contacts.add(hc);
            }
            cursor.close();
        }
        return contacts;
    }

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        int total = 0;
        int open = 0;
        int inProgress = 0;
        int resolved = 0;
        int critical = 0;

        Cursor cursor = db.rawQuery("SELECT " + COL_REPORT_STATUS + ", " + COL_REPORT_PRIORITY + " FROM " + TABLE_REPORTS, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                total++;
                String status = cursor.getString(0);
                String priority = cursor.getString(1);

                if ("OPEN".equalsIgnoreCase(status)) open++;
                else if ("IN_PROGRESS".equalsIgnoreCase(status)) inProgress++;
                else if ("RESOLVED".equalsIgnoreCase(status)) resolved++;

                if ("CRITICAL".equalsIgnoreCase(priority) || "HIGH".equalsIgnoreCase(priority)) critical++;
            }
            cursor.close();
        }

        stats.put("total", total);
        stats.put("open", open);
        stats.put("in_progress", inProgress);
        stats.put("resolved", resolved);
        stats.put("critical", critical);

        return stats;
    }

    private Report cursorToReport(Cursor cursor) {
        Report report = new Report();
        report.setId(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_ID)));
        report.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_TITLE)));
        report.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_DESC)));
        report.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_CATEGORY)));
        report.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_LOCATION)));
        report.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_PRIORITY)));
        report.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_STATUS)));
        report.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_IMAGE_PATH)));
        report.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_REPORT_CREATED_AT)));
        report.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_REPORT_UPDATED_AT)));
        report.setHazard(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPORT_IS_HAZARD)) == 1);
        report.setContactEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_REPORT_CONTACT_EMAIL)));

        int demoColIdx = cursor.getColumnIndex(COL_REPORT_IS_DEMO);
        if (demoColIdx != -1) {
            report.setDemo(cursor.getInt(demoColIdx) == 1);
        } else {
            report.setDemo(false);
        }

        return report;
    }
}
