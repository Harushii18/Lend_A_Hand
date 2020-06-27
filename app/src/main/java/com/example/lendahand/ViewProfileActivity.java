package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    //dialog components
    private TextInputLayout txtEmail, txtFName, txtLName, txtStrAddress, txtProvLayout, txtPostCode, txtPhoneNumber, txtSuburb, txtCurrPassword, txtNewPassword1, txtNewPassword2;
    private Button btnChangeEmail, btnChangeLName, btnChangeFName, btnFName, btnLName, btnEmail, btnPhone, btnAddress, btnVerifyCurrPassword, btnChangePassword;
    private AutoCompleteTextView txtProvince;

    //for populating province drop down menu
    private String strStreetAddress, strSuburb, strProvince = "", strPostalCode;
    private String[] arrProvinces = new String[]{"KwaZulu-Natal", "Western Cape", "North West", "Northern Cape", "Free State", "Gauteng", "Limpopo", "Mpumalanga", "Eastern Cape"};
    private ArrayAdapter<String> adapter;

    private String strProfName,strProfSurname;

    //profile components
    private Button btnGenDetails, btnProfEditPhone, btnProfEditPassword, btnProfEditAddress;
    private TextView txtProfName, txtProfEmail, txtProfAddress, txtProfSuburb, txtProfProvince, txtProfPostalCode,txtProfPhone,txtProfUsername;

    private AlertDialog alertDialog;

    //for re-use of okhttp requests
    private RequestBody formBody;
    private String endLink = "";
    private OkHttpClient client;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";

    //for validating
    private boolean blnExist;
    private boolean blnValid;

    //variables for navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtNavName,txtNavEmail;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current user
        String user=StayLoggedIn.getUserType(ViewProfileActivity.this);
        //reuse of code, but only differentcontent views
        if (user.equals("Admin")){
            setContentView(R.layout.activity_view_profile);
        }else if (user.equals("Donor")){
            setContentView(R.layout.activity_donor_view_profile);
        }else if(user.equals("Donee")){
            //TODO: set content view to the donee one that has a menu pointing to donee menu
        }

        //initialise drawer views
        initialiseNavBarViews();

        /*---------------------nav view-----------------------------------------*/
        //initialise navigation drawer
        setSupportActionBar(toolbar);

        navigationView.bringToFront(); //nav view can slide back


        //show which nav item was selected
        if (user.equals("Admin")){
            navigationView.setCheckedItem(R.id.nav_admin_profile);
        }else if (user.equals("Donor")){
            navigationView.setCheckedItem(R.id.nav_profile);
        }else if(user.equals("Donee")){
            //TODO: set checked view to donee nav one
        }


        //initialise nav view header values
        headerView=navigationView.getHeaderView(0);

        txtNavName=headerView.findViewById(R.id.txtNavName);
        txtNavEmail=headerView.findViewById(R.id.txtNavEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(ViewProfileActivity.this));
        txtNavName.setText(StayLoggedIn.getFName(ViewProfileActivity.this)+' '+StayLoggedIn.getLName(ViewProfileActivity.this));

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------nav view end------------------------------------*/

        //initialise all components
        initViews();

        //set on click listeners for all buttons
        setOnClickListenersForProfileButtons();

        //check connectivity status
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //populate all text views for profile activity via okhttp
            populateTextViews();
        }
    }

    //=========================================================
    //navigation view methods
    private void initialiseNavBarViews() {
        drawerLayout = findViewById(R.id.dlViewProfile);
        navigationView = findViewById(R.id.nav_view_view_profile);
        toolbar = findViewById(R.id.tbViewProfile);
    }


    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_add_courier:
                i = new Intent(this, AdminAddCourierActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_admin_courier_list:
                i = new Intent(this, AdminViewCourierListActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_profile:
                i = new Intent(this, ViewProfileActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_donor_list:
                i = new Intent(this, DonorRankingList.class);
                startActivity(i);
                break;
            case R.id.nav_admin_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                break;
            case R.id.nav_admin_pending_req:
                i = new Intent(this, AdminPendingReqActivity.class);
                startActivity(i);
                break;
            case R.id.nav_request:
                i = new Intent(this, CategoryListActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_list:
                i = new Intent(this, DonorRankingList.class);
                startActivity(i);
                break;
            case R.id.nav_home:
                i = new Intent(this, DoneeDashboard.class);
                startActivity(i);
                break;
            case R.id.nav_about:
                i = new Intent(this, AboutUs.class);
                startActivity(i);
                break;
            case R.id.nav_logout:
                StayLoggedIn.clearUserDetails(this);
                i = new Intent(this, LoginScreenActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); //Close drawer after menu item is selected
        return true;
    }

    //so that when back button is pressed, it only closes the nav bar and the app doesn't close
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //==========================================================

    @Override
    public void onClick(View v) {
        //case statement for button clicks and their corresponding procedures
        switch (v.getId()) {
            //edit password
            case R.id.btnConfirmNewPassword:
                ChangePassword();
                break;
            case R.id.btnVerifyCurrPassword:
                VerifyCurrPassword(v);
                break;
            case R.id.btnProfEditPassword:
                SelEditPassword(v);
                break;
            //edit address
            case R.id.btnEditAddress:
                EditAddress();
                break;
            case R.id.btnProfEditAddress:
                SelEditAddress(v);
                break;
            //edit phone number
            case R.id.btnEditPhone:
                EditPhone();
                break;
            case R.id.btnProfEditPhone:
                selEditPhone(v);
                break;
            //the general activities editing
            case R.id.btnedtFName:
                EditFName();
                break;
            case R.id.btnedtLName:
                EditLName();
                break;
            case R.id.btnedtEmail:
                EditEmail();
                break;
            //selecting a general activity editing
            case R.id.btnSelEdtFName:
                selEditFName();
                break;
            case R.id.btnSelEdtEmail:
                selEditEmail();
                break;
            case R.id.btnSelEditLName:
                selEditLName();
                break;
            case R.id.btnedtGenDetails:
                editGenDetails(v);
                break;
            default:
                break;
        }
    }


    private void reusableOKHTTPRequester() {
        //this method was created so the same method can be used for editing all data
        client = new OkHttpClient();
        String link = urlLink + endLink;

        Request request = new Request.Builder()
                .url(link)
                .post(formBody)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("EDIT", "Editing successful");
                } else {
                    Log.d("EDIT", "Editing failed");
                }
                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //================================================================================
    private void populateTextViews() {
        txtProfUsername.setText(StayLoggedIn.getUserName(ViewProfileActivity.this));

        client = new OkHttpClient();
        String url = urlLink + "populateprofile.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if (responseData.equals("")) {
                    } else {

                        JSONArray JArray;
                        try {
                            JArray = new JSONArray(responseData);

                            String strPhoneNum,strFormattedPhoneNum="";
                            //get the donee's status from the server
                            for (int i = 0; i < JArray.length(); i++) {
                                JSONObject object = JArray.getJSONObject(i);
                                //store name and surname in class variables for re-use
                                setProfName(object.getString("NAME"));
                                setProfSurname(object.getString("SURNAME"));

                                //populate text views with correct data
                                txtProfName.setText(object.getString("NAME")+' '+object.getString("SURNAME"));
                                txtProfEmail.setText(object.getString("EMAIL"));
                                txtProfAddress.setText(object.getString("STREET_ADDRESS"));
                                txtProfSuburb.setText(object.getString("SUBURB"));
                                txtProfProvince.setText(object.getString("PROVINCE"));
                                txtProfPostalCode.setText(object.getString("POSTAL_CODE"));

                                //format phone number
                                strPhoneNum=object.getString("PHONE_NO");
                                for (int j=0;j<10;j++){
                                    if ((j==3) || (j==6)){
                                        strFormattedPhoneNum=strFormattedPhoneNum+' ';
                                    }
                                    strFormattedPhoneNum=strFormattedPhoneNum+strPhoneNum.charAt(j);
                                }
                                txtProfPhone.setText(strFormattedPhoneNum);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setProfName(String name) {
        strProfName=name;
    }

    private void setProfSurname(String surname) {
        strProfSurname=surname;
    }

    private String capitalizeWord(String str) {
        String words[] = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizeWord.trim();
    }

    //====================================================================================
    //Edit password methods

    private void ChangePassword() {
        //check if new passwords match
        blnValid = true;
        String strPassword = txtNewPassword1.getEditText().getText().toString().trim();
        String strPassword2 = txtNewPassword2.getEditText().getText().toString().trim();
        if (!strPassword.equals(strPassword2)) {
            txtNewPassword2.setError(getText(R.string.txt_pword_no_match));
            blnValid = false;
        }
        if (strPassword.length() == 0) {
            txtNewPassword1.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }

        if (blnValid) {
            //encrypt and hash password with SHA-512 for database
            String generatedPassword = "";
            try {
                String salt = "A$thy*BJFK_P_$%#";
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(salt.getBytes(StandardCharsets.UTF_8));
                byte[] hashedPassword = md.digest(strPassword.getBytes(StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < hashedPassword.length; i++) {
                    stringBuilder.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            //check connectivity
            GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
            if (!globalConnectivityCheck.isNetworkConnected()) {
                //if internet is not connected
                Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //initialise values needed for okhttp request
                endLink = "editpassword.php";
                formBody = new FormBody.Builder()
                        .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                        .add("pass", generatedPassword)
                        .build();
                reusableOKHTTPRequester();
                //show successful password change toast
                Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_password), Toast.LENGTH_SHORT).show();

                //close dialog
                alertDialog.dismiss();
            }

        } else {
            //set component interactivity
            txtNewPassword1.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtNewPassword1.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtNewPassword1.setError(null);
                        txtNewPassword1.setErrorEnabled(false);
                    }
                }
            });
            txtNewPassword2.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String passwrd = txtNewPassword1.getEditText().getText().toString().trim();
                    txtNewPassword1.getEditText().setText(passwrd);
                    String passwrd2 = txtNewPassword2.getEditText().getText().toString();
                    if (!passwrd2.equals(passwrd)) {
                        txtNewPassword2.setError(getText(R.string.txt_pword_no_match));
                    } else {
                        //to sort out error where it randomly underlines next tab
                        txtNewPassword2.setError(null);
                        txtNewPassword2.setErrorEnabled(false);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });
        }
    }

    private void VerifyCurrPassword(View v) {
        blnValid = true;
        final String strCurrPassword = txtCurrPassword.getEditText().getText().toString().trim();

        //check connectivity
        GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
        if (!globalConnectivityCheck.isNetworkConnected()) {
            //if internet is not connected
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //check if password is valid via okhttp
            //okhttp call to server
            checkPasswordIsCorrect(strCurrPassword);

            //if password is valid
            if (blnValid) {

                //hide dialog
                alertDialog.dismiss();

                //show toast that password is verified
                Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_verify_password), Toast.LENGTH_SHORT).show();

                //move to next dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_password2, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

                //initialise buttons on dialog
                btnChangePassword = dialogView.findViewById(R.id.btnConfirmNewPassword);

                //initialise text views
                txtNewPassword1 = dialogView.findViewById(R.id.txtEditNewPassword1);
                txtNewPassword2 = dialogView.findViewById(R.id.txtEditNewPassword2);

                //on click listeners for dialog button
                btnChangePassword.setOnClickListener(this);
            } else {
                //show toast that password is incorrect
                Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_failure_password), Toast.LENGTH_SHORT).show();

                txtCurrPassword.getEditText().setText("");
                //enable component interactivity
                txtCurrPassword.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (s.length() == 0) {
                            txtCurrPassword.setError(getText(R.string.txt_empty_field));
                        } else {
                            txtCurrPassword.setError(null);
                            txtCurrPassword.setErrorEnabled(false);
                        }
                    }
                });
            }
        }

    }

    private void checkPasswordIsCorrect(final String strCurrPassword) {
        client = new OkHttpClient();
        String url = urlLink + "checkpassword.php";

        RequestBody formBody = new FormBody.Builder()
                .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                setblnValid(false);
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if (responseData.equals("")) {
                        setblnValid(false);
                    } else {
                        try {
                            JSONArray JArray = new JSONArray(responseData);
                            String objPassword, objType, objEmail, objFName, objLName, objProvince;
                            //encrypt password
                            String generatedPassword = "";
                            try {
                                String salt = "A$thy*BJFK_P_$%#";
                                MessageDigest md = MessageDigest.getInstance("SHA-512");
                                md.update(salt.getBytes(StandardCharsets.UTF_8));
                                byte[] hashedPassword = md.digest(strCurrPassword.getBytes(StandardCharsets.UTF_8));
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < hashedPassword.length; i++) {
                                    stringBuilder.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                                }
                                generatedPassword = stringBuilder.toString();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < JArray.length(); i++) {
                                JSONObject object = JArray.getJSONObject(i);
                                objPassword = object.getString("PASSWORD");
                                if (objPassword.equals(generatedPassword)) {
                                    setblnValid(true);
                                } else {
                                    setblnValid(false);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setblnValid(boolean blnChange) {
        blnValid = blnChange;
    }

    private void SelEditPassword(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_password1, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        //initialise buttons on dialog
        btnVerifyCurrPassword = dialogView.findViewById(R.id.btnVerifyCurrPassword);

        //initialise text view
        txtCurrPassword = dialogView.findViewById(R.id.txtEditCurrPassword);

        //on click listeners for dialog button
        btnVerifyCurrPassword.setOnClickListener(this);
    }


    //========================================================================================
    //Edit address methods

    private void SelEditAddress(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_address, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        //initialise buttons on dialog
        btnAddress = dialogView.findViewById(R.id.btnEditAddress);

        //initialise text view
        txtStrAddress = dialogView.findViewById(R.id.txtEditStAddress);
        txtPostCode = dialogView.findViewById(R.id.txtEditPostalCode);
        txtSuburb = dialogView.findViewById(R.id.txtEditSuburb);
        txtProvLayout = dialogView.findViewById(R.id.txtEditProvLayout);
        adapter =
                new ArrayAdapter<>(
                        ViewProfileActivity.this,
                        R.layout.reg_prov_combo_box,
                        arrProvinces);
        txtProvince = dialogView.findViewById(R.id.txtEditProvince);
        txtProvince.setAdapter(adapter);
        txtProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //extract items
                strProvince = parent.getItemAtPosition(position).toString();
                txtProvLayout.setError(null);
                txtProvLayout.setErrorEnabled(false);
            }
        });
        //on click listeners for dialog button
        btnAddress.setOnClickListener(this);
    }

    private void EditAddress() {
        blnValid = true;
        strPostalCode = txtPostCode.getEditText().getText().toString().trim();
        strStreetAddress = txtStrAddress.getEditText().getText().toString().trim();
        strSuburb = txtSuburb.getEditText().getText().toString().trim();

        if (strPostalCode.length() == 0) {
            txtPostCode.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            if ((strPostalCode.length() < 4) || (!Pattern.matches("^[0-9]*$", strPostalCode))) {
                txtPostCode.setError(getText(R.string.txt_invalid_postal_code));
                blnValid = false;
            }
        }
        if (strSuburb.length() == 0) {
            txtSuburb.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }
        if (strStreetAddress.length() == 0) {
            txtStrAddress.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        }

        String strChkProvince = txtProvince.getText().toString();

        if ((strProvince.length() == 0) && (strChkProvince.length() == 0)) {
            txtProvLayout.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            if (strChkProvince != strProvince) {
                boolean blnFound = false;
                //checking that user didn't type in their own province
                for (int i = 0; i < arrProvinces.length; i++) {
                    if (strChkProvince.equals(arrProvinces[i])) {
                        blnFound = true;
                    }
                }
                if (blnFound == false) {
                    blnValid = false;
                    txtProvLayout.setError(getText(R.string.txt_invalid_province));
                    txtProvince.setText("");
                }
            }
        }

        if (blnValid) {
            //format address accordingly
            strStreetAddress=capitalizeWord(strStreetAddress);
            strSuburb=capitalizeWord(strSuburb);

            //initialise values needed for okhttp request
            endLink = "editaddress.php";
            formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                    .add("staddress", strStreetAddress)
                    .add("suburb", strSuburb)
                    .add("prov", strProvince)
                    .add("code", strPostalCode)
                    .build();
            reusableOKHTTPRequester();

            //change shared preferences
            StayLoggedIn.setProvince(ViewProfileActivity.this,strProvince);

            //change activity's contents
            txtProfProvince.setText(strProvince);
            txtProfPostalCode.setText(strPostalCode);
            txtProfSuburb.setText(strSuburb);
            txtProfAddress.setText(strStreetAddress);

            //close dialog
            alertDialog.dismiss();
            Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_address), Toast.LENGTH_SHORT).show();

        } else {
            //set user component error interactivity
            txtSuburb.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtSuburb.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtSuburb.setError(null);
                        txtSuburb.setErrorEnabled(false);
                    }
                }
            });
            txtStrAddress.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtStrAddress.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtStrAddress.setError(null);
                        txtStrAddress.setErrorEnabled(false);
                    }
                }
            });

            txtPostCode.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String postalCode = txtPostCode.getEditText().getText().toString().trim();
                    if (postalCode.length() != 0) {
                        if (!Pattern.matches("^[0-9]*$", postalCode)) {
                            txtPostCode.setError(getText(R.string.txt_invalid_postal_code));
                        } else {
                            if (postalCode.length() < 4) {
                                txtPostCode.setError(getText(R.string.txt_invalid_postal_code));
                            } else {
                                txtPostCode.setError(null);
                                txtPostCode.setErrorEnabled(false);
                            }
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtPostCode.setError(getText(R.string.txt_empty_field));
                    }
                }
            });
        }

    }

    //========================================================================================
    //Edit phone number methods

    private void EditPhone() {
        String strPhoneNumber = txtPhoneNumber.getEditText().getText().toString();
        blnValid = true;
        if (strPhoneNumber.length() == 0) {
            txtPhoneNumber.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if it is only numbers
            if ((strPhoneNumber.length() < 10) || (!Pattern.matches("^[0-9]*$", strPhoneNumber))) {
                txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                blnValid = false;
            }
        }

        if (blnValid) {
            //initialise values needed for okhttp request
            endLink = "editphone.php";
            formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                    .add("phone", strPhoneNumber)
                    .build();
            reusableOKHTTPRequester();

            //change activity's contents
            String strFormattedPhoneNum="";

            //format phone number
            for (int i=0;i<10;i++){
                if ((i==3) || (i==6)){
                    strFormattedPhoneNum=strFormattedPhoneNum+' ';
                }
                strFormattedPhoneNum=strFormattedPhoneNum+strPhoneNumber.charAt(i);
            }

            txtProfPhone.setText(strFormattedPhoneNum);

            //close dialog
            alertDialog.dismiss();
            Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_phone), Toast.LENGTH_SHORT).show();
        } else {
            //implement component interactivity
            txtPhoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String phoneNum = txtPhoneNumber.getEditText().getText().toString().trim();
                    if (phoneNum.length() != 0) {
                        if (!Pattern.matches("^[0-9]*$", phoneNum)) {
                            txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                        } else {
                            if (phoneNum.length() < 10) {
                                txtPhoneNumber.setError(getText(R.string.txt_invalid_phone_number));
                            } else {
                                txtPhoneNumber.setError(null);
                                txtPhoneNumber.setErrorEnabled(false);
                            }
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    if (s.length() == 0) {
                        txtPhoneNumber.setError(getText(R.string.txt_empty_field));
                    }

                }
            });

        }
    }

    private void selEditPhone(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_phone, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        //initialise buttons on dialog
        btnPhone = dialogView.findViewById(R.id.btnEditPhone);

        //initialise text view
        txtPhoneNumber = dialogView.findViewById(R.id.txtEditPhone);

        //on click listeners for dialog button
        btnPhone.setOnClickListener(this);

    }


    //=======================================================================================
    //General profile activity methods
    private void setOnClickListenersForProfileButtons() {
        //on click listeners for profile activity buttons
        btnGenDetails.setOnClickListener(this);
        btnProfEditPhone.setOnClickListener(this);
        btnProfEditPassword.setOnClickListener(this);
        btnProfEditAddress.setOnClickListener(this);
    }

    private void initViews() {
        //initialise buttons on view profile xml
        btnGenDetails = findViewById(R.id.btnedtGenDetails);
        btnProfEditAddress = findViewById(R.id.btnProfEditAddress);
        btnProfEditPassword = findViewById(R.id.btnProfEditPassword);
        btnProfEditPhone = findViewById(R.id.btnProfEditPhone);

        //initialise text views on viewprofile xml
        txtProfAddress = findViewById(R.id.txtProfStAddress);
        txtProfEmail = findViewById(R.id.txtProfEmail);
        txtProfPhone=findViewById(R.id.txtProfPhone);
        txtProfName = findViewById(R.id.txtProfName);
        txtProfSuburb = findViewById(R.id.txtProfStSuburb);
        txtProfProvince = findViewById(R.id.txtProfStProvince);
        txtProfPostalCode = findViewById(R.id.txtProfStPostalCode);
        txtProfUsername=findViewById(R.id.txtProfUsername);

    }

    //==========================================================================================
    //edit general details
    private void EditFName() {
        String strFName = txtFName.getEditText().getText().toString();
        blnValid = true;
        if (strFName.length() == 0) {
            txtFName.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if name contains numbers
            if (!Pattern.matches("^\\D*$", strFName)) {
                txtFName.setError(getText(R.string.txt_invalid_fname));
                blnValid = false;
            }
        }

        if (blnValid) {
            //initialise values needed for okhttp request
            strFName=capitalizeWord(strFName);
            endLink = "editfname.php";
            formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                    .add("name", strFName)
                    .build();
            reusableOKHTTPRequester();

            //change shared preferences
            StayLoggedIn.setFName(ViewProfileActivity.this,strFName);

            //change activity's contents
            txtProfName.setText(strFName+' '+strProfSurname);

            //close dialog
            alertDialog.dismiss();
            Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_fname), Toast.LENGTH_SHORT).show();
        } else {
            //set component interactivity
            txtFName.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtFName.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtFName.setError(null);
                        txtFName.setErrorEnabled(false);
                    }
                }
            });

        }

    }

    private void EditEmail() {
        final String strEmail = txtEmail.getEditText().getText().toString();
        blnValid = true;
        if (strEmail.length() == 0) {
            txtEmail.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if email is valid
            if (!Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", strEmail.toLowerCase())) {
                txtEmail.setError(getText(R.string.txt_invalid_email));
                blnValid = false;
            } else {
                //ensuring user is connected to the internet
                GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                if (!globalConnectivityCheck.isNetworkConnected()) {
                    //if internet is not connected
                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //checking that email exists using Mailbox Validator
                    if (!checkEmailExists()) {
                        txtEmail.setError(getText(R.string.txt_email_exists));
                        blnValid = false;
                    }
                }
            }
        }
        if (blnValid) {
            //initialise values needed for okhttp request
            endLink = "editemail.php";
            formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                    .add("email", strEmail)
                    .build();
            reusableOKHTTPRequester();

            //change shared preferences
            StayLoggedIn.setEmail(ViewProfileActivity.this,strEmail);

            //change text view contents
            txtProfEmail.setText(strEmail);

            //close dialog
            alertDialog.dismiss();
            Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_email), Toast.LENGTH_SHORT).show();

            //send email to user telling them that their email has changed
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                getText(R.string.txt_developer_pword).toString());
                        sender.sendMail(getText(R.string.txt_email_edit_subject).toString(), getText(R.string.txt_email_edit_body).toString()+StayLoggedIn.getUserName(ViewProfileActivity.this)+getText(R.string.txt_email_edit_body_2).toString(),
                                getText(R.string.txt_developer_email).toString(), strEmail);
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }

            }).start();
        } else {
            //add component interactivity
            txtEmail.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtEmail.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtEmail.setError(null);
                        txtEmail.setErrorEnabled(false);
                    }
                }
            });
        }
    }

    private void EditLName() {
        String strLName = txtLName.getEditText().getText().toString();
        blnValid = true;
        if (strLName.length() == 0) {
            txtLName.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if name contains numbers
            if (!Pattern.matches("^\\D*$", strLName)) {
                txtLName.setError(getText(R.string.txt_invalid_lname));
                blnValid = false;
            }
        }

        if (blnValid) {
            //initialise values needed for okhttp request
            strLName=capitalizeWord(strLName);
            endLink = "editlname.php";
            formBody = new FormBody.Builder()
                    .add("username", StayLoggedIn.getUserName(ViewProfileActivity.this))
                    .add("surname", strLName)
                    .build();
            reusableOKHTTPRequester();

            //change shared preferences
            StayLoggedIn.setLName(ViewProfileActivity.this,strLName);

            //change activity's contents
            txtProfName.setText(strProfName+' '+strLName);

            //close dialog
            alertDialog.dismiss();
            Toast.makeText(getApplicationContext(), getText(R.string.txt_profile_dialog_success_lname), Toast.LENGTH_SHORT).show();
        } else {
            //set component interactivity
            txtLName.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() == 0) {
                        txtLName.setError(getText(R.string.txt_empty_field));
                    } else {
                        txtLName.setError(null);
                        txtLName.setErrorEnabled(false);
                    }
                }
            });
        }

    }

    public void editGenDetails(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_name, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        //initialise buttons on dialogs
        btnChangeEmail = dialogView.findViewById(R.id.btnSelEdtEmail);
        btnChangeFName = dialogView.findViewById(R.id.btnSelEdtFName);
        btnChangeLName = dialogView.findViewById(R.id.btnSelEditLName);
        btnFName = dialogView.findViewById(R.id.btnedtFName);
        btnLName = dialogView.findViewById(R.id.btnedtLName);
        btnEmail = dialogView.findViewById(R.id.btnedtEmail);
        //initialise text views
        txtEmail = dialogView.findViewById(R.id.txtEditEmail);
        txtLName = dialogView.findViewById(R.id.txtEditLName);
        txtFName = dialogView.findViewById(R.id.txtEditFname);
        //on click listeners for dialog buttons
        btnEmail.setOnClickListener(this);
        btnLName.setOnClickListener(this);
        btnFName.setOnClickListener(this);
        btnChangeLName.setOnClickListener(this);
        btnChangeFName.setOnClickListener(this);
        btnChangeEmail.setOnClickListener(this);
    }

    private void selEditFName() {
        //method to reduce repeated code
        setVisibilityToGone();

        txtFName.setVisibility(View.VISIBLE);
        btnFName.setVisibility(View.VISIBLE);
    }

    private void selEditEmail() {
        //method to reduce repeated code
        setVisibilityToGone();

        txtEmail.setVisibility(View.VISIBLE);
        btnEmail.setVisibility(View.VISIBLE);
    }

    private void selEditLName() {
        //method to reduce repeated code
        setVisibilityToGone();

        txtLName.setVisibility(View.VISIBLE);
        btnLName.setVisibility(View.VISIBLE);
    }

    private void setVisibilityToGone() {
        btnChangeEmail.setVisibility(View.GONE);
        btnChangeFName.setVisibility(View.GONE);
        btnChangeLName.setVisibility(View.GONE);
    }

    //misc functions and procedures
    private boolean checkEmailExists() {
        //connect to mailbox validator
        blnExist = false;
        client = new OkHttpClient();
        String url = urlLink + "emailvalidator.php";

        RequestBody formBody = new FormBody.Builder()
                .add("email", txtEmail.getEditText().getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                setblnExist(false);
                countDownLatch.countDown();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        //extract is verified from epi response object
                        JSONObject JObj = new JSONObject(responseData);
                        String objEmailVerified = JObj.getString("is_verified");
                        if (objEmailVerified.equals("True")) {
                            setblnExist(true);
                        } else {
                            setblnExist(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                countDownLatch.countDown();
            }
        });

        try {
            //to ensure that main thread waits for this
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return blnExist;
    }

    private void setblnExist(boolean blnChange) {
        //method to change bln exist value due to it being changed in another method
        blnExist = blnChange;
    }
    //===================================================================================

}
