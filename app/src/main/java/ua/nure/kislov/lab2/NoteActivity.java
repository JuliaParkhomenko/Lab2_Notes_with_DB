package ua.nure.kislov.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NoteActivity extends AppCompatActivity {
    SQLiteManager sqLiteManager;
    Note note;
    Intent outputIntent = new Intent();
    Intent inputIntent;

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int result = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            if (result == RESULT_OK && data != null) {
                                Uri selectedImage = data.getData();
                                ImageView iv_photo = findViewById(R.id.iv_photo);
                                iv_photo.setImageURI(selectedImage);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        sqLiteManager = SQLiteManager.instanceOfDatabase(this);

        // получаем Intent, который вызывал это Activity
        inputIntent = getIntent();
        // читаем из него action
        String action = inputIntent.getAction();
        int index_position=0;
        ImageView iv_importance = findViewById(R.id.iv_importance);
        // в зависимости от action заполняем переменные
        if (action.equals("android.intent.action.EDIT")) {
            Bundle extras = inputIntent.getExtras();
            if (extras != null) {
                index_position = inputIntent.getIntExtra("INDEX_POSITION", -1); // default value causes an error!
            }
            note = sqLiteManager.getNoteByIndex(index_position);

            TextInputEditText et_title = findViewById(R.id.et_title);
            TextInputEditText et_description = findViewById(R.id.et_description);
            ImageView iv_image = findViewById(R.id.iv_photo);

            if (note.getIsImage()) {
                Bitmap myBitmap = note.getImageBitmap();
                iv_image.setImageBitmap(myBitmap);
                iv_importance.setImageResource(note.getImportanceImage(note.getImportance()));
            } else {
                iv_image.setImageResource(R.drawable.note_default_img);
            }
            Spinner spinner = findViewById(R.id.spinner);
            et_description.setText(note.getDescription());
            et_title.setText(note.getTitle());
            spinner.setSelection(note.getImportance());
            iv_importance.setVisibility(View.INVISIBLE);

            outputIntent.putExtra("action", "edit");
            outputIntent.putExtra("INDEX_POSITION", index_position);

        } else if (action.equals("android.intent.action.CREATE")) {
            iv_importance.setVisibility(View.GONE);
            ImageView img = (ImageView) findViewById(R.id.iv_photo);
            img.setImageResource(R.drawable.note_default_img);
            note = new Note(sqLiteManager.getNewNoteId(), "", "","", 0, false);
            outputIntent.putExtra("action", "create");
            iv_importance.setVisibility(View.GONE);
        }
        else if (action.equals("android.intent.action.DISPLAY")) {
            TextInputEditText et_title = findViewById(R.id.et_title);
            TextInputEditText et_description = findViewById(R.id.et_description);
            ImageView iv_image = findViewById(R.id.iv_photo);

            Button btn_setPicture = (Button) findViewById(R.id.btn_addPhoto);
            btn_setPicture.setVisibility(View.GONE);
            et_title.setEnabled(false);
            et_description.setEnabled(false);
            Button btn_saveChanges = (Button) findViewById(R.id.btn_saveNote);
            btn_saveChanges.setVisibility(View.GONE);
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setVisibility(View.INVISIBLE);

            Bundle extras = inputIntent.getExtras();
            if (extras != null) {
                 index_position = inputIntent.getIntExtra("INDEX_POSITION", -1); // default value causes an error!
            }
            note = sqLiteManager.getNoteByIndex(index_position);
            if (note.getIsImage()) {
                Bitmap myBitmap = note.getImageBitmap();
                iv_image.setImageBitmap(myBitmap);
            } else {
                iv_image.setImageResource(R.drawable.note_default_img);
            }
            et_description.setText(note.getDescription());
            et_title.setText(note.getTitle());

            iv_importance.setImageResource(note.getImportanceImage(note.getImportance()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Завершается работа activity. (например, при повороте экрана или при многоконном режиме),
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //В методе onStop следует особождать используемые ресурсы, которые не нужны пользователю, когда
    // он не взаимодействует с activity. Здесь также можно сохранять данные, например, в БД. При
    // этом во время состояния Stopped activity остается в памяти устройства, сохраняется состояние
    // всех элементов интерфейса.
    @Override
    protected void onStop(){
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ImageView imageView = (ImageView)findViewById(R.id.iv_photo);

        if (!imageView.getDrawable().getConstantState().equals(getDrawable(R.drawable.note_default_img).getConstantState()) ) {
            String filename = "temp.jpg";
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(folder,filename);
                try {
                        file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//Запись
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (Exception e) {
                   e.printStackTrace();
                }

            outState.putString("IMAGE", "temp.jpg");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("IMAGE")){
            ImageView imageView = findViewById(R.id.iv_photo);
            Bitmap myBitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator+savedInstanceState.getString("IMAGE"));

            imageView.setImageBitmap(myBitmap);

        }
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

    public void uploadImage(View view) {
        /*ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                23);
         */
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    public void saveNote(View view) {
        TextInputEditText et_title = findViewById(R.id.et_title);
        TextInputEditText et_description = findViewById(R.id.et_description);
        // проверка на заполненность всех полей
        if (!TextUtils.isEmpty(et_description.getText()) && !TextUtils.isEmpty(et_title.getText())) {

            ImageView iv_photo = findViewById(R.id.iv_photo);
            if (!iv_photo.getDrawable().getConstantState().equals(getDrawable(R.drawable.note_default_img).getConstantState()) ) {

                Bitmap bitmap = ((BitmapDrawable) iv_photo.getDrawable()).getBitmap();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byte[] image = bytes.toByteArray();
                note.setIsImage();
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                // Storing the data in file with name as noteTitle.jpg
                File file = new File(folder,note.getId()+".jpg");
                try {
                    if(file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Spinner spinner = findViewById(R.id.spinner);
            note.setImportance(spinner.getSelectedItemPosition());
            note.setDescription(et_description.getText().toString());
            note.setTitle(et_title.getText().toString());
            note.setDateTime();
            if(inputIntent.getAction().equals("android.intent.action.CREATE")) {
                sqLiteManager.addNote(note);
            }
            else if(inputIntent.getAction().equals("android.intent.action.EDIT")) {
                sqLiteManager.updateNote(note);
            }
            setResult(RESULT_OK, outputIntent);
            finish();
        } else {
            Toast.makeText(this, R.string.error_fillAllFields, Toast.LENGTH_LONG).show();
        }
    }
}