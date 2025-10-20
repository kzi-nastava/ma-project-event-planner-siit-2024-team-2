package com.example.eventplanner.fragments.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.PriceListAdapter;
import com.example.eventplanner.clients.repositories.serviceproduct.PriceListRepository;
import com.example.eventplanner.dto.serviceproduct.PriceListDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;

public class PriceListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PriceListAdapter adapter;
    private PriceListViewModel viewModel;
    private Long sppId;
    Button btnDownloadPdf;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_price_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_price_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        btnDownloadPdf = view.findViewById(R.id.btn_generate_pdf);

        if (getArguments() != null) sppId = getArguments().getLong("sppId");

        PriceListRepository repository = new PriceListRepository();
        PriceListViewModelFactory factory = new PriceListViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(PriceListViewModel.class);

        viewModel.getPriceListBySppId(sppId).observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                setupAdapter(list);
            }
        });

        btnDownloadPdf.setOnClickListener(v -> {
            if (sppId != null) {
                viewModel.downloadPdf(sppId).observe(getViewLifecycleOwner(), responseBody -> {
                    if (responseBody != null) {
                        savePdfFile(responseBody);
                    } else {
                        Toast.makeText(requireContext(), "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "No service provider ID", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupAdapter(List<PriceListDto> list) {
        // Create the listener explicitly
        PriceListAdapter.OnItemSaveListener listener = new PriceListAdapter.OnItemSaveListener() {
            @Override
            public void onSave(PriceListDto item) {
                viewModel.updateItem(item);
                Toast.makeText(requireContext(), "✅ Saved " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        };

        // Create adapter with explicit listener
        adapter = new PriceListAdapter(list, listener);

        // Attach adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }

    private void savePdfFile(ResponseBody body) {
        try {
            File pdfFile = new File(requireContext().getCacheDir(), "pricelist-" + sppId + ".pdf");
            InputStream inputStream = body.byteStream();
            OutputStream outputStream = new FileOutputStream(pdfFile);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            Toast.makeText(requireContext(), "PDF generated successfully", Toast.LENGTH_SHORT).show();

            // Optional: open the PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider", // must match manifest
                    pdfFile
            );
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
