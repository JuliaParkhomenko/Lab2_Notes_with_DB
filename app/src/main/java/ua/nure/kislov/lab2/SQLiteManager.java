package ua.nure.kislov.lab2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static ArrayList<Note> notes;
    private static int newNoteId;

    private static final String DATABASE_NAME = "NoteDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Note";
    private static final String COUNTER = "Counter";

    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String DATE_FIELD = "date";
    private static final String IMPORTANCE_FIELD = "importance";
    private static final String IMAGE_FIELD = "image";


    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT, ")
                .append(TITLE_FIELD)
                .append(" TEXT, ")
                .append(DESCRIPTION_FIELD)
                .append(" TEXT, ")
                .append(DATE_FIELD)
                .append(" TEXT, ")
                .append(IMPORTANCE_FIELD)
                .append(" INT, ")
                .append(IMAGE_FIELD)
                .append(" BLOB)");

        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addNote(Note note){
        addNoteToDB(note);
        addNoteToList(note);
    }

    private void addNoteToList(Note note) {
        notes.add(note);
    }

    private void addNoteToDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESCRIPTION_FIELD, note.getDescription());
        contentValues.put(DATE_FIELD, note.getDateTime());
        contentValues.put(IMPORTANCE_FIELD, note.getImportance());
        contentValues.put(IMAGE_FIELD, note.getImage());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }
    public void removeNote(int pos) {
        removeNoteFromDB(pos);
        removeNoteFromList(pos);
    }

    private void removeNoteFromList(int pos) {
        notes.remove(pos);
    }

    private void removeNoteFromDB(int pos) {
        //todo
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ID_FIELD+" =? ", new String[]{String.valueOf(pos)});
    }

    public void updateNote(Note note){
        updateNoteInDB(note);
        updateNoteInList(note);
    }

    private void updateNoteInList(Note note) {
        for(int i=0; i<notes.size(); i++){
            if(note.getId()==notes.get(i).getId()){
                notes.set(i, note);
                break;
            }
        }
    }

    private void updateNoteInDB(Note note) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESCRIPTION_FIELD, note.getDescription());
        contentValues.put(DATE_FIELD, note.getDateTime());
        contentValues.put(IMPORTANCE_FIELD, note.getImportance());
        contentValues.put(IMAGE_FIELD, note.getImage());

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD+" =? ", new String[]{String.valueOf(note.getId())});
    }

    public void populateNoteListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Note note=null;
        Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        notes=new ArrayList<Note>();
        if(result.getCount()!=0){
            while ((result.moveToNext())){
                int id = result.getInt(1);
                String title = result.getString(2);
                String description = result.getString(3);
                String date = result.getString(4);
                int importance = result.getInt(5);
                byte[] image = result.getBlob(6);
                note = new Note(id, title, description, date, importance, image);
                notes.add(note);
            }
            newNoteId=note.getId()+1;
        }
        else {
            newNoteId=1;
        }
        result.close();
    }
    public int getNewNoteId(){
        return newNoteId++;
    }

    public List<Note> getNotesList() {
        return notes;
    }
    public Note getNoteByIndex(int index){
        return notes.get(index);
    }





    /*public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
        contentValues.put(IMAGE_FIELD, );
    }*/
}
