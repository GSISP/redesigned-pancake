package com.acadgild.vpledge.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acadgild.vpledge.MyPledgesActivity;
import com.acadgild.vpledge.R;
import com.acadgild.vpledge.constants.AllPledgesConstant;
import com.acadgild.vpledge.constants.MyProfileConstant;
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
public class AllPledgesAdapter extends ArrayAdapter<AllPledgeModel> {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<AllPledgeModel> objects;
    public ImageLoader imageLoader;

    Connectivity isInternet;

    public AllPledgesAdapter(Context context, int resource, ArrayList<AllPledgeModel> objects) {
        super(context, resource, objects);

        this.objects=objects;
        this.context=context;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        isInternet=new Connectivity(context.getApplicationContext());

        imageLoader = new ImageLoader(context.getApplicationContext());


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.all_pledges, null);

        final AllPledgeModel allPledgeModel=objects.get(position);

        holder.tv_title=(TextView) rowView.findViewById(R.id.tv_title);
        holder.tv_description=(TextView) rowView.findViewById(R.id.tv_description);
        holder.tv_points=(TextView) rowView.findViewById(R.id.tv_points);
        holder.tv_quantity=(TextView) rowView.findViewById(R.id.tv_quantity);
        holder.tv_take_status=(TextView) rowView.findViewById(R.id.tv_take_status);
        holder.take_status=(Button) rowView.findViewById(R.id.bt_take);

        holder.img_pledge=(ImageView) rowView.findViewById(R.id.img_pledge);

        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText(""+allPledgeModel.getPoints());
        holder.tv_quantity.setText("" + allPledgeModel.getPledge_unit_quantity());

       // blob:https%3A//drive.google.com/f7197bc3-f8ef-4c84-979b-1c02aa215b3e

        if(allPledgeModel.getPledge_image_url().equals("")) {
            imageLoader.DisplayImage("https://s3.amazonaws.com/acadgildsite/wordpress_images/app/logo.png",holder.img_pledge);
        }
        else{
            imageLoader.DisplayImage(allPledgeModel.getPledge_image_url(), holder.img_pledge);

        }

        if(allPledgeModel.isAlready_taken()) {
            //  holder.take_status.setBackground(getContext().getResources().getDrawable(R.drawable.button_style_no_border));
            holder.take_status.setVisibility(View.GONE);
        }
        else{
            holder.tv_take_status.setVisibility(View.GONE);
        }
/*
            holder.take_status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            holder.take_status.setGravity(Gravity.RIGHT);

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

            llp.setMargins(
                    (int)getContext().getResources().getDimension(R.dimen.textsize30),0,0,0);

            holder.take_status.setLayoutParams(llp);

            holder.take_status.setTextSize(getContext().getResources().getDimension(R.dimen.textsize));
            holder.take_status.setText("PLEDGE TAKEN");
*/


        holder.take_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


        if (!allPledgeModel.isAlready_taken()) {

            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //setting custom layout to dialog
            dialog.setContentView(R.layout.take_dialog);
            //adding text dynamically

            Button bt_update=(Button)dialog.findViewById(R.id.bt_update);
            Button bt_cancel=(Button)dialog.findViewById(R.id.bt_cancel);


            bt_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isInternet.isConnectingToInternet()) {


                        try {
                            // if this button is clicked, close
                            // current activity
                            ServiceHandler sh = new ServiceHandler();

                            String user_id = PrefUtils.getFromPrefs(context.getApplicationContext(), MyProfileConstant.KEY_ID, "");

                            // Building Parameters
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair(MyProfileConstant.KEY_ID, user_id));
                            params.add(new BasicNameValuePair(AllPledgesConstant.KEY_PLEDGE_ID, "" + allPledgeModel.getId()));

                            // posting JSON string to server URL
                            String data = sh.makeServiceCall(ServiceConstants.ADD_PLEDGE_TO_USER_URL, 2, params);
                            //holder.take_status.setText("Taken");

                            allPledgeModel.setAlready_taken(true);

                            dialog.cancel();

                            Intent pledges = new Intent(getContext().getApplicationContext(),MyPledgesActivity.class);
                            pledges.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           // pledges.putExtra("pledge",allPledgeModel.getName());
                            getContext().startActivity(pledges);

                        }
                        catch(Exception e){
                            Toast.makeText(context.getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Internet Connection");
                        alertDialog.setMessage("Please check your internet connection");
                        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }


                }
            });

          bt_cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  dialog.cancel();

              }
          });

            dialog.show();
      }
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
    TextView tv_take_status;
    TextView tv_quantity;
    Button take_status;
    ImageView img_pledge;

}