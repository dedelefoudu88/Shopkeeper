package com.example.shopkeeper.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.example.shopkeeper.Manager.CallBack;
import com.example.shopkeeper.Manager.ProductManager;
import com.example.shopkeeper.Manager.ShooperKeeperException;
import com.example.shopkeeper.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class Image extends Activity {

	private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	private Button btnSelect;
	private ImageView ivImage;
	private String userChoosenTask;
	ZoomControls simpleZoomControls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String name = preferences.getString("CoName", "");
		setTitle(name);

		btnSelect = (Button) findViewById(R.id.selectPhoto_Button);
		btnSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
		ivImage = (ImageView) findViewById(R.id.imageDisplay);

		simpleZoomControls = (ZoomControls) findViewById(R.id.simpleZoomControl); // initiate a ZoomControls

		// perform setOnZoomInClickListener event on ZoomControls
		simpleZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onClick(View v) {
				// calculate current scale x and y value of ImageView
				float x = ivImage.getScaleX();
				float y = ivImage.getScaleY();
				// set increased value of scale x and y to perform zoom in functionality
				ivImage.setScaleX((float) (x + 0.5));
				ivImage.setScaleY((float) (y + 0.5));
			}
		});
		simpleZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onClick(View v) {
				// calculate current scale x and y value of ImageView
				float x = ivImage.getScaleX();
				float y = ivImage.getScaleY();
				// set decreased value of scale x and y to perform zoom out functionality
				ivImage.setScaleX((float) (x - 0.5));
				ivImage.setScaleY((float) (y - 0.5));
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if(userChoosenTask.equals("Take Photo"))
						cameraIntent();
					else if(userChoosenTask.equals("Choose from Library"))
						galleryIntent();
				} else {
					//code for deny
				}
				break;
		}
	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Image.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				boolean result=Utility.checkPermission(Image.this);

				if (items[item].equals("Take Photo")) {
					userChoosenTask ="Take Photo";
					if(result)
						cameraIntent();

				} else if (items[item].equals("Choose from Library")) {
					userChoosenTask ="Choose from Library";
					if(result)
						galleryIntent();

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private void galleryIntent()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
	}

	private void cameraIntent()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE)
				onSelectFromGalleryResult(data);
			else if (requestCode == REQUEST_CAMERA)
				onCaptureImageResult(data);
		}
	}

	private void onCaptureImageResult(Intent data) {
		Bitmap newImage = data.getParcelableExtra("data");
		ivImage.setImageBitmap(newImage);

		// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
		Uri tempUri = getImageUri(getApplicationContext(), newImage);

		// CALL THIS METHOD TO GET THE ACTUAL PATH
		File finalFile = new File(getRealPathFromURI(tempUri));

		// *****newImagePath is the new image URL*****
		String newImagePath = finalFile.toString();
		Toast.makeText(getBaseContext(), newImagePath, Toast.LENGTH_LONG).show();
	}

	private void onSelectFromGalleryResult(Intent data) {

		Bitmap selectedImage=null;
		if (data != null) {
			try {
				selectedImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//****should be return to david page and give him the image****
		ivImage.setImageBitmap(selectedImage);

		// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
		Uri tempUri = getImageUri(getApplicationContext(), selectedImage);

		// CALL THIS METHOD TO GET THE ACTUAL PATH
		File finalFile = new File(getRealPathFromURI(tempUri));

		// *****newImagePath is the new image URL*****
		String newImagePath = finalFile.toString();
		Toast.makeText(getBaseContext(), newImagePath, Toast.LENGTH_LONG).show();
	}

	//method to get the Image URI
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	//method to get the Image Path
	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}
}