package org.nd4j.compression.impl;

import lombok.val;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.compression.CompressedDataBuffer;
import org.nd4j.linalg.compression.CompressionDescriptor;
import org.nd4j.linalg.compression.CompressionType;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Compressor implementation based on half-precision floats, aka FP16
 *
 * @author raver119@gmail.com
 */
public class Float16 extends AbstractCompressor {

    @Override
    public String getDescriptor() {
        return "FLOAT16";
    }

    /**
     * This method returns compression opType provided by specific NDArrayCompressor implementation
     *
     * @return
     */
    @Override
    public CompressionType getCompressionType() {
        return CompressionType.LOSSY;
    }

    @Override
    public DataBuffer decompress(DataBuffer buffer) {
        val type = getGlobalTypeEx();
        DataBuffer result = Nd4j.getNDArrayFactory().convertDataEx(DataBuffer.TypeEx.FLOAT16, buffer, type);

        return result;
    }

    @Override
    public DataBuffer compress(DataBuffer buffer) {
        DataBuffer result = Nd4j.getNDArrayFactory().convertDataEx(getBufferTypeEx(buffer), buffer,
                        DataBuffer.TypeEx.FLOAT16);
        return result;
    }

    @Override
    protected CompressedDataBuffer compressPointer(DataBuffer.TypeEx srcType, Pointer srcPointer, int length,
                    int elementSize) {

        BytePointer ptr = new BytePointer(length * 2);
        CompressionDescriptor descriptor = new CompressionDescriptor();
        descriptor.setCompressedLength(length * 2);
        descriptor.setOriginalLength(length * elementSize);
        descriptor.setOriginalElementSize(elementSize);
        descriptor.setNumberOfElements(length);

        descriptor.setCompressionAlgorithm(getDescriptor());
        descriptor.setCompressionType(getCompressionType());

        CompressedDataBuffer buffer = new CompressedDataBuffer(ptr, descriptor);

        Nd4j.getNDArrayFactory().convertDataEx(srcType, srcPointer, DataBuffer.TypeEx.FLOAT16, ptr, length);

        return buffer;
    }
}
