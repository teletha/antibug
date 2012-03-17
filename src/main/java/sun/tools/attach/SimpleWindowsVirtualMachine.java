/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package sun.tools.attach;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * @version 2012/03/17 22:50:10
 */
public final class SimpleWindowsVirtualMachine extends InputStream {

    /** The enqueue code stub (copied into each target VM). */
    private static final byte[] stub;

    // initialization
    static {
        // load attach library
        System.loadLibrary("attach");

        // initialize it
        init();

        // generate stub to copy into target process
        stub = generateStub();
    }

    /** The native pipe identifier. */
    private volatile long pipe;

    /**
     * @param pid
     */
    public SimpleWindowsVirtualMachine(int pid, String path) throws Exception {
        // create a pipe using a random name
        String name = "\\\\.\\pipe\\agent" + new Random().nextInt();

        // create native pipe
        pipe = createPipe(name);

        try {
            // enqueue the command to the process
            enqueue(openProcess(pid), stub, "load", name, "instrument", "true", path);

            // wait for command to complete - process will connect with the
            // completion status
            connectPipe(pipe);
        } finally {
            close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int read() throws IOException {
        byte[] b = new byte[1];
        int n = read(b, 0, 1);

        return n == 1 ? b[0] & 0xff : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int read(byte[] bs, int off, int len) throws IOException {
        return len == 0 ? 0 : readPipe(pipe, bs, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        closePipe(pipe);
    }

    /**
     * Initialize
     */
    private static native void init();

    /**
     * Generate stub
     * 
     * @return
     */
    private static native byte[] generateStub();

    /**
     * Open process
     * 
     * @param pid
     * @return
     */
    private static native long openProcess(int pid);

    /**
     * Close process.
     * 
     * @param hProcess
     */
    private static native void closeProcess(long hProcess);

    /**
     * Create Pipe.
     * 
     * @param name
     * @return
     */
    private static native long createPipe(String name);

    /**
     * Close pipe.
     * 
     * @param hPipe
     */
    private static native void closePipe(long hPipe);

    /**
     * Connect pipe.
     * 
     * @param hPipe
     */
    private static native void connectPipe(long hPipe);

    /**
     * Read pipe.
     * 
     * @param hPipe
     * @param buf
     * @param off
     * @param bufLen
     * @return
     */
    private static native int readPipe(long hPipe, byte[] buf, int off, int bufLen);

    /**
     * Enqueue.
     * 
     * @param hProcess
     * @param stub
     * @param cmd
     * @param pipeName
     * @param args
     */
    private static native void enqueue(long hProcess, byte[] stub, String cmd, String pipeName, Object... args);

}
