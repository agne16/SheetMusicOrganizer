package agne.myproject.sheetmusicorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

public class MyMultiAutoCompleteTextView extends MultiAutoCompleteTextView {

	private static String currImage;

	public MyMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void replaceText(CharSequence text)
	{
		this.setText(text);

		currImage = MainActivity.getDirectory() + "/" + text;
		setCurrImage(currImage);		
	}

	public static String getCurrImage(){
		return currImage;
	}

	public static void setCurrImage(String init){
		currImage = init;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(currImage, options);
//		if(bm.getHeight() > 2048 || bm.getWidth() > 2048)
//		{
//			options.inSampleSize = 4;
//		}
//		else
//		{
//			options.inSampleSize = 2;
//		}
		bm = BitmapFactory.decodeFile(currImage, options);
		MainActivity.getImageView().setImageBitmap(bm);
	}




}
