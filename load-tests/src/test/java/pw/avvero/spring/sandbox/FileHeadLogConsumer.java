package pw.avvero.spring.sandbox;

import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileHeadLogConsumer extends BaseConsumer<FileHeadLogConsumer> implements Closeable {

    private final OutputStream os;
    private int size;

    public FileHeadLogConsumer(String filepath) throws IOException {
        this.os = new FileOutputStream(filepath);
    }

    @Override
    public void accept(OutputFrame outputFrame) {
        if (outputFrame.getBytes() == null || outputFrame.getBytes().length == 0 || size > 1000_000) return;
        size += outputFrame.getBytes().length;
        if (outputFrame.getBytes() == null || outputFrame.getBytes().length == 0) return;
        try {
            os.write(outputFrame.getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
    }
}
