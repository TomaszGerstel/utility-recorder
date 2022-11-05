import java.util.Comparator;

public class RecordsComparator implements Comparator<RecordModel> {

    @Override
    public int compare(RecordModel o1, RecordModel o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
