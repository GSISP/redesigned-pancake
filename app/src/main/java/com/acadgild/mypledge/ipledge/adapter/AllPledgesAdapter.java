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
import java.util.List;

/**
 * Created by pushp_000 on 5/22/2016.
 */
public class AllPledgesAdapter extends ArrayAdapter<AllPledgeModel> {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<AllPledgeModel> objects;

    public AllPledgesAdapter(Context context, int resource, ArrayList<AllPledgeModel> objects) {
        super(context, resource, objects);

        this.objects=objects;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.all_pledges, null);

        AllPledgeModel allPledgeModel=objects.get(position);

        holder.tv_title=(TextView) rowView.findViewById(R.id.tv_title);
        holder.tv_description=(TextView) rowView.findViewById(R.id.tv_description);
        holder.tv_points=(TextView) rowView.findViewById(R.id.tv_points);
        holder.tv_quantity=(TextView) rowView.findViewById(R.id.tv_quantity);


        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText(""+allPledgeModel.getPoints());
        holder.tv_quantity.setText(""+allPledgeModel.getPledge_unit_quantity());

        // holder.tv_description.setText(result[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        return rowView;
    }

}
class Holder
{
    TextView tv_title;
    TextView tv_description;
    TextView tv_points;
    TextView tv_quantity;
    Button take_status;


}