package com.example.biobolt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TermekListaActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisztracioActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<VasarlasItem> mItemList;
    private VasarlasItemAdapter mAdapter;

    private ErtesitesHandler myNotificationHandler;
    private AlarmManager myAlarmManage;

    private FirebaseFirestore myFirestore;
    private CollectionReference myItems;

    private FrameLayout greenCircle;
    private TextView cTextView;

    private int gridNumber = 1;
    private int carts = 0;
    private int limit = 16;
    private boolean stream =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termek_lista);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Bejelentkezett felhasználó!");
        }else{
            Log.d(LOG_TAG, "Nem bejelentkezett felhasználó!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new VasarlasItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        myFirestore = FirebaseFirestore.getInstance();
        myItems = myFirestore.collection("Products");
        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        this.registerReceiver(battery, filter);

        myNotificationHandler = new ErtesitesHandler(this);
        myAlarmManage = (AlarmManager) getSystemService(ALARM_SERVICE);

        if ((int) Build.VERSION.SDK_INT < 33){
            setMyAlarmManage();
        }

    }

    BroadcastReceiver battery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null){
                return;
            }
            switch (action){
                case Intent.ACTION_POWER_DISCONNECTED:
                    limit = 6;
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    limit = 12;
                    break;
            }
            queryData();
        }
    };

    private void queryData() {
        mItemList.clear();
        myItems.orderBy("cartInCount", Query.Direction.DESCENDING).limit(limit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                VasarlasItem item = document.toObject(VasarlasItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }
            if(mItemList.size()==0){
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteProduct(VasarlasItem product){
        DocumentReference reference = myItems.document(product._get_ID());

        reference.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Termék sikeresen törölve: " + product._get_ID());
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this,"Termék "+product._get_ID()+" törlése nem sikerült.",Toast.LENGTH_LONG).show();
        });
        queryData();
        myNotificationHandler.cancel();
    }

    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.itemsName);
        String[] itemSub = getResources().getStringArray(R.array.itemsSub);
        String[] itemPrice = getResources().getStringArray(R.array.itemsPrice);
        TypedArray itemImg = getResources().obtainTypedArray(R.array.itemsImg);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.itemsRate);


        for(int i=0; i<itemList.length;i++){
            myItems.add(new VasarlasItem(itemList[i], itemSub[i], itemPrice[i], itemRate.getFloat(i,0), itemImg.getResourceId(i,0),0));
        }
        itemImg.recycle();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:
                Log.d(LOG_TAG,"Kijelenkezésre kattintottál!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting:
                Log.d(LOG_TAG,"Beállításokra kattintottál!");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG,"Kosárra kattintottál!");
                return true;
            case R.id.viewSelect:
                Log.d(LOG_TAG,"Nézetváltásra kattintottál!");
                if(stream){
                    changeSpan(item, R.drawable.column,2);

                }else{
                    changeSpan(item, R.drawable.stream,1);
                    
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void changeSpan(MenuItem item, int drawable, int spanCount) {
        stream = !stream;
        item.setIcon(drawable);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cartmenu);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        greenCircle = (FrameLayout)rootView.findViewById(R.id.alert_circle);
        cTextView = (TextView)rootView.findViewById(R.id.alert_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }
    public void update(VasarlasItem product){
        carts = (carts+1);
        if(0 < carts){
            cTextView.setText(String.valueOf(carts));

        }else{
            cTextView.setText("");
        }
        greenCircle.setVisibility((carts > 0) ? VISIBLE : GONE);

        myItems.document(product._get_ID()).update("cartInCount",product.getCartInCount()+1)
                .addOnFailureListener(failure ->{
                    Toast.makeText(this,"Termék "+product._get_ID()+" módosítása nem sikerült.",Toast.LENGTH_LONG).show();
                });


        if ((int) Build.VERSION.SDK_INT < 33){
            myNotificationHandler.send(product.getName());
        }

        queryData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(battery);
    }

    private void setMyAlarmManage(){
        long plustime = AlarmManager.INTERVAL_HALF_HOUR;
        long triggerTime = SystemClock.elapsedRealtime() + plustime;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        myAlarmManage.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,plustime,pendingIntent);
        //end method:  myAlarmManage.cancel(pendingIntent);
    }
}