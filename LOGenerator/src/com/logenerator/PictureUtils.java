package com.logenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.core.util.Base64Encoder;

import android.graphics.Bitmap;
import android.util.Log;

public class PictureUtils {
	public static String getImageHexaBase64(Bitmap bitmap, String imageformat) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d("PictureUtils","bitmap  "+bitmap);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		try {
			stream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    byte[] resultImageAsRawBytes = stream.toByteArray();
	String encodedString = new Base64Encoder().encode(resultImageAsRawBytes);
	return encodedString;
}
}
