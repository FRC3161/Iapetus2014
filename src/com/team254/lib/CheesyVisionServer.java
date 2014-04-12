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
                    } catch (InterruptedException ex) {
                        System.out.println("Thread sleep failed.");
                    }
                }
            } catch (final IOException e) {
                System.out.println("Socket failure.");
                e.printStackTrace();
            }
        }
    }

    private static CheesyVisionServer INSTANCE;
    private final Thread serverThread = new Thread(new ServerTask());
    private final int port;
    private final Vector connections = new Vector();
    private boolean counting = false;
    private int leftCount = 0, rightCount = 0, totalCount = 0;
    private boolean curLeftStatus = false, curRightStatus = false;
    private double lastHeartbeatTime = -1.0d;
    private boolean listening = true;

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
        this(1180);
    }

    private CheesyVisionServer(int port) {
        this.port = port;
    }

    public boolean hasClientConnection() {
        return lastHeartbeatTime > 0 && (Timer.getFPGATimestamp() - lastHeartbeatTime) < 3.0;
    }

    private void updateCounts(boolean left, boolean right) {
        if (counting) {
            leftCount += left ? 1 : 0;
            rightCount += right ? 1 : 0;
            totalCount++;
        }
    }

    public void startSamplingCounts() {
        counting = true;
    }

    public void stopSamplingCounts() {
        counting = false;
    }

    public void reset() {
        leftCount = rightCount = totalCount = 0;
        curLeftStatus = curRightStatus = false;
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

        SocketConnection connection;

        public VisionServerConnectionHandler(SocketConnection c) {
            connection = c;
        }

        public void run() {
            try {
                InputStream is = connection.openInputStream();

                int ch = 0;
                byte[] b = new byte[1024];
                double timeout = 10.0;
                double lastHeartbeat = Timer.getFPGATimestamp();
                CheesyVisionServer.this.lastHeartbeatTime = lastHeartbeat;
                while (Timer.getFPGATimestamp() < lastHeartbeat + timeout) {
                    boolean gotData = false;
                    while (is.available() > 0) {
                        gotData = true;
                        int read = is.read(b);
                        for (int i = 0; i < read; ++i) {
                            byte reading = b[i];
                            boolean leftStatus = (reading & (1 << 1)) > 0;
                            boolean rightStatus = (reading & (1 << 0)) > 0;
                            CheesyVisionServer.this.curLeftStatus = leftStatus;
                            CheesyVisionServer.this.curRightStatus = rightStatus;
                            CheesyVisionServer.this.updateCounts(leftStatus, rightStatus);
                        }
                        lastHeartbeat = Timer.getFPGATimestamp();
                        CheesyVisionServer.this.lastHeartbeatTime = lastHeartbeat;
                    }

                    try {
                        Thread.sleep(50); // sleep a bit
                    } catch (InterruptedException ex) {
                        System.out.println("Thread sleep failed.");
                    }
                }
                is.close();
                connection.close();

            } catch (IOException e) {
            }
        }
    }
}
