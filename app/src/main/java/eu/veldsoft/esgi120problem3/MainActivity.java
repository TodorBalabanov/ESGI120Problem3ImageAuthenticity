package eu.veldsoft.esgi120problem3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
	/**
	 *
	 */
	private static final int REQUEST_IMAGE_CAPTURE = 1;

	/**
	 *
	 */
	private String PUBLIC_KEY = "000";

	/**
	 *
	 */
	private String PRIVATE_KEY = "000";

	/**
	 * Time stamp for the moment of image shoot.
	 */
	private String timestamp = "";

	/**
	 * Path to folder for the images.
	 */
	private File path = Environment.getExternalStoragePublicDirectory(
			  Environment.DIRECTORY_PICTURES);


	/**
	 * Take photo request.
	 *
	 * @param view
	 */
	public void shoot(View view) {
		timestamp = "" + System.currentTimeMillis();

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (intent.resolveActivity(getPackageManager()) == null) {
			return;
		}

		File store = null;
		try {
			store = File.createTempFile("camera" + timestamp, ".png", path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(store == null) {
			return;
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(store));

		startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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

		//TODO Do all time consuming calculations in separate thread.

		/*
		 * Image information as array of RGB pixels.
		 */
		int pixels[] = new int[bitmap.getWidth() * bitmap.getHeight()];

		/*
		 * Obtain image pixels as bytes array.
		 */
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		Util.savePixelsToFile(this, pixels, bitmap.getWidth(), bitmap.getHeight(), new File(path, "original" + timestamp + ".png"));

		/*
		 * Put zeros all bits which will be used in the watermarking process.
		 */
		int[] mask = Util.zeroWatarmarkBits(Util.watermarkBitsMaskGeneration(bitmap.getWidth(), bitmap.getHeight()), pixels, bitmap.getWidth(), bitmap.getHeight());
		Util.savePixelsToFile(this, pixels, bitmap.getWidth(), bitmap.getHeight(), new File(path, "meshed" + timestamp + ".png"));

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
		Util.savePixelsToFile(this, pixels, bitmap.getWidth(), bitmap.getHeight(), new File(path, "grayed" + timestamp + ".png"));

		/*
		 * Watermarking with digital stamp.
		 */
		Util.watermarkImage(signature, pixels, bitmap.getWidth(), bitmap.getHeight());
		Util.savePixelsToFile(this, pixels, bitmap.getWidth(), bitmap.getHeight(), new File(path, "watermarked" + timestamp + ".png"));

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
		Util.saveImageToFile(this, watermarked, crcCodes, path, "final" + timestamp + ".png");

		/*
		 * Show image in the view.
		 */
		((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);

		/*
		 * Report signal to noise ratio in the user interface.
		 */
		String text = "";
		for (int i = 0; i < snr.length; i++) {
			text += snr[i];
			text += "\n";
		}
		text = text.trim();
		((TextView) findViewById(R.id.text)).setText(text);
	}
}
