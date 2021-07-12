package android.example.videocallapp.Activities;

import android.example.videocallapp.R;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;


public class VideocallFragment extends Fragment {


    private EditText secretCodeBox;
    private Button joinBtn;

    private URL serverURL;

    public VideocallFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflating the layout
        View view = inflater.inflate(R.layout.fragment_videocall, container, false);

        //Getting refrence to the layout variables
        secretCodeBox = (EditText) view.findViewById(R.id.secretCode);
        joinBtn = (Button) view.findViewById(R.id.joinButton);


        try {
            //Here we connect to the Gitsi server
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            //Throws exception if there is any error in connection to server
            e.printStackTrace();
        }

        //Added click listener to join button
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(container.getContext(),"Joined" , Toast.LENGTH_SHORT).show();
                //Here we connect to the jisti server by creating Jitsi object
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(secretCodeBox.getText().toString())
                        .setWelcomePageEnabled(false)
                        .build();
                //Here the server get launched
                JitsiMeetActivity.launch(container.getContext() , options);
            }
        });

        return view;
    }
}