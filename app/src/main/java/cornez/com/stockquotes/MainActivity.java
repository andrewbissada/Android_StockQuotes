package cornez.com.stockquotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String symbol;
    TextView symbolText;
    TextView nameText, tradePriceText, tradeTimeText, changeText, rangeText;
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symbolText = (TextView) findViewById(R.id.symbolText);
        nameText = (TextView) findViewById(R.id.nameText);
        tradePriceText = (TextView) findViewById(R.id.tradePriceText);
        tradeTimeText = (TextView) findViewById(R.id.tradeTimeText);
        changeText= (TextView) findViewById(R.id.changeText);
        rangeText = (TextView) findViewById(R.id.rangeText);


        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    // actions when "Done" key is pressed


                    symbol = editText.getText().toString();
                    if(!containsWhiteSpace(symbol)) {
                        getStockInfoTask stockTask = new getStockInfoTask();
                        stockTask.execute(symbol);
                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }

                    //If it contains white space, clear entry & give toast message
                    else {
                        editText.setText("");
                        symbolText.setText("");
                        nameText.setText("");
                        tradePriceText.setText("");
                        tradeTimeText.setText("");
                        changeText.setText("");
                        rangeText.setText("");
                        Toast.makeText(MainActivity.this, "Error in retrieving stock symbol", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_STATE_VISIBLE);
    }


    public static boolean containsWhiteSpace(String symbol) {
        if (!hasLength(symbol)) {
            return false;
        }
        int strLen = symbol.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(symbol.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLength(String symbol) {
        return (symbol != null && symbol.length() > 0);
    }

    //AsyncTask for separate thread due to internet connection
    private class getStockInfoTask extends AsyncTask<String, Void, Stock>
    {

        protected Stock doInBackground(String... params)
        {

            //Stock stock = new Stock(symbol);
            stock = new Stock(symbol);
            try {
                stock.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return stock;
        }

        protected void onPostExecute(Stock stock)
        {

            if(symbol.equals(stock.getSymbol())) {
                symbolText.setText(stock.getSymbol());
            }
            else {
                Toast.makeText(MainActivity.this, "Error in retrieving stock symbol", Toast.LENGTH_SHORT).show();
            }

            nameText.setText(stock.getName());
            tradePriceText.setText(stock.getLastTradePrice());
            tradeTimeText.setText(stock.getLastTradeTime());
            changeText.setText(stock.getChange());
            rangeText.setText(stock.getRange());

        }
    }



    //Save State for rotating screens
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("stock", stock);
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stock = (Stock) savedInstanceState.getSerializable("stock");
        symbolText.setText(stock.getSymbol());
        nameText.setText(stock.getName());
        tradePriceText.setText(stock.getLastTradePrice());
        tradeTimeText.setText(stock.getLastTradeTime());
        changeText.setText(stock.getChange());
        rangeText.setText(stock.getRange());
    }


}
