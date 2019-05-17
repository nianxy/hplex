package com.nianxy.hplex.limit;

/**
 * Created by nianxingyan on 17/8/17.
 */
public class Limit implements ILimit {
    private Integer size;
    private Integer offset;

    public static Limit limit() {
        return new Limit();
    }

    protected Limit() {
    }

    @Override
    public Limit setMaxSize(int size) {
        this.size = new Integer(size);
        return this;
    }

    @Override
    public Limit setOffset(int offset) {
        this.offset = new Integer(offset);
        return this;
    }

    @Override
    public String getLimitString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" limit ");
        if (offset!=null) {
            sb.append(offset).append(",");
        }
        if (size!=null) {
            sb.append(size);
        } else {
            sb.append("-1");
        }
        return sb.toString();
    }

    public static String getLimitClause(ILimit limit) {
        if (limit!=null) {
            return limit.getLimitString();
        }
        return "";
    }
}
