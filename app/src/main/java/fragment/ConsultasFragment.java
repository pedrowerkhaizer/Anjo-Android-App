package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.werkhaizer.anjo.ConsultaFechadaContent;
import com.werkhaizer.anjo.R;
import com.werkhaizer.anjo.ConsultaAbertaContent;
import com.werkhaizer.anjo.ConsultaProximaContent;
import com.werkhaizer.anjo.filtrosListaConsultaActivity;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static utils.Vibrar.vibrar;

public class ConsultasFragment extends Fragment {


    private FragmentPagerItemAdapter adapter;

    private ImageButton btnFiltros;

    public ConsultasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultas, container, false);


        btnFiltros = view.findViewById(R.id.btnFiltrosConsultas);

//        //Configurar o adapter

        adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add("próximas", ConsultaProximaContent.class)
                .add("abertas", ConsultaAbertaContent.class)
                .add("histórico", ConsultaFechadaContent.class)
                .create());

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = view.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        viewPager.setAdapter(adapter);



        btnFiltros.setOnClickListener(v -> {
            vibrar(getContext());
            Intent i = new Intent(getContext(), filtrosListaConsultaActivity.class);
            startActivity(i);
        });



        return view;
    }
    public void onPageSelected(int position) {

        //.instantiateItem() from until .destoryItem() is called it will be able to get the Fragment of page.
        Fragment page = adapter.getPage(position);

    }
}
