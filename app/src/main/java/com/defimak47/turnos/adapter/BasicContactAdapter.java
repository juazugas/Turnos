package com.defimak47.turnos.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.defimak47.turnos.R;
import com.defimak47.turnos.model.ContactInfo;

import java.util.List;

/**
 * Created by jzuriaga on 27/12/14.
 */
public class BasicContactAdapter extends RecyclerView.Adapter<BasicContactAdapter.BasicContactViewHolder>  {

    private List<ContactInfo> contactList;

    public BasicContactAdapter(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(BasicContactViewHolder contactViewHolder, int i) {
        ContactInfo ci = contactList.get(i);
        contactViewHolder.vName.setText(ci.getName());
//        contactViewHolder.vSurname.setText(ci.getSurname());
        contactViewHolder.vEmail.setText(ci.getEmail());
//        contactViewHolder.vTitle.setText(ci.getName() + " " + ci.getSurname());
    }

    @Override
    public BasicContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.view_card, viewGroup, false);
        return new BasicContactViewHolder(itemView);
    }

    /**
     * Holder for the Recycler view.
     */
    public static class BasicContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public BasicContactViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);
        }
    }

}
