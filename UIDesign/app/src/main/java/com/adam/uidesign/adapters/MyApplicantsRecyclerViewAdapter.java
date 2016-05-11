package com.adam.uidesign.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.uidesign.R;
import com.adam.uidesign.contents.ApplicantsContent;
import com.adam.uidesign.fragments.ApplicantsFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a  and makes a call to the
 * specified .
 */
public class MyApplicantsRecyclerViewAdapter extends RecyclerView.Adapter<MyApplicantsRecyclerViewAdapter.ViewHolder> {

    private final List<ApplicantsContent.ApplicantItem> mValues;
    private final ApplicantsFragment.OnListFragmentInteractionListener mListener;

    public String job_id;

    public MyApplicantsRecyclerViewAdapter(List<ApplicantsContent.ApplicantItem> items, ApplicantsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_applicant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
//        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, job_id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ApplicantsContent.ApplicantItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.applicant_name);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
