import java.util.Date;

public class RecordModel {
    private Date date;
    private Float value;

    public RecordModel(Date date, Float value) {
        this.date = date;
        this.value = value;
    }

    public RecordModel() {

    }

    public Date getDate() {
        return date;
    }

    public Float getValue() {
        return value;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RecordModel{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }
}
