package net.aliveplex.alive.on_timeonandroid.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.aliveplex.alive.on_timeonandroid.Models.SubjectViewModel;
import net.aliveplex.alive.on_timeonandroid.R;

import java.util.List;

/**
 * Created by Aliveplex on 30/4/2560.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    public List<SubjectViewModel> getSubjectList() {
        return subjectList;
    }

    private List<SubjectViewModel> subjectList;

    public SubjectAdapter(List<SubjectViewModel> subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listtextview, parent, false);

        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        SubjectViewModel subject = subjectList.get(position);
        holder.getSubjectId().setText(subject.getSubjectId());
        holder.getSection().setText("Section " + Integer.toString(subject.getSection()));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView getSubjectId() {
            return subjectId;
        }

        public TextView getSection() {
            return section;
        }

        private TextView subjectId;
        private TextView section;

        public SubjectViewHolder(View itemView) {
            super(itemView);

            subjectId = (TextView)itemView.findViewById(R.id.subject_id_textview);
            section = (TextView)itemView.findViewById(R.id.section_textview);
        }
    }
}
