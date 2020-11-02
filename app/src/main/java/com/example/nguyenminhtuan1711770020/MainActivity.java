package com.example.nguyenminhtuan1711770020;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TabActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.widget.TabHost;
import android.app.TabActivity;
import android.widget.AdapterView;

public class MainActivity extends TabActivity {


    private List<Restaurant> listRestaurant = new ArrayList<Restaurant>();
    Cursor curRestaurant = null;
    RestaurantAdapter adapter = null;
    RestaurantHelper helper = null;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            curRestaurant.moveToPosition(position); // lấy item được chọn
            EditText name;
            EditText address;
            RadioGroup types;
            name = (EditText) findViewById(R.id.name);
            address = (EditText) findViewById(R.id.addr);
            types = (RadioGroup) findViewById(R.id.type);
            name.setText(helper.getName(curRestaurant));
            address.setText(helper.getAddress(curRestaurant));
            if (helper.getType(curRestaurant).equals("Sit down"))
                types.check(R.id.sit_down);
            else if (helper.getType(curRestaurant).equals("Take out"))
                types.check(R.id.take_out);
            else
                types.check(R.id.delivery);
            getTabHost().setCurrentTab(1);
        }
    };

    class RestaurantAdapter extends CursorAdapter {
        public RestaurantAdapter(Cursor c) {
            super(MainActivity.this, c);
        }

        public RestaurantAdapter(Context context, Cursor c) {
            super(context, c);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            View row = view;
            ((TextView) row.findViewById(R.id.title)).setText(helper.getName(cursor));
            ((TextView) row.findViewById(R.id.address)).setText(helper.getAddress(cursor));
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            String type = helper.getType(cursor);
            if (type.equals("Take out"))
                icon.setImageResource(R.drawable.t);
            else if (type.equals("Sit down"))
                icon.setImageResource(R.drawable.s);
            else
                icon.setImageResource(R.drawable.d);
        }



        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new RestaurantHelper(this);
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(onSave);
        ListView list = (ListView) findViewById(R.id.restaurants);
        list.setOnItemClickListener(onItemClickListener);
        curRestaurant = helper.getAll();
        startManagingCursor(curRestaurant);
        adapter = new RestaurantAdapter(curRestaurant);
        list.setAdapter(adapter);
        TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
        spec.setContent(R.id.restaurants);
        spec.setIndicator("List", getResources().getDrawable(R.drawable.checklist));
        getTabHost().addTab(spec);
        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.details);
        spec.setIndicator("Details", getResources().getDrawable(R.drawable.restaurant));
        getTabHost().addTab(spec);
        getTabHost().setCurrentTab(0);

    }

    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }


    private View.OnClickListener onSave = new View.OnClickListener() {
        public void onClick(View v) {
            Restaurant r = new Restaurant();
            EditText name = (EditText) findViewById(R.id.name);
            EditText address = (EditText) findViewById(R.id.addr);
            r.setName(name.getText().toString());
            r.setAddress(address.getText().toString());
            RadioGroup type = (RadioGroup) findViewById(R.id.type);
            String str = "";
            switch (type.getCheckedRadioButtonId()) {
                case R.id.take_out:
                    r.setType("Take out");
                    str = name.getText().toString() + " " + address.getText().toString() + " " + "Take out";
                    break;
                case R.id.sit_down:
                    r.setType("Sit down");
                    str = name.getText().toString() + " " + address.getText().toString() + " " + "Take out";
                    break;
                case R.id.delivery:
                    r.setType("Delivery");
                    str = name.getText().toString() + " " + address.getText().toString() + " " + "Delivery";
                    break;
            }
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            listRestaurant.add(r);
            helper.insert(r.getName(), r.getAddress(), r.getType());
            curRestaurant.requery();
        }
    };
}