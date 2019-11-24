package com.werkhaizer.anjo;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import fragment.ConsultasFragment;
import fragment.FavoritosFragment;
import fragment.ListaMedicosFragment;
import fragment.PerfilFragment;
import utils.TintableImageView;

import static utils.FragmentAtual.getConsulta;
import static utils.FragmentAtual.getFavorito;
import static utils.FragmentAtual.getMain;
import static utils.FragmentAtual.getPerfil;
import static utils.FragmentAtual.setConsulta;
import static utils.FragmentAtual.setFavorito;
import static utils.FragmentAtual.setMain;
import static utils.FragmentAtual.setPerfil;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        verificaConexao();


        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(MainActivity.this, intro_anjo.class));
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();

        try {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                mAuth.updateCurrentUser(user);
            }
        } catch (Exception e){
            e.printStackTrace();
        }



        configurarAbas();

        try {
            if (getMain()) {
                chamarHome();
            } else if (getConsulta()) {
                chamarConsultas();
            } else if (getFavorito()) {
                chamarFavoritos();
            } else if (getPerfil()) {
                chamarPerfil();
            }
        } catch (Exception e) {
            e.printStackTrace();
            chamarHome();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void configurarAbas() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Explorar", ListaMedicosFragment.class)
                .add("Consultas", ConsultasFragment.class)
                .add("Favoritos", FavoritosFragment.class)
                .add("Perfil", PerfilFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);

        final LayoutInflater inflater = LayoutInflater.from(this);
        final Resources res = getResources();

        viewPagerTab.setCustomTabView((container, position, adapter1) -> {
            View itemView = inflater.inflate(R.layout.custom_bottom_navigation_icon, container, false);
            TintableImageView icon = itemView.findViewById(R.id.custom_tab_icon);
            switch (position) {
                case 0:
                    icon.setImageDrawable(res.getDrawable(R.drawable.ic_compassai));
                    break;
                case 1:
                    icon.setImageDrawable(res.getDrawable(R.drawable.ic_calendar));
                    break;
                case 2:
                    icon.setImageDrawable(res.getDrawable(R.drawable.ic_favoritos));
                    break;
                case 3:
                    icon.setImageDrawable(res.getDrawable(R.drawable.ic_profile));
                    break;
                default:
                    throw new IllegalStateException("Invalid position: " + position);
            }

            return itemView;
        });

        viewPagerTab.setViewPager(viewPager);


        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        chamarHome();
                        break;
                    case 1:
                        chamarConsultas();
                        break;
                    case 2:
                        chamarFavoritos();
                        break;
                    case 3:
                        chamarPerfil();
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void chamarHome() {
        setMain(true);
        setConsulta(false);
        setFavorito(false);
        setPerfil(false);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setCurrentItem(0);

    }

    public void chamarConsultas() {
        setMain(false);
        setConsulta(true);
        setFavorito(false);
        setPerfil(false);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setCurrentItem(1);

    }

    public void chamarFavoritos() {
        setMain(false);
        setConsulta(false);
        setFavorito(true);
        setPerfil(false);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setCurrentItem(2);
    }

    public void chamarPerfil() {
        setMain(false);
        setConsulta(false);
        setFavorito(false);
        setPerfil(true);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setCurrentItem(3);
    }


    @Override
    public void onBackPressed() {
        try {
            if (getMain()) {
                super.onBackPressed();
            } else {
                chamarHome();
            }
        }
        catch (Exception e) {e.printStackTrace();}

    }
}
