package ml.medyas.wallbay.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Ignore;

import java.util.List;

import ml.medyas.wallbay.R;
import ml.medyas.wallbay.models.unsplash.PreviewPhoto;
import ml.medyas.wallbay.models.unsplash.Tag;
import ml.medyas.wallbay.utils.GlideApp;


public class CollectionEntity implements Parcelable {
    private int id;
    private String title;
    private int totalPhotos;
    private String username;
    private String userImg;
    private List<Tag> tags;
    private List<PreviewPhoto> imagePreviews;

    @Ignore
    public static final Creator<CollectionEntity> CREATOR = new Creator<CollectionEntity>() {
        @Override
        public CollectionEntity createFromParcel(Parcel in) {
            return new CollectionEntity(in);
        }

        @Override
        public CollectionEntity[] newArray(int size) {
            return new CollectionEntity[size];
        }
    };

    @Ignore
    public static DiffUtil.ItemCallback<CollectionEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<CollectionEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CollectionEntity collectionEntity, @NonNull CollectionEntity t1) {
            return collectionEntity.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CollectionEntity collectionEntity, @NonNull CollectionEntity t1) {
            return collectionEntity.equals(t1);
        }
    };

    @BindingAdapter({"list", "index"})
    public static void loadImagePreview(ImageView imageView, List<PreviewPhoto> list, int index) {
        GlideApp.with(imageView)
                .load(list.get(index).getUrls().getRegular())
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(imageView);
    }

    @BindingAdapter({"totalPhotos"})
    public static void setTotalPhotosNumber(TextView text, int number) {
        text.setText(String.format(text.getContext().getResources().getConfiguration().locale, "%d\nPhotos", number));
    }


    public CollectionEntity() {
    }

    public CollectionEntity(int id, String title, int totalPhotos, List<Tag> tags, String username, String userImg, List<PreviewPhoto> imagePreviews) {
        this.id = id;
        this.title = title;
        this.totalPhotos = totalPhotos;
        this.tags = tags;
        this.username = username;
        this.userImg = userImg;
        this.imagePreviews = imagePreviews;
    }

    protected CollectionEntity(Parcel in) {
        id = in.readInt();
        title = in.readString();
        totalPhotos = in.readInt();
        tags = in.createTypedArrayList(Tag.CREATOR);
        username = in.readString();
        userImg = in.readString();
        imagePreviews = in.createTypedArrayList(PreviewPhoto.CREATOR);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(int totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public List<PreviewPhoto> getImagePreviews() {
        return imagePreviews;
    }

    public void setImagePreviews(List<PreviewPhoto> imagePreviews) {
        this.imagePreviews = imagePreviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(totalPhotos);
        parcel.writeTypedList(tags);
        parcel.writeString(username);
        parcel.writeString(userImg);
        parcel.writeTypedList(imagePreviews);
    }
}
