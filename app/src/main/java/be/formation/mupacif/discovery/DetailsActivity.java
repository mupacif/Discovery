package be.formation.mupacif.discovery;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import be.formation.mupacif.discovery.databinding.ActivityDetailsBinding;
import be.formation.mupacif.discovery.model.Interest;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding dababinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActivityDetailsBinding databinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        Intent intent = getIntent();
        Interest interest;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
//            interest = (Interest) bundle.getSerializable("interest");
            String title = bundle.getString("title");
//            String date = bundle.getString("date");
            String location = bundle.getString("location");
            String description = bundle.getString("description");
//            databinding.tvDetailsDate.setText(date);
            databinding.tvDetailsDescription.setText(description);
            databinding.tvDetailsLocation.setText(location);
      getSupportActionBar().setTitle(title);
        }



    }


}
