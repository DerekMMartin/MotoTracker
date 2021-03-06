package martin.derek.mototracker.adapters;

import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import martin.derek.mototracker.Part;
import martin.derek.mototracker.R;
import martin.derek.mototracker.TagEditFragment;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.MyViewHolder> implements Filterable {

    private final DatabaseReference ref;
    private List<Part> partList;
    private List<Part> filteredParts;
    private Context context;
    private RecyclerView recyclerView;
    private PartFilter partFilter;
    public List<String> tagsToFilter;

    public BikeAdapter(List<Part> partList, Context context, RecyclerView recyclerView, DatabaseReference reference){
        this.ref = reference;
        this.partList = partList;
        this.context = context;
        this.recyclerView = recyclerView;
        tagsToFilter = new ArrayList<>();
        filteredParts = partList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tag_part_display_button,viewGroup,false);
        return new BikeAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Part p = filteredParts.get(i);

        myViewHolder.text_name.setText(p.Name);
        myViewHolder.text_brand.setText(p.Brand);
        myViewHolder.text_price.setText(p.Price);
        myViewHolder.text_installedOn.setText(p.InstalledOn);
        myViewHolder.text_description.setText(p.Description);
    }

    @Override
    public int getItemCount() {
        return filteredParts.size();
    }

    @Override
    public Filter getFilter() {
        if(partFilter == null){
            partFilter = new PartFilter();
        }
        return partFilter;
    }


    public void runAnimations(){
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_animation_scale_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView text_name;
        private TextView text_brand;
        private TextView text_price;
        private TextView text_description;
        private TextView text_installedOn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> OpenEditFrag());

            text_price = itemView.findViewById(R.id.bike_price);
            text_name = itemView.findViewById(R.id.bike_name);
            text_brand = itemView.findViewById(R.id.bike_brand);
            text_description = itemView.findViewById(R.id.bike_description);
            text_installedOn = itemView.findViewById(R.id.bike_installedOn);
        }

        public void OpenEditFrag(){
            Toast.makeText(context, ref.toString(), Toast.LENGTH_SHORT).show();

            ref.getRoot().getKey();
            TagEditFragment tagEditFragment = TagEditFragment.newInstance(ref.getKey());






        }
    }

    private class PartFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(tagsToFilter!= null && tagsToFilter.size()>0 || constraint.length() > 0){
                ArrayList<Part> tempList = new ArrayList<>();

                for (Part p : partList){
                    boolean canAdd = true;
                    if(tagsToFilter.size() > 0 ){

                        for(String tag : tagsToFilter){
                            if(!p.Tags.contains(tag)){
                                canAdd = false;
                                break;
                            }
                        }
                    }
                    if(!p.Name.toLowerCase().contains(constraint.toString().toLowerCase())){
                        canAdd = false;
                    }
                    if(canAdd)
                        tempList.add(p);
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }
            else
            {
                filterResults.count = partList.size();
                filterResults.values = partList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredParts = (List<Part>) filterResults.values;
            runAnimations();
        }

    }
}
