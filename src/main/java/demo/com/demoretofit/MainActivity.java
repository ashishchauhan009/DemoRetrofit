package demo.com.demoretofit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, Callback<LoginModel>{

    EditText username,password;
    Button loginbtn;
    private LoginModel loginModel;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123456;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        loginbtn = (Button) findViewById(R.id.button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> permissionsList = new ArrayList<String>();
            addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
            addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
            addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE);
            addPermission(permissionsList, Manifest.permission.CAMERA);
            if (permissionsList.isEmpty()) {

                // btnDonthavepass.setOnClickListener(this);
                loginbtn.setOnClickListener(this);
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        } else {

            //btnDonthavepass.setOnClickListener(this);
            loginbtn.setOnClickListener(this);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, permission))
                    return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted

                   // btnDonthavepass.setOnClickListener(this);
                    loginbtn.setOnClickListener(this);

                } else {
                    Toast.makeText(getApplicationContext(), "Please Allow Required Permissions to App", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                loginItdUser(username.getText().toString(), password.getText().toString());

                break;


        }
    }

    public void loginItdUser(String email, String password) {

        Log.e("check unfriend", email);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("your BaseUrl")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        logininterface iloginitduser = retrofit.create(logininterface.class);
        Call<LoginModel> call = null;

        call = iloginitduser.getData(email, password);
        call.enqueue(loginitd);

    }

    Callback<LoginModel> loginitd = new Callback<LoginModel>() {

        @Override
        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
            LoginModel logindata = response.body();
            if (logindata != null) {

                if (Integer.parseInt(logindata.getStatus()) == 1) {
                    Intent intent = new Intent(MainActivity.this, dashboard.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }

        }

        @Override
        public void onFailure(Call<LoginModel> call, Throwable t) {
           /* try {
                throw t;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }*/
        }
    };

    @Override
    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
        loginModel = response.body();

        if (loginModel != null) {

            if (Integer.parseInt(loginModel.getStatus()) == 1) {

                Log.e("check data login", loginModel.toString());
            } else {

                // Toast.makeText(SignInActivity.this,loginModel.getTag(),Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onFailure(Call<LoginModel> call, Throwable t) {


    }


    public interface logininterface {
        @GET("your login url here")
       // @GET
            // if api in get use this otherwise @Post
            //@POST
            //@FormUrlEncoded // is must
            // use tags Body or Fields
            // if u send images in multipart use multipart create request for multipart for only image upload
       // Call<LoginModel> getData(@Query("email") String email, @Query("password") String password);
        // Call<LoginModel> getData(@Multipart("email") String email, @Query("password") String password);

        Call<LoginModel> getData(@Query("email") String email, @Query("password") String password);
    }


    }
