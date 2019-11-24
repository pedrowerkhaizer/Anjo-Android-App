package com.werkhaizer.anjo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import static utils.Vibrar.vibrar;
import static utils.selectedMedicData.getCrm;
import static utils.selectedMedicData.getEspecialidade;
import static utils.selectedMedicData.setDia;
import static utils.selectedMedicData.setHorario;
import static utils.selectedMedicData.setSimpleHorario;

public class horariosDispActivity extends AppCompatActivity {

    private SectionedRecyclerViewAdapter sectionAdapter;
    private static final Random RANDOM = new Random();

    private Button close, datas;
    private RecyclerView recyclerView;

    private DatabaseReference mRef;
    private DatabaseReference mHorariosRef;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios_disp);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        close = findViewById(R.id.btnCloseHorariosMed);
        datas = findViewById(R.id.btnDatas);
        recyclerView = findViewById(R.id.recyclerview);

        GridLayoutManager glm = new GridLayoutManager(horariosDispActivity.this, 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionAdapter);

//        Intent i = getIntent();
//        final String crm_selecionado = i.getStringExtra("crm");
//        final String esp_selecionado = i.getStringExtra("espMed");
        mRef = mFirebaseDatabase.getReference("Especialidades").child(getEspecialidade()).child("medicos").child(getCrm()).child("horarios");

//        Date date = new Date();
        sectionAdapter.removeAllSections();

        Date data = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);
        gerarHorarios(420, 1140, 750, 810,
                60, 15, dia, mes, ano);
//        gerarCalendar();

        close.setOnClickListener(v -> {
            vibrar(horariosDispActivity.this);
            horariosDispActivity.this.finish();
        });

        datas.setOnClickListener(v -> {
            vibrar(horariosDispActivity.this);
            gerarCalendar();
        });
    }

    public void gerarCalendar() {
            SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                (view, yearStart, monthStart, dayStart, yearEnd, monthEnd, dayEnd) -> {
                    //TODO grab the date range and filter by it
                    int range;
                    try {
                        sectionAdapter.removeAllSections();
                        range = view.getHighlightedDays().length;


                        //TODO pegar os dados do atendimento do perfil do medico
                        //TODO bloquear os dias passados
                        gerarHorarios(420, 1140, 750, 810,
                                60, range, dayStart, monthStart, yearStart);

                        recyclerView = findViewById(R.id.recyclerview);
                        GridLayoutManager glm = new GridLayoutManager(horariosDispActivity.this, 2);
                        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                switch (sectionAdapter.getSectionItemViewType(position)) {
                                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                                        return 2;
                                    default:
                                        return 1;
                                }
                            }
                        });
                        recyclerView.setLayoutManager(glm);
                        recyclerView.setAdapter(sectionAdapter);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(horariosDispActivity.this, "Clique em pelo menos 1 dia para pesquisar horarios disponíveis", Toast.LENGTH_SHORT).show();
                    }
                });

        Date data = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        smoothDateRangePickerFragment.setMinDate(calendar);

        smoothDateRangePickerFragment.show(

                getFragmentManager(), "smoothDateRangePicker");
    }


    public void gerarHorarios(int inicioAtendimento, int finalAtendimento,
                              int inicioAlmoco, int finalAlmoco,
                              int duracaoMediaAtendimento, int intervaloDatas,
                              int diaInicio, int mesInicio, int anoInicio) {

        int funcionamentoAntesAlmoco = inicioAlmoco - inicioAtendimento;
        int funcionamentoDepoisAlmoco = finalAtendimento - finalAlmoco;
        int qtdHorariosAntesAlmoco = (int) (Math.floor(funcionamentoAntesAlmoco / duracaoMediaAtendimento) + 1);
        int qtdHorariosDepoisAlmoco = (int) (Math.floor(funcionamentoDepoisAlmoco / duracaoMediaAtendimento) + 1);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, anoInicio);
        calendar.set(Calendar.MONTH, mesInicio);
        calendar.set(Calendar.DAY_OF_MONTH, diaInicio);


        for (int i = 0; i < intervaloDatas; i++) {

            String randomNumber = getRandomStringNumber();
            String sectionTag = String.format("section%sTag", randomNumber);

            String nomeDiaSemana = "";
            int diaSemana = calendar.get(calendar.DAY_OF_WEEK);
            switch (diaSemana) {
                case Calendar.SUNDAY:
                    nomeDiaSemana = "Domingo";
                    break;
                case Calendar.MONDAY:
                    nomeDiaSemana = "Segunda";
                    break;
                case Calendar.TUESDAY:
                    nomeDiaSemana = "Terça";
                    break;
                case Calendar.WEDNESDAY:
                    nomeDiaSemana = "Quarta";
                    break;
                case Calendar.THURSDAY:
                    nomeDiaSemana = "Quinta";
                    break;
                case Calendar.FRIDAY:
                    nomeDiaSemana = "Sexta";
                    break;
                case Calendar.SATURDAY:
                    nomeDiaSemana = "Sábado";
                    break;
            }
            String nomeMes = "";
            int diaMes = calendar.get(Calendar.MONTH);
            switch (diaMes) {
                case 0:
                    nomeMes = "janeiro";
                    break;
                case 1:
                    nomeMes = "fevereiro";
                    break;
                case 2:
                    nomeMes = "março";
                    break;
                case 3:
                    nomeMes = "abril";
                    break;
                case 4:
                    nomeMes = "maio";
                    break;
                case 5:
                    nomeMes = "junho";
                    break;
                case 6:
                    nomeMes = "julho";
                    break;
                case 7:
                    nomeMes = "agosto";
                    break;
                case 8:
                    nomeMes = "setembro";
                    break;
                case 9:
                    nomeMes = "outubro";
                    break;
                case 10:
                    nomeMes = "novembro";
                    break;
                case 11:
                    nomeMes = "dezembro";
                    break;
            }


            NameSection section = new NameSection(sectionTag, nomeDiaSemana + ", " + calendar.get(Calendar.DAY_OF_MONTH) + " de " + nomeMes);

            sectionAdapter.addSection(sectionTag, section);
            int qtdPercorrido = qtdHorariosAntesAlmoco - 1;

            calendar.add(Calendar.DAY_OF_MONTH, 1);


            for (int o = 0; o < qtdHorariosAntesAlmoco; o++) {
                int horarioEmMinutos = (inicioAtendimento) + (duracaoMediaAtendimento * o);
                int minutos = horarioEmMinutos % 60;
                int horas = (horarioEmMinutos - minutos) / 60;

                String hrsStr = horas < 10 ? "0" + horas : "" + horas;
                String minStr = minutos < 10 ? "0" + minutos : "" + minutos;

                String horarioFormatado = (hrsStr + ":" + minStr);

                Calendar calendar1 = new GregorianCalendar();
                calendar1.setTime(calendar.getTime());
                calendar1.add(Calendar.DAY_OF_MONTH, -1);
                Date horarioGeralDate = calendar1.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String data = dateFormat.format(horarioGeralDate);
                String horarioGeral = data + " " + horarioFormatado;


                section.list.add(o, new Person(horarioFormatado, getRandomStringNumber(), horarioGeral));

            }
            for (int x = 0; x < qtdHorariosDepoisAlmoco; x++) {
                int horarioEmMinutos = (finalAlmoco) + (duracaoMediaAtendimento * x);
                int minutos = horarioEmMinutos % 60;
                int horas = (horarioEmMinutos - minutos) / 60;

                String hrsStr = horas < 10 ? "0" + horas : "" + horas;
                String minStr = minutos < 10 ? "0" + minutos : "" + minutos;

                String horarioFormatado = (hrsStr + ":" + minStr);

                qtdPercorrido++;


                Calendar calendar1 = new GregorianCalendar();
                calendar1.setTime(calendar.getTime());
                calendar1.add(Calendar.DAY_OF_MONTH, -1);
                Date horarioGeralDate = calendar1.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String data = dateFormat.format(horarioGeralDate.getTime());
                String horarioGeral = data + " " + horarioFormatado;


                section.list.add(qtdPercorrido, new Person(horarioFormatado, getRandomStringNumber(), horarioGeral));

            }

            try {
                //Removendo os horarios marcados
                mHorariosRef = mFirebaseDatabase.getReference("Especialidades")
                        .child(getEspecialidade())
                        .child("medicos")
                        .child(getCrm())
                        .child("horarios")
                        .child("marcados");

                mHorariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dados : dataSnapshot.getChildren()) {
                            String[] diaMarcado = dados.getKey().split(" - ");

                            for (int i = 0; i < section.list.size(); i++) {
                                if (diaMarcado[0].trim().equals(section.title)
                                        && diaMarcado[1].trim().equals(section.list.get(i).getName())) {
                                    section.list.remove(i);
                                    sectionAdapter.notifyAllItemsChangedInSection(section);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Remover os horários passados
                //capturando a hora corrente
                Date data = new Date();
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTime(data);
                Date data_atual = currentCalendar.getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
                String horaAtual = dateFormat_hora.format(data_atual);

                String[] horaAtualArray = horaAtual.split(":");
                int minHoraAtual = Integer.parseInt(horaAtualArray[0]) * 60;
                int minutosAtual = Integer.parseInt(horaAtualArray[1]) + minHoraAtual;
                String nomeDiaSemanaHoje = "";
                switch (currentCalendar.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.SUNDAY:
                        nomeDiaSemanaHoje = "Domingo";
                        break;
                    case Calendar.MONDAY:
                        nomeDiaSemanaHoje = "Segunda";
                        break;
                    case Calendar.TUESDAY:
                        nomeDiaSemanaHoje = "Terça";
                        break;
                    case Calendar.WEDNESDAY:
                        nomeDiaSemanaHoje = "Quarta";
                        break;
                    case Calendar.THURSDAY:
                        nomeDiaSemanaHoje = "Quinta";
                        break;
                    case Calendar.FRIDAY:
                        nomeDiaSemanaHoje = "Sexta";
                        break;
                    case Calendar.SATURDAY:
                        nomeDiaSemanaHoje = "Sábado";
                        break;
                }
                String nomeMesHoje = "";
                switch (currentCalendar.get(Calendar.MONTH)) {
                    case 0:
                        nomeMesHoje = "janeiro";
                        break;
                    case 1:
                        nomeMesHoje = "fevereiro";
                        break;
                    case 2:
                        nomeMesHoje = "março";
                        break;
                    case 3:
                        nomeMesHoje = "abril";
                        break;
                    case 4:
                        nomeMesHoje = "maio";
                        break;
                    case 5:
                        nomeMesHoje = "junho";
                        break;
                    case 6:
                        nomeMesHoje = "julho";
                        break;
                    case 7:
                        nomeMesHoje = "agosto";
                        break;
                    case 8:
                        nomeMesHoje = "setembro";
                        break;
                    case 9:
                        nomeMesHoje = "outubro";
                        break;
                    case 10:
                        nomeMesHoje = "novembro";
                        break;
                    case 11:
                        nomeMesHoje = "dezembro";
                        break;
                }

                String title_hoje = nomeDiaSemanaHoje + ", " + currentCalendar.get(Calendar.DAY_OF_MONTH) + " de " + nomeMesHoje;

                try {
                    //percorrendo a lista inteira
                    for (int position = 0; position < section.list.size(); position++) {
                        //verificar cada posição e comparar com as horas passadas
                        for (int min = inicioAtendimento; min < minutosAtual; min++) {
                            int minutos = min % 60;
                            int hora = (min - minutos) / 60;

                            String hrsStr = hora < 10 ? "0" + hora : "" + hora;
                            String minStr = minutos < 10 ? "0" + minutos : "" + minutos;

                            String horarioFormatado = (hrsStr + ":" + minStr);

                            if (title_hoje.trim().equals(section.title) &&
                                    section.list.get(position).getName().equals(horarioFormatado)) {
                                section.list.remove(position);
                                sectionAdapter.notifyAllItemsChangedInSection(section);
                            }
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    @NonNull
    private String getRandomStringNumber() {
        return String.valueOf(RANDOM.nextInt(100000));
    }


    private class NameSection extends StatelessSection {

        final String TAG;
        final String title;
        final List<Person> list;
        boolean expanded = false;

//        Bundle bundle = getIntent().getExtras();
//        String crm = bundle.getString("crm");
//        String espMed = bundle.getString("espMed");

        NameSection(String tag, String title) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.horarios_section_item)
                    .headerResourceId(R.layout.horarios_section_header)
                    .build());

            this.TAG = tag;
            this.title = title;
            this.list = new ArrayList<>();
        }


        @Override
        public int getContentItemsTotal() {
            return expanded ? list.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        public int findPositionWithName(String name, ArrayList<Person> list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(name)) {
                    return i;
                }
            }
            return -1; //-1 is Not found
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            final String name = list.get(position).getName();
            final String horario = list.get(position).getHr();

            itemHolder.btnItem.setText(name);

            itemHolder.btnItem.setOnClickListener(v -> {
                vibrar(horariosDispActivity.this);
                int adapterPosition = itemHolder.getAdapterPosition();
                recyclerView.smoothScrollToPosition(adapterPosition);
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    Intent i = new Intent(horariosDispActivity.this, ConfirmarHorarioActivity.class);
                    setDia(title);
                    setHorario(name);
                    setSimpleHorario(horario);

//                        i.putExtra("dia", title);
//                        i.putExtra("horario", name);
//                        i.putExtra("horarioFormatado", horario);
//                        i.putExtra("crm", getCrm());
//                        i.putExtra("esp", getEspecialidade());
                    startActivity(i);

//                        horariosDispActivity.this.finish();


//                        int positionInSection = sectionAdapter.getPositionInSection(adapterPosition);
//                        list.remove(positionInSection);
//                        sectionAdapter.notifyItemRemovedFromSection(TAG, positionInSection);
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvTitle.setText(title);

            headerHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrar(horariosDispActivity.this);
                    HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                    int adapterPosition = headerHolder.getAdapterPosition();
                    expanded = !expanded;
                    headerHolder.imgArrow.setImageResource(
                            expanded ? R.drawable.ic_arrow_drop_up_24dp : R.drawable.ic_arrow_drop_down_24dp
                    );
                    sectionAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(adapterPosition);

//                    if (adapterPosition != RecyclerView.NO_POSITION) {
//                        final int sectionItemsTotal = getSectionItemsTotal();
//
//                        sectionAdapter.removeSection(TAG);
//
//                        sectionAdapter.notifyItemRangeRemoved(adapterPosition, sectionItemsTotal);
//                    }
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;
        private final TextView tvTitle;
        private final ImageView imgArrow;


        HeaderViewHolder(View view) {
            super(view);

            rootView = view;
            tvTitle = view.findViewById(R.id.tvTitle);
            imgArrow = view.findViewById(R.id.imgArrow);

        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final Button btnItem;
        private final TextView lblHorarioPPassar;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            btnItem = view.findViewById(R.id.btnItem);
            lblHorarioPPassar = view.findViewById(R.id.lblHorario_formatado);
        }
    }

    private class Person {
        final String name;
        final String id;
        final String hr;

        Person(String name, String id, String hr) {
            this.name = name;
            this.id = id;
            this.hr = hr;
        }

        String getName() {
            return name;
        }

        String getId() {
            return id;
        }

        public String getHr() {
            return hr;
        }

    }

}
