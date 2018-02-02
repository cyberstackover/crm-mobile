package com.sinergiinformatika.sisicrm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.data.models.ItemPrice;

import java.util.List;


/**
 * Created by wendi on 06-Jan-15.
 */
public class ItemPriceAdapter extends ArrayAdapter<ItemPrice> {

    private List<ItemPrice> prices;
    private Context context;


    public ItemPriceAdapter(Context context, int resource, List<ItemPrice> prices) {
        super(context, resource, prices);
        this.prices = prices;
        this.context = context;
    }


    @Override
    public int getCount() {
        if(prices == null){
            return 0;
        }
        return prices.size();
    }

    @Override
    public ItemPrice getItem(int position) {
        return prices.get(position);
    }

    @Override
    public int getPosition(ItemPrice item) {
        return prices.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_survey_price, parent, false);
        }

        return convertView;
    }

}
