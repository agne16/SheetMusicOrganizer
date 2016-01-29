package agne.myproject.sheetmusicorganizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * TODO: Swipe left/right to change
 * Number Scroll
 */
public class MainActivity extends Activity implements OnTouchListener, OnDragListener {

    final Context context = this;

    private static File databaseDirectory;
    private static File sdCardRoot;
    private static ImageView imageView;
    private MyMultiAutoCompleteTextView textView;
    private String[] nameArray;
    private File currDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set path of default storage
        sdCardRoot = Environment.getExternalStorageDirectory();

        //retrieve saved database directory, if not present set to Sheet Music
        SharedPreferences settings = getSharedPreferences("Config", 0);
        String directory = settings.getString("directory", sdCardRoot.getPath() + "/Sheet Music");

        //set database and current directory to that of the saved file
        databaseDirectory = new File(directory);
        currDir = new File(directory);

        //if there is no such directory, prompt the user
        if (!currDir.isDirectory()) {
            noDatabaseDialog();
        }

        //creates text view and listeners
        textView = (MyMultiAutoCompleteTextView) findViewById(R.id.mactv);
        textView.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        textView.setOnTouchListener(this);

        initializeDatabase();

        imageView = (ImageView) findViewById(R.id.imageView1);
        imageView.setOnTouchListener(this);
        imageView.setOnDragListener(this);

        ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(findViewById(R.id.imageView1).getWindowToken(), 0);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v == textView) {
            textView.setText("");
        }
        else if (v == imageView) {
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(findViewById(R.id.imageView1).getWindowToken(), 0);
            imageView.setFocusable(true);
            imageView.setFocusableInTouchMode(true);
            imageView.requestFocus();
        }
        return false;
    }

    public void initializeDatabase() {

        Hashtable<Integer, File> database = new Hashtable<Integer, File>();

        try {
            int count = 1;
            for (File f : databaseDirectory.listFiles()) {
                if (f.isFile()) {
                    database.put(count, f);
                    ++count;
                }
            }
        } catch (NullPointerException npe) {
            System.out.println("Folder does not contain files");

        }

        Enumeration<File> blah = database.elements();
        String name = "";
        String[] filenames = new String[database.size()];
        int x = 0;
        while (blah.hasMoreElements()) {
            File file = blah.nextElement();
            name = file.getName();
            filenames[x] = name;
            ++x;
        }
        Arrays.sort(filenames);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, filenames);
        textView.setAdapter(adapter);
        textView.setTokenizer(new MyMultiAutoCompleteTextView.CommaTokenizer());

        textView.setText("Current directory: " + databaseDirectory.getPath());
    }

    /**
     * showDialogue - creates and displays a startup window
     */
    public void noDatabaseDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("WELCOME");

        // set dialog message
        alertDialogBuilder.setMessage(R.string.first_run)
                .setCancelable(false)

                .setNegativeButton("SPECIFY", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int id) {
                        chooseDirectory();
                    }
                })
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File sdCardRoot = Environment.getExternalStorageDirectory();
                        File directory = new File(sdCardRoot, "/Sheet Music");
                        directory = new File(sdCardRoot + "/Sheet Music");
                        if (directory.mkdirs()) {
                            Log.w("my app", "success");
                        }
                        else {
                            Log.w("my app", "fail");
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * showDialogue - creates and displays a startup window
     */
    public void chooseDirectory() {
        if (!currDir.isDirectory()) {
            currDir = sdCardRoot;
        }
        File[] files = currDir.listFiles();
        ArrayList<String> nameList = new ArrayList<String>();

        //		if(!currDir.equals(sdCardRoot))
        //		{
        nameList.add("...");
        //		}

        if (files != null) {
            for (File inFile : files)
                if (inFile.isDirectory()) {
                    nameList.add(inFile.getName());
                }
            nameArray = new String[nameList.size()];
            nameArray = nameList.toArray(nameArray);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle(currDir.getPath());

        // set dialog message
        alertDialogBuilder
                .setItems(nameArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setDirectory(which);
                    }
                })
                .setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int id) {
                        noDatabaseDialog();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseDirectory = currDir;
                        // We need an Editor object to make preference changes.
                        // All objects are from android.context.Context
                        SharedPreferences settings = getSharedPreferences("Config", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("directory", databaseDirectory.getPath());

                        // Commit the edits!
                        editor.commit();
                        initializeDatabase();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void setDirectory(int which) {
        String dir = currDir.getPath();
        if (nameArray[which].equals("...")) {
            currDir = new File(dir.substring(0, dir.lastIndexOf('/')));
        }
        else {
            currDir = new File(dir + "/" + nameArray[which]);
        }
        chooseDirectory();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // respond to menu item selection
        int itemId = item.getItemId();
        if (itemId == R.id.set_directory) {
            chooseDirectory();
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences("Config", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("directory", databaseDirectory.getPath());

        // Commit the edits!
        editor.commit();
    }

    public static File getDirectory() {
        return databaseDirectory;
    }

    public static ImageView getImageView() {
        return imageView;
    }

    /*
     * onStaveInstanceState - saves the state before rotation happens (plus some other things)
     * */
    protected void onSaveInstanceState(Bundle currState) {
        //store the boolean
        currState.putString("image", MyMultiAutoCompleteTextView.getCurrImage());
        super.onSaveInstanceState(currState);

    }

    /*
     * onRestoreInstanceState - restores the state back into the system after rotate happens
     */
    protected void onRestoreInstanceState(Bundle currState) {
        BitmapWorkerTask task = new BitmapWorkerTask(context, imageView);
        task.execute(currState.getString("image", ""));
        //BitmapWorkerTask.setCurrImage(currState.getString("image", ""));
        super.onRestoreInstanceState(currState);
    }

    @Override
    public boolean onDrag(View arg0, DragEvent arg1) {
        // TODO Auto-generated method stub
        return false;
    }


}
