package com.project.zhimer.studentdesk.view.tabPerkuliahan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.project.zhimer.studentdesk.R;
import com.project.zhimer.studentdesk.SessionManager;
import com.project.zhimer.studentdesk.adapter.JadwalPenggantiAdapter;
import com.project.zhimer.studentdesk.model.Kuliah;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class Pengganti extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Kuliah> listKuliahPengganti;

    SessionManager sessionManager;
    Kuliah kuliah;

    ProgressView progressView;


    public Pengganti() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.tab_perkuliahan_pengganti, container, false);

        listKuliahPengganti = new ArrayList<>();
        adapter = new JadwalPenggantiAdapter(listKuliahPengganti, getActivity());

        recyclerView = view.findViewById(R.id.recyclerViewNilaiAktif);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        progressView = view.findViewById(R.id.circular);

        sessionManager = new SessionManager(getContext());

        progressView.setVisibility(View.VISIBLE);
        progressView.start();


        return view;
    }

    private void GetjadwalPengganti() {
        String url = sessionManager.getUrl() + "/jadwal/JadwalKuliahPengganti/format/json";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setBasicAuth(sessionManager.getAuthUsername(), sessionManager.getAuthPassword());
        params.put("uname", sessionManager.getNim());
        params.put("pwd", sessionManager.getPassword());
    }

}
