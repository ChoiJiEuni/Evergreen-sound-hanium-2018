package com.microsoft.projectoxford.face.samples.db;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.microsoft.projectoxford.face.samples.persongroupmanagement.AddFaceToPersonActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonActivity;

public class DBService extends Service {
    public DBService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processCommand(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent) {
        String command = intent.getStringExtra("COMMAND");
        if(command.equals("Registered_TB_Pref")){
            SharedPreferences pref = getSharedPreferences("Registered_TB_Pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Boolean registration = intent.getBooleanExtra("REGISTRATION",false);
            editor.putBoolean("registration",registration);

            String DATA = intent.getStringExtra("DATA");
            if(DATA.equals("NAME")){
                String  name = intent.getStringExtra("NAME");
                editor.putString("name", name);
                editor.commit();
            }
            if(DATA.equals("PERSON_IMG_PATH")){
                String  person_img_path = intent.getStringExtra("PERSON_IMG_PATH");
                editor.putString("person_img_path", person_img_path);
                editor.commit();
            }
        }

    }

}
