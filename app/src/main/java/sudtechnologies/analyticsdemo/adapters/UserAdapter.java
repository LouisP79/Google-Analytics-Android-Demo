package sudtechnologies.analyticsdemo.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.model.User;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> items;
    private Context ctx;


    public UserAdapter(Context ctx, List<User> items) {
        this.items = items;
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User item = items.get(position);

        holder.tvUserName.setText(ctx.getString(R.string.blank_2s_s, item.getName(), item.getLastName()));

        EmailAdapter adapter = new EmailAdapter(ctx,item.getEmails());
        holder.rvEmails.setAdapter(adapter);
        holder.rvEmails.setLayoutManager(new LinearLayoutManager(ctx));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_user_name)
        TextView tvUserName;

        @BindView(R.id.rv_emails)
        RecyclerView rvEmails;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
