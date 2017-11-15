package com.example.test.ecuaruta.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ecuarutaDB";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		String CREATE_RUTAS = "CREATE TABLE historial( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"nombre TEXT)";
		
		// create books table
		db.execSQL(CREATE_RUTAS);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS historial");

        
        // create fresh books table
        this.onCreate(db);
	}

    public void eliminar_historial(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from historial");


    }

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    
	public void addHistorial(String nombre){
		//Log.d("addBook", book.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// 2. create ContentValues to add key "column"/value

        ContentValues values = new ContentValues();
        values.put(KEY_NOMBRE, nombre);


        // 3. insert
        db.insert("historial", // table
        		null, //nullColumnHack
        		values); // key/value -> keys = column names/ values = column values
        
        // 4. close
        db.close(); 
	}
	
	public String[] getLista(String Tabla){

		// 1. get reference to readable DB
        ArrayList<String> lugares = new ArrayList<String>();
        //Object[] lugar = new Object[2];
		SQLiteDatabase db = this.getReadableDatabase();
		 
		// 2. build query
        Cursor cursor = db.rawQuery("SELECT * FROM "+Tabla+"",null);

        
        // 3. if we got results get the first one
        int i=0;
        if (cursor != null) {
            //cursor.moveToFirst();
                //do {
                    Log.i("contador",""+i++);
            while (cursor.moveToNext()){
                //lugar[0] = cursor.getInt(cursor.getColumnIndex("id"));//id
                //lugar[1] = cursor.getString(cursor.getColumnIndex("nombre"));//nombre
                lugares.add(cursor.getString(cursor.getColumnIndex("nombre")));
            }

        }
        String[] resultado = new String[lugares.size()];
        resultado = lugares.toArray(resultado);

        db.close();
        return resultado;
	}

    public int getSize(String Tabla){
        int size=0;
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.rawQuery("SELECT * FROM "+Tabla+"",null);
        while(cursor.moveToNext()){
            size++;
        }

        return size;
    }


}
