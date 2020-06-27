package com.example.lendahand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminAddCourierActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextInputLayout txtProvLayout, txtID, txtFName, txtLName, txtPhoneNum;
    private String[] arrProvinces = new String[]{"KwaZulu-Natal", "Western Cape", "North West", "Northern Cape", "Free State", "Gauteng", "Limpopo", "Mpumalanga", "Eastern Cape"};
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView txtProvince;
    private String strFName, strLName, strID, strPhone, strProvince = "";
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    //variables for navbar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtNavName,txtNavEmail;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_courier);
        //initialise drawer views
        initialiseNavBarViews();

        /*---------------------nav view-----------------------------------------*/
        //initialise navigation drawer
        setSupportActionBar(toolbar);

        navigationView.bringToFront(); //nav view can slide back

        //show which nav item was selected
        navigationView.setCheckedItem(R.id.nav_admin_add_courier);


        //initialise nav view header values
        headerView=navigationView.getHeaderView(0);

        txtNavName=headerView.findViewById(R.id.txtNavName);
        txtNavEmail=headerView.findViewById(R.id.txtNavEmail);
        txtNavEmail.setText(StayLoggedIn.getEmail(AdminAddCourierActivity.this));
        txtNavName.setText(StayLoggedIn.getFName(AdminAddCourierActivity.this)+' '+StayLoggedIn.getLName(AdminAddCourierActivity.this));

        //toggle is for the nav bar to go back and forth
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*make menu clickable*/
        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------nav view end------------------------------------*/

        //initialise views
        initViews();
        //populate province drop down menu
        populateComboBox();
    }

    //=========================================================
    //navigation view methods
    private void initialiseNavBarViews() {
        drawerLayout = findViewById(R.id.dlAdminAddCourier);
        navigationView = findViewById(R.id.admin_nav_view_add_courier);
        toolbar = findViewById(R.id.tbAdminAddCourier);
    }


    /*OnClick for navigation bar menu items*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.nav_admin_add_courier:
                i = new Intent(this, AdminAddCourierActivity.class); //Request items menu item
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.nav_admin_courier_list:
                i = new Intent(this, AdminViewCourierListActivity.class);
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
            case R.id.nav_admin_profile:
                i = new Intent(this, ViewProfileActivity.class);
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
    private void setUserComponentErrorInteractivity() {
        txtPhoneNum.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    txtPhoneNum.setError(getText(R.string.txt_empty_field));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = txtPhoneNum.getEditText().getText().toString().trim();
                if (phoneNum.length() != 0) {
                    if (!Pattern.matches("^[0-9]*$", phoneNum)) {
                        txtPhoneNum.setError(getText(R.string.txt_invalid_phone_number));
                    } else {
                        if (phoneNum.length() < 10) {
                            txtPhoneNum.setError(getText(R.string.txt_invalid_phone_number));
                        } else {
                            txtPhoneNum.setError(null);
                            txtPhoneNum.setErrorEnabled(false);
                        }
                    }
                }

            }
        });
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

        txtID.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    txtID.setError(getText(R.string.txt_empty_field));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ID = txtID.getEditText().getText().toString().trim();
                if (ID.length() != 0) {
                    if (!Pattern.matches("^[0-9]*$", ID)) {
                        txtID.setError(getText(R.string.txt_invalid_id_number));
                    } else {
                        if (ID.length() < 13) {
                            txtID.setError(getText(R.string.txt_invalid_id_number));
                        } else {
                            txtID.setError(null);
                            txtID.setErrorEnabled(false);
                        }
                    }
                }

            }
        });
    }

    private boolean validateInput() {
        //calling this up to prevent the error messages from getting cut off
        resetErrorMessages();
        //get input that user selected
        extractInput();
        boolean blnValid = true;

        //validate id number
        if (strID.length() == 0) {
            txtID.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if it is only numbers
            if ((strID.length() < 13) || (!Pattern.matches("^[0-9]*$", strID))) {
                txtID.setError(getText(R.string.txt_invalid_id_number));
                blnValid = false;
            }
        }

        //validate fname
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

        //validate lname
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


        //validate phone number
        if (strPhone.length() == 0) {
            txtPhoneNum.setError(getText(R.string.txt_empty_field));
            blnValid = false;
        } else {
            //checking if it is only numbers
            if ((strPhone.length() < 10) || (!Pattern.matches("^[0-9]*$", strPhone))) {
                txtPhoneNum.setError(getText(R.string.txt_invalid_phone_number));
                blnValid = false;
            }
        }

        String strChkProvince = txtProvince.getText().toString();
        //validate selected province
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

        return blnValid;
    }

    private void initViews() {
        txtProvLayout = findViewById(R.id.txtAddCourierProvinceLayout);
        txtFName = findViewById(R.id.txtAdminCourierFName);
        txtLName = findViewById(R.id.txtAdminCourierLName);
        txtID = findViewById(R.id.txtAdminCourierID);
        txtPhoneNum = findViewById(R.id.txtAdminCourierPhone);
        txtProvince = this.findViewById(R.id.txtAddCourierProvince);
    }

    private void populateComboBox() {
        adapter =
                new ArrayAdapter<>(
                        AdminAddCourierActivity.this,
                        R.layout.reg_prov_combo_box,
                        arrProvinces);

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
    }

    public void AddCourierToDatabase(View view) {
        //validate input
        if (validateInput()) {
            //ensuring user is connected to the internet
            GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
            if (!globalConnectivityCheck.isNetworkConnected()) {
                //if internet is not connected
                Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //add courier to database
                executeRequest();

                //clear all components'values so new user can input
                clearComponents();
                Toast toastSuccess = Toast.makeText(getApplicationContext(), getText(R.string.txt_admin_add_courier_successful), Toast.LENGTH_SHORT);
                toastSuccess.show();

            }

        } else {
            //make error texts on text change for text inputting
            setUserComponentErrorInteractivity();
        }
    }

    private String capitalizeWord(String str) {
        //function to capitalise words for names
        String words[] = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizeWord.trim();
    }

    private void clearComponents() {
        //clear text
        txtID.getEditText().setText("");
        txtPhoneNum.getEditText().setText("");
        txtLName.getEditText().setText("");
        txtFName.getEditText().setText("");
        txtProvince.setText("");

        //remove errors
        resetErrorMessages();


    }

    private void resetErrorMessages() {
        txtFName.setError(null);
        txtFName.setErrorEnabled(false);
        txtLName.setError(null);
        txtLName.setErrorEnabled(false);
        txtPhoneNum.setError(null);
        txtPhoneNum.setErrorEnabled(false);
        txtID.setError(null);
        txtID.setErrorEnabled(false);
        txtProvLayout.setError(null);
        txtProvLayout.setErrorEnabled(false);
    }

    private void executeRequest() {
        client = new OkHttpClient();
        String link = urlLink + "courierpost.php";

        //format the input appropriately
        formatInput();

        RequestBody formBody = new FormBody.Builder()
                .add("username", strID)
                .add("fname", strFName)
                .add("surname", strLName)
                .add("phone", strPhone)
                .add("prov", strProvince)
                .add("num", "0")
                .build();

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
                    Log.d("INSERT", "Inserting new courier to database successful");
                } else {
                    Log.d("INSERT", "Inserting new courier to database failed");
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

    private void formatInput() {
        strFName = capitalizeWord(strFName.toLowerCase());
        strLName = capitalizeWord(strLName.toLowerCase());
    }

    private void extractInput() {
        strFName = txtFName.getEditText().getText().toString().trim();
        strLName = txtLName.getEditText().getText().toString().trim();
        strID = txtID.getEditText().getText().toString().trim();
        strPhone = txtPhoneNum.getEditText().getText().toString().trim();
    }

}
