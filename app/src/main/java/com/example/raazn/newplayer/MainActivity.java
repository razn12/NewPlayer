package com.example.raazn.newplayer;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Cursor c;
    MediaMetadataRetriever retriever;
    ImageView albumart;
    TextView songfooter,t1,t2;
    MediaPlayer mplayer;
    String filepath;
    SeekBar seekbar;
    int index;
    private android.os.Handler myhandler;
    SearchView searchView;
    MenuItem searchMenuItem;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        retriever = new MediaMetadataRetriever();
        albumart = (ImageView) findViewById(R.id.albumart);
        songfooter = (TextView) findViewById(R.id.songfooter);
        t1=(TextView)findViewById(R.id.timer1);
        t2=(TextView)findViewById(R.id.timer2);
        seekbar=(SeekBar)findViewById(R.id.progress);
        myhandler=new android.os.Handler();

        c = getAllSongs();
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(new CustomAdapter(this, c));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                index = position;
                c.moveToPosition(position);
                filepath = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                mediainfo();

            }


        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    if (mplayer != null)
                        mplayer.seekTo(progress);
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Exit New Player?");

            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("*****************");
                            if (mplayer != null)
                                mplayer.stop();
                            finish();
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.out.println("---------------");
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
           //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private Cursor getAllSongs(){

        //projection specifies from which coloumns of the database you want. Database is really big and
        //specifying null to projection is inefficient since we don't want everything.
        String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        //Getting music files from external storage
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return c;
    }

    private Bitmap getAlbumart(String filepath){

        retriever.setDataSource(filepath);
        //getEmbeddedPictures() returns null when album art is not available in the media
        byte[] art = retriever.getEmbeddedPicture();
        if(art == null)
            return null;
        Bitmap albumart = BitmapFactory.decodeByteArray(art, 0, art.length);
        //I don't want big image so downscaling it to 100 X 100
        return Bitmap.createScaledBitmap(albumart, 600, 600, false);
    }

    public void buttonhandler(View v){

        switch(v.getId()){

            case R.id.pause :    //make sure media player is initialized and is currently playing a song
                if(mplayer!=null){
                    if(mplayer.isPlaying())
                        mplayer.pause();
                }
                break;
            case R.id.play  :    // start playing only if mediaplayer is not null
                if(mplayer!=null)mplayer.start();
                break;
            case R.id.stop  :    //make sure media player is initialized and is currently playing a song

                if(mplayer!=null){
                    if(mplayer.isPlaying()){
                        mplayer.stop();
                        initMediaPlayer(filepath);
                    }
                }
                break;
            case  R.id.forward : if(mplayer !=null)
                next();
                //mplayer.seekTo(mplayer.getCurrentPosition()+mplayer.getDuration()/10);
                break;
            case R.id.reverse :  if(mplayer!=null)
                //mplayer.seekTo(mplayer.getCurrentPosition()-mplayer.getDuration()/10);
                preview();
                break;
        }

    }

    private void initMediaPlayer(String filename){
        if (mplayer != null)
            mplayer.release();
        mplayer = new MediaPlayer();
        try {
            mplayer.setDataSource(filename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            mplayer.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void timer2(String lastplayed){
        retriever.setDataSource(lastplayed);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        String formattedTime = String.format("%d : %d", TimeUnit.MILLISECONDS.toMinutes(dur), TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));
        t2.setText(formattedTime);
    }

    Runnable run=new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    private void updateSeekBar(){
        seekbar.setProgress(mplayer.getCurrentPosition());
        int dur = seekbar.getProgress();
        String currentDuration = String.format("%d : %d", TimeUnit.MILLISECONDS.toMinutes(dur), TimeUnit.MILLISECONDS.toSeconds(dur) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));
        t1.setText(currentDuration);
        myhandler.postDelayed(run, 100);

    }

    public void next(){
        c.moveToPosition(++index);
       filepath=c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
        mediainfo();


    }

    public void preview(){
        c.moveToPosition(--index);
        filepath=c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
        mediainfo();


    }

    public void mediainfo(){
        Bitmap art = getAlbumart(filepath);
        if (art != null)
            albumart.setImageBitmap(art);
        else
            albumart.setImageResource(R.drawable.audiofile);

        String displayname = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        songfooter.setText(displayname);
        songfooter.setSelected(true);


        initMediaPlayer(filepath);
        mplayer.start();

        timer2(filepath);

        seekbar.setMax(mplayer.getDuration());
        updateSeekBar();

    }

}
