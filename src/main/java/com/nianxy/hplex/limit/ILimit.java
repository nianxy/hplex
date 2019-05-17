package com.nianxy.hplex.limit;

/**
 * Created by nianxingyan on 17/8/17.
 */
public interface ILimit {
    /**
     * 数据大小，如果不设置，则返回从offset开始，后边所有的数据
     * {@link #setMaxSize(int)}和{@link #setOffset(int)}至少要设置一个
     * @param size
     */
    ILimit setMaxSize(int size);

    /**
     * 数据起始点，如果不设置，则从头开始
     * @param offset 从0开始的起始点
     */
    ILimit setOffset(int offset);

    /**
     * 返回limit子句
     * @return
     */
    String getLimitString();
}
