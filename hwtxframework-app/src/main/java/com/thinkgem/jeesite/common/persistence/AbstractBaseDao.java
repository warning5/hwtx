package com.thinkgem.jeesite.common.persistence;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractBaseDao<T extends Model<T>> extends BaseDao {

    public static final String select_prefix = "_";

    public List<T> find(Model<T> model, String sqlLabel, Object... paras) {
        String sql = SqlKit.sql(sqlLabel);
        return model.find(sql, paras);
    }

    public T findOne(Model<T> model, String sqllabel, Object... paras) {
        String sql = SqlKit.sql(sqllabel);
        return model.findFirst(sql, paras);
    }

    public List<Record> findComplex(String sqllabel, Object... paras) {
        String sql = SqlKit.sql(sqllabel);
        List<Record> records = Db.find(sql, paras);
        return records;
    }

    public void delete(String sqllabel, Object[][] paras) {
        String sql = SqlKit.sql(sqllabel);
        Db.batch(sql, paras, paras.length);
    }

    public <M extends Model<?>> List<M> getList(Model<M> model, String sqllabel, Object... paras) {
        String sql = SqlKit.sql(sqllabel);
        return model.find(sql, paras);
    }

    @SuppressWarnings("unchecked")
    public <M extends Model<?>> Map<String, Object> getRecordModel(Record record) {
        Map<String, Pair<Class<?>, String>> mapping = SqlKit.getModelMapping();
        Map<String, Object> clos = record.getColumns();
        Map<String, Object> result = Maps.newHashMap();
        for (Entry<String, Object> entry : clos.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            String key = entry.getKey();
            int index = key.indexOf(select_prefix);
            if (index < 0) {
                throw new RuntimeException("sql select item must start with prefix.");
            }
            String prefix = key.substring(0, index);
            String name = mapping.get(prefix).getRight();
            Object model = result.get(name);
            if (model == null) {
                Class<?> modelClazz = mapping.get(prefix).getLeft();
                ConstructorAccess<?> access = ConstructorAccess.get(modelClazz);
                model = access.newInstance();
                result.put(name, model);
            }
            if (model == null) {
                continue;
            }
            String attr = key.substring(index + 1);
            if (model instanceof Model) {
                M mModel = (M) model;
                if (mModel.hasAttr(attr)) {
                    mModel.set(attr, entry.getValue());
                } else {
                    MethodAccess access = MethodAccess.get(model.getClass());
                    access.invoke(model, "set" + StrKit.firstCharToUpperCase(attr), entry.getValue());
                }
            } else {
                MethodAccess access = MethodAccess.get(model.getClass());
                access.invoke(model, "set" + StrKit.firstCharToUpperCase(attr), entry.getValue());
            }
        }
        return result;
    }
}
