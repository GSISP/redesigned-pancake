package com.acadgild.mypledge.ipledge.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.R;
import com.acadgild.mypledge.ipledge.constants.AllPledgesConstant;
import com.acadgild.mypledge.ipledge.constants.MyProfileConstant;
import com.acadgild.mypledge.ipledge.constants.ServiceConstants;
import com.acadgild.mypledge.ipledge.model.AllPledgeModel;
import com.acadgild.mypledge.ipledge.service.PrefUtils;
import com.acadgild.mypledge.ipledge.service.ServiceHandler;
import com.acadgild.mypledge.ipledge.util.Connectivity;

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

    public AllPledgesAdapter(Context context, int resource, ArrayList<AllPledgeModel> objects) {
        super(context, resource, objects);

        this.objects=objects;
        this.context=context;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        holder.take_status=(Button) rowView.findViewById(R.id.bt_take);


        holder.tv_title.setText(allPledgeModel.getName());
        holder.tv_description.setText(allPledgeModel.getDescription());

        holder.tv_points.setText(""+allPledgeModel.getPoints());
        holder.tv_quantity.setText("" + allPledgeModel.getPledge_unit_quantity());


        if(allPledgeModel.isAlready_taken()) {
            holder.take_status.setBackground(getContext().getResources().getDrawable(R.drawable.taken));
        }

        holder.take_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connectivity.isConnected(context.getApplicationContext())) {
        if (!allPledgeModel.isAlready_taken()) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Confirm Pledge !");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to take this pledge ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                        holder.take_status.setText("Taken");

                        Log.e("data take :", data);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
            else{
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                alertDialog.show();
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
    TextView tv_quantity;
    Button take_status;

}