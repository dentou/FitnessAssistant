package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProgressFragment extends Fragment {

    public static final String TAG = "ProgressFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;
    private Callbacks mCallbacks;
    private FloatingActionButton mFab;
    private GraphView mGraph;
    private LineGraphSeries<DataPoint> mPercentFatSeries;

    public static ProgressFragment newInstance(UUID userId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);

        ProgressFragment fragment = new ProgressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onBodyCreated(Body body);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID userId = (UUID) getArguments().getSerializable(ARG_USER_ID);
        mUser = UserHandler.get(getActivity()).getUser(userId);

        Log.i(TAG, "Bodies = " + BodyHandler.get(getActivity()).getBodies(userId).size());

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        mFab = (FloatingActionButton) view.findViewById(R.id.progress_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Body body = new Body(mUser.getId());
                Log.i(TAG, "New body created for user " + mUser.getName() + " with id " + body.getId());
                BodyHandler.get(getActivity()).addBody(body);
                mCallbacks.onBodyCreated(body);
            }
        });

        mGraph = (GraphView) view.findViewById(R.id.graph);
        mGraph.setTitle("% Body Fat");
        mGraph.getViewport().setScalable(true);

        mGraph.getViewport().setYAxisBoundsManual(true);
        mGraph.getViewport().setMinY(0);
        mGraph.getViewport().setMaxY(100);

        mPercentFatSeries = new LineGraphSeries<>();
        mPercentFatSeries.setDrawDataPoints(true);
        mPercentFatSeries.setDrawBackground(true);
        mPercentFatSeries.setAnimated(true);
        mGraph.addSeries(mPercentFatSeries);



        // set date label formatter
        mGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        mGraph.getGridLabelRenderer().setHumanRounding(false);

        updateUI();

        return view;
    }


    private void updateUI() {
        mUser = UserHandler.get(getActivity()).getUser(mUser.getId());

        // todo
        updateGraph();
    }

    private void updateGraph() {
        List<Body> bodies = BodyHandler.get(getActivity()).getBodies(mUser.getId());
        if (bodies.isEmpty()) {
            return;
        }
        List<BodyIndex> indices = FitnessAnalyzer.analyze(mUser, bodies);
        DataPoint[] points = createPercentFatDataPoints(indices);
        mPercentFatSeries.resetData(points);

        // set manual x bounds to have nice steps
        mGraph.getViewport().setMinX(points[0].getX());
        mGraph.getViewport().setMaxX(points[points.length > 3 ? 2 : points.length - 1].getX());
        mGraph.getViewport().setXAxisBoundsManual(true);
        mGraph.getViewport().scrollToEnd();

    }


    private DataPoint[] createPercentFatDataPoints(List<BodyIndex> indices) {
        List<DataPoint> points = new ArrayList<>();
        DateTime lastDate = null;
        for (BodyIndex index : indices) {
            DateTime currentDate = new DateTime(index.getDate());
            if (lastDate == null) {
                points.add(new DataPoint(index.getDate(), index.getFatPercentage()));
            } else if (currentDate.getDayOfWeek() != lastDate.getDayOfWeek()) {
                points.add(new DataPoint(index.getDate(), index.getFatPercentage()));
            } else if (new Period(lastDate, currentDate).getHours() > 24) {
                points.add(new DataPoint(index.getDate(), index.getFatPercentage()));
            }
            lastDate = currentDate;

        }
        return points.toArray(new DataPoint[points.size()]);
    }


}
