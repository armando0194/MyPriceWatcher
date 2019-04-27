package cs4330.cs.utep.edu;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkPriceFinder extends AsyncTask<NetworkPriceFinder.Params, Void, NetworkPriceFinder.Params>
    implements PriceFinder{

    private enum Store{
        HOME_DEPOT ("www.homedepot.com", "span.price__dollars", "span.price__cents"),
        AMAZON ("www.amazon.com", "priceblock_ourprice", "priceblock_dealprice"),
        EBAY ("www.ebay.com", "prcIsum", "");

        private final String host;
        private final String dll_tag;
        private final String cent_tag;

        Store(String host, String dll_tag, String cent_tag) {
            this.host = host;
            this.dll_tag = dll_tag;
            this.cent_tag = cent_tag;
        }

        static Store fromHost(String host){
            if ( host.equals(Store.HOME_DEPOT.host) ) {
                return Store.HOME_DEPOT;
            }
            else if ( host.equals(Store.AMAZON.host) ) {
                return Store.AMAZON;
            }
            else if ( host.equals(Store.EBAY.host) ) {
                return Store.EBAY;
            }
            else {
                return null;
            }
        }

        String getDll_tag(){
            return dll_tag;
        }

        String getCent_tag(){
            return cent_tag;
        }
    }

    class Params{
        private final Store store;
        private final int pos;
        private final String url;
        private double price;
        private boolean isNew;

        public Params(int pos, String url, boolean isNew, Store store){
            this.pos = pos;
            this.url = url;
            this.store = store;
            this.isNew = isNew;
        }

        public void setPrice(double price){ this.price = price; }

        public double getPrice(){ return price; }

        public int getPos(){ return pos; }

        public String getUrl(){ return url; }

        public Store getStore(){ return store; }

        public boolean getIsNew(){ return isNew; }
    }

    private static final int TIMEOUT = 1000 * 60 * 1;
    private static ProgressDialog dialog;
    private WatchlistActivity ctx;
    private static int numTasks = 0;


    public NetworkPriceFinder(WatchlistActivity ctx){
        if (dialog == null)
            this.dialog = new ProgressDialog(ctx);
        this.ctx = ctx;

    }

    @Override
    public double getPrice(String url) {
        return 0;
    }

    public void getPrice(String url, boolean isNew, int pos) {

        try {
            URL netUrl = new URL(url);
            String host = netUrl.getHost();
            Store store = Store.fromHost(host);
            Params params = new Params(pos, url, isNew, store);

            if (store == null) {
                ctx.showToast("Store not supported");
                return;
            }

            execute(params);

        }catch (MalformedURLException ex){
            ctx.showToast("Malformed URL");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (!dialog.isShowing())
            dialog.setMessage("Retriving Prices...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        numTasks++;
    }

    @Override
    protected void onPostExecute(Params params) {
        super.onPostExecute(params);

        if (params != null){
            if(!params.getIsNew())
                ctx.getAdapter().updateItem(params.price, params.pos);
            else
                ctx.getAdapter().updateItem(params.price, params.price,  params.pos);
        }
        else{
            Toast.makeText(ctx,"Timeout", Toast.LENGTH_LONG).show();
        }

        if (dialog.isShowing() && --numTasks == 0) {
            dialog.hide();
        }
    }

    @Override
    protected Params doInBackground(Params... params) {

        try {
            Document doc = Jsoup.connect(params[0].getUrl()).timeout(TIMEOUT).cookie("zip", "79968").get();
            double price = 0;
            Store store = params[0].getStore();
            switch(store){
                case HOME_DEPOT:
                    price = scrapHomeDepot(doc, store.getDll_tag(),
                                           store.getCent_tag());
                    break;
                case AMAZON:
                    price = scrapAmazon(doc, store.dll_tag, store.cent_tag);
                    break;
                case EBAY:
                    price = scrapEbay(doc, store.dll_tag);
                    break;
            }

            params[0].setPrice(price);

        } catch (IOException e) {
            Log.e("Soup", e.getMessage());

            return null;
        }

        return params[0];
    }

    protected double scrapHomeDepot(Document doc, String dollar_tag, String cent_tag){
        Element dollars = doc.select(dollar_tag).first();
        Element cents = doc.select(cent_tag).first();

        double price = Double.parseDouble(dollars.text()) +
                Double.parseDouble(cents.text());

        return price;
    }
    protected double scrapAmazon(Document doc, String price_tag, String deal_tag){
        Element priceElement = doc.getElementById(price_tag);
        if (priceElement == null)
            priceElement = doc.getElementById(deal_tag);
        String price =  Util.stripNumber(priceElement.text());
        return (priceElement != null) ? Double.parseDouble(price): 0;
    }

    protected double scrapEbay(Document doc, String tag){
        Element priceElement = doc.getElementById(tag);
        String price = Util.stripNumber(priceElement.text());
        return (priceElement != null) ? Double.parseDouble(price): 0;
    }
}
