package com.venu.venutheta.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.venu.venutheta.R;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.NetUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SignUpActivity extends AppCompatActivity {

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
        add("user_friends");
        add(" publish_actions");
    }};
    StateButton mBtnFb;
    StateButton gotoLogin,mEmailSignInButton;
    private ProgressDialog progress;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPhoneView;
    private EditText mUsername;
    private View mProgressView;
    private View mLoginFormView;
    private MobiComUserPreference mobiComUserPreference;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.signup_email);
        mPhoneView = (EditText) findViewById(R.id.signup_phone);
        mUsername = (EditText) findViewById(R.id.signup_username);
        gotoLogin = (StateButton) findViewById(R.id.signup_goto_login);
        mBtnFb = (StateButton) findViewById(R.id.fb_signup);
        mEmailSignInButton = (StateButton) findViewById(R.id.signup_signup_btn);

        gotoLogin.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
        mPasswordView = (EditText) findViewById(R.id.signup_password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.sign_up || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.signup_sign_up_form);
        mProgressView = findViewById(R.id.signup_sign_up_progress);

        mBtnFb.setOnClickListener(v -> {
            if (NetUtils.hasInternetConnection(getApplicationContext()))
                onFbSignup();

        });
    }

    private void updateUser() {
        // request phonenumber
        //
    }


    private void onFbSignup() {
            progress = ProgressDialog.show(SignUpActivity.this, null,
                    getResources().getString(R.string.progress_connecting), true);
        ParseFacebookUtils.logInWithReadPermissionsInBackground(SignUpActivity.this, mPermissions, new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException err) {
                if (user == null) {
                    Timber.i("MyApp Uh oh. The user cancelled the Facebook login.");

                    Toast.makeText(getApplicationContext(), "Log-out from Facebook and try again please!", Toast.LENGTH_SHORT).show();

                    ParseUser.logOut();

                    progress.hide();
                } else if (user.isNew()) {
                    Timber.i("MyApp User signed up and logged in through Facebook!");

                    if (!ParseFacebookUtils.isLinked(user)) {
                        ParseFacebookUtils.linkWithReadPermissionsInBackground(user, SignUpActivity.this, mPermissions, new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ParseFacebookUtils.isLinked(user)) {
                                    Timber.i("MyApp Woohoo, user logged in with Facebook!");
                                    getUserDetailsFromFB();
                                }
                            }
                        });
                    } else {
                        getUserDetailsFromFB();
                    }
                } else {
                    Timber.i("MyApp User logged in through Facebook!");

                    if (!ParseFacebookUtils.isLinked(user)) {
                        ParseFacebookUtils.linkWithReadPermissionsInBackground(user, SignUpActivity.this, mPermissions, new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ParseFacebookUtils.isLinked(user)) {
                                    Timber.i("MyApp Woohoo, user logged in with Facebook!");
                                    ParseUser.logOut();

                                    progress.hide();
                                }
                            }
                        });
                    } else {
                        ParseUser.logOut();
                        progress.hide();
                    }
                }
            }
        });
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPhoneView.setError(null);
        mUsername.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String username = mUsername.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username is empty");
            focusView = mUsername;
            cancel = true;
        } else if (!isUserNameValid(username)) {
            mUsername.setError("min lenght is 5");
            focusView = mUsername;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email is empty");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password is empty");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("Min lenght is 8 ");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError("Phone Contact is empty");
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError("Must be more 10 number");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
//            focusView.requestFocus();
        } else {

            if (NetUtils.hasInternetConnection(getApplicationContext())){
                progress = ProgressDialog.show(SignUpActivity.this, null,
                        getResources().getString(R.string.progress_connecting), true);
                userSignUp(username, email, password, phone);
            }
        }


    }


    private void userSignUp(String username, String email, String password, String phone) {

        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.put("phone", phone);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    progress.dismiss();
                    // Show the error message
                    Timber.d("error signing user : %s" ,e.getMessage());
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Timber.i("success signing up");
                    initSettings();


                }
            }
        });
    }

    private void initSettings(){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.put("user_id", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();

        ParseObject userRelations = new ParseObject(GlobalConstants.CLASS_USER_RELATION);
        userRelations.put("user", ParseUser.getCurrentUser());
        userRelations.saveInBackground();

        mobiComUserPreference = MobiComUserPreference.getInstance(getApplicationContext());
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                ApplozicSetting.getInstance(context).setSentMessageBackgroundColor(R.color.venu_flat_color); // accepts the R.color.name
                ApplozicSetting.getInstance(context).setReceivedMessageBackgroundColor(R.color.venu_orange); // accepts the R.color.name
                ApplozicSetting.getInstance(context).disableLocationSharingViaMap();
                PushNotificationTask.TaskListener pushNotificationTaskListener = new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {

                    }

                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                    }
                };
                PushNotificationTask pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(), pushNotificationTaskListener, context);
                pushNotificationTask.execute((Void) null);
                progress.dismiss();
                startActivity(new Intent(SignUpActivity.this, OnboardUsersActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();

            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                mAuthTask = null;
                progress.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                alertDialog.setTitle("");
                alertDialog.setMessage(exception.toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                        });
                if (!isFinishing()) {
                    alertDialog.show();
                }
            }
        };

        User applozicUser = new User();
        applozicUser.setUserId(ParseUser.getCurrentUser().getObjectId()); //applozicUserId it can be any unique applozicUser identifier
        applozicUser.setDisplayName(ParseUser.getCurrentUser().getUsername()); //displayName is the name of the applozicUser which will be shown in chat messages
        applozicUser.setEmail(ParseUser.getCurrentUser().getEmail()); //optional
        applozicUser.setImageLink("");//optional,pass your image link


        mAuthTask = new UserLoginTask(applozicUser, listener, getApplicationContext());
        mAuthTask.execute((Void) null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void getFbUserPhoneNumber(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dailog_settings, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setHint("+233XXXXXXXXX");
        dialogBuilder.setTitle("Required Phone Number");
        dialogBuilder.setMessage("This allows us to find friend in your contact ");
        dialogBuilder.setPositiveButton("Continue", (dialog, whichButton) -> {
            progress.show();
            String phoneNumber = edt.getText().toString();
            if (phoneNumber.length() > 0) {
                ParseUser.getCurrentUser().put("phone",phoneNumber);
                ParseUser.getCurrentUser().saveInBackground(e -> {
                    if (e == null){
                        Toast.makeText(this,"Phone nuber Updated",Toast.LENGTH_SHORT).show();
                        initSettings();

                    }else {
                        Timber.e("error updateing user %s",e.getMessage());
                        progress.dismiss();
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> getUserDetailsFromFB());
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void getUserDetailsFromFB() {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    try {
//                        mEmailView.setText(email);
//                        mUsername.setText(name);

                        String name = response.getJSONObject().getString("name");
                        String email = response.getJSONObject().getString("email");

                        ParseUser user = ParseUser.getCurrentUser();
                        user.setUsername(name);
                        user.setEmail(email);
                        user.save();

//                        JSONObject picture = response.getJSONObject().getJSONObject("picture");
//                        JSONObject data = picture.getJSONObject("data");
                        getFbUserPhoneNumber();
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }finally {
                        progress.dismiss();

                    }
                });

        request.setParameters(parameters);
        request.executeAsync();

    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    private boolean isPhoneValid(String number) {
        return number.length() > 9;
    }

    private boolean isUserNameValid(String username) {
        return username.length() > 4;
    }


}
