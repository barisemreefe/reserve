package com.bee.reserve.providers;

import com.bee.reserve.utils.ReserveApplication;
import com.bee.reserve.datatypes.Table;

import java.util.List;

import io.objectbox.Box;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class TableProvider {
    private static Box<Table> tableBox;
    private Box<Table> getTableBox() {
        if (tableBox == null) {
            tableBox = ReserveApplication.getBoxStore().boxFor(Table.class);
        }
        return tableBox;
    }
    public void updateTable(long tableId, boolean reserved) {
        getTableBox().remove(tableId);
        getTableBox().put(new Table(tableId,reserved));
    }
    public List<Table> getTables() {
        return getTableBox().getAll();
    }
    public int size() {
        long tableCount = getTableBox().count();
        return (int)tableCount;
    }
    public void put(Table table) {
        getTableBox().put(table);
    }

    public void clear() {
        getTableBox().removeAll();
    }
}
