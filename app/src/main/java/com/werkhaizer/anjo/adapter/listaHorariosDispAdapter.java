package com.werkhaizer.anjo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.werkhaizer.anjo.R;

public class listaHorariosDispAdapter extends BaseAdapter {

    Context context;
    private final String [] values;
    View view;
    LayoutInflater layoutInflater;

    public listaHorariosDispAdapter(Context context, String[] values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            view = new View(context);
            view = layoutInflater.inflate(R.layout.row_horario, null);
            Button button = view.findViewById(R.id.btnHorario);
            button.setText(values[position]);
        }
        return view;
    }
}
