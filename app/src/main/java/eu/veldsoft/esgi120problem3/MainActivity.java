package eu.veldsoft.esgi120problem3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

public class MainActivity extends AppCompatActivity {
	/**
	 *
	 */
	private static final Checksum CHECKSUM = new CRC32();

	/**
	 *
	 */
	private static final int REQUEST_IMAGE_CAPTURE = 1;

	/**
	 * Distance between dots in small mesh in pixels.
	 */
	private static int SMALL_MESH_GAP_SIZE = 32;

	/**
	 * Distance between dots in big mesh in pixels.
	 */
	private static int BIG_MESH_GAP_SIZE = 128;

	private static int GRAY_CODES_8[][] = {
			  {0, 0, 0, 0, 0, 0, 0, 0,},
			  {0, 0, 0, 0, 0, 0, 0, 1,},
			  {0, 0, 0, 0, 0, 0, 1, 1,},
			  {0, 0, 0, 0, 0, 0, 1, 0,},
			  {0, 0, 0, 0, 0, 1, 1, 0,},
			  {0, 0, 0, 0, 0, 1, 1, 1,},
			  {0, 0, 0, 0, 0, 1, 0, 1,},
			  {0, 0, 0, 0, 0, 1, 0, 0,},
			  {0, 0, 0, 0, 1, 1, 0, 0,},
			  {0, 0, 0, 0, 1, 1, 0, 1,},
			  {0, 0, 0, 0, 1, 1, 1, 1,},
			  {0, 0, 0, 0, 1, 1, 1, 0,},
			  {0, 0, 0, 0, 1, 0, 1, 0,},
			  {0, 0, 0, 0, 1, 0, 1, 1,},
			  {0, 0, 0, 0, 1, 0, 0, 1,},
			  {0, 0, 0, 0, 1, 0, 0, 0,},
			  {0, 0, 0, 1, 1, 0, 0, 0,},
			  {0, 0, 0, 1, 1, 0, 0, 1,},
			  {0, 0, 0, 1, 1, 0, 1, 1,},
			  {0, 0, 0, 1, 1, 0, 1, 0,},
			  {0, 0, 0, 1, 1, 1, 1, 0,},
			  {0, 0, 0, 1, 1, 1, 1, 1,},
			  {0, 0, 0, 1, 1, 1, 0, 1,},
			  {0, 0, 0, 1, 1, 1, 0, 0,},
			  {0, 0, 0, 1, 0, 1, 0, 0,},
			  {0, 0, 0, 1, 0, 1, 0, 1,},
			  {0, 0, 0, 1, 0, 1, 1, 1,},
			  {0, 0, 0, 1, 0, 1, 1, 0,},
			  {0, 0, 0, 1, 0, 0, 1, 0,},
			  {0, 0, 0, 1, 0, 0, 1, 1,},
			  {0, 0, 0, 1, 0, 0, 0, 1,},
			  {0, 0, 0, 1, 0, 0, 0, 0,},
			  {0, 0, 1, 1, 0, 0, 0, 0,},
			  {0, 0, 1, 1, 0, 0, 0, 1,},
			  {0, 0, 1, 1, 0, 0, 1, 1,},
			  {0, 0, 1, 1, 0, 0, 1, 0,},
			  {0, 0, 1, 1, 0, 1, 1, 0,},
			  {0, 0, 1, 1, 0, 1, 1, 1,},
			  {0, 0, 1, 1, 0, 1, 0, 1,},
			  {0, 0, 1, 1, 0, 1, 0, 0,},
			  {0, 0, 1, 1, 1, 1, 0, 0,},
			  {0, 0, 1, 1, 1, 1, 0, 1,},
			  {0, 0, 1, 1, 1, 1, 1, 1,},
			  {0, 0, 1, 1, 1, 1, 1, 0,},
			  {0, 0, 1, 1, 1, 0, 1, 0,},
			  {0, 0, 1, 1, 1, 0, 1, 1,},
			  {0, 0, 1, 1, 1, 0, 0, 1,},
			  {0, 0, 1, 1, 1, 0, 0, 0,},
			  {0, 0, 1, 0, 1, 0, 0, 0,},
			  {0, 0, 1, 0, 1, 0, 0, 1,},
			  {0, 0, 1, 0, 1, 0, 1, 1,},
			  {0, 0, 1, 0, 1, 0, 1, 0,},
			  {0, 0, 1, 0, 1, 1, 1, 0,},
			  {0, 0, 1, 0, 1, 1, 1, 1,},
			  {0, 0, 1, 0, 1, 1, 0, 1,},
			  {0, 0, 1, 0, 1, 1, 0, 0,},
			  {0, 0, 1, 0, 0, 1, 0, 0,},
			  {0, 0, 1, 0, 0, 1, 0, 1,},
			  {0, 0, 1, 0, 0, 1, 1, 1,},
			  {0, 0, 1, 0, 0, 1, 1, 0,},
			  {0, 0, 1, 0, 0, 0, 1, 0,},
			  {0, 0, 1, 0, 0, 0, 1, 1,},
			  {0, 0, 1, 0, 0, 0, 0, 1,},
			  {0, 0, 1, 0, 0, 0, 0, 0,},
			  {0, 1, 1, 0, 0, 0, 0, 0,},
			  {0, 1, 1, 0, 0, 0, 0, 1,},
			  {0, 1, 1, 0, 0, 0, 1, 1,},
			  {0, 1, 1, 0, 0, 0, 1, 0,},
			  {0, 1, 1, 0, 0, 1, 1, 0,},
			  {0, 1, 1, 0, 0, 1, 1, 1,},
			  {0, 1, 1, 0, 0, 1, 0, 1,},
			  {0, 1, 1, 0, 0, 1, 0, 0,},
			  {0, 1, 1, 0, 1, 1, 0, 0,},
			  {0, 1, 1, 0, 1, 1, 0, 1,},
			  {0, 1, 1, 0, 1, 1, 1, 1,},
			  {0, 1, 1, 0, 1, 1, 1, 0,},
			  {0, 1, 1, 0, 1, 0, 1, 0,},
			  {0, 1, 1, 0, 1, 0, 1, 1,},
			  {0, 1, 1, 0, 1, 0, 0, 1,},
			  {0, 1, 1, 0, 1, 0, 0, 0,},
			  {0, 1, 1, 1, 1, 0, 0, 0,},
			  {0, 1, 1, 1, 1, 0, 0, 1,},
			  {0, 1, 1, 1, 1, 0, 1, 1,},
			  {0, 1, 1, 1, 1, 0, 1, 0,},
			  {0, 1, 1, 1, 1, 1, 1, 0,},
			  {0, 1, 1, 1, 1, 1, 1, 1,},
			  {0, 1, 1, 1, 1, 1, 0, 1,},
			  {0, 1, 1, 1, 1, 1, 0, 0,},
			  {0, 1, 1, 1, 0, 1, 0, 0,},
			  {0, 1, 1, 1, 0, 1, 0, 1,},
			  {0, 1, 1, 1, 0, 1, 1, 1,},
			  {0, 1, 1, 1, 0, 1, 1, 0,},
			  {0, 1, 1, 1, 0, 0, 1, 0,},
			  {0, 1, 1, 1, 0, 0, 1, 1,},
			  {0, 1, 1, 1, 0, 0, 0, 1,},
			  {0, 1, 1, 1, 0, 0, 0, 0,},
			  {0, 1, 0, 1, 0, 0, 0, 0,},
			  {0, 1, 0, 1, 0, 0, 0, 1,},
			  {0, 1, 0, 1, 0, 0, 1, 1,},
			  {0, 1, 0, 1, 0, 0, 1, 0,},
			  {0, 1, 0, 1, 0, 1, 1, 0,},
			  {0, 1, 0, 1, 0, 1, 1, 1,},
			  {0, 1, 0, 1, 0, 1, 0, 1,},
			  {0, 1, 0, 1, 0, 1, 0, 0,},
			  {0, 1, 0, 1, 1, 1, 0, 0,},
			  {0, 1, 0, 1, 1, 1, 0, 1,},
			  {0, 1, 0, 1, 1, 1, 1, 1,},
			  {0, 1, 0, 1, 1, 1, 1, 0,},
			  {0, 1, 0, 1, 1, 0, 1, 0,},
			  {0, 1, 0, 1, 1, 0, 1, 1,},
			  {0, 1, 0, 1, 1, 0, 0, 1,},
			  {0, 1, 0, 1, 1, 0, 0, 0,},
			  {0, 1, 0, 0, 1, 0, 0, 0,},
			  {0, 1, 0, 0, 1, 0, 0, 1,},
			  {0, 1, 0, 0, 1, 0, 1, 1,},
			  {0, 1, 0, 0, 1, 0, 1, 0,},
			  {0, 1, 0, 0, 1, 1, 1, 0,},
			  {0, 1, 0, 0, 1, 1, 1, 1,},
			  {0, 1, 0, 0, 1, 1, 0, 1,},
			  {0, 1, 0, 0, 1, 1, 0, 0,},
			  {0, 1, 0, 0, 0, 1, 0, 0,},
			  {0, 1, 0, 0, 0, 1, 0, 1,},
			  {0, 1, 0, 0, 0, 1, 1, 1,},
			  {0, 1, 0, 0, 0, 1, 1, 0,},
			  {0, 1, 0, 0, 0, 0, 1, 0,},
			  {0, 1, 0, 0, 0, 0, 1, 1,},
			  {0, 1, 0, 0, 0, 0, 0, 1,},
			  {0, 1, 0, 0, 0, 0, 0, 0,},
			  {1, 1, 0, 0, 0, 0, 0, 0,},
			  {1, 1, 0, 0, 0, 0, 0, 1,},
			  {1, 1, 0, 0, 0, 0, 1, 1,},
			  {1, 1, 0, 0, 0, 0, 1, 0,},
			  {1, 1, 0, 0, 0, 1, 1, 0,},
			  {1, 1, 0, 0, 0, 1, 1, 1,},
			  {1, 1, 0, 0, 0, 1, 0, 1,},
			  {1, 1, 0, 0, 0, 1, 0, 0,},
			  {1, 1, 0, 0, 1, 1, 0, 0,},
			  {1, 1, 0, 0, 1, 1, 0, 1,},
			  {1, 1, 0, 0, 1, 1, 1, 1,},
			  {1, 1, 0, 0, 1, 1, 1, 0,},
			  {1, 1, 0, 0, 1, 0, 1, 0,},
			  {1, 1, 0, 0, 1, 0, 1, 1,},
			  {1, 1, 0, 0, 1, 0, 0, 1,},
			  {1, 1, 0, 0, 1, 0, 0, 0,},
			  {1, 1, 0, 1, 1, 0, 0, 0,},
			  {1, 1, 0, 1, 1, 0, 0, 1,},
			  {1, 1, 0, 1, 1, 0, 1, 1,},
			  {1, 1, 0, 1, 1, 0, 1, 0,},
			  {1, 1, 0, 1, 1, 1, 1, 0,},
			  {1, 1, 0, 1, 1, 1, 1, 1,},
			  {1, 1, 0, 1, 1, 1, 0, 1,},
			  {1, 1, 0, 1, 1, 1, 0, 0,},
			  {1, 1, 0, 1, 0, 1, 0, 0,},
			  {1, 1, 0, 1, 0, 1, 0, 1,},
			  {1, 1, 0, 1, 0, 1, 1, 1,},
			  {1, 1, 0, 1, 0, 1, 1, 0,},
			  {1, 1, 0, 1, 0, 0, 1, 0,},
			  {1, 1, 0, 1, 0, 0, 1, 1,},
			  {1, 1, 0, 1, 0, 0, 0, 1,},
			  {1, 1, 0, 1, 0, 0, 0, 0,},
			  {1, 1, 1, 1, 0, 0, 0, 0,},
			  {1, 1, 1, 1, 0, 0, 0, 1,},
			  {1, 1, 1, 1, 0, 0, 1, 1,},
			  {1, 1, 1, 1, 0, 0, 1, 0,},
			  {1, 1, 1, 1, 0, 1, 1, 0,},
			  {1, 1, 1, 1, 0, 1, 1, 1,},
			  {1, 1, 1, 1, 0, 1, 0, 1,},
			  {1, 1, 1, 1, 0, 1, 0, 0,},
			  {1, 1, 1, 1, 1, 1, 0, 0,},
			  {1, 1, 1, 1, 1, 1, 0, 1,},
			  {1, 1, 1, 1, 1, 1, 1, 1,},
			  {1, 1, 1, 1, 1, 1, 1, 0,},
			  {1, 1, 1, 1, 1, 0, 1, 0,},
			  {1, 1, 1, 1, 1, 0, 1, 1,},
			  {1, 1, 1, 1, 1, 0, 0, 1,},
			  {1, 1, 1, 1, 1, 0, 0, 0,},
			  {1, 1, 1, 0, 1, 0, 0, 0,},
			  {1, 1, 1, 0, 1, 0, 0, 1,},
			  {1, 1, 1, 0, 1, 0, 1, 1,},
			  {1, 1, 1, 0, 1, 0, 1, 0,},
			  {1, 1, 1, 0, 1, 1, 1, 0,},
			  {1, 1, 1, 0, 1, 1, 1, 1,},
			  {1, 1, 1, 0, 1, 1, 0, 1,},
			  {1, 1, 1, 0, 1, 1, 0, 0,},
			  {1, 1, 1, 0, 0, 1, 0, 0,},
			  {1, 1, 1, 0, 0, 1, 0, 1,},
			  {1, 1, 1, 0, 0, 1, 1, 1,},
			  {1, 1, 1, 0, 0, 1, 1, 0,},
			  {1, 1, 1, 0, 0, 0, 1, 0,},
			  {1, 1, 1, 0, 0, 0, 1, 1,},
			  {1, 1, 1, 0, 0, 0, 0, 1,},
			  {1, 1, 1, 0, 0, 0, 0, 0,},
			  {1, 0, 1, 0, 0, 0, 0, 0,},
			  {1, 0, 1, 0, 0, 0, 0, 1,},
			  {1, 0, 1, 0, 0, 0, 1, 1,},
			  {1, 0, 1, 0, 0, 0, 1, 0,},
			  {1, 0, 1, 0, 0, 1, 1, 0,},
			  {1, 0, 1, 0, 0, 1, 1, 1,},
			  {1, 0, 1, 0, 0, 1, 0, 1,},
			  {1, 0, 1, 0, 0, 1, 0, 0,},
			  {1, 0, 1, 0, 1, 1, 0, 0,},
			  {1, 0, 1, 0, 1, 1, 0, 1,},
			  {1, 0, 1, 0, 1, 1, 1, 1,},
			  {1, 0, 1, 0, 1, 1, 1, 0,},
			  {1, 0, 1, 0, 1, 0, 1, 0,},
			  {1, 0, 1, 0, 1, 0, 1, 1,},
			  {1, 0, 1, 0, 1, 0, 0, 1,},
			  {1, 0, 1, 0, 1, 0, 0, 0,},
			  {1, 0, 1, 1, 1, 0, 0, 0,},
			  {1, 0, 1, 1, 1, 0, 0, 1,},
			  {1, 0, 1, 1, 1, 0, 1, 1,},
			  {1, 0, 1, 1, 1, 0, 1, 0,},
			  {1, 0, 1, 1, 1, 1, 1, 0,},
			  {1, 0, 1, 1, 1, 1, 1, 1,},
			  {1, 0, 1, 1, 1, 1, 0, 1,},
			  {1, 0, 1, 1, 1, 1, 0, 0,},
			  {1, 0, 1, 1, 0, 1, 0, 0,},
			  {1, 0, 1, 1, 0, 1, 0, 1,},
			  {1, 0, 1, 1, 0, 1, 1, 1,},
			  {1, 0, 1, 1, 0, 1, 1, 0,},
			  {1, 0, 1, 1, 0, 0, 1, 0,},
			  {1, 0, 1, 1, 0, 0, 1, 1,},
			  {1, 0, 1, 1, 0, 0, 0, 1,},
			  {1, 0, 1, 1, 0, 0, 0, 0,},
			  {1, 0, 0, 1, 0, 0, 0, 0,},
			  {1, 0, 0, 1, 0, 0, 0, 1,},
			  {1, 0, 0, 1, 0, 0, 1, 1,},
			  {1, 0, 0, 1, 0, 0, 1, 0,},
			  {1, 0, 0, 1, 0, 1, 1, 0,},
			  {1, 0, 0, 1, 0, 1, 1, 1,},
			  {1, 0, 0, 1, 0, 1, 0, 1,},
			  {1, 0, 0, 1, 0, 1, 0, 0,},
			  {1, 0, 0, 1, 1, 1, 0, 0,},
			  {1, 0, 0, 1, 1, 1, 0, 1,},
			  {1, 0, 0, 1, 1, 1, 1, 1,},
			  {1, 0, 0, 1, 1, 1, 1, 0,},
			  {1, 0, 0, 1, 1, 0, 1, 0,},
			  {1, 0, 0, 1, 1, 0, 1, 1,},
			  {1, 0, 0, 1, 1, 0, 0, 1,},
			  {1, 0, 0, 1, 1, 0, 0, 0,},
			  {1, 0, 0, 0, 1, 0, 0, 0,},
			  {1, 0, 0, 0, 1, 0, 0, 1,},
			  {1, 0, 0, 0, 1, 0, 1, 1,},
			  {1, 0, 0, 0, 1, 0, 1, 0,},
			  {1, 0, 0, 0, 1, 1, 1, 0,},
			  {1, 0, 0, 0, 1, 1, 1, 1,},
			  {1, 0, 0, 0, 1, 1, 0, 1,},
			  {1, 0, 0, 0, 1, 1, 0, 0,},
			  {1, 0, 0, 0, 0, 1, 0, 0,},
			  {1, 0, 0, 0, 0, 1, 0, 1,},
			  {1, 0, 0, 0, 0, 1, 1, 1,},
			  {1, 0, 0, 0, 0, 1, 1, 0,},
			  {1, 0, 0, 0, 0, 0, 1, 0,},
			  {1, 0, 0, 0, 0, 0, 1, 1,},
			  {1, 0, 0, 0, 0, 0, 0, 1,},
			  {1, 0, 0, 0, 0, 0, 0, 0,},
	};

	/**
	 *
	 */
	private String PUBLIC_KEY = "";

	/**
	 *
	 */
	private String PRIVATE_KEY = "";

	/**
	 * Extract all bits from byte array.
	 *
	 * @param bytes Bytes array.
	 * @return All bits from byte array as single values in integer array.
	 */
	private int[] extractBits(byte[] bytes) {
		int result[] = new int[bytes.length * 8];

		int word[] = new int[8];
		for (int i = 0, k = 0; i < bytes.length; i++) {
			if (bytes[i] < 0) {
				word[7] = 1;
				bytes[i] *= -1;
			} else {
				word[7] = 0;
			}

			for (int j = 0; j < 7; j++) {
				word[j] = bytes[i] % 2;
				bytes[i] >>= 1;
			}

			/*
			 * Revers bits in order to be MSB first and LSB last.
			 */
			for (int j = word.length - 1; j >= 0; j--) {
				result[k++] = word[j];
			}
		}

		return result;
	}

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
		for (int j = 0, gap = SMALL_MESH_GAP_SIZE / 2, k = 0; j < height; j++) {
			for (int i = 0; i < width; i++, k++) {
				mask[k] = 0xFFFFFF;

				if (i % SMALL_MESH_GAP_SIZE == 0 && j % SMALL_MESH_GAP_SIZE == 0) {
					/*
					 * Less significant bit.
					 */
					mask[k] &= 0xFEFEFE;
				} else if ((gap + i) % BIG_MESH_GAP_SIZE == 0 && (gap + j) % BIG_MESH_GAP_SIZE == 0) {
					/*
					 * Third less significant bit.
					 */
					mask[k] &= 0xFBFBFB;
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
	 * @return Mask wich will be used for watermarking.
	 */
	private int[] zeroWatarmarkBits(int pixels[], int width, int height) {
		if (pixels == null || pixels.length != width * height) {
			//TODO Exception handling.
		}

		int[] mask = watermarkBitsMaskGeneration(width, height);

		for (int k = 0; k < mask.length && k < pixels.length; k++) {
			pixels[k] &= mask[k];
		}

		return mask;
	}

	/**
	 * @param pixels
	 * @param key
	 * @return
	 */
	private byte[] signImage(int pixels[], String key) {
		/*
		 * Convert int array to byte array.
		 */
		ByteBuffer buffer = ByteBuffer.allocate(pixels.length * 4);
		buffer.asIntBuffer().put(pixels);
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			generator.initialize(1024, random);

			Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
			signature.initSign(generator.generateKeyPair().getPrivate());
			signature.update(buffer);

			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

		return new byte[0];
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
	private void saveImageToFile(Bitmap image, long crcs[], String name) {
		/*
		 * Temporary file without meta data written.
		 */
		File noMetadataPng = new File("" + System.currentTimeMillis() + ".png");

		/*
		 * Store PNG file in the local file system.
		 */
		try {
			FileOutputStream out = new FileOutputStream(noMetadataPng);
			image.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Put image size and CRCs in file meta data.
		 */
		PngReader reader = new PngReader(noMetadataPng);
		PngWriter writer = new PngWriter(new File(name), reader.imgInfo, true);
		writer.copyChunksFrom(reader.getChunksList(), ChunkCopyBehaviour.COPY_ALL);

		//TODO Create parameters for keys as constants or as meta data in manifest file.
		writer.getMetadata().setText("width", "" + image.getWidth());
		writer.getMetadata().setText("heigth", "" + image.getHeight());

		/*
		 * Add CRC values.
		 */
		for (int i = 0; i < crcs.length; i++) {
			writer.getMetadata().setText("CRC" + i, "" + crcs[i]);
		}

		/*
		 * Copy image information.
		 */
		for (int row = 0; row < reader.imgInfo.rows; row++) {
			writer.writeRow(reader.readRow());
		}

		/*
		 * Close files.
		 */
		reader.end();
		writer.end();
	}

	/**
	 * Generate array with CRC codes by blocks.
	 *
	 * @param pixels Array with RGB image pixels.
	 * @param width  Width of the image.
	 * @param height Height of the image.
	 * @return Array of blocks CRCs.
	 */
	private long[] calculateCRCs(int[] pixels, int width, int height) {
		/*
		 * Multiply by 3 for each base color chanel.
		 */
		byte[] pixelsAsBytes = new byte[SMALL_MESH_GAP_SIZE * SMALL_MESH_GAP_SIZE * 3];


		/*
		 * CRC codes for all small square blocks and no-square edges.
		 */
		long[] crcCodes = new long[(int) Math.ceil((double) width / SMALL_MESH_GAP_SIZE) * (int) Math.ceil((double) height / SMALL_MESH_GAP_SIZE)];

		/*
		 * Calculate CRCs.
		 */
		for (int q = 0, l = 0; q < height; q += SMALL_MESH_GAP_SIZE) {
			for (int p = 0; p < width; p += SMALL_MESH_GAP_SIZE, l++) {
				Arrays.fill(pixelsAsBytes, (byte) 0);

				/*
				 * Analyse squares.
				 */
				for (int j = 0, k = 0, color; j < SMALL_MESH_GAP_SIZE && (q + j) < height; j++) {
					for (int i = 0; i < SMALL_MESH_GAP_SIZE && (p + i) < width; i++, k += 3) {
						color = pixels[(p + i) + width * (q + j)];
						pixelsAsBytes[k + 0] = (byte) ((color >> 16) & 0xFF);
						pixelsAsBytes[k + 1] = (byte) ((color >> 8) & 0xFF);
						pixelsAsBytes[k + 2] = (byte) ((color >> 0) & 0xFF);
					}
				}

				CHECKSUM.update(pixelsAsBytes, 0, pixelsAsBytes.length);
				crcCodes[l] = CHECKSUM.getValue();
			}
		}

		return crcCodes;
	}

	/**
	 * Put gray codes in the less significant bits as mash.
	 *
	 * @param pixels Array with RGB image pixels.
	 * @param width  Width of the image.
	 * @param height Height of the image.
	 */
	private void grayCodeImage(int[] pixels, int width, int height) {
		/*
		 * Loop over image diagonally.
		 */
		for (int j1 = 0, k = 0, l = 0; j1 < 2 * height; j1 += SMALL_MESH_GAP_SIZE) {
			for (int i = 0; i <= j1; i += SMALL_MESH_GAP_SIZE) {
				/*
				 * Calculate index for the diagonal order.
				 */
				int j = j1 - i;

				/*
				 * Check for array bounds.
				 */
				if (i >= width || j >= height) {
					continue;
				}

				/*
				 * Loop over red, green and blue channels.
				 */
				for (int c = 0; c <= 16; c += 8) {
					pixels[i + width * j] &= (0xFFFFFF & (GRAY_CODES_8[k][l] << c));

					/*
					 * Move across gray bits array.
					 */
					l++;
					if (l >= GRAY_CODES_8[k].length) {
						l = 0;
						k++;
					}
					if (k >= GRAY_CODES_8.length) {
						k = 0;
					}
				}
			}
		}
	}

	/**
	 * Put digital signature as watermark in the third less significant bits.
	 *
	 * @param signature Digital signature.
	 * @param pixels    Array with RGB image pixels.
	 * @param width     Width of the image.
	 * @param height    Height of the image.
	 */
	private void watermarkImage(byte[] signature, int[] pixels, int width, int height) {
		int bits[] = extractBits(signature);

		/*
		 * Loop over big mesh points.
		 */
		for (int j = SMALL_MESH_GAP_SIZE / 2, k = 0; j < height; j += BIG_MESH_GAP_SIZE) {
			for (int i = SMALL_MESH_GAP_SIZE / 2; i < width; i += BIG_MESH_GAP_SIZE) {
				/*
				 * Loop over red, green and blue channels.
				 */
				for (int c = 0; c <= 16; c += 8) {
					/*
					 * Third less significant bits are used.
					 */
					pixels[i + width * j] &= (0xFFFFFF & (bits[k] << (c + 3)));

					/*
					 * Loop over digital signature bits.
					 */
					k = (k + 1) % bits.length;
				}
			}
		}
	}

	/**
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/**
	 * @param view
	 */
	public void takePhoto(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	/**
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
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
			int[] mask = zeroWatarmarkBits(pixels, bitmap.getWidth(), bitmap.getHeight());

			/*
			 * CRC codes generation.
			 */
			long[] crcCodes = calculateCRCs(pixels, bitmap.getWidth(), bitmap.getHeight());

			/*
			 * DSA digital sign.
			 */
			byte[] signature = signImage(pixels, PUBLIC_KEY);

			/*
			 * Gray codes mash generation into image.
			 */
			grayCodeImage(pixels, bitmap.getWidth(), bitmap.getHeight());

			/*
			 * Watermarking with digital stamp.
			 */
			watermarkImage(signature, pixels, bitmap.getWidth(), bitmap.getHeight());

			/*
			 * SNR calculation.
			 */
			int original[] = new int[bitmap.getWidth() * bitmap.getHeight()];
			bitmap.getPixels(original, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
			double snr[] = calculateSignalToNoiceRatio(original, pixels);

			/*
			 * Save bitmap image file.
			 */
			Bitmap watermarked = Bitmap.createBitmap(pixels, 0, bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			saveImageToFile(watermarked, crcCodes, "" + System.currentTimeMillis() + ".png");

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
}
