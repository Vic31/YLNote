package com.yang.ylnote;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.bean.NoteDetail;
import com.yang.ylnote.bean.User;
import com.yang.ylnote.util.Config;
import com.yang.ylnote.util.GlideApp;
import com.yang.ylnote.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.setting_btn)
    TextView settingBtn;
    @BindView(R.id.addpic_btn)
    ImageView addpicBtn;
    @BindView(R.id.post_text)
    EditText postText;

    public static Uri uri;
    private Bitmap bmp = null;//save camera photos and gallery pictures
    // storage reference
    private FirebaseStorage storage = MyApplication.getStoreInstance();
    // db reference
    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();

    private Note note = new Note();

    private SharedPreferences sharedPre;
    private String username;
    private boolean chosePic = false;
    //choose image dialog
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        title.setText(getResources().getString(R.string.title_post));
        sharedPre = this.getSharedPreferences("config", MODE_PRIVATE);
        username = sharedPre.getString("username", "");
        settingBtn.setText("Share");
    }

    @OnClick({R.id.back_btn, R.id.setting_btn, R.id.addpic_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.setting_btn:
                uploadInfo();
                Toast.makeText(PostActivity.this, "Uploding the picture...", Toast.LENGTH_SHORT).show();
                settingBtn.setClickable(false);
                break;
            case R.id.addpic_btn:
                chooseImage();
                break;
        }
    }

    //choose camera or gallery.
    public void chooseImage() {
        TextView update_dialog_TK, update_dialog_PZ, update_dialog_cancel;
        View view = getLayoutInflater().inflate(R.layout.show_choose_dialog, null);
        dialog = new Dialog(this, R.style.dialog_photo);

        Display display = getWindowManager().getDefaultDisplay();
        dialog.setContentView(view, new ViewGroup.LayoutParams(
                display.getWidth()
                , display.getHeight()
        ));
        dialog.getWindow().setWindowAnimations(R.style.Dialog_Anim_Style);
//        dialog.setCanceledOnTouchOutside(true);
        //图库
        update_dialog_TK = (TextView) view.findViewById(R.id.update_dialog_TK);
        update_dialog_TK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                startActivityForResult(intent2, Config.GALLERY);
            }
        });
        //相机拍照
        update_dialog_PZ = (TextView) view.findViewById(R.id.update_dialog_PZ);
        update_dialog_PZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCamera(PostActivity.this);
                dialog.dismiss();
            }
        });
        //取消
        update_dialog_cancel = (TextView) view.findViewById(R.id.update_dialog_cancel);
        update_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * upload images to cloud storage,
     * use username_date_post.jpg as file name
     *
     */
    private void uploadInfo(){
        /*
        set date postfix for image uploaded
         */
        SimpleDateFormat df = new SimpleDateFormat("MMdd_HHmmss");
        String postDate = df.format(new Date());
        /*
        set note content
         */
        note.setText(postText.getText().toString()+"");

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        final String imgName = username + "_" + postDate+"_post.jpg";
        // Create a reference to "*.jpg"
        final StorageReference postRef = storageRef.child("images").child(imgName);
        // Get the data from an ImageView as bytes
        // upload image by bytes will cost lot of memories
        addpicBtn.setDrawingCacheEnabled(true);
        addpicBtn.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) addpicBtn.getDrawable()).getBitmap();
        if (chosePic){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            // upload image by steam will cost less memory
    //        InputStream stream = new FileInputStream(new File("path/to/images/rivers.jpg"));
            /*
            uploading process
             */
            UploadTask uploadTask = postRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(PostActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                }
            })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    note.setPic(imgName);
                    uploadToDB();
//                    getUrlFromCloud(imgName);
                }
            });
        }
        else {
            uploadToDB();
        }
        settingBtn.setClickable(true);
    }

    /**
     * try get url if upload succeeded
     * @param imgName
     */
//    private void getUrlFromCloud(String imgName){
//        final StorageReference postRef = storage.getReference().child("images").child(imgName);
//        postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Log.i("url ", uri.getPath());
//                note.setPic(uri.getPath());
//                uploadToDB();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("url get failed","");
//            }
//        });
//    }

    /**
     * upload information to database
     */
    private void uploadToDB(){
        Date now = new Date();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH");
        note.setNote_id("note_"+df1.format(now));
        note.setPost_time(df2.format(now)+"h");
        note.setUsername(username);
        myRef.child("note").child(note.getNote_id()).setValue(note);
        myRef.child("user").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setPost_list(user.getPost_list()+","+note.getNote_id());
                myRef.child("user").child(username).setValue(user);
                NoteDetail noteDetail = new NoteDetail();
                noteDetail.setUsername(username);
                noteDetail.setNote_id(note.getNote_id());
                noteDetail.setComment_id("comment_"+note.getNote_id());
                noteDetail.setText(note.getText());
                noteDetail.setPic(note.getPic());
                noteDetail.setPost_time(note.getPost_time());
                myRef.child("note_detail").child(noteDetail.getNote_id()).setValue(noteDetail);
                setResult(Config.UPDATE_POST_LIST);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String prefix;
        if(resultCode != RESULT_CANCELED)
        {
//            BitmapFactory.Options resample = new BitmapFactory.Options();;
            switch (requestCode)
            {
                case Config.CAMERA:
                    chosePic = true;
//                    String pathCamera = mPhotoPath.getPath();
//                    try {
//                        //bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mPhotoPath);
//                        resample.inJustDecodeBounds = true;
//                        bmp = BitmapFactory.decodeFile(pathCamera,resample);
//                        resample.inJustDecodeBounds = false;
//                        if(resample.outWidth > 800 || resample.outHeight>600)
//                        {
//                            int heightS = resample.outHeight/200;
//                            int widthS = resample.outWidth/400;
//                            //compress times :inSampleSize
//                            resample.inSampleSize = (heightS<widthS)?heightS:widthS;
//                            bmp = BitmapFactory.decodeFile(pathCamera, resample);
//                        }
//                        //get smaller size
//                        //bmp = getSmallImage(bmp);
//                        bmp = getCorrectOrientation(bmp, pathCamera);
//                        addpicBtn.setImageBitmap(bmp);
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }

                    Uri pictur;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
                        pictur = uri;
                    } else {
                        pictur = Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory() + "/temp.jpg"));
                    }
                    addpicBtn.refreshDrawableState();
                    startPhotoZoom(pictur);
                    break;
                case Config.GALLERY:
                    chosePic = true;
//                    String picPath = null;
                    Uri selectImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(selectImage, filePathColumn,
//                            null, null, null);
//                    if(null == cursor)
//                    {
//                        picPath = selectImage.getPath();
//                    }
//                    else
//                    {
//                        cursor.moveToFirst();
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        picPath = cursor.getString(columnIndex);
//                        cursor.close();
//                    }
//                    try {
//                        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
//                        bmp = getCorrectOrientation(bmp, picPath);
//                        addpicBtn.refreshDrawableState();
//                        addpicBtn.setImageBitmap(bmp);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    startPhotoZoom(selectImage);
                    break;
                case Config.PHOTO_CROP:
                    GlideApp.with(PostActivity.this).load(uritempFile).into(addpicBtn);
                default:
                    break;
            }
        }
    }
    private Uri uritempFile;
    /**
     * crop the photo
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.equals("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        intent.putExtra("outputX", 400); // 宽高
        intent.putExtra("outputY", 400);
        //uritempFile为Uri类变量，实例化uritempFile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //开启临时权限
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //重点:针对7.0以上的操作
            intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
            uritempFile = uri;
        } else {
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Config.PHOTO_CROP);
    }

}
