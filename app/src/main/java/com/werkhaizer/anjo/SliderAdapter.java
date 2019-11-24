package com.werkhaizer.anjo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;


    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.ic_medicine,
            R.drawable.ic_acessibilidade,
            R.drawable.ic_clock,
            R.drawable.ic_touch,
            R.drawable.ic_attach_money
    };

    public String[] slide_heading = {
            "TRANSPARÊNCIA",
            "FACILIDADE",
            "AGILIDADE",
            "COMODIDADE",
            "RECOMPENSAS"
    };

    public String[] slide_body = {
            "veja comentários e avaliações de \n" + "pacientes do médico escolhido",
            "faça uma busca de anjos por horários \n" + "disponíveis, distância, avaliações entre \n" + "outros diversos filtros disponíveis",
            "ache o anjo perfeito para você em \n questão de segundos",
            "teve algum imprevisto? desmarque \n seu horário em instantes e libere \n a vaga a outros pacientes",
            "ganhe recompensas ao simplesmente indicar o aplicativo a anjos conhecidos!"
    };

    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImgView = (ImageView) view.findViewById(R.id.img_slide);
        TextView slideHeading = (TextView) view.findViewById(R.id.textViewHeadSlide);
        TextView slideBody = (TextView) view.findViewById(R.id.textViewBodySlide);

        slideImgView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_heading[position]);
        slideBody.setText(slide_body[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
