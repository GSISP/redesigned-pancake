package com.acadgild.mypledge.ipledge.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.FBFriendsListActivity;
import com.acadgild.mypledge.ipledge.R;
import com.acadgild.mypledge.ipledge.constants.ServiceConstants;
import com.acadgild.mypledge.ipledge.model.AllPledgeModel;
import com.acadgild.mypledge.ipledge.service.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

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
        holder.take_status = (Button) rowView.findViewById(R.id.bt_take);
        holder.pb = (ProgressBar) rowView.findViewById(R.id.progressBar);

        final int completed=allPledgeModel.getPledge_units_completed()/allPledgeModel.getPledge_unit_quantity();
        Log.e("fb check",completed+"");


        if(completed==1){
            holder.take_status.setBackground(context.getResources().getDrawable(R.drawable.challenge));
        }
        else  if(allPledgeModel.getProgress_auto_update()==1){
            holder.take_status.setVisibility(View.INVISIBLE);
        }
        else{
            holder.take_status.setBackground(context.getResources().getDrawable(R.drawable.update));
        }

        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText("" + getPoints(allPledgeModel.getPledge_units_completed(),
                allPledgeModel.getPledge_unit_quantity(),
                allPledgeModel.getPoints()));
        holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
      //  holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
        holder.pb.setMax(allPledgeModel.getPledge_unit_quantity());

        holder.pb.setProgress(allPledgeModel.getPledge_units_completed());

        holder.take_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(completed==1){
                    Intent fb_friends_list=new Intent(getContext().getApplicationContext(), FBFriendsListActivity.class);
                    getContext().startActivity(fb_friends_list);
                }
                else{

                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    //setting custom layout to dialog
                    dialog.setContentView(R.layout.update_dialog);
                    //adding text dynamically

                    TextView title=(TextView)dialog.findViewById(R.id.tv_title);
                    title.setText(allPledgeModel.getName());

                    final EditText et_units = (EditText) dialog.findViewById(R.id.tv_units_completed);
                    Button bt_update=(Button)dialog.findViewById(R.id.bt_update);
                    Button bt_cancel=(Button)dialog.findViewById(R.id.bt_cancel);

                    bt_update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int units=Integer.parseInt(et_units.getText().toString().trim());
                            int max_units=allPledgeModel.getPledge_unit_quantity()-allPledgeModel.getPledge_units_completed();

                            if(!(units>0 && units<=max_units)) {
                                et_units.setError("Valid Range ["+0+","+max_units+"]");
                            }
                            else {
                                ServiceHandler sh = new ServiceHandler();

                                // Building Parameters
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                params1.add(new BasicNameValuePair("user_pledge_id",String.valueOf(allPledgeModel.getPledge_user_id())));
                                Log.e("data update :", allPledgeModel.getId()+"");
                                params1.add(new BasicNameValuePair("units_completed", String.valueOf(units)));

                                // posting JSON string to server URL
                                String data = sh.makeServiceCall(ServiceConstants.UPDATE_PLEDGE_URL, 2, params1);

                                Log.e("data update :",data);

                                allPledgeModel.setPledge_units_completed(+allPledgeModel.getPledge_units_completed() + units);

                                holder.tv_points.setText("" + getPoints(allPledgeModel.getPledge_units_completed(),
                                        allPledgeModel.getPledge_unit_quantity(),
                                        allPledgeModel.getPoints()));
                                holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
                                // holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
                                holder.pb.setMax(allPledgeModel.getPledge_unit_quantity());

                                holder.pb.setProgress(allPledgeModel.getPledge_units_completed());

                                dialog.cancel();

                            }
                        }
                    });
                    //adding button click event
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        return rowView;
    }


    class Holder {
        TextView tv_title;
        TextView tv_description;
        TextView tv_points;
        TextView tv_quantity;
        Button take_status;
        ProgressBar pb;

    }

    float getPoints(int completed,int total,int points){
        return Math.round(((float) completed / total)*points);
    }
}