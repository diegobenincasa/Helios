package br.lightbase.helios.users.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.lightbase.helios.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{
    
    @PersistenceContext
    private EntityManager entityManager;

    public Page<User> findUsers(String login, String name, Boolean active, Pageable pageable) {

        String sql = """
                SELECT id, login, fullname, active
                FROM main.users
                :clause
                """;

        String fullClause = "";
        List<String> clauses = new ArrayList<>();

        if(login != null && !login.isEmpty()) {
            String cl = "login ILIKE '%" + login + "%'";
            clauses.add(cl);
        }

        if(name != null && !name.isEmpty()) {
            String cl = "fullname ILIKE '%" + name + "%'";
            clauses.add(cl);
        }

        if(active != null) {
            String cl = "active = " + active;
            clauses.add(cl);
        }
        
        if(!clauses.isEmpty()) {
            fullClause = String.join(" AND ", clauses);
            fullClause = "WHERE " + fullClause;
            sql = sql.replace(":clause", fullClause);
        }
        else
            sql = sql.replace(":clause", "");

        Query query = entityManager.createNativeQuery(sql);
        if(pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = query.getResultList();
        
        long total = getTotalResults(sql);

        List<User> resp = result.stream().map(row -> 
            new User(
                        (Long) row[0],
                        (String) row[1],
                        null,
                        (String) row[2],
                        (Boolean) row[3]
                    )
            )
            .toList();
        
        if(pageable != null)
            return new PageImpl<>(resp, pageable, total);
        else
            return new PageImpl<>(resp);
    }

    private long getTotalResults(String sql) {
        String sqlCount = "SELECT COUNT(*) FROM (" + sql + ") AS countQuery";
        Query countQuery = entityManager.createNativeQuery(sqlCount);
        return ((Number) countQuery.getSingleResult()).longValue();
    }
}
