import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class RecordsOfUtilityModel {
    private String name;
    private TreeSet<RecordModel> records = new TreeSet<>(new RecordsComparator());

    public RecordsOfUtilityModel(String name, TreeSet<RecordModel> records) {
        this.name = name;
        this.records = records;
    }

    public RecordsOfUtilityModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeSet<RecordModel> getRecords() {
        return records;
    }

    public void setRecords(TreeSet<RecordModel> records) {
        this.records = records;
    }

    public void addRecord(RecordModel record) {

        this.records.add(record);

    }

    @Override
    public String toString() {
        return "RecordsOfUtilityModel{" +
                "name='" + name + '\'' +
                ", records=" + records +
                '}';
    }
}
