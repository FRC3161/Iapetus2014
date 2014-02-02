package ca.team3161.lib.utils.io;

import com.sun.squawk.util.Arrays;
import edu.wpi.first.wpilibj.communication.FRCControl;

/**
 * Provide access to LCD on the Driver Station.
 */
public final class DriverStationLCD {

    private static final DriverStationLCD INSTANCE = new DriverStationLCD();

    /**
     * Driver station timeout in milliseconds. 20ms -> 50hz
     */
    private static final int TIMEOUT_MS = 20;

    /**
     * Command to display text
     */
    private static final int FULL_DISPLAY_TEXT_CMD = 0x9FFF;

    /**
     * Maximum line length for Driver Station display
     */
    public static final int LINE_LENGTH = 21;

    /**
     * Number of lines for the Driver Station display
     */
    public static final int NUM_LINES = FRCControl.USER_DS_LCD_DATA_SIZE / LINE_LENGTH;

    private final String EMPTY_STRING;

    private final byte[][] textBuffer;

    /**
     * Get an instance of the DriverStationLCD
     *
     * @return an instance of the DriverStationLCD
     */
    public static DriverStationLCD getInstance() {
        return INSTANCE;
    }

    /**
     * DriverStationLCD constructor.
     *
     * This is only called once the first time GetInstance() is called
     */
    private DriverStationLCD() {
        final char[] blank_bytes = new char[LINE_LENGTH];
        Arrays.fill(blank_bytes, ' ');
        EMPTY_STRING = new String(blank_bytes);
        textBuffer = new byte[NUM_LINES][FRCControl.USER_DS_LCD_DATA_SIZE];
        clear();
    }

    /**
     * Send the text data to the Driver Station.
     */
    private void updateLCD() {
        synchronized (textBuffer) {
            FRCControl.setUserDsLcdData(flattenTextBuffer(textBuffer), FRCControl.USER_DS_LCD_DATA_SIZE, TIMEOUT_MS);
        }
    }

    /**
     * Sets every character on a line to spaces.
     *
     * @param line The line that is being cleared.
     */
    public void clear(final int line) {
        validateLineNumber(line);
        println(line, EMPTY_STRING);
    }

    /**
     * Clears all lines
     */
    public void clear() {
        for (int i = 0; i < NUM_LINES; ++i) {
            clear(i);
        }
    }

    /**
     * Print formatted text to the Driver Station LCD text buffer. Messages
     * which are too long will be truncated.
     *
     * @param line The line on the LCD to print to.
     * @param text the text to print
     */
    public void println(final int line, final String text) {
        synchronized (textBuffer) {
            validateLineNumber(line);
            final byte[] bytes = getBytes(text);
            textBuffer[line] = bytes;
            updateLCD();
        }
    }

    /* Creates a one-dimensional array of the appropriate size, adds the two
     * "command bytes" to the beginning, then copies the contents of the 2D
     * textBuffer into it */
    private byte[] flattenTextBuffer(final byte[][] buffer) {
        synchronized (textBuffer) {
            final byte[] result = new byte[NUM_LINES * LINE_LENGTH + 2];
            result[0] = (byte) (FULL_DISPLAY_TEXT_CMD >> 8);
            result[1] = (byte) FULL_DISPLAY_TEXT_CMD;
            int pos = 2;
            for (int i = 0; i < NUM_LINES; ++i) {// copy lines into flat buffer
                System.arraycopy(buffer[i], 0, result, pos, LINE_LENGTH);
                pos += LINE_LENGTH;
            }
            return result;
        }
    }

    /* Ensure that we don't try to print to line numbers that don't exist */
    private void validateLineNumber(final int lineNumber) {
        if (lineNumber < 0 || NUM_LINES < lineNumber) {
            throw new IndexOutOfBoundsException("Cannot print to line " + lineNumber
                    + " on the DSLCD! Must be in range [0, " + NUM_LINES + "]");
        }
    }

    /* Get the byte[] behind a String, truncating if it's too long */
    private byte[] getBytes(final String text) {
        final int length = (text.length() > LINE_LENGTH ? LINE_LENGTH : text.length());
        final byte[] result = EMPTY_STRING.getBytes();
        System.arraycopy(text.getBytes(), 0, result, 0, length);
        return result;
    }

}
