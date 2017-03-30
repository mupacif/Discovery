package be.formation.mupacif.discovery.userManager;

import android.support.v4.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import be.formation.mupacif.discovery.R;
import be.formation.mupacif.discovery.databinding.FragmentSignupBinding;
import be.formation.mupacif.discovery.model.User;


public class SignupFragment extends Fragment implements Validator.ValidationListener{
    private static SignupFragment instance;
    private FragmentSignupBinding dataBinging;

    @NotEmpty
    private EditText userName;
    @Password(min = 6,message = "minimum 6 characters")
    private EditText password;
    @ConfirmPassword
    private EditText confirmPassword;
    @Email
    private EditText email;

    Validator validator;

    @Override
    public void onValidationSucceeded() {

        User user = new User();
        user.setUsername(dataBinging.fragmentSignupUsername.getText().toString());
        user.setPassword(dataBinging.fragmentSignupPassword.getText().toString());
        user.setEmail(dataBinging.fragmentSignupEmail.getText().toString());


        listener.onSignUp(user);
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

    public interface SignupListener {
        public void onSignUp(User user);
    }

    private SignupListener listener;

    public void setListener(SignupListener listener) {
        this.listener = listener;
    }

    public static SignupFragment getInstance() {
        if (instance == null)
            instance = new SignupFragment();

        return instance;
    }

    public SignupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        validator = new Validator(this);
        validator.setValidationListener(this);
        dataBinging = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        View v = dataBinging.getRoot();
        dataBinging.fragmentSignupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            validator.validate();
            }
        });

        userName = dataBinging.fragmentSignupUsername;
                password = dataBinging.fragmentSignupPassword;
        confirmPassword = dataBinging.fragmentSignupConfirmpassword;
                email = dataBinging.fragmentSignupEmail;
        return v;
    }
}
