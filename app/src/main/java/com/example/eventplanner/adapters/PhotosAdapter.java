package com.example.eventplanner.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private List<Uri> photoList;

    public PhotosAdapter(List<Uri> photoList) {
        this.photoList = photoList;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Uri photoUri = photoList.get(position);
        holder.imageView.setImageURI(photoUri);

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Photo")
                    .setMessage("Are you sure you want to delete this photo?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        photoList.remove(position);
                        notifyDataSetChanged();  // Refresh the RecyclerView
                        Toast.makeText(v.getContext(), "Photo deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.dialog_full_screen_photo);

            ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
            ImageView closeButton = dialog.findViewById(R.id.close_button);
            Uri photoDialogUri = photoList.get(position);

            imageView.setImageURI(photoDialogUri); // Replace with your image loading logic
            closeButton.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_view);
        }
    }
}



