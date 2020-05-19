package com.example.multipart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.provider.Contacts.ExtensionsColumns.NAME;


public class MainActivity extends AppCompatActivity {

    private Button selectimage, uploadimage;
    private EditText edittext;
    private ImageView imageview;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private String URL = "http://192.168.43.191/MyApi/api.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectimage = findViewById(R.id.selectimage);
        uploadimage = findViewById(R.id.uploadimage);
        edittext = findViewById(R.id.edittext);
        imageview = findViewById(R.id.imageview);

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMG_REQUEST);
            }
        });

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        int statusCode = response.statusCode;
                        if(statusCode==200){
                            Toast.makeText(getApplicationContext(), "Uploaded Successfully",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Toast.makeText(getApplicationContext(), err.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> param = new HashMap<>();
                        param.put("name", edittext.getText().toString().trim());
                        param.put("image", imagetoString(bitmap));
                        return param;
                    }
                };
                RequestQueue que = Volley.newRequestQueue(MainActivity.this);
                que.add(multipartRequest);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imagetoString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imgbytes = outputStream.toByteArray();
        return Base64.encodeToString(imgbytes,Base64.DEFAULT);
    }
}
