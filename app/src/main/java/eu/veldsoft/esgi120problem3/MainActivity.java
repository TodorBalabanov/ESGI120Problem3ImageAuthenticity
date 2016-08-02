package eu.veldsoft.esgi120problem3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class MainActivity extends AppCompatActivity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	/**
	 * Distance between dots in small mesh in pixels.
	 */
	private static int SMALL_MESH_GAP_SIZE = 32;
	/**
	 * Distance between dots in big mesh in pixels.
	 */
	private static int BIG_MESH_GAP_SIZE = 128;
	/**
	 * Public key generated by: http://travistidwell.com/blog/2013/09/06/an-online-rsa-public-and-private-key-generator/
	 */
	private String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
			  "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClA5cKzu5XEGRvDjuJOG73JKiJ\n" +
			  "7P7G+qxnAYKlzkfE7QXYopiKhX8cFp6sD0W+amR/HOrOBU4DGqazwqpU1kwMLag1\n" +
			  "r7W1DXJg69EIIznpo18PDN7mYGa0u9FdW5ENKkfTUUvWqMi098ObCC/p2xCTjmEQ\n" +
			  "x5HQY3s+eJsLZK0UbQIDAQAB\n" +
			  "-----END PUBLIC KEY-----";
	/**
	 * Private key generated by: http://travistidwell.com/blog/2013/09/06/an-online-rsa-public-and-private-key-generator/
	 */
	private String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
			  "MIICXQIBAAKBgQClA5cKzu5XEGRvDjuJOG73JKiJ7P7G+qxnAYKlzkfE7QXYopiK\n" +
			  "hX8cFp6sD0W+amR/HOrOBU4DGqazwqpU1kwMLag1r7W1DXJg69EIIznpo18PDN7m\n" +
			  "YGa0u9FdW5ENKkfTUUvWqMi098ObCC/p2xCTjmEQx5HQY3s+eJsLZK0UbQIDAQAB\n" +
			  "AoGARkizKs1cwwSeYpcDUL0Stn2Ms8KX+hSHHhCMnyavdvclyFHo+wdFTqdryglv\n" +
			  "QV17lJCyijHEOpo9as99UUk9djloaaUUDr7LCklGyqxlwlyq66Ud/qzgtK1v4XGt\n" +
			  "5jneRs3wgizUPFNoXQct692SkJ0He8ere8LHh8VRBit3RVkCQQDuqyC92jo8aA4W\n" +
			  "VTQSTeRxWicT5JoIURV/kV0Gptmle3GLGJhKlAOOV8rlU5ldZ2CRT/tf0zWqZZ9y\n" +
			  "5lGMMn43AkEAsP832rZgGkzp5Wm+W0bTJ4QIMv83PiLZyCh12m7ClsAo8tUqi9r9\n" +
			  "Y0ngUWwf9mJlq92XDfw9KGP5Lx1yCDEQewJAFfWs54sCvLgeQ7PHPL/p+vv+iHgK\n" +
			  "LCW5wqkPVCNZ9z3qbo/uwz3nLduqEXulqtBuNDCVwnVehLUg/KNwcWPb9QJBAJna\n" +
			  "wCqWLaOvCAIrkRS21AWdd6McxmB02upqgUeG0A9Kqk2rjnhTu777EMq2OnJpxgdH\n" +
			  "b27wvBjIDmsuJVmJjNECQQDSoShRrwKLsfU9f1A3THQIZrC71TBRKawbT+jAzBQ+\n" +
			  "F0oTkJk+Fawestlx8mr+2MHd1W92fEnb8eGn9wfNMRUC\n" +
			  "-----END RSA PRIVATE KEY-----";

	/**
	 * Generate mask for watermark bits,
	 *
	 * @param width  Image width.
	 * @param height Image height.
	 * @return Mask for the bits.
	 */
	private int[] watermarkBitsMaskGeneration(int width, int height) {
		int mask[] = new int[width * height];

		//TODO Check which is column and which is row.
		for (int j = 0, k = 0; j < height; j++) {
			for (int i = 0; i < width; i++, k++) {
				mask[k] = 0xFFFFFF;

				if (i % SMALL_MESH_GAP_SIZE == 0 && j % SMALL_MESH_GAP_SIZE == 0) {
					mask[k] &= 0xFEFEFE;
				} else if (i % BIG_MESH_GAP_SIZE == 0 && j % BIG_MESH_GAP_SIZE == 0) {
					mask[k] &= 0xEFEFEF;
				}
			}
		}

		return mask;
	}

	/**
	 * Put zeros in each bit which will be used during watarmarking process.
	 *
	 * @param pixels Array with RGB image pixels.
	 * @param width  Width of the image.
	 * @param height Height of the image.
	 */
	private void zeroWatarmarkBits(int pixels[], int width, int height) {
		if (pixels == null || pixels.length != width * height) {
			//TODO Exception handling.
		}

		int[] mask = watermarkBitsMaskGeneration(width, height);

		for (int k = 0; k < mask.length && k < pixels.length; k++) {
			pixels[k] &= mask[k];
		}
	}

	private byte[] signImage(int pixels[], String key) {
		/*
		 * Convert int array to byte array.
		 */
		ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
		buffer.asIntBuffer().put(pixels);
		try {
			/*
			 * Obtain cipher object.
			 */
			//Cipher cipher = Cipher.getInstance("RSA");
			//cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key,Base64.CRLF))));

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(priv);
			dsa.update(buffer);

			return dsa.sign();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Singal to noise ratio calculation.
	 *
	 * @param original    The origial image as RGB pixesl array.
	 * @param watermarked The watermarked image as RGB pixels array.
	 * @return Three values SNR for each channel (red, green, blue).
	 */
	private double[] calculateSignalToNoiceRatio(int original[], int watermarked[]) {
		if (original == null || watermarked == null || original.length != watermarked.length) {
			//TODO Rise an exception.
		}

		double rgb[] = {0, 0, 0};

		int value;
		for (int k = 0; k < original.length && k < watermarked.length; k++) {
			/*
			 * Red channel.
			 */
			value = (original[k] >> 16) & 0xFF - (watermarked[k] >> 16) & 0xFF;
			rgb[0] += value * value;

			/*
			 * Green channel.
			 */
			value = (original[k] >> 8) & 0xFF - (watermarked[k] >> 8) & 0xFF;
			rgb[1] += value * value;

			/*
			 * Blue channel.
			 */
			value = (original[k] >> 0) & 0xFF - (watermarked[k] >> 0) & 0xFF;
			rgb[2] += value * value;
		}

        /*
         * Normalize sum as mean square error.
         */
		rgb[0] /= (double) original.length;
		rgb[1] /= (double) original.length;
		rgb[2] /= (double) original.length;

		final int MAX_INTENSITY_VALUE = 255;

        /*
         * Logarithm transform in order to get dB.
         */
		rgb[0] = 10D * Math.log10(MAX_INTENSITY_VALUE * MAX_INTENSITY_VALUE / rgb[0]);
		rgb[1] = 10D * Math.log10(MAX_INTENSITY_VALUE * MAX_INTENSITY_VALUE / rgb[1]);
		rgb[2] = 10D * Math.log10(MAX_INTENSITY_VALUE * MAX_INTENSITY_VALUE / rgb[2]);

		return rgb;
	}

	/**
	 * Save watermarked image in the local device storage.
	 *
	 * @param image Bitmap object.
	 * @param name  Name of the file.
	 */
	private void saveImageToFile(Bitmap image, String name) {
		try {
			FileOutputStream out = new FileOutputStream(new File(name));

			image.compress(Bitmap.CompressFormat.PNG, 100, out);

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private long[] calculateCRCs(int[] pixels, int width, int height) {
		int[][] pixels2D = new int[height][width];

		for (int i = 0, k = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pixels2D[i][j] = pixels[k++];
			}
		}

		Checksum checksum = new CRC32();
		byte[] pixelsAsBytes = new byte[SMALL_MESH_GAP_SIZE * SMALL_MESH_GAP_SIZE * 4];
		long[] CRCs = new long[(height / SMALL_MESH_GAP_SIZE) * (width / SMALL_MESH_GAP_SIZE)];
		int L = 0;
		for (int p = 0; p < height / SMALL_MESH_GAP_SIZE; p++) {
			for (int q = 0; q < width / SMALL_MESH_GAP_SIZE; q++) {

				for (int i = 0, k = 0; i < SMALL_MESH_GAP_SIZE; i++) {
					for (int j = 0; j < SMALL_MESH_GAP_SIZE; j++, k++) {
						pixelsAsBytes[k] = (byte) ((pixels2D[i][j] >> 24) & 0xFF); //Alpha
						pixelsAsBytes[k + 1] = (byte) ((pixels2D[i][j] >> 16) & 0xFF); //Red
						pixelsAsBytes[k + 2] = (byte) ((pixels2D[i][j] >> 8) & 0xFF); //Green
						pixelsAsBytes[k + 3] = (byte) (pixels2D[i][j] & 0xFF); //Blue
					}
				}
				checksum.update(pixelsAsBytes, 0, pixelsAsBytes.length);
				CRCs[L++] = checksum.getValue();
			}
		}

		return CRCs;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void takePhoto(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
			zeroWatarmarkBits(pixels, bitmap.getWidth(), bitmap.getHeight());

            /*
             * DSA digital sign.
             */
			byte[] signature = signImage(pixels, PUBLIC_KEY);

            /*
             * CRC codes generation.
             */
			long[] CRCs = calculateCRCs(pixels, bitmap.getWidth(), bitmap.getHeight());

			//TODO Watermarking with digital stamp and CRC codes.
			int[] watermarked = null;//watermarkTheImage(pixels, digitalSignature);

			//TODO Meta data createion.

            /*
             * SNR calculation.
             */
			calculateSignalToNoiceRatio(pixels, watermarked);

            /*
             * Save bitmap image file.
             */
			saveImageToFile(Bitmap.createBitmap(pixels, 0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888), "" + System.currentTimeMillis() + ".png");
		}
	}
}
