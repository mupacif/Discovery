package be.formation.mupacif.discovery.userManager;

import android.support.v4.app.Fragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.databinding.tool.Binding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.formation.mupacif.discovery.R;


public class SignupFragment extends Fragment {
    private static SignupFragment instance;

    public static SignupFragment getInstance()
    {
        if(instance!=null)
            instance = new SignupFragment();

        return instance;
    }
    public SignupFragment()
    {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewDataBinding inflate = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        View v = inflate.getRoot();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
