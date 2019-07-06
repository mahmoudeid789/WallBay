package ml.medyas.wallbay.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;
import ml.medyas.wallbay.models.ImageEntity;
import ml.medyas.wallbay.repositories.FavoriteRepository;

public class FavoriteViewModel extends AndroidViewModel {
    private FavoriteRepository favoriteRepo;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepo = new FavoriteRepository(application);
    }

    public LiveData<List<ImageEntity>> getFavorites() {
        return favoriteRepo.getImageEntities();
    }

    public Completable insertFavorite(ImageEntity imageEntity) {
        return favoriteRepo.insertFavorite(imageEntity);
    }

    public Completable insertFavorite(List<ImageEntity> imageEntity) {
        return favoriteRepo.insertFavorite(imageEntity);
    }

    public Completable deleteFavorite(ImageEntity imageEntity) {
        return favoriteRepo.deleteFavorite(imageEntity);
    }

    public Completable deleteFavorite(List<ImageEntity> imageEntity) {
        return favoriteRepo.deleteFavorite(imageEntity);
    }
}
