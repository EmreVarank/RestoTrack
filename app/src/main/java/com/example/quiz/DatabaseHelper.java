package com.example.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "masaDB.db";
    private static final int DB_VERSION = 2;

    private static final String TABLE_MASA = "masalar";
    private static final String COL_ID = "masa_id";
    private static final String COL_DURUM = "durum";

    private static final String TABLE_SIPARIS = "siparisler";
    private static final String COL_MASA_ADI = "masa_adi";
    private static final String COL_URUN = "urun_adi";
    private static final String COL_FIYAT = "fiyat";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_MASA + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_DURUM + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_SIPARIS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MASA_ADI + " TEXT, " +
                COL_URUN + " TEXT, " +
                COL_FIYAT + " INTEGER)");

        for (int i = 0; i < 8; i++) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ID, i);
            cv.put(COL_DURUM, 0);
            db.insert(TABLE_MASA, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIPARIS);
        onCreate(db);
    }

    public void masaGuncelle(int id, boolean doluMu) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DURUM, doluMu ? 1 : 0);
        db.update(TABLE_MASA, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public boolean masaDurumuGetir(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT durum FROM masalar WHERE masa_id=?",
                new String[]{String.valueOf(id)}
        );
        boolean dolu = false;
        if (c.moveToFirst()) dolu = c.getInt(0) == 1;
        c.close();
        return dolu;
    }

    public void siparisEkle(String masaAdi, String urun, int fiyat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MASA_ADI, masaAdi);
        cv.put(COL_URUN, urun);
        cv.put(COL_FIYAT, fiyat);
        db.insert(TABLE_SIPARIS, null, cv);
    }

    public int getMasaToplam(String masaAdi) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(fiyat) FROM siparisler WHERE masa_adi=?",
                new String[]{masaAdi}
        );
        int toplam = 0;
        if (c.moveToFirst() && !c.isNull(0)) toplam = c.getInt(0);
        c.close();
        return toplam;
    }

    public String getMasaSiparisi(String masaAdi) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT urun_adi, fiyat FROM siparisler WHERE masa_adi=?",
                new String[]{masaAdi}
        );

        StringBuilder sb = new StringBuilder();
        int toplam = 0;

        while (c.moveToNext()) {
            sb.append(c.getString(0))
                    .append(" = ")
                    .append(c.getInt(1))
                    .append(" TL\n");
            toplam += c.getInt(1);
        }

        sb.append("\nTOPLAM: ").append(toplam).append(" TL");
        c.close();
        return sb.toString();
    }

    public void siparisSil(String masaAdi) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SIPARIS, COL_MASA_ADI + "=?", new String[]{masaAdi});
    }
}
