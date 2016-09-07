package com.dostavkagruzov.calculating;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Cities.isInitializedMainActivity == false) {
            initialize();
        }

        //Если город выбран - выводим название
        TextView tv = (TextView) findViewById(R.id.textView4);
        if(Cities.from != -1){
            tv.setText(Cities.names.get(Cities.from));
        }
        tv = (TextView) findViewById(R.id.textView5);
        if(Cities.in != -1){
            tv.setText(Cities.names.get(Cities.in));
        }else{
            tv.setText("...");
        }

        //Устанавливаем обрабочики событий
        Button _button = (Button) findViewById(R.id.button);
        _button.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, FromCitiesActivity.class);
                                           startActivity(intent);
                                           finish();
                                       }
                                   }
        );

        //Если город "из какого" не выбран, тогда кнопка "в какой город" не будет нажиматся.
        if(Cities.from != -1) {
            _button = (Button) findViewById(R.id.button2);
            _button.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(MainActivity.this, InCitiesActivity.class);
                                               startActivity(intent);
                                               finish();
                                           }
                                       }
            );
        }

        EditText et2 = (EditText) findViewById(R.id.editText2);
        EditText et = (EditText) findViewById(R.id.editText);

        //Нужно чтобы приложение запоминала набранные цыфры
        et2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText et2 = (EditText) findViewById(R.id.editText2);
                Cities.value = et2.getText().toString();
                return false;
            }
        });

        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText et = (EditText) findViewById(R.id.editText);
                Cities.weight = et.getText().toString();
                return false;
            }
        });

        if(!Cities.value.equals("")){
            et2.setText(Cities.value);
        }

        if(!Cities.weight.equals("")){
            et.setText(Cities.weight);
        }




        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Если не все данные введены кнопка "рассчитать" не будет работать
                String s = Cities.value;
                Double volume = 0.0;
                if(!s.equals("")) {
                    volume = Double.parseDouble(s);
                }

                s = Cities.weight;
                Double weight = 0.0;
                if(!s.equals("")){
                    weight = Double.parseDouble(s);
                }

                if(volume > 0 && weight > 0 && Cities.from >= 0 && Cities.in >=0) {
                    try {
                        calculationPice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private Integer getGradeWeight(){
        EditText et = (EditText) findViewById(R.id.editText);
        Double weight = Double.parseDouble(et.getText().toString());

        Integer grade = 0;
        if(weight >= 1 && weight < 100){
            grade = 1;
        }
        if(weight >= 100 && weight < 200){
            grade = 2;
        }
        if(weight >= 200 && weight < 600){
            grade = 3;
        }
        if(weight >= 600 && weight < 1000){
            grade = 4;
        }
        if(weight >= 1000 && weight < 1500){
            grade = 5;
        }
        if(weight >= 1500 && weight < 3000){
            grade = 6;
        }
        if(weight >= 3000 && weight < 5000){
            grade = 7;
        }
        if(weight >= 5000 && weight < 10000){
            grade = 8;
        }
        if(weight >= 10000){
            grade = 9;
        }
        return grade;
    }

    private Integer getGradeVolume(){

        EditText et2 = (EditText) findViewById(R.id.editText2);
        Double volume = Double.parseDouble(et2.getText().toString());

        Integer grade = 0;
        if(volume > 0 && volume < 1){
            grade = 1;
        }
        if(volume >= 1 && volume < 2){
            grade = 2;
        }
        if(volume >= 2 && volume < 3){
            grade = 3;
        }
        if(volume >= 3 && volume < 4){
            grade = 4;
        }
        if(volume >= 4 && volume < 6){
            grade = 5;
        }
        if(volume >= 6 && volume < 12){
            grade = 6;
        }
        if(volume >= 12 && volume < 20){
            grade = 7;
        }
        if(volume >= 20 && volume < 40){
            grade = 8;
        }
        if(volume >= 40){
            grade = 9;
        }
        return grade;
    }

    private void initialize(){
        //Заполняем список городов
        HSSFWorkbook wb = null;
        try {
            wb = new HSSFWorkbook(getResources().openRawResource(R.raw.cool));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = wb.getSheet("sb_price_base_v");
        Row row = sheet.getRow(0);
        Iterator<Cell> cells = row.iterator();
        Cell cell = cells.next();
        while (cells.hasNext()) {
            cell = cells.next();
            Cities.names.add(cell.getStringCellValue());
        }

        //Заполняем списки цен по обьему
        Iterator<Row> rows = sheet.iterator();
        rows.next();
        while (rows.hasNext()){
            cells = rows.next().iterator();
            List<Double> list = new ArrayList<>();
            cells.next();
            while (cells.hasNext()){
                cell = cells.next();
                if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    list.add(cell.getNumericCellValue());
                }else{
                    list.add(0.0);
                }
            }
            Cities.pricesValue.add(list);
        }

        //Заполняем списки цен по весу
        sheet = wb.getSheet("sb_price_base_m");
        rows = sheet.iterator();
        rows.next();
        while (rows.hasNext()){
            cells = rows.next().iterator();
            List<Double> list = new ArrayList<>();
            cells.next();
            while (cells.hasNext()){
                cell = cells.next();
                if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    list.add(cell.getNumericCellValue());
                }else{
                    list.add(0.0);
                }
            }
            Cities.pricesWeight.add(list);
        }

        Cities.isInitializedMainActivity = true;
    }

    private void calculationPice() throws IOException {
        //Нужна проверка EXCEL файла на правильность содержимого

        Integer from = Cities.from;
        Integer in = Cities.in;
        Integer gradeVolume = getGradeVolume();
        Integer gradeWeight = getGradeWeight();
        Double priceVolume = 0.0;
        Double priceWeight = 0.0;

        EditText et2 = (EditText) findViewById(R.id.editText2);
        Double volume = Double.parseDouble(et2.getText().toString());

        EditText et = (EditText) findViewById(R.id.editText);
        Double weight = Double.parseDouble(et.getText().toString());

        //Вычисление по обьему
        List<Double> list = null;
        if(from == 0) {
            list = Cities.pricesValue.get(gradeVolume - 1);
        }else if(from > 0){
            list = Cities.pricesValue.get(gradeVolume + 9*from - 1);
        }
        Double price = list.get(in);
        priceVolume = price * volume;

        //Вычисляем по весу
        if(from == 0) {
            list = Cities.pricesWeight.get(gradeWeight - 1);
        }else if(from > 0){
            list = Cities.pricesWeight.get(gradeWeight + 9*from - 1);
        }
        price = list.get(in);
        priceWeight = price * weight;

        //Выводим результат
        TextView t = (TextView) findViewById(R.id.textView3);
        if(priceVolume > priceWeight) {
            t.setText(priceVolume + " Рублей");
        }else{
            t.setText(priceWeight + " Рублей");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
