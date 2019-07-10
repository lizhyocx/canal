package com.alibaba.otter.canal.client.adapter.rdb.support;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.alibaba.otter.canal.client.adapter.rdb.config.MappingConfig;

public class SyncUtil {

    public static Map<String, String> getColumnsMap(MappingConfig.DbMapping dbMapping, Map<String, Object> data) {
        return getColumnsMap(dbMapping, data.keySet());
    }

    public static Map<String, String> getColumnsMap(MappingConfig.DbMapping dbMapping, Collection<String> columns) {
        Map<String, String> columnsMap;
        if (dbMapping.isMapAll()) {
            columnsMap = new LinkedHashMap<>();
            for (String srcColumn : columns) {
                boolean flag = true;
                if (dbMapping.getTargetColumns() != null) {
                    for (Map.Entry<String, String> entry : dbMapping.getTargetColumns().entrySet()) {
                        if (srcColumn.equals(entry.getValue())) {
                            columnsMap.put(entry.getKey(), srcColumn);
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    columnsMap.put(srcColumn, srcColumn);
                }
            }
        } else {
            columnsMap = dbMapping.getTargetColumns();
        }
        return columnsMap;
    }

    /**
     * 设置 preparedStatement
     *
     * @param type sqlType
     * @param pstmt 需要设置的preparedStatement
     * @param value 值
     * @param i 索引号
     */
    public static void setPStmt(int type, PreparedStatement pstmt, Object value, int i) throws SQLException {
        switch (type) {
            case Types.BIT:
            case Types.BOOLEAN:
                if (value instanceof Boolean) {
                    pstmt.setBoolean(i, (Boolean) value);
                } else if (value instanceof String) {
                    boolean v = !value.equals("0");
                    pstmt.setBoolean(i, v);
                } else if (value instanceof Number) {
                    boolean v = ((Number) value).intValue() != 0;
                    pstmt.setBoolean(i, v);
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                if (value instanceof String) {
                    pstmt.setString(i, (String) value);
                } else {
                    pstmt.setString(i, value.toString());
                }
                break;
            case Types.TINYINT:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
                    pstmt.setByte(i, (byte) value);
                } else if (value instanceof Number) {
                    pstmt.setByte(i, ((Number) value).byteValue());
                } else if (value instanceof String) {
                    pstmt.setByte(i, Byte.parseByte((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.SMALLINT:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
                    pstmt.setShort(i, (short) value);
                } else if (value instanceof Number) {
                    pstmt.setShort(i, ((Number) value).shortValue());
                } else if (value instanceof String) {
                    pstmt.setShort(i, Short.parseShort((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.INTEGER:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer
                    || value instanceof Long) {
                    pstmt.setInt(i, (int) value);
                } else if (value instanceof Number) {
                    pstmt.setInt(i, ((Number) value).intValue());
                } else if (value instanceof String) {
                    pstmt.setInt(i, Integer.parseInt((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.BIGINT:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer
                    || value instanceof Long) {
                    pstmt.setLong(i, (long) value);
                } else if (value instanceof Number) {
                    pstmt.setLong(i, ((Number) value).longValue());
                } else if (value instanceof String) {
                    pstmt.setLong(i, Long.parseLong((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                if (value instanceof BigDecimal) {
                    pstmt.setBigDecimal(i, (BigDecimal) value);
                } else if (value instanceof Byte) {
                    pstmt.setInt(i, (int) value);
                } else if (value instanceof Short) {
                    pstmt.setInt(i, (int) value);
                } else if (value instanceof Integer) {
                    pstmt.setInt(i, (int) value);
                } else if (value instanceof Long) {
                    pstmt.setLong(i, (long) value);
                } else if (value instanceof Float) {
                    pstmt.setBigDecimal(i, new BigDecimal((float) value));
                } else if (value instanceof Double) {
                    pstmt.setBigDecimal(i, new BigDecimal((double) value));
                } else {
                    pstmt.setBigDecimal(i, new BigDecimal(value.toString()));
                }
                break;
            case Types.REAL:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long
                    || value instanceof Float || value instanceof Double) {
                    pstmt.setFloat(i, (float) value);
                } else if (value instanceof Number) {
                    pstmt.setFloat(i, ((Number) value).floatValue());
                } else if (value instanceof String) {
                    pstmt.setFloat(i, Float.parseFloat((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.FLOAT:
            case Types.DOUBLE:
                if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long
                    || value instanceof Float || value instanceof Double) {
                    pstmt.setDouble(i, (double) value);
                } else if (value instanceof Number) {
                    pstmt.setDouble(i, ((Number) value).doubleValue());
                } else if (value instanceof String) {
                    pstmt.setDouble(i, Double.parseDouble((String) value));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                if (value instanceof Blob) {
                    pstmt.setBlob(i, (Blob) value);
                } else if (value instanceof byte[]) {
                    pstmt.setBytes(i, (byte[]) value);
                } else if (value instanceof String) {
                    pstmt.setBytes(i, ((String) value).getBytes(StandardCharsets.ISO_8859_1));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.CLOB:
                if (value instanceof Clob) {
                    pstmt.setClob(i, (Clob) value);
                } else if (value instanceof byte[]) {
                    pstmt.setBytes(i, (byte[]) value);
                } else if (value instanceof String) {
                    Reader clobReader = new StringReader((String) value);
                    pstmt.setCharacterStream(i, clobReader);
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.DATE:
                if (value instanceof java.sql.Date) {
                    pstmt.setDate(i, (java.sql.Date) value);
                } else if (value instanceof java.util.Date) {
                    pstmt.setDate(i, new java.sql.Date(((java.util.Date) value).getTime()));
                } else if (value instanceof String) {
                    String v = (String) value;
                    if (!v.startsWith("0000-00-00")) {
                        v = v.trim().replace(" ", "T");
                        DateTime dt = new DateTime(v);
                        pstmt.setDate(i, new Date(dt.toDate().getTime()));
                    } else {
                        pstmt.setNull(i, type);
                    }
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.TIME:
                if (value instanceof java.sql.Time) {
                    pstmt.setTime(i, (java.sql.Time) value);
                } else if (value instanceof java.util.Date) {
                    pstmt.setTime(i, new java.sql.Time(((java.util.Date) value).getTime()));
                } else if (value instanceof String) {
                    String v = (String) value;
                    v = "T" + v;
                    DateTime dt = new DateTime(v);
                    pstmt.setTime(i, new Time(dt.toDate().getTime()));
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            case Types.TIMESTAMP:
                if (value instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i, (java.sql.Timestamp) value);
                } else if (value instanceof java.util.Date) {
                    pstmt.setTimestamp(i, new java.sql.Timestamp(((java.util.Date) value).getTime()));
                } else if (value instanceof String) {
                    String v = (String) value;
                    if (!v.startsWith("0000-00-00")) {
                        v = v.trim().replace(" ", "T");
                        DateTime dt = new DateTime(v);
                        pstmt.setTimestamp(i, new Timestamp(dt.toDate().getTime()));
                    } else {
                        pstmt.setNull(i, type);
                    }
                } else {
                    pstmt.setNull(i, type);
                }
                break;
            default:
                pstmt.setObject(i, value, type);
        }
    }
}
