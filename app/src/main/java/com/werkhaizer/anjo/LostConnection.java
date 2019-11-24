package com.werkhaizer.anjo;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class LostConnection extends Fragment {
    Button tryAgainBtn;

    int i = 1;

    public LostConnection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lost_connection, container, false);

        tryAgainBtn = view.findViewById(R.id.tryAgainBtn);

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        i ++;
        Thread lostThread;
        lostThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3500 * (i*1.5)) {
                        sleep(160 * (i*2));
                        waited += 160 * (i*2);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        lostThread.start();
    }

}
