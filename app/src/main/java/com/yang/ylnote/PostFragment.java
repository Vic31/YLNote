package com.yang.ylnote;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.ylnote.bean.Note;
import com.yang.ylnote.camera.CameraActivity;
import com.yang.ylnote.util.Config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static android.app.Activity.RESULT_CANCELED;

public class PostFragment extends Fragment{
    public PostFragment(){}
    private ImageView addPicBtn;
    private OnPostFragmentListener postListener = null;
    private Note note = new Note();

    //for camera and gallery
    private Uri mPhotoPath;//camera photo path
    private File f;
    private Bitmap bmp = null;//save camera photos and gallery pictures

    public interface OnPostFragmentListener {
        void onPostFragmentInteraction(Note item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        // Inflate the layout for this fragment
//        back = (ImageView) view.findViewById(R.id.back_btn);
//        title = (TextView) view.findViewById(R.id.title);
//        title.setText(R.string.new_post);
//        shareBtn = (TextView) view.findViewById(R.id.setting_btn);
//        shareBtn.setText(R.string.share);
//        back.setOnClickListener(listener);
//        shareBtn.setOnClickListener(listener);
        addPicBtn = (ImageView) view.findViewById(R.id.addpic_btn);
        addPicBtn.setOnClickListener(listener);
        //camera mPhotoPath: the absolute file's name
        mPhotoPath = Uri.fromFile(new File(
                android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), "tmp_profile.jpg"));
        //gallery path:
        f = new File(
                android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), "newpic.jpg");
        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.back_btn:
//                    postListener.onPostFragmentInteraction(null);
//                    break;
//                case R.id.setting_btn:
//                    postListener.onPostFragmentInteraction(note);
//                    break;
                case R.id.addpic_btn:
                    chooseImage();
            }
        }
    };

    //choose camera or gallery.
    public void chooseImage() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.select_dialog_item, getResources().getStringArray(R.array.select_photo)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a way to upload your picture:");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                try {
                    switch (item) {
                        case Config.CAMERA:
                            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                //Start camera preview
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoPath);
                                startActivityForResult(intent, Config.CAMERA);
                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 101);
                            }
                            break;
                        case Config.GALLERY:
                            Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent2.setType("image/*");
                            //Intent intent2 = new Intent(Intent.ACTION_PICK,null);
                            /*intent2.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image");*/
                            startActivityForResult(intent2, Config.GALLERY);
                            break;
                        default:
                            break;
                    }
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        builder.setNegativeButton("Cancel", null);
        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //CAMERA PERMISSION
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoPath);
                        startActivityForResult(intent, Config.CAMERA);
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String prefix;
        if(resultCode != RESULT_CANCELED)
        {
            BitmapFactory.Options resample = new BitmapFactory.Options();;
            switch (requestCode)
            {
                case Config.CAMERA:
                    String pathCamera = mPhotoPath.getPath();
                    try {
                        //bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mPhotoPath);
                        resample.inJustDecodeBounds = true;
                        bmp = BitmapFactory.decodeFile(pathCamera,resample);
                        resample.inJustDecodeBounds = false;
                        if(resample.outWidth > 800 || resample.outHeight>600)
                        {
                            int heightS = resample.outHeight/200;
                            int widthS = resample.outWidth/400;
                            //compress times :inSampleSize
                            resample.inSampleSize = (heightS<widthS)?heightS:widthS;
                            bmp = BitmapFactory.decodeFile(pathCamera, resample);
                        }
                        //get smaller size
                        //bmp = getSmallImage(bmp);
                        bmp = getCorrectOrientation(bmp, pathCamera);
                        addPicBtn.setImageBitmap(bmp);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case Config.GALLERY:
                    String picPath = null;
                    Uri selectImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    addPicBtn.setImageURI(selectImage);
                    Cursor cursor = getActivity().getContentResolver().query(selectImage, filePathColumn,
                            null, null, null);
                    if(null == cursor)
                    {
                        picPath = selectImage.getPath();
                    }
                    else
                    {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picPath = cursor.getString(columnIndex);
                        cursor.close();
                    }
                    try {
                        bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                        bmp = getCorrectOrientation(bmp, picPath);
                        addPicBtn.setImageBitmap(bmp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    //if want to store picture under app
//                    try{
//                        if (f.exists())
//                            f.delete();
//                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
//                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                        bos.flush();
//                        bos.close();
//                    }
//                    catch (FileNotFoundException e)
//                    {
//                        e.printStackTrace();
//                    }catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
                    break;
                default:
                    break;
            }
        }
    }

    //get right Orientation of camera's picture.
    //you may get a 90 degrees rotation about your picture
    public Bitmap getCorrectOrientation(Bitmap bmp,String path)
    {
        try
        {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);

            Matrix m = new Matrix();
            if(orientation == 3)
            {
                m.postRotate(180);
            }
            else if(orientation == 6)
            {
                m.postRotate(90);
            }
            else if(orientation == 8)
            {
                m.postRotate(270);
            }
            else{}
            bmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),
                    m,true);
            return bmp;
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return bmp;
    }
}