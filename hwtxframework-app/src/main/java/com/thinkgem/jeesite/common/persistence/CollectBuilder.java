package com.thinkgem.jeesite.common.persistence;

import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class CollectBuilder {

    enum ClauseType {
        control, condition
    }

    @AllArgsConstructor
    @Getter
    class Clause {
        String expression;
        ClauseType clauseType;
    }

    private List<Clause> clauses = Lists.newLinkedList();

    public CollectBuilder add(String clause) {
        clauses.add(new Clause(clause, ClauseType.condition));
        return this;
    }

    public static CollectBuilder builderInClause(String field, List<? extends Object> values) {
        CollectBuilder collectBuilder = new CollectBuilder();
        collectBuilder.in(field, values);
        return collectBuilder;
    }

    public CollectBuilder addFirst(String clause) {
        clauses.set(0, new Clause(clause, ClauseType.condition));
        return this;
    }

    public static String whereClause() {
        return "where ";
    }

    public CollectBuilder and() {
        clauses.add(new Clause("and", ClauseType.control));
        return this;
    }

    public CollectBuilder where() {
        clauses.add(new Clause("where", ClauseType.control));
        return this;
    }

    public CollectBuilder between(String field, Object one, Object two) {
        clauses.add(new Clause(field + " between " + one + " and " + two, ClauseType.condition));
        return this;
    }

    public CollectBuilder eq(String field, Object value) {
        if (value != null) {
            if (value instanceof String) {
                clauses.add(new Clause(field + " = '" + value + "'", ClauseType.condition));
            } else {
                clauses.add(new Clause(field + " = " + value, ClauseType.condition));
            }
        }
        return this;
    }

    public CollectBuilder like(String field, Object value) {
        if (value != null) {
            clauses.add(new Clause(field + " like '" + value + "'", ClauseType.condition));
        }
        return this;
    }

    public CollectBuilder orderBy(String field) {
        if (!StrKit.isBlank(field)) {
            clauses.add(new Clause(" order by " + field, ClauseType.control));
        }
        return this;
    }

    public CollectBuilder in(String field, List<? extends Object> values) {
        StringBuilder builder = new StringBuilder();
        builder.append(field);
        builder.append(" in (");
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) instanceof String) {
                builder.append("'" + values.get(i) + "'");
            } else {
                builder.append(values.get(i));
            }
            if (i + 1 != values.size()) {
                builder.append(",");
            }
        }
        builder.append(")");
        clauses.add(new Clause(builder.toString(), ClauseType.condition));
        return this;
    }

    public String build(boolean includeFirstAnd) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < clauses.size(); i++) {
            if (i != 0) {
                if (clauses.get(i).getClauseType() != ClauseType.control) {
                    builder.append(" and ");
                }
            } else {
                if (includeFirstAnd) {
                    builder.append(" and ");
                }
            }
            builder.append(clauses.get(i).getExpression());
        }
        return builder.toString();
    }

    public boolean isEmpty() {
        return clauses.size() == 0;
    }
}
