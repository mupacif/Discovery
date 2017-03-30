package be.formation.mupacif.discovery.db.ExternalDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import be.formation.mupacif.discovery.model.User;

/**
 * Created by mupac_000 on 29-03-17.
 */

public class UserDAO {

    public static final String REFERENCE_NAME="users";
    public static final String REFERENCE_USERNAME="username";
    User user=null;


    DatabaseReference databaseReference;
    public UserDAO() {
       databaseReference = FirebaseDatabase.getInstance().getReference(REFERENCE_NAME);
    }

    public void insert(User user)
    {
        databaseReference.push().setValue(user);
    }
    public void tryToConnect(String username, final String password, final LoginEventListener loginEventListener){

        Query query = databaseReference.orderByChild(REFERENCE_USERNAME).equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                loginEventListener.connect(dataSnapshot.getChildren().iterator().next().getValue(User.class),password);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void test(User u)
    {
        user = u;
    }

    public interface LoginEventListener
    {
        public void connect(User user,String password);
    }
}
