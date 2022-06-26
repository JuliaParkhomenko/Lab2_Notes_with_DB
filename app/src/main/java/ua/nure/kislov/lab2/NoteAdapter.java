package ua.nure.kislov.lab2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class NoteAdapter<N> extends ArrayAdapter<Note> implements Filterable {
    private LayoutInflater inflater;
    private int layout;
    SQLiteManager sqLiteManager;
    private List<Note> notes;
    private String searchCharText = "";
    private String importanceFilter="All";

    // invoke the suitable constructor of the ArrayAdapter class
    public NoteAdapter(Context context, int resource, List<Note> notes) {
        // pass the context and arrayList for the super constructor of the ArrayAdapter class
        super(context, resource, notes);
        sqLiteManager = SQLiteManager.instanceOfDatabase(context);
        //notes=sqLiteManager.getNotesList();
        this.notes = notes;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public Note getItemAtPosition(int position){
        return notes.get(position);
    }

    @Override
    public View getView(int position, View currentItemView, ViewGroup parent) {
        currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.view_note, parent, false);

        ImageView importanceImage = currentItemView.findViewById(R.id.iv_importance);
        // get the position of the view from the ArrayAdapter
        Note note = getItem(position);
        boolean isHide=true;
        if(importanceFilter.equals("All")){
            isHide = false;
            switch (note.getImportance()){
                case 0:
                    importanceImage.setImageResource(R.drawable.red);
                    break;
                case 1:
                    importanceImage.setImageResource(R.drawable.yellow);
                    break;
                case 2:
                    importanceImage.setImageResource(R.drawable.green);
                    break;
                default:
                    break;
            }

        }else{
            switch (note.getImportance()) {
                case 0: {
                    if(importanceFilter.equals("MostImportant")) {
                        isHide = false;
                        importanceImage.setImageResource(R.drawable.red);
                    }
                    break;
                }
                case 1: {
                    if(importanceFilter.equals("Important")) {
                        isHide = false;
                        importanceImage.setImageResource(R.drawable.yellow);
                    }
                    break;
                }
                case 2: {
                    if(importanceFilter.equals("NotVeryImportant")) {
                        isHide=false;
                        importanceImage.setImageResource(R.drawable.green);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        if(isHide){
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.view_hide_note, parent, false);
            return currentItemView;
        }

        //          SEARCH
        if(!searchCharText.isEmpty()){
            searchCharText = searchCharText.toLowerCase(Locale.getDefault());

            if (!note.getTitle().toLowerCase(Locale.getDefault())
                    .contains(searchCharText)){
                currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.view_hide_note, parent, false);
                return currentItemView;
            }
        }

        ImageView ivImage = currentItemView.findViewById(R.id.iv_notePicture);
        if(note.getIsImage()) {
            Bitmap myBitmap = note.getImageBitmap();
            ivImage.setImageBitmap(myBitmap);
        }
        else {
            ivImage.setImageResource(R.drawable.note_default_img);
        }
        TextView tvTitle = currentItemView.findViewById(R.id.tv_title);
        tvTitle.setText(note.getTitle());

        TextView tvDateTime = currentItemView.findViewById(R.id.tv_dateTime);
        tvDateTime.setText(note.getDateTime());

        return currentItemView;
    }

    public void setImportanceFilter(String importanceFilter) {
        this.importanceFilter=importanceFilter;
    }

    public void setSearchCharText(String s) {
        searchCharText=s;
    }

    public String getSearchCharText(){
        return searchCharText;
    }
}
