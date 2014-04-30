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
import java.util.Calendar;
import java.util.TimeZone;

public class Logger {
    
    private LogWriter writer;
    private static Logger INSTANCE;
    
    private Logger() {
        this.writer = AllOutputsLogger.getInstance();
    }
    
    private Logger(final LogWriter writer) {
        this.writer = writer;
    }
    
    public static Logger getLogger() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }
    
    public void setLogWriter(final LogWriter writer) {
        this.writer = writer;
    }
    
    public void log(final Exception e) {
        log(e.toString());
    }
    
    public void log(final String s) {
        try {
            writer.write(getHeader() + s);
        } catch (final IOException e) {
            // can't really do anything about it
        }
    }
    
    private static String getHeader() {
        final StringBuffer buff = new StringBuffer();
        buff.append('[');
        buff.append(Calendar.getInstance(TimeZone.getTimeZone("EST")).getTime());
        buff.append("] ");
        return buff.toString();
    }
    
}
