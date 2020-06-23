package com.example.lendahand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity4 extends AppCompatActivity {
    private TabLayout tbDonorReg;
    private TextInputLayout txtStreetAddress, txtSuburb, txtPostalCode,txtProvLayout;
    private AutoCompleteTextView txtProvince;
    private String strStreetAddress, strSuburb, strProvince="", strPostalCode;
    private  String[] arrProvinces = new String[] {"KwaZulu-Natal", "Western Cape", "North West", "Northern Cape","Free State","Gauteng", "Limpopo","Mpumalanga", "Eastern Cape"};
    private ArrayAdapter<String> adapter;
    private String urlLink = "https://lamp.ms.wits.ac.za/home/s2089676/";
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reg4);
        initViews();

        //select current tab
        tbDonorReg.getTabAt(3).select();

        //this method changes tab icons to arrows to show step was completed
        setTabIcons();

        //this method ensures that only the next step can be accessed
        setTabInteractivity();

        //this method is to handle the on change event handlers of the edit texts
        setUserComponentErrorInteractivity();

        tbDonorReg.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 4) {
                    //check connectivity
                    GlobalConnectivityCheck globalConnectivityCheck = new GlobalConnectivityCheck(getApplicationContext());
                    if (!globalConnectivityCheck.isNetworkConnected()) {
                        //if internet is not connected
                        Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.txt_internet_disconnected), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        if (validateInput()) {
                            Intent intent = new Intent(RegActivity4.this, RegActivityFinal.class);
                            //get from previous activity
                            Bundle bundle = getIntent().getExtras();
                            String strPassword = bundle.getString("password");
                            final String strUsername = bundle.getString("username");
                            String strFName = bundle.getString("fname");
                            String strLName = bundle.getString("lname");
                            final String strEmail = bundle.getString("email");
                            String strPhoneNumber = bundle.getString("phoneNo");

                            //ensure the values are of proper format
                            assert strFName != null;
                            strFName = capitalizeWord(strFName.toLowerCase());
                            assert strLName != null;
                            strLName = capitalizeWord(strLName.toLowerCase());
                            assert strStreetAddress != null;
                            strStreetAddress = capitalizeWord(strStreetAddress.toLowerCase());
                            assert strSuburb != null;
                            strSuburb = capitalizeWord(strSuburb.toLowerCase());

                            client = new OkHttpClient();
                            String link = urlLink + "donorpost.php";

                            RequestBody formBody = new FormBody.Builder()
                                    .add("username", strUsername)
                                    .add("pass", strPassword)
                                    .add("fname", strFName)
                                    .add("surname", strLName)
                                    .add("email", strEmail)
                                    .add("phone", strPhoneNumber)
                                    .add("street", strStreetAddress)
                                    .add("sub", strSuburb)
                                    .add("prov", strProvince)
                                    .add("postcode", strPostalCode)
                                    .add("utype", "donor")
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
                                        Log.d("INSERT","Inserting new donee to database successful");
                                    }else{
                                        Log.d("INSERT","Inserting new donee to database failed");
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

                            //send email to user telling them that their account has been created
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        GMailSender sender = new GMailSender(getText(R.string.txt_developer_email).toString(),
                                                getText(R.string.txt_developer_pword).toString());
                                        sender.sendMail(getText(R.string.txt_email_subject).toString(), getText(R.string.txt_email_body_common).toString()+strUsername+getText(R.string.txt_email_body_donor).toString(),
                                                getText(R.string.txt_developer_email).toString(), strEmail);
                                    } catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }
                                }

                            }).start();

                            //go to next activity
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            tbDonorReg.getTabAt(3).select();
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

    private boolean validateInput() {
        boolean blnValid=true;
        //extract contents of textboxes
        extractInput();
       if (strPostalCode.length()==0){
            txtPostalCode.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            if ((strPostalCode.length()<4) || (!Pattern.matches("^[0-9]*$", strPostalCode))){
                txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                blnValid=false;
            }
        }
        if (strSuburb.length()==0){
            txtSuburb.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }
        if (strStreetAddress.length()==0){
            txtStreetAddress.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }

        String strChkProvince=txtProvince.getText().toString();

        if ((strProvince.length()==0) && (strChkProvince.length()==0)){
            txtProvLayout.setError(getText(R.string.txt_empty_field));
            blnValid=false;
        }else{
            if (strChkProvince!=strProvince){
                boolean blnFound=false;
                //checking that user didn't type in their own province
                for (int i=0;i<arrProvinces.length;i++){
                    if (strChkProvince.equals(arrProvinces[i])){
                        blnFound=true;
                    }
                }
                if (blnFound==false){
                    blnValid=false;
                    txtProvLayout.setError(getText(R.string.txt_invalid_province));
                    txtProvince.setText("");
                }
            }
        }
        return blnValid;
    }

    private void extractInput() {
        strPostalCode=txtPostalCode.getEditText().getText().toString().trim();
        strStreetAddress=txtStreetAddress.getEditText().getText().toString().trim();
        strSuburb=txtSuburb.getEditText().getText().toString().trim();
    }

    private void setUserComponentErrorInteractivity() {
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
                tbDonorReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtSuburb.setError(getText(R.string.txt_empty_field));
                }else{
                    txtSuburb.setError(null);
                    txtSuburb.setErrorEnabled(false);
                }
            }
        });
        txtStreetAddress.getEditText().addTextChangedListener(new TextWatcher() {
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
                tbDonorReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtStreetAddress.setError(getText(R.string.txt_empty_field));
                }else{
                    txtStreetAddress.setError(null);
                    txtStreetAddress.setErrorEnabled(false);
                }
            }
        });

        txtPostalCode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String postalCode = txtPostalCode.getEditText().getText().toString().trim();
                if (postalCode.length()!=0) {
                    if (!Pattern.matches("^[0-9]*$", postalCode)) {
                        txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                    } else {
                        if (postalCode.length() < 4) {
                            txtPostalCode.setError(getText(R.string.txt_invalid_postal_code));
                        } else {
                            txtPostalCode.setError(null);
                            txtPostalCode.setErrorEnabled(false);
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
                tbDonorReg.getTabAt(3).select();
                if(s.length() == 0) {
                    txtPostalCode.setError(getText(R.string.txt_empty_field));
                }
            }


        });
    }

    private void initViews() {
        tbDonorReg = findViewById(R.id.tbDonorReg4);
        txtPostalCode=findViewById(R.id.txtDonorPostalCode);
        txtStreetAddress=findViewById(R.id.txtDonorStreetAddress);
        txtSuburb=findViewById(R.id.txtDonorSuburb);
        //populate province drop down menu
        txtProvLayout=findViewById(R.id.txtDonorProvinceLayout);
        adapter =
                new ArrayAdapter<>(
                        RegActivity4.this,
                        R.layout.reg_prov_combo_box,
                        arrProvinces);
        txtProvince = this.findViewById(R.id.txtDonorProvince);
        txtProvince.setAdapter(adapter);
        txtProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //extract items
                strProvince=parent.getItemAtPosition(position).toString();
                txtProvLayout.setError(null);
                txtProvLayout.setErrorEnabled(false);
            }
        });
    }

    private void setTabInteractivity() {
        LinearLayout tabStrip1 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip1.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip2 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip2.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip3 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip3.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip4 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip4.getChildAt(3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout tabStrip5 = ((LinearLayout) tbDonorReg.getChildAt(0));
        tabStrip5.getChildAt(4).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    private void setTabIcons() {
        tbDonorReg.getTabAt(0).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(1).setIcon(R.drawable.ic_progress_complete_sel);
        tbDonorReg.getTabAt(2).setIcon(R.drawable.ic_progress_complete_sel);
    }

}
