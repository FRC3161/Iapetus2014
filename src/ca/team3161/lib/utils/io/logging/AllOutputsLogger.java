/* Copyright (c) 2014, FRC3161
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice, this
*   list of conditions and the following disclaimer in the documentation and/or
*   other materials provided with the distribution. 
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ca.team3161.lib.utils.io.logging;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class AllOutputsLogger implements LogWriter {
    
    private final Vector writers;
    private static AllOutputsLogger INSTANCE;
    
    private AllOutputsLogger() {
        writers = new Vector();
        final LogWriter stdout = StdoutLogger.getInstance();
        writers.addElement(stdout);
        final String logFileName = "AllOutputs-" + (new Date()).getTime() + ".txt";
        try {
            writers.addElement(new FileLogger(logFileName));
        } catch (final IOException e) {
            try {
                stdout.write("WARNING: Could not open FileLogger for log file " + logFileName + "!");
            } catch (final IOException e2) {
                
            }
        }
    }
    
    public static synchronized AllOutputsLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AllOutputsLogger();
        }
        return INSTANCE;
    }
    
    public void write(final String s) throws IOException {
        final Enumeration e = writers.elements();
        while (e.hasMoreElements()) {
            final LogWriter writer = (LogWriter) e.nextElement();
            writer.write(s);
        }
    }
}
