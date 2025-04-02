package org.renpy.iap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

public class Store {

    static public Store store = new Store();

    static public Store getStore() {
        return store;
    }

    public void destroy() {
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        return false;
    }

    public boolean getFinished() {
        return true;
    }

    public String getStoreName() {
        return "none";
    }

    public ArrayList<String> skus = new ArrayList<String>();
    public HashMap<String, String> prices = new HashMap<String, String>();

    public void clearSKUs() {
        skus.clear();
    }

    public void addSKU(String sku) {
        skus.add(sku);
    }

    public void updatePrices() {
        return;
    }

    public String getPrice(String sku) {
        return prices.get(sku);
    }

    HashSet<String> purchased = new HashSet<String>();

    public boolean hasPurchased(String sku) {
       return purchased.contains(sku);
    }

    public void restorePurchases() {
    }

    public void beginPurchase(String sku) {
    }

}
