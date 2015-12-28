package com.defimak47.turnos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.defimak47.turnos.R;
import com.defimak47.turnos.helpers.ContactInfoHelper;
import com.defimak47.turnos.model.ContactInfo;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by jzuriaga on 27/12/14.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>  {

    private final View.OnLongClickListener itemLongClickListener;
    private Context context;
    private List<ContactInfo> contactList;

    public ContactAdapter(Context context, List<ContactInfo> contactList) {
        this.context = context;
        this.contactList = contactList;
        if (context instanceof View.OnLongClickListener) {
            itemLongClickListener = (View.OnLongClickListener) context;
        } else {
            itemLongClickListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        ContactInfo ci = contactList.get(i);
        contactViewHolder.vName.setText(ci.getName());
        contactViewHolder.vPosition.setText(ci.getPosition());
        contactViewHolder.vEmail.setText(ci.getEmail());
        contactViewHolder.vTitle.setText(ci.getAlias());
        contactViewHolder.vTitle.setTag(ci.getLogin());
        Ion.with(getContext())
           .load(String.format(ContactInfoHelper.HTTP_IMAGE_URI_TEMPLATE, ci.getLogin()))
           .setLogging("onBindViewHolder", Log.INFO)
           .withBitmap()
           .fitXY()
           .intoImageView(contactViewHolder.vImage);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.card_contactinfo, viewGroup, false);
        itemView.setOnLongClickListener(itemLongClickListener);
        return new ContactViewHolder(itemView);
    }

    public Context getContext() {
        return context;
    }

    /**
     * Holder for the Recycler view.
     */
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected ImageView vImage;
        protected TextView vName;
        protected TextView vPosition;
        protected TextView vEmail;
        protected TextView vTitle;

        public ContactViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.textName);
            vPosition = (TextView)  v.findViewById(R.id.textPosition);
            vEmail = (TextView)  v.findViewById(R.id.textEmail);
            vTitle = (TextView) v.findViewById(R.id.title);
            vImage = (ImageView) v.findViewById(R.id.imImage);
        }
    }


}
