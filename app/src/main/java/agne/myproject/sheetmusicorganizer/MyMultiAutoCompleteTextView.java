package agne.myproject.sheetmusicorganizer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;

public class MyMultiAutoCompleteTextView extends MultiAutoCompleteTextView {

    private static String currImage;

    public MyMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void replaceText(CharSequence text) {
        this.setText(text);
        currImage = MainActivity.getDirectory() + "/" + text;
        //BitmapWorkerTask.setCurrImage(currImage);

        ImageView imageView = MainActivity.getImageView();
        BitmapWorkerTask bitTask = new BitmapWorkerTask(this.getContext(), imageView);
        bitTask.execute(currImage);
    }

    public static String getCurrImage() {
        return currImage;
    }


}
