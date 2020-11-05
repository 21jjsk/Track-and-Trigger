package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.todo_HomePage;
import com.example.todo.R;

import java.util.List;

import Objects.todo_ThemeObject;

public class todo_ThemeSelectorAdapter extends RecyclerView.Adapter<todo_ThemeSelectorAdapter.ViewHolder> {

    private List<todo_ThemeObject> images;
    private int resource;
    private Context context;

    private int selectedPosition;

    public todo_ThemeSelectorAdapter(Context context, int resource, List<todo_ThemeObject> images){
        this.context = context;
        this.resource = resource;
        this.images = images;

        //default selected theme
        selectedPosition = 0;
        todo_HomePage.setSelectedTheme(images.get(selectedPosition).getImageRes());
        this.images.get(selectedPosition).setSelected(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new todo_ThemeSelectorAdapter.ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.image.setImageResource(images.get(position).getImageRes());

        if(images.get(position).isSelected()){
            //set selected overlay image (white dot over the image)
            holder.isSelectedRadio.setVisibility(View.VISIBLE);
        } else {
            holder.isSelectedRadio.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deselecting old selection
                images.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);

                //updating new selection (also the static field for selected theme in HomePage)
                selectedPosition = position;
                images.get(position).setSelected(true);
                todo_HomePage.setSelectedTheme(images.get(position).getImageRes());
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, isSelectedRadio;

        public ViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.list_theme_image);
            isSelectedRadio = view.findViewById(R.id.list_theme_image_selected_radio);
        }
    }
}