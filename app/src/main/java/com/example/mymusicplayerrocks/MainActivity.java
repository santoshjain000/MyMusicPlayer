package com.example.mymusicplayerrocks;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    ArrayList<String> mp3s = new ArrayList<String>();

    String[] permission = {READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSongs);
        runtimePermission();

    }

//For getting the permission of reading storage
    public void runtimePermission(){

        Dexter.withContext(this).withPermission(READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

//For displayning the songs from the storage
    void displaySongs(){
        Log.d("songs", String.valueOf(Environment.getExternalStorageDirectory()));
        final  ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        Log.d("songs", String.valueOf(mySongs));

        items = new String[mySongs.size()];

        for(int i = 0;i<mySongs.size();i++){
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        //listView.setAdapter(myAdapter);

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);


        //creating onclicklistner for listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(),PlayerActivity.class);
                i.putExtra("songs",mySongs);
                i.putExtra("songname",songName);
                i.putExtra("pos",position);

                startActivity(i);
            }
        });
    }

    public ArrayList<File> findSong (File file){
        ArrayList<File> arrayList = new ArrayList<File>();
        Log.d("tag","check1");

        File[] files = file.listFiles();
        Log.d("Fill", String.valueOf(files));

        if(files!=null){
        for (File singleFile:files){
            Log.d("tag","check2");
            Log.d("singlefile", String.valueOf(singleFile));
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findSong(singleFile));
            }else{
                Log.d("tag","check3");
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    Log.d("tag","check4");
                    arrayList.add(singleFile);
                }
            }

        }}
        Log.d("tag","check5");
        Log.d("filess", String.valueOf(arrayList));
        return  arrayList;
    }




    //Custom Adapter
    class customAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong = view.findViewById(R.id.txtSongName);
            textsong.setSelected(true);
            textsong.setText(items[position]);

            return view;
        }
    }






}

