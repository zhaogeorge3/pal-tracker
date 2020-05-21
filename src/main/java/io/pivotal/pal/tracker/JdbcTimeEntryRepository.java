package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        try{
            String sql = "SELECT * FROM time_entries WHERE id = ?";

            return this.jdbcTemplate.queryForObject(sql, new Object[]{id}, new TimeEntryMapper());
        } catch(Exception e){
            return null;
        }

    }

    @Override
    public List<TimeEntry> list() {
        String sql = "SELECT * FROM time_entries";

        List<TimeEntry> timeEntries = this.jdbcTemplate.query(sql, new TimeEntryMapper());

        return timeEntries;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        this.jdbcTemplate.update("UPDATE time_entries SET project_id = ?, user_id = ?, date = ?,  hours = ? WHERE id = ?",
                timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours(), id);
        return this.find(id);
    }

    @Override
    public void delete(long id) {
        this.jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    public class TimeEntryMapper implements RowMapper<TimeEntry> {

        @Override
        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            TimeEntry timeEntry = new TimeEntry();

            timeEntry.setId(rs.getLong("id"));
            timeEntry.setProjectId(rs.getLong("project_id"));
            timeEntry.setUserId(rs.getLong("user_id"));
            timeEntry.setDate(LocalDate.parse(rs.getString("date")));
            timeEntry.setHours(rs.getInt("hours"));


            return timeEntry;
        }

    }
}
