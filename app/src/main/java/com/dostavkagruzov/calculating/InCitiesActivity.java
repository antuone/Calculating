package com.dostavkagruzov.calculating;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InCitiesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_cities);

        ListView lv = (ListView) findViewById(R.id.listView2);
        //Обработчик нажатия на элемент списка
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) parent.getItemAtPosition(position);
                Cities.in = city.getId();
                Intent intent = new Intent(InCitiesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Cities.withoutPrices.clear();
        //Поиск id городов у которых нет тарифов
        List<Double> row = Cities.pricesValue.get(0);
        if(Cities.from == 1){
            row = Cities.pricesValue.get(10);
        } else if(Cities.from > 1){
            row = Cities.pricesValue.get(9*Cities.from+1);
        }
        Iterator<Double> prices = row.iterator();
        Integer id = 0;
        while (prices.hasNext()) {
            Double price = prices.next();
                if(price == 0.0) {
                    Cities.withoutPrices.add(id);
                }
            id++;
        }

        //Заполняем список городами
        List<City> cities = new ArrayList<>();
        Integer i = 0;
        for(String s: Cities.names){
            cities.add(new City(s, i++));
        }

        // создаем адаптер
        final ArrayAdapter<City> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, cities);
        // присваиваем адаптер списку
        lv.setAdapter(adapter);

        //Удаляем города в которых нет тарифов
        Integer j = 0;
        for(Integer in: Cities.withoutPrices){
            cities.remove(in-j);
            j++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_cities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class City {
        public String name;
        public Integer id;

        public City(String name, Integer id) {
            this.name = name;
            this.id = id;
        }
        public Integer getId(){
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
