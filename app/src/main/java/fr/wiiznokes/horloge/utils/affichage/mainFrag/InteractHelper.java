package fr.wiiznokes.horloge.utils.affichage.mainFrag;


import static fr.wiiznokes.horloge.app.MainActivity.ListActif;
import static fr.wiiznokes.horloge.app.MainActivity.ListInactif;
import static fr.wiiznokes.horloge.app.MainActivity.MapIdAlarm;
import static fr.wiiznokes.horloge.app.MainActivity.MapIdDate;
import static fr.wiiznokes.horloge.app.MainActivity.items;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fr.wiiznokes.horloge.R;
import fr.wiiznokes.horloge.app.MainActivity;
import fr.wiiznokes.horloge.fragments.app.AddFragment;
import fr.wiiznokes.horloge.fragments.app.MainFragment;
import fr.wiiznokes.horloge.utils.affichage.mainFrag.Affichage;
import fr.wiiznokes.horloge.utils.notif.alert.AlertHelper;
import fr.wiiznokes.horloge.utils.storage.Alarm;
import fr.wiiznokes.horloge.utils.storage.StorageUtils;
import fr.wiiznokes.horloge.utils.storage.Trie;

public class InteractHelper {

    private final TextView activeAlarmTextView;
    private final TextView timeLeftTextView;
    private final MainActivity mainActivity;







    public InteractHelper(MainActivity mainActivity, TextView activeAlarmTextView, TextView timeLeftTextView){
        this.activeAlarmTextView = activeAlarmTextView;
        this.timeLeftTextView = timeLeftTextView;
        this.mainActivity = mainActivity;
    }

    public void switchHelper(Alarm currentAlarm){

        //l'alarm devient inactive
        if(currentAlarm.active){
            currentAlarm.active = false;

            ListActif.remove(currentAlarm.id);
            Trie.listInactifChange(currentAlarm.id);

            //remove Alarm de AlarmManager
            AlertHelper.remove(currentAlarm, mainActivity);
        }
        //l'alarm devient active
        else {
            currentAlarm.active = true;

            ListInactif.remove(currentAlarm.id);
            Trie.listActifChange(currentAlarm.id);

            //ajout de l'alarm au AlarmManager
            AlertHelper.add(currentAlarm, mainActivity, MapIdDate.get(currentAlarm.id).getTimeInMillis());
        }

        MapIdAlarm.put(currentAlarm.id, currentAlarm);
        Trie.listSortId();

        //time left
        try {
            timeLeftTextView.setText(Affichage.tempsRestant(MapIdAlarm.get(ListActif.get(0))));
        }catch (Exception e){
            timeLeftTextView.setText(Affichage.tempsRestant(null));
        }
        activeAlarmTextView.setText(Affichage.NombreAlarmsActives(ListActif.size()));

        //ecriture
        StorageUtils.writeObject(mainActivity, MapIdAlarm, StorageUtils.alarmsFile);



        //maj affichage
        Trie.listItems();

        MainFragment.adapter.notifyDataSetChanged();
    }




    public void effacer(Alarm currentAlarm){

        long id = currentAlarm.id;


        MapIdAlarm.remove(id);
        MapIdDate.remove(id);

        if(ListActif.contains(id)){
            ListActif.remove(id);
        }
        else{
            ListInactif.remove(id);
        }



        Trie.listSortId();

        //maj nb alarm active
        activeAlarmTextView.setText(Affichage.NombreAlarmsActives(ListActif.size()));

        //time left
        try {
            timeLeftTextView.setText(Affichage.tempsRestant(MapIdAlarm.get(ListActif.get(0))));
        }catch (Exception e){
            timeLeftTextView.setText(Affichage.tempsRestant(null));
        }

        //ecriture
        StorageUtils.writeObject(mainActivity, MapIdAlarm, StorageUtils.alarmsFile);

        //maj affichage
        Trie.itemsRemove(currentAlarm.id);
        MainFragment.adapter.notifyDataSetChanged();
        
        AlertHelper.remove(currentAlarm, mainActivity);

        Toast.makeText(mainActivity, "effacé", Toast.LENGTH_SHORT).show();

    }

    public void modifier(Alarm currentAlarm, View v){

        MainActivity activity = (MainActivity) v.getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, AddFragment.newInstance(true, currentAlarm))
                .commit();
    }

    public void actualiser(){
        Trie.actualiser();

        //number active alarm
        activeAlarmTextView.setText(Affichage.NombreAlarmsActives(ListActif.size()));

        //time left
        try {
            timeLeftTextView.setText(Affichage.tempsRestant(MapIdAlarm.get(ListActif.get(0))));
        }catch (Exception e){
            timeLeftTextView.setText(Affichage.tempsRestant(null));
        }

        MainFragment.adapter.notifyDataSetChanged();
    }


}
