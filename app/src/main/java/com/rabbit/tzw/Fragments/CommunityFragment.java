package com.rabbit.tzw.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rabbit.tzw.R;


public class CommunityFragment extends Fragment {
    private TextView mTvintegral;
    private TextView mTvput_in;
    private TextView mTvranking;
    private TextView mTvrule;
    private TextView mTvachievement;
    private TextView mTvsetting;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvput_in = view.findViewById(R.id.put_in_community);
        mTvranking = view.findViewById(R.id.ranking_community);
        mTvrule = view.findViewById(R.id.rule_community);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community,container,false);
    }
}
