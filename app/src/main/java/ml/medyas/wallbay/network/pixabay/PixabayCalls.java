package ml.medyas.wallbay.network.pixabay;

import ml.medyas.wallbay.BuildConfig;
import ml.medyas.wallbay.entities.pixabay.PixabayEntity;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ml.medyas.wallbay.utils.Utils.REQUEST_SIZE;

public class PixabayCalls {
    private static final String URL = "https://pixabay.com/api/";

    private Retrofit builder() {
        return new Retrofit.Builder()
                .baseUrl(PixabayCalls.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Call<PixabayEntity> getSearch(String query, int page, String category, String colors, boolean editorsChoice, String orderBy) {
        return builder().create(PixabayService.class).
                search(BuildConfig.PixabayApiKey, query, REQUEST_SIZE, page, category, colors, editorsChoice, orderBy);
    }
}
