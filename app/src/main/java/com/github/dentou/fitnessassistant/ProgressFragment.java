package com.github.dentou.fitnessassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.dentou.fitnessassistant.model.Body;
import com.github.dentou.fitnessassistant.model.BodyIndex;
import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.BodyHandler;
import com.github.dentou.fitnessassistant.worker.FitnessAnalyzer;
import com.github.dentou.fitnessassistant.worker.UserHandler;
import com.github.vipulasri.timelineview.TimelineView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProgressFragment extends Fragment {

    public static final String TAG = "ProgressFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;
    private Callbacks mCallbacks;
    private FloatingActionButton mFab;
    private GraphView mGraph;
    private LineGraphSeries<DataPoint> mPercentFatSeries;

    private RecyclerView mRecyclerView;
    private TimelineAdapter mAdapter;

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
        void onBodyEdited(Body body);
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

        mFab = view.findViewById(R.id.progress_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Body latestBody = BodyHandler.get(getActivity()).getLatestBody(mUser.getId());
                final Body body = new Body(mUser.getId());

                if (latestBody == null ||
                        (new DateTime(latestBody.getDate()).getDayOfWeek() != new DateTime(body.getDate()).getDayOfWeek())) {

                    Log.i(TAG, "New body created for user " + mUser.getName() + " with id " + body.getId());
                    BodyHandler.get(getActivity()).addBody(body);
                    mCallbacks.onBodyEdited(body);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("You have already created a record today. Do you want to edit it?");
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCallbacks.onBodyEdited(latestBody);
                            }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));



        updateUI();

        return view;
    }


    private void updateUI() {
        mUser = UserHandler.get(getActivity()).getUser(mUser.getId());

        // todo
        updateGraph();
        updateTimeline();
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
        for (BodyIndex index : indices) {
            points.add(new DataPoint(index.getDate(), index.getFatPercentage()));
        }
        return points.toArray(new DataPoint[points.size()]);
    }

    private void updateTimeline() {
        List<Body> bodies = BodyHandler.get(getActivity()).getBodies(mUser.getId());
        List<BodyIndex> indices = FitnessAnalyzer.analyze(mUser, bodies);

        if (mAdapter == null) {
            mAdapter = new TimelineAdapter(indices);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBodyIndices(indices);
            mAdapter.notifyDataSetChanged();
        }
    }


    private class TimelineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private BodyIndex mBodyIndex;

        private TimelineView mTimelineView;
        private TextView mPercentFatView;
        private TextView mDateView;

        public TimelineViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeline_view);
            mTimelineView.initLine(viewType);

            mPercentFatView = (TextView) itemView.findViewById(R.id.bodyindex_percentfat);
            mDateView = (TextView) itemView.findViewById(R.id.bodyindex_date);
        }

        public void bind(BodyIndex bodyIndex) {
            mBodyIndex = bodyIndex;
            mPercentFatView.setText(getString(R.string.bodyindex_percentfat_format, mBodyIndex.getFatPercentage()));
            mDateView.setText(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US).format(mBodyIndex.getDate()));
        }

        @Override
        public void onClick(View view) {
            Body body = BodyHandler.get(getActivity()).getBody(mBodyIndex.getUserId(), mBodyIndex.getBodyId());
            mCallbacks.onBodyEdited(body);
        }
    }

    private class TimelineAdapter extends RecyclerView.Adapter<TimelineViewHolder> {

        private List<BodyIndex> mBodyIndices;

        public TimelineAdapter(List<BodyIndex> bodyIndices) {
            mBodyIndices = bodyIndices;
        }

        public void setBodyIndices(List<BodyIndex> bodyIndices) {
            mBodyIndices = bodyIndices;
        }

        @NonNull
        @Override
        public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_timeline, parent, false);
            return new TimelineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
            BodyIndex bodyIndex = mBodyIndices.get(position);
            holder.bind(bodyIndex);
        }

        @Override
        public int getItemCount() {
            return mBodyIndices.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position, getItemCount());
        }

    }

}
