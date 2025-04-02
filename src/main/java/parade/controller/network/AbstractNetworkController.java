package parade.controller.network;

import parade.common.exceptions.NetworkFailureException;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

public class AbstractNetworkController<Send extends Serializable, Recv extends Serializable>
        implements Closeable {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    protected volatile boolean running = false;

    protected final Socket socket;
    protected final ObjectInputStream in;
    protected final ObjectOutputStream out;

    protected BlockingQueue<Recv> recvDataQueue;

    protected Thread listenThread = Thread.ofVirtual().unstarted(this::listenForData);

    protected final Class<Recv> recvType;

    protected AbstractNetworkController(
            Socket socket, BlockingQueue<Recv> recvDataQueue, Class<Recv> recvType)
            throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush(); // Flush the stream to ensure the TCP header is sent first
        this.in = new ObjectInputStream(socket.getInputStream());

        if (recvDataQueue == null) {
            throw new IllegalArgumentException("recvDataQueue cannot be null");
        }
        this.recvDataQueue = recvDataQueue;

        if (recvType == null) {
            throw new IllegalArgumentException("recvType cannot be null");
        }
        this.recvType = recvType;
    }

    public void start() {
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }

        if (running) {
            LOGGER.log("Controller is already running");
            return;
        }

        running = true;
        listenThread.start();
        LOGGER.log("Controller started listening for data");
    }

    /**
     * Sets the recvDataQueue to the specified BlockingQueue. This method is used to change where
     * the received data is sent.
     *
     * @param recvDataQueue the BlockingQueue to set
     */
    public void setRecvDataQueue(BlockingQueue<Recv> recvDataQueue) {
        this.recvDataQueue = recvDataQueue;
    }

    /**
     * sends a data object of type Send, what else you want bro? send means send
     *
     * @param send the data to be sent
     * @throws IOException if an I/O error occurs
     */
    public void send(Send send) throws IOException {
        out.writeObject(send);
        out.flush(); // Flush the stream to ensure the data is sent
        LOGGER.log("Sent data: " + send);
    }

    /**
     * readSingleInput reads a single input from the socket with a timeout. It sets the socket
     * timeout to the specified time in milliseconds, and then reads the input.
     *
     * <p>If the read operation times out, it catches the SocketTimeoutException and returns null.
     *
     * <p>This is a blocking method until it receives the method or times out.
     *
     * @param timeInMs the time in milliseconds to wait for the read operation
     * @return the data read from the socket, or null if the read operation timed out
     * @throws IOException if an I/O error occurs
     * @throws NetworkFailureException if the data read is not of type Recv
     */
    protected Recv readSingleInput(int timeInMs) throws IOException, NetworkFailureException {
        Recv data = null;
        try {
            socket.setSoTimeout(timeInMs);
            data = readSingleInput();
        } catch (SocketTimeoutException e) {
            LOGGER.log("Socket timeout while waiting for receiving data, returning null");
        } finally {
            socket.setSoTimeout(0);
        }
        return data;
    }

    /**
     * readSingleInput reads a single input from the socket. It reads the input and checks if it is
     * of the expected type Recv. If it is, it returns the data; otherwise, it throws a
     * NetworkFailureException.
     *
     * <p>This is a blocking method until it receives the method.
     *
     * @return the data read from the socket
     * @throws IOException if an I/O error occurs
     * @throws NetworkFailureException if the data read is not of type Recv
     */
    protected Recv readSingleInput() throws IOException, NetworkFailureException {
        Recv data;
        try {
            Object inputObject = in.readObject();
            if (recvType.isInstance(inputObject)) {
                data = recvType.cast(inputObject);
                LOGGER.log("Received " + recvType.getName() + " data type: " + data);
            } else {
                throw new NetworkFailureException(
                        "Server received data is not of type" + recvType.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new NetworkFailureException("Invalid class for object input", e);
        }
        return data;
    }

    private void listenForData() {
        LOGGER.log("Listening for data");
        try {
            while (running) {
                LOGGER.log("Waiting for the next data");
                try {
                    Recv data = readSingleInput();
                    if (data == null) {
                        LOGGER.log("Received null data, ignoring...");
                    } else {
                        LOGGER.log("Forwarding data to recvDataQueue...");
                        recvDataQueue.offer(data);
                    }
                } catch (NetworkFailureException e) {
                    LOGGER.log("Failed to read data from input stream", e);
                }
            }
        } catch (EOFException e) {
            if (running) {
                LOGGER.log("Connection closed by the other end");
            }
        } catch (IOException e) {
            if (running) {
                LOGGER.log("I/O error occurred", e);
            }
        } catch (Exception e) {
            if (running) {
                LOGGER.log("Unexpected error occurred", e);
            }
        } finally {
            if (running) {
                try {
                    close();
                } catch (Exception e) {
                    LOGGER.log("Failed to close controller", e);
                }
            }
        }

        LOGGER.log("Controller stopped listening for data");
    }

    @Override
    public void close() throws IOException {
        if (!running && socket.isClosed()) {
            LOGGER.log("Controller is already closed");
            return;
        }

        running = false;
        out.flush(); // Flush all data before closing
        out.close(); // Close the output stream first to ensure all data is sent
        in.close(); // Close the input stream first to avoid blocking on read
        socket.close();
        LOGGER.log("Controller closed");
    }
}
