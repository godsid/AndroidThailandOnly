package info.srihawong.thailandonly;

/**
 * Created by Banpot.S on 10/1/2557.
 */
public class ListItem {
    private String title;
    private String imageItem;
    private String imageUser;

    public ListItem(String title, String imageItem, String imageUser) {
        this.title = title;
        this.imageItem = imageItem;
        this.imageUser = imageUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageItem() {
        return imageItem;
    }

    public void setImageItem(String imageItem) {
        this.imageItem = imageItem;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }
}
