package com.acadgild.vpledge.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acadgild.vpledge.FBFriendsListActivity;
import com.acadgild.vpledge.R;
import com.acadgild.vpledge.constants.ServiceConstants;
import com.acadgild.vpledge.lazyload.ImageLoader;
import com.acadgild.vpledge.model.AllPledgeModel;
import com.acadgild.vpledge.service.PrefUtils;
import com.acadgild.vpledge.service.ServiceHandler;
import com.acadgild.vpledge.util.Connectivity;

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
    public ImageLoader imageLoader;

    Connectivity isNetwork;

    public MyPledgesAdapter(Context context, int resource, ArrayList<AllPledgeModel> objects) {
        super(context, resource, objects);

        this.objects = objects;
        this.context = context;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context.getApplicationContext());

        isNetwork=new Connectivity(context.getApplicationContext());

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
        holder.take_challenge = (Button) rowView.findViewById(R.id.bt_challenge);
        holder.pb = (ProgressBar) rowView.findViewById(R.id.progressBar);
        holder.img_pledge=(ImageView) rowView.findViewById(R.id.img_pledge);


        holder.pb.getProgressDrawable().setColorFilter(
                getContext().getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);


        final int completed=allPledgeModel.getPledge_units_completed()/allPledgeModel.getPledge_unit_quantity();
        Log.e("fb check", completed + "");



        if(completed==1){
          //  holder.take_status.setVisibility(View.INVISIBLE);
            holder.pb.getProgressDrawable().setColorFilter(
                    getContext().getResources().getColor(android.R.color.holo_green_dark), android.graphics.PorterDuff.Mode.SRC_IN);

        }
    /*    else  if(allPledgeModel.getProgress_auto_update()==1){
            holder.take_status.setVisibility(View.INVISIBLE);
            holder.take_challenge.setVisibility(View.INVISIBLE);
        }
        else{
            holder.take_challenge.setVisibility(View.INVISIBLE);
        }
*/

        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText("" + getPoints(allPledgeModel.getPledge_units_completed(),
                allPledgeModel.getPledge_unit_quantity(),
                allPledgeModel.getPoints()));
        holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed() + "/" + allPledgeModel.getPledge_unit_quantity());
      //  holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
        holder.pb.setMax(allPledgeModel.getPledge_unit_quantity());

        holder.pb.setProgress(allPledgeModel.getPledge_units_completed());

        if(allPledgeModel.getPledge_image_url().equals("")) {
            imageLoader.DisplayImage("https://s3.amazonaws.com/acadgildsite/wordpress_images/app/logo.png",holder.img_pledge);
        }

        else{
            imageLoader.DisplayImage(allPledgeModel.getPledge_image_url(), holder.img_pledge);
        }


        holder.take_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String unlock=PrefUtils.getFromPrefs(getContext().getApplicationContext(), "unlock", "0");

                if(completed==1 && unlock.equals("1")) {
                    if (isNetwork.isConnectingToInternet()) {
                        Intent fb_friends_list = new Intent(getContext().getApplicationContext(), FBFriendsListActivity.class);
                        fb_friends_list.putExtra("title", allPledgeModel.getName());
                        fb_friends_list.putExtra("description", allPledgeModel.getDescription());
                        getContext().startActivity(fb_friends_list);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Internet Connection");
                        alertDialog.setMessage("Please check your internet connection");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setTitle("To Unlock Challenge Friends");
                    if(allPledgeModel.getId()==1){
                        alertDialog.setMessage("Complete \"" + allPledgeModel.getName());
                    }
                    else {
                        alertDialog.setMessage("Complete \"" + allPledgeModel.getName() + "\" & \"Use Public Transport for 30 days\"");
                    }
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }
        });

        holder.take_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allPledgeModel.getProgress_auto_update()==0) {
                    if(completed!=1){
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        //setting custom layout to dialog
                        dialog.setContentView(R.layout.update_dialog);
                        //adding text dynamically

                        final EditText et_units = (EditText) dialog.findViewById(R.id.tv_units_completed);
                        Button bt_update = (Button) dialog.findViewById(R.id.bt_update);
                        Button bt_cancel = (Button) dialog.findViewById(R.id.bt_cancel);

                        bt_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String value = et_units.getText().toString().trim();
                                if (!value.equals("") && value.matches("^\\d+$")) {

                                    int units = Integer.parseInt(value);

                                    int max_units = allPledgeModel.getPledge_unit_quantity() - allPledgeModel.getPledge_units_completed();

                                    if (!(units > 0 && units <= max_units)) {
                                        if(max_units!=1) {
                                            et_units.setError("Valid Range [" + 1 + "-" + max_units + "]");
                                        }
                                        else{
                                            et_units.setError("Valid Range [ 1 ]");
                                        }
                                    } else {

                                        if (isNetwork.isConnectingToInternet()) {
                                            ServiceHandler sh = new ServiceHandler();

                                            try {
                                                // Building Parameters
                                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                                                params1.add(new BasicNameValuePair("user_pledge_id", String.valueOf(allPledgeModel.getPledge_user_id())));
                                             //   Log.e("data update :", allPledgeModel.getId() + "");
                                                params1.add(new BasicNameValuePair("units_completed", String.valueOf(units)));

                                                // posting JSON string to server URL
                                                String data = sh.makeServiceCall(ServiceConstants.UPDATE_PLEDGE_URL, 2, params1);

                                                allPledgeModel.setPledge_units_completed(+allPledgeModel.getPledge_units_completed() + units);

                                                holder.tv_points.setText("" + getPoints(allPledgeModel.getPledge_units_completed(),
                                                        allPledgeModel.getPledge_unit_quantity(),
                                                        allPledgeModel.getPoints()));
                                                holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed() + "/" + allPledgeModel.getPledge_unit_quantity());
                                                // holder.tv_quantity.setText("" + allPledgeModel.getPledge_units_completed()+"/"+allPledgeModel.getPledge_unit_quantity());
                                                holder.pb.setMax(allPledgeModel.getPledge_unit_quantity());

                                                holder.pb.setProgress(allPledgeModel.getPledge_units_completed());

                                                dialog.cancel();
                                            } catch (Exception e) {
                                                Toast.makeText(context.getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                            alertDialog.setTitle("Internet Connection");
                                            alertDialog.setMessage("Please check your internet connection");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }

                                    }
                                } else {
                                    et_units.setError("Please enter quantity");
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
                    else{
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        alertDialog.setMessage("You have already completed this pledge! Now its time to challenge your friends.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    if(completed==1){
                        alertDialog.setMessage("You have already completed this pledge!");
                    }
                    else {
                        alertDialog.setMessage("We've got it covered! We'll automatically keep updating your progress every day!");
                    }
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

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
        Button take_challenge;
        ProgressBar pb;
        ImageView img_pledge;
    }

    float getPoints(int completed,int total,int points){
        return Math.round(((float) completed / total)*points);
    }
}