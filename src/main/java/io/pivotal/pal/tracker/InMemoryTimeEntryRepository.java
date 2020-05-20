package io.pivotal.pal.tracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> timeEntries = new HashMap<>();

    private long currentId = 1L;

    public TimeEntry create(TimeEntry timeEntry) {
        long id = this.currentId;
        TimeEntry newTimeEntry = new TimeEntry(
                id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
        timeEntries.put(id, newTimeEntry);
        this.currentId+=1;
        return newTimeEntry;
    }

    public TimeEntry find(long id) {
        return this.timeEntries.get(id);
    }

    public List<TimeEntry> list() {
        List<TimeEntry> timeEntryList = new ArrayList<>();
        this.timeEntries.values().forEach(timeEntry -> {
            timeEntryList.add(timeEntry);
        });
        return timeEntryList;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (find(id) == null) return null;
        TimeEntry newTimeEntry = new TimeEntry(
                id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
        timeEntries.replace(id, newTimeEntry);
        return newTimeEntry;
    }

    public void delete(long id) {
        this.timeEntries.remove(id);
    }
}
