package eu.veldsoft.esgi120problem3;

import android.graphics.Bitmap;

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

/**
 */
final class Util {
	/*
	 *
	 */
	private static final Checksum CHECKSUM = new CRC32();

	/**
	 * Distance between dots in small mesh in pixels.
	 */
	private static int SMALL_MESH_GAP_SIZE = 32;

	/**
	 * Distance between dots in big mesh in pixels.
	 */
	private static int BIG_MESH_GAP_SIZE = 128;

	/**
	 * Gray codes values.
	 */
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
	 * Extract all bits from byte array.
	 *
	 * @param bytes Bytes array.
	 * @return All bits from byte array as single values in integer array.
	 */
	static int[] extractBits(byte[] bytes) {
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
	static int[] watermarkBitsMaskGeneration(int width, int height) {
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
	static int[] zeroWatarmarkBits(int mask[], int pixels[], int width, int height) {
		if (pixels == null || pixels.length != width * height) {
			//TODO Exception handling.
		}

		for (int k = 0; k < mask.length && k < pixels.length; k++) {
			pixels[k] &= mask[k];
		}

		return mask;
	}

	/**
	 * Digital signing of the image.
	 *
	 * @param pixels Array with RGB image pixels.
	 * @param key    Key to sign with.
	 * @return Signature bytes.
	 */
	static byte[] signImage(int pixels[], String key) {
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
	 * Signal to noise ratio calculation.
	 *
	 * @param original    The origial image as RGB pixesl array.
	 * @param watermarked The watermarked image as RGB pixels array.
	 * @return Three values SNR for each channel (red, green, blue).
	 */
	static double[] calculateSignalToNoiceRatio(int original[], int watermarked[]) {
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
	static void saveImageToFile(Bitmap image, long crcs[], String name) {
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
	static long[] calculateCRCs(int[] pixels, int width, int height) {
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
	static void grayCodeImage(int[] pixels, int width, int height) {
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
	static void watermarkImage(byte[] signature, int[] pixels, int width, int height) {
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
	 *
	 */
	private Util() {
	}
}
