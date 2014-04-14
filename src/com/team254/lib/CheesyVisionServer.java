package com.team254.lib;

/**
 * @author Tom Bottiglieri Team 254, The Cheesy Poofs
 */
import edu.wpi.first.wpilibj.Timer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

public class CheesyVisionServer {

    public static final int DEFAULT_PORT = 1180;
    private static CheesyVisionServer INSTANCE = null;
    private final Thread serverThread = new Thread(new ServerTask());
    private final int port;
    private final Vector connections = new Vector();
    private volatile boolean counting = false, listening = true, curLeftStatus = false, curRightStatus = false;
    private volatile int leftCount = 0, rightCount = 0, totalCount = 0;
    private volatile double lastHeartbeatTime = -1.0d;

    public static CheesyVisionServer getInstance(final int port) {
        if (INSTANCE == null) {
            INSTANCE = new CheesyVisionServer(port);
        }
        return INSTANCE;
    }

    public void start() {
        serverThread.start();
    }

    public void stop() {
        listening = false;
    }

    private CheesyVisionServer() {
        this(DEFAULT_PORT);
    }

    private CheesyVisionServer(final int port) {
        this.port = port;
    }

    public boolean hasClientConnection() {
        return lastHeartbeatTime > 0 && (Timer.getFPGATimestamp() - lastHeartbeatTime) < 3.0;
    }

    private void updateCounts(final boolean left, final boolean right) {
        if (!counting) {
            return;
        }
        
        if (left) {
            ++leftCount;
        }
        if (right) {
            ++rightCount;
        }
        ++totalCount;
    }

    public void startSamplingCounts() {
        counting = true;
    }

    public void stopSamplingCounts() {
        counting = false;
    }

    public void reset() {
        leftCount = 0;
        rightCount = 0;
        curLeftStatus = false;
        curRightStatus = false;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public int getRightCount() {
        return rightCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean getLeftStatus() {
        return curLeftStatus;
    }

    public boolean getRightStatus() {
        return curRightStatus;
    }

    // This class handles incoming TCP connections
    private class VisionServerConnectionHandler implements Runnable {
        private final SocketConnection connection;
        public VisionServerConnectionHandler(final SocketConnection c) {
            connection = c;
        }

        public void run() {
            try {
                final InputStream is = connection.openInputStream();

                final byte[] b = new byte[1024];
                final double timeout = 10.0d;
                double lastHeartbeat = Timer.getFPGATimestamp();
                lastHeartbeatTime = lastHeartbeat;
                while (Timer.getFPGATimestamp() < lastHeartbeat + timeout) {
                    while (is.available() > 0) {
                        final int read = is.read(b);
                        for (int i = 0; i < read; ++i) {
                            final byte reading = b[i];
                            final boolean leftStatus = (reading & (1 << 1)) > 0;
                            final boolean rightStatus = (reading & 1) > 0;
                            curLeftStatus = leftStatus;
                            curRightStatus = rightStatus;
                            updateCounts(leftStatus, rightStatus);
                        }
                        lastHeartbeat = Timer.getFPGATimestamp();
                        lastHeartbeatTime = lastHeartbeat;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (final InterruptedException ex) {
                        System.out.println("Thread sleep failed.");
                    }
                }
                is.close();
                connection.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class ServerTask implements Runnable {
        // This method listens for incoming connections and spawns new
        // VisionServerConnectionHandlers to handle them
        public void run() {
            try {
                final ServerSocketConnection s = (ServerSocketConnection) Connector.open("serversocket://:" + port);
                while (listening) {
                    final SocketConnection connection = (SocketConnection) s.acceptAndOpen(); // blocks until a connection is made
                    final Thread t = new Thread(new VisionServerConnectionHandler(connection));
                    t.start();
                    connections.addElement(connection);
                    try {
                        Thread.sleep(100);
                    } catch (final InterruptedException ex) {
                        System.out.println("Thread sleep failed.");
                    }
                }
            } catch (final IOException e) {
                System.out.println("Socket failure.");
                e.printStackTrace();
            }
        }
    }
}
