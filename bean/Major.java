package vyshak.sdsu.edu.sdsuclassregistration.bean;

/**
 * Created by vyshak on 4/3/18.
 */

public class Major {
    private String title;
    private int id;
    private String college;
    private int classes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "Major{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", college='" + college + '\'' +
                ", classes='" + classes + '\'' +
                '}';
    }
}
