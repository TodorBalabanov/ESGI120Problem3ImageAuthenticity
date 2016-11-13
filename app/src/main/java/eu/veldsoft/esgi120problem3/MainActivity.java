package eu.veldsoft.esgi120problem3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	 /**
	  *
	  */
	 private static final int REQUEST_IMAGE_CAPTURE = 1;

	 /**
	  *
	  */
	 private String PUBLIC_KEY = "";

	 /**
	  *
	  */
	 private String PRIVATE_KEY = "";

	 /**
	  * Take photo request.
	  *
	  * @param view
	  */
	 private void takePhoto(View view) {
		  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		  }
	 }

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_main);
	 }

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode != REQUEST_IMAGE_CAPTURE) {
				return;
		  }

		  if (resultCode != RESULT_OK) {
				return;
		  }

		  Bitmap bitmap = (Bitmap) data.getExtras().get("data");
		  ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

		  //TODO Do all time consuming calculations in separate thread.

		/*
		 * Image information as array of RGB pixels.
		 */
		  int pixels[] = new int[bitmap.getWidth() * bitmap.getHeight()];

		/*
		 * Obtain image pixels as bytes array.
		 */
		  bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		/*
		 * Put zeros all bits which will be used in the watermarking process.
		 */
		  int[] mask = Util.zeroWatarmarkBits(Util.watermarkBitsMaskGeneration(bitmap.getWidth(), bitmap.getHeight()), pixels, bitmap.getWidth(), bitmap.getHeight());

		/*
		 * CRC codes generation.
		 */
		  long[] crcCodes = Util.calculateCRCs(pixels, bitmap.getWidth(), bitmap.getHeight());

		/*
		 * DSA digital sign.
		 */
		  byte[] signature = Util.signImage(pixels, PUBLIC_KEY);

		/*
		 * Gray codes mash generation into image.
		 */
		  Util.grayCodeImage(pixels, bitmap.getWidth(), bitmap.getHeight());

		/*
		 * Watermarking with digital stamp.
		 */
		  Util.watermarkImage(signature, pixels, bitmap.getWidth(), bitmap.getHeight());

		/*
		 * SNR calculation.
		 */
		  int original[] = new int[bitmap.getWidth() * bitmap.getHeight()];
		  bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		  double snr[] = Util.calculateSignalToNoiceRatio(original, pixels);

		/*
		 * Save bitmap image file.
		 */
		  Bitmap watermarked = Bitmap.createBitmap(pixels, 0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		  Util.saveImageToFile(watermarked, crcCodes, "" + System.currentTimeMillis() + ".png");

		/*
		 * Report signal to noise ratio in the user interface.
		 */
		  String text = "";
		  for (int i = 0; i < snr.length; i++) {
				text += snr[i];
				text += "\t";
		  }
		  text = text.trim();
		  ((TextView) findViewById(R.id.textView)).setText(text);
	 }
}
