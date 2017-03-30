package be.formation.mupacif.discovery;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import be.formation.mupacif.discovery.db.ExternalDatabase.UserDAO;
import be.formation.mupacif.discovery.model.User;
import be.formation.mupacif.discovery.userManager.SigninFragment;
import be.formation.mupacif.discovery.userManager.SignupFragment;

import static be.formation.mupacif.discovery.Utils.Utils.getSecureSalt;
import static be.formation.mupacif.discovery.Utils.Utils.get_SHA_512_SecurePassword;

public class UserScreen extends AppCompatActivity implements SigninFragment.SigninCallBackListener, SignupFragment.SignupListener {
    private static final String IS_SIGNIN="isSignin";
    boolean isSigningIn;
    FragmentManager fragmentManager;
    UserDAO userDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userDAO = new UserDAO();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            isSigningIn = savedInstanceState.getBoolean(IS_SIGNIN, true);
            if(isSigningIn)
            signInScreen();
            else
                signUpScreen();
        }else
        {
            signInScreen();
        }


    }
    public void signUpScreen()
    {
        isSigningIn = false;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SignupFragment signupFragment = SignupFragment.getInstance();
        signupFragment.setListener(this);
        fragmentTransaction.replace(R.id.users_sign, SignupFragment.getInstance(),"signup");
        fragmentTransaction.commit();
    }
    public void signInScreen()
    {
        isSigningIn = true;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SigninFragment signinFragment = (SigninFragment) fragmentManager.findFragmentByTag("signin");
        if(signinFragment == null) {
            signinFragment = SigninFragment.getInstance();
            signinFragment.setListener(this);
            fragmentTransaction.add(R.id.users_sign, signinFragment, "signin");
        }
        else
        {
            fragmentTransaction.replace(R.id.users_sign,signinFragment);
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onSignIn(final String username, String password) {

        userDAO.tryToConnect(username, password, new UserDAO.LoginEventListener() {
            @Override
            public void connect(User user, String password) {

                if(user==null)
                    Toast.makeText(UserScreen.this,"Uknown username",Toast.LENGTH_SHORT).show();
                else if(get_SHA_512_SecurePassword(password,user.getSalt()).equals(user.getPassword()))
                {
                    Connect();
                }else
                    Toast.makeText(UserScreen.this,"Wrong password",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void Connect()
    {
        Toast.makeText(UserScreen.this,"Connection successed",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onRegister() {
        signUpScreen();
    }

    @Override
    public void onSignUp(User user) {

//        userDAO.tryToConnect(user.getUsername(), null, new UserDAO.LoginEventListener() { 
//            @Override
//            public void connect(User user, String password) {
//
//                if(user==null)
//                { //// FIXME: 30-03-17 why does it break here? 
                    String salt = getSecureSalt();
                    String encryptedPassword= get_SHA_512_SecurePassword(user.getPassword(),salt);
                    user.setSalt(salt);
                    user.setPassword(encryptedPassword);
                    userDAO.insert(user);
                    Toast.makeText(UserScreen.this,"Your account has been created",Toast.LENGTH_SHORT).show();
                    signInScreen();
//                }
//                else
//                {
//                    Toast.makeText(UserScreen.this,"this login already exists",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putBoolean(IS_SIGNIN, isSigningIn);


        super.onSaveInstanceState(savedInstanceState);
    }

}
