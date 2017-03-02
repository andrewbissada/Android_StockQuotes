package cornez.com.stockquotes;

/**
 * Created by Andrew on 2/24/2017.
 */

        import android.util.Log;
        import java.net.*;
        import java.io.*;


public class Stock implements Serializable
{
    private static final long serialVersionUID = 46827221313596640L;

    private static final boolean DEBUG = true;

    private static final String QUOTE_FORMAT = "&f=lcwn";
    // format for symbols: last trade (with time), change & percent change,
    // 52-week range, name

    private static final String TAG_PREFIX = "Stock";

    private String symbol;
    private String lastTradeTime;
    private String lastTradePrice;
    private String change;
    private String range;
    private String name;


    public Stock(String symbol)
    {
        this.symbol = symbol.toUpperCase();

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock()", "symbol = " + symbol);
    }


    public void load() throws MalformedURLException, IOException
    {
        URL url = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + symbol
                + QUOTE_FORMAT);

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "url = " + url);

        URLConnection connection = url.openConnection();

        BufferedReader in = new
                BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line = in.readLine();

        if (DEBUG)
            Log.i(TAG_PREFIX + "Stock.load()", "line = " + line);


        // consume any data remaining in the input stream
        while (in.readLine() != null)
            ;

        in.close();

        if (line != null && line.length() > 0)
        {
            // parse the line and remove quotes where necessary
            String[] values = line.split(",");
            change = values[1].substring(1, values[1].length() - 1);
            range  = values[2].substring(1, values[2].length() - 1);
            name   = values[3].substring(1, values[3].length() - 1);

            // Since real names can have commas, handle possible rest of name.
            for (int i = 4;  i < values.length;  ++i)
                name = name + ", " + values[i].substring(1, values[i].length() - 1);

            if (DEBUG)
                Log.i(TAG_PREFIX + "Stock.load()", "name = " + name);

            String lastTrade = values[0];

            // parse last trade time
            int start = 1; // skip opening quote
            int end = lastTrade.indexOf(" - ");
            lastTradeTime = lastTrade.substring(start, end);

            // parse last trade price
            start = lastTrade.indexOf(">") + 1;
            end = lastTrade.indexOf("<", start);
            lastTradePrice = lastTrade.substring(start, end);
        }
    }


    /**
     * Returns the stock's last trade time.
     */
    public String getLastTradeTime()
    {
        return lastTradeTime;
    }


    /**
     * Returns the stock's last trade price.
     */
    public String getLastTradePrice()
    {
        return lastTradePrice;
    }


    /**
     * Returns the stock's .
     */
    public String getChange()
    {
        return change;
    }


    /**
     * Returns the stock's 52-week range.
     */
    public String getRange()
    {
        return range;
    }


    /**
     * Returns the stock's name; e.g., Google, Inc.
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the stock's symbol; e.g., GOOG.
     */
    public String getSymbol()
    {
        return symbol;
    }
}