package be.formation.mupacif.discovery.userManager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import be.formation.mupacif.discovery.R;
import be.formation.mupacif.discovery.databinding.FragmentSigninBinding;
import be.formation.mupacif.discovery.model.User;


public class SigninFragment extends Fragment implements Validator.ValidationListener{
    private static SigninFragment instance;
    FragmentSigninBinding dataBinding;

    @NotEmpty
    EditText login;
    @Password
    EditText password;

    Validator validator;
    @Override
    public void onValidationSucceeded() {

        listener.onSignIn(dataBinding.tvFragmentSigninLogin.getText().toString(), dataBinding.tvFragmentSigninPassword.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this.getContext());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface SigninCallBackListener
    {
        public void onSignIn(String username, String password);
        public void onRegister();
    }
    private SigninCallBackListener listener;

    public void setListener(SigninCallBackListener listener) {
        this.listener = listener;
    }

    public static SigninFragment getInstance()
    {
        if(instance==null)
            instance = new SigninFragment();

        return instance;
    }
    public SigninFragment()
    {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        validator = new Validator(this);
        validator.setValidationListener(this);
         dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signin, container, false);
        View v = dataBinding.getRoot();
        dataBinding.tvFragmentSigninBtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validator.validate();

            }
        });

        dataBinding.tvFragmentSigninBtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                listener.onRegister();
            }
        });
        login = dataBinding.tvFragmentSigninLogin;
        password = dataBinding.tvFragmentSigninPassword;
//        View v = inflater.dataBinding(R.layout.fragment_signin,container,false);
        return v;
    }
}
