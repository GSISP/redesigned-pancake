package com.acadgild.mypledge.ipledge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.R;
import com.acadgild.mypledge.ipledge.model.AllPledgeModel;

import java.util.ArrayList;

/**
 * Created by pushp_000 on 5/22/2016.
 */
public class MyPledgesAdapter extends ArrayAdapter<AllPledgeModel> {

    Context context;
    private static LayoutInflater inflater = null;
    ArrayList<AllPledgeModel> objects;

    public MyPledgesAdapter(Context context, int resource, ArrayList<AllPledgeModel> objects) {
        super(context, resource, objects);

        this.objects = objects;
        this.context = context;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.my_pledges, null);

        final AllPledgeModel allPledgeModel = objects.get(position);

        holder.tv_title = (TextView) rowView.findViewById(R.id.tv_title);
        holder.tv_description = (TextView) rowView.findViewById(R.id.tv_description);
        holder.tv_points = (TextView) rowView.findViewById(R.id.tv_points);
        holder.tv_quantity = (TextView) rowView.findViewById(R.id.tv_quantity);
        holder.take_status = (Button) rowView.findViewById(R.id.bt_Take);


        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText("" + getPoints(allPledgeModel.getPledge_units_completed(),
                allPledgeModel.getPledge_unit_quantity(),allPledgeModel.getPoints()));
        holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());

        return rowView;
    }


    class Holder {
        TextView tv_title;
        TextView tv_description;
        TextView tv_points;
        TextView tv_quantity;
        Button take_status;

    }

    float getPoints(int completed,int total,int points){
        return (completed/total)*points;
    }
}