package ua.nure.kislov.lab2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ListView notesList;
    NoteAdapter noteAdapter;
    SQLiteManager sqLiteManager;
    String importance="All";
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notesList = findViewById(R.id.notesList);
        sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        if(savedInstanceState==null) {
            loadNotesFromDB();
            noteAdapter = new NoteAdapter(this, R.layout.view_note, sqLiteManager.getNotesList());
            notesList.setAdapter(noteAdapter);
            Toast.makeText(this, R.string.dataRecovered, Toast.LENGTH_LONG).show();
        }else{
            sqLiteManager.setNotesList(savedInstanceState.getParcelableArrayList("NOTES"));
            noteAdapter = new NoteAdapter(this, R.layout.view_note, sqLiteManager.getNotesList());
            notesList.setAdapter(noteAdapter);
        }
        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note item = noteAdapter.getItemAtPosition(i);
                //based on item add info to intent
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.setAction("android.intent.action.DISPLAY");
                intent.putExtra("INDEX_POSITION", i);
                startActivity(intent);
            }
        });
        registerForContextMenu(notesList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SearchView searchView = findViewById(R.id.btnSearch);
        outState.putParcelableArrayList("NOTES", sqLiteManager.getNotesList());
        outState.putString("IMPORTANCE", importance);


        if(!searchView.getQuery().toString().isEmpty()){
            outState.putString("QUERY", searchView.getQuery().toString());

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        importance = savedInstanceState.getString("IMPORTANCE");
        noteAdapter.setImportanceFilter(importance);
        if(savedInstanceState.containsKey("QUERY")){
            noteAdapter.setSearchCharText(savedInstanceState.getString("QUERY"));
        }
        else
            noteAdapter.setSearchCharText("");

        noteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        super.onStop();
        sqLiteManager.closeDB();
        //saveData();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    //  Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //     NEW FOR SEARCH
        // Initialise menu item search bar with id and take its object
        MenuItem searchViewItem
                = menu.findItem(R.id.btnSearch);
        searchView = (SearchView) searchViewItem.getActionView();
        if(!noteAdapter.getSearchCharText().isEmpty()){
            searchView.setQuery(noteAdapter.getSearchCharText(), true);
            searchView.requestFocus();
        }
        // attach setOnQueryTextListener to search view defined above
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    // Override onQueryTextSubmit method which is call when submit query is searched

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query.isEmpty()) {
                            noteAdapter.setSearchCharText("");
                        } else {
                            noteAdapter.setSearchCharText(query);
                        }
                        noteAdapter.notifyDataSetChanged();
                        return false;
                    }
                    // This method is overridden to filter the adapter according to a search query
                    // when the user is typing search
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            noteAdapter.setSearchCharText("");
                        } else {
                            noteAdapter.setSearchCharText(newText);
                        }
                        noteAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

        searchView.setOnCloseListener(() -> {
            noteAdapter.setSearchCharText("");
            noteAdapter.notifyDataSetChanged();
            return false;
        });

        return true;
    }

    // Interaction with Actionbar menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.btnAddNote:
                intent = new Intent(this, NoteActivity.class);
                intent.setAction("android.intent.action.CREATE");
                activityResultLauncher.launch(intent);
                return true;
            case R.id.btnFilterShowAll:
                importance="All";
                noteAdapter.setImportanceFilter(importance);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.filterShowAll, Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnFilterByMostImportant:
                importance="MostImportant";
                noteAdapter.setImportanceFilter(importance);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.filterByMostImportant_noteBtn, Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnFilterByImportant:
                importance = "Important";
                noteAdapter.setImportanceFilter(importance);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.filterByImportant_noteBtn, Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnFilterByNotVeryImportant:
                importance = "NotVeryImportant";
                noteAdapter.setImportanceFilter(importance);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.filterByNotVeryImportant_noteBtn, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  ListView Item menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.notesList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    // Interaction with ListView`s Item menu items
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.btnEditNote:
                intent = new Intent(this, NoteActivity.class);
                intent.setAction("android.intent.action.EDIT");
                intent.putExtra("INDEX_POSITION", info.position);
                activityResultLauncher.launch(intent);
                return true;
            case R.id.btnDeleteNote:
                int pos = info.position;
                sqLiteManager.removeNote(pos);
                noteAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void loadNotesFromDB() {
        sqLiteManager.populateNoteListArray();
        // MAYBE IT REQUIRES
        // notesList = sqLiteManager.getNotesList();
    }
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();

                            if (result == RESULT_OK) {
                                Intent data = activityResult.getData();
                                Bundle extras = data.getExtras();
                                String action = extras.getString("action");
                                if (action.equals("create") || action.equals("edit")) {
                                    noteAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
            );
}