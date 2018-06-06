package tw.com.team13.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.com.team13.firebaselogin.R;
import tw.com.team13.model.Comment;
import tw.com.team13.model.Photo;
import tw.com.team13.model.User;

/**
 * @author Chun-Kai Kao 2018/6/2 下午 01:54
 * @github http://github.com/cckaron
 */
public class CommentListAdapter extends ArrayAdapter<Comment>{

    private static final String TAG = "CommentListAdapter";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference myRef;
    private FirebaseUser user;

    //vars
    private Photo photo;
    private User myuser;


    private LayoutInflater mInflater;
    private int layoutResources;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull Comment[] objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResources = resource;
    }

    private static class ViewHolder{
        TextView comment, username, timestamp, reply, likes;
        CircleImageView profileImage;
        ImageView like;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null){
            convertView = mInflater.inflate(layoutResources, parent, false);
            holder = new ViewHolder();

            holder.comment = convertView.findViewById(R.id.comment);
            holder.username = convertView.findViewById(R.id.comment_username);
            holder.timestamp = convertView.findViewById(R.id.comment_time_posted);
            holder.reply = convertView.findViewById(R.id.comment_reply);
            holder.like = convertView.findViewById(R.id.comment_like);
            holder.profileImage = convertView.findViewById(R.id.comment_profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set the comment
        holder.comment.setText(getItem(position).getComment());

        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem((position)));
        if(!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + "天");
        }else{
            holder.timestamp.setText("今天");
        }

        //set the username

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = mFirebaseFirestore
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Photos")
                .document("photo_id")
                .collection("Comments");
//                .document(mContext.getString(R.string.user_id))
//                .collection()
        reference.document(getItem(position).getUser_id()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: going to set Photo object");
                            DocumentSnapshot documentSnapshot = task.getResult();
                            photo = documentSnapshot.toObject(Photo.class);
                        }
                    }
                });

        myRef = mFirebaseFirestore.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: going to set User object");
                myuser = documentSnapshot.toObject(User.class);
            }
        });

        return convertView;
    }


    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Comment comment){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei")); // google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        }catch (ParseException e){
            Log.d(TAG, "getTimestampDifference: ParseException:" + e.getMessage());
            difference = "0";
        }
        return difference;
    }
}
