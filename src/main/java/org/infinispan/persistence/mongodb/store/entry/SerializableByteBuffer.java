package org.infinispan.persistence.mongodb.store.entry;

import org.infinispan.commons.io.ByteBuffer;

import java.io.Serializable;

public class SerializableByteBuffer implements Serializable, ByteBuffer {
    private static final long serialVersionUID = -215291623591872364L;

    private int offset;
    private int lenght;
    private byte[] buf;

    public SerializableByteBuffer(byte[] buf, int lenght, int offset) {
        super();
        this.offset = offset;
        this.lenght = lenght;
        this.buf = buf;
    }

    public SerializableByteBuffer() {
        super();
    }

    @Override
    public byte[] getBuf() {
        return buf;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return lenght;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    @Override
    public ByteBuffer copy() {
        return new SerializableByteBuffer(buf, lenght, offset);
    }
}