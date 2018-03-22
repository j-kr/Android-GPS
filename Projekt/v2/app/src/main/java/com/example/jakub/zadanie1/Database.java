package com.example.jakub.zadanie1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Lokalizacje.db";
    public static final String TABLE_NAME = "lokalizacja_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAZWA";
    public static final String COL_3 = "OPIS";
    public static final String COL_4 = "SZEROKOSC";
    public static final String COL_5 = "DLUGOSC";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAZWA TEXT,OPIS TEXT,SZEROKOSC DOUBLE, DLUGOSC DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String nazwa,String opis,String szerokosc,String dlugosc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,nazwa);
        contentValues.put(COL_3,opis);
        contentValues.put(COL_4,szerokosc);
        contentValues.put(COL_5,dlugosc);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public Cursor getWybrane(double szukanaSzerokoscMIN, double szukanaSzerokoscMAX, double szukanaDlugoscMIN, double szukanaDlugoscMAX) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where NAZWA = ?",new String[] {"Katedra 1"});

        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where SZEROKOSC >= ? and SZEROKOSC <= ? and DLUGOSC >= ? and DLUGOSC <= ?",new String[] {String.valueOf(szukanaSzerokoscMIN), String.valueOf(szukanaSzerokoscMAX), String.valueOf(szukanaDlugoscMIN), String.valueOf(szukanaDlugoscMAX)});
        return res;
    }

}
