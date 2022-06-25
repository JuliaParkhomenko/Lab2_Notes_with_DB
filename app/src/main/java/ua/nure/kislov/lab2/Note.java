package ua.nure.kislov.lab2;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Note /*implements Parcelable*/ {
    private int id;
    private byte[] image;
    private String dateTime;
    //private String imagePath;
    private String title;
    private int importance;
    private String description;

    /*Note(String newTitle, String newDescription, int importance, String imagePath) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss aaa z"); //"dd.MM.yyyy HH:mm:ss aaa z"
        dateTime = simpleDateFormat.format(calendar.getTime());
        title = newTitle;
        this.importance = importance;
        description = newDescription;
        //this.imagePath = imagePath;
    }*/
    Note(int id, String title, String description, String date,  int importance, byte[] image) {
        this.id=id;
        this.title = title;
        this.description = description;
        if(date.isEmpty()){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss aaa z"); //"dd.MM.yyyy HH:mm:ss aaa z"
        dateTime = simpleDateFormat.format(calendar.getTime());}
        else {
            dateTime=date;
        }
        this.importance = importance;
        this.image = image;
    }

    public Note(Parcel in) {
        //dateTime = in.readTypedObject(Creator<Date>)/*readString()*/;
        /*title = in.readString();
        importance = in.readInt();
        description = in.readString();
        imagePath = in.readString();*/
    }

    /*public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };*/

    public int getImportanceImage(int index) {
        switch (index) {
            case 0:
                return R.drawable.red;
            case 1:
                return R.drawable.yellow;
            case 2:
                return R.drawable.green;
            default:
                return R.drawable.green;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //"dd.MM.yyyy HH:mm:ss aaa z"
        dateTime = simpleDateFormat.format(calendar.getTime());
    }

    public int getId(){
        return id;
    }

    public byte[] getImage(){
        return image;
    }

    public void setImage(byte[] image) {
        this.image=image;
    }
    /* @Override
     public int describeContents() {
         return 0;
     }
    /* @Override
     public void writeToParcel(Parcel parcel, int i) {
         parcel.writeString(dateTime);
         parcel.writeString(title);
         parcel.writeInt(importance);
         parcel.writeString(description);
         parcel.writeString(imagePath);
     }
 */
    /*public String getImagePath() {
        return imagePath;
    }*/

    /*public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }*/
}
