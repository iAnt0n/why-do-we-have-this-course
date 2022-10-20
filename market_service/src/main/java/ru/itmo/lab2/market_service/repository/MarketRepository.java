package ru.itmo.lab2.market_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itmo.lab2.market_service.model.MIC;
import ru.itmo.lab2.market_service.model.MarketDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MarketRepository {
    private final JdbcTemplate jdbcTemplate;

    public Page<MarketDto> findAll(Pageable page) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from market", Integer.class);
        List<MarketDto> markets = jdbcTemplate.query("select * from market " +
                        " limit " + page.getPageSize() + " offset " + page.getOffset(),
                (resultSet, rowNum) -> mapResult(resultSet));
        return new PageImpl<>(markets, page, count);
    }

    public Optional<MarketDto> findById(UUID id) {
        return jdbcTemplate.queryForObject(
                "select * from market where id = ?", (resultSet, rowNum) ->
                        Optional.of(mapResult(resultSet)), id);
    }

    public int save(MarketDto marketDto) {
        return jdbcTemplate.update(
                "insert into market (id, mic, location) values(?,?,?)",
                marketDto.getId(), marketDto.getMic().toString(), marketDto.getLocation());
    }

    private MarketDto mapResult(final ResultSet resultSet) throws SQLException {
        return new MarketDto(
                resultSet.getObject("id", java.util.UUID.class),
                MIC.valueOf(resultSet.getString("mic")),
                resultSet.getString("location"));
    }
}
