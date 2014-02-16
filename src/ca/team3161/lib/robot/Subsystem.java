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

package ca.team3161.lib.robot;

import edu.wpi.first.wpilibj.communication.Semaphore;
import edu.wpi.first.wpilibj.communication.SemaphoreException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Subsystem {
    
    protected final Vector resources = new Vector();
    protected final long TASK_TIMEOUT;
    protected final boolean repeating;
    private volatile boolean cancelled;
    
    protected Subsystem(final long timeout, final boolean repeating) {
        TASK_TIMEOUT = timeout;
        this.repeating = repeating;
        this.cancelled = false;
    }
    
    private final Thread thread = new Thread(new Runnable() {
        public void run() {
            if (repeating && !cancelled) {
                while (!cancelled) {
                    try {
                        acquireResources();
                        task();
                        Thread.sleep(TASK_TIMEOUT);
                    } catch (final Exception e) {
                    } finally {
                        releaseResources();
                    }
                }
            } else {
                try {
                    acquireResources();
                    task();
                } catch (final Exception e) {
                } finally {
                    releaseResources();
                }
            }
        }
    });
    
    protected final void require(Object resource) {
        resources.addElement(ResourceTracker.track(resource));
    }
    
    private void acquireResources() throws SemaphoreException {
        Enumeration e = resources.elements();
        while (e.hasMoreElements()) {
            Semaphore s = (Semaphore) e.nextElement();
            s.takeMillis(500);
        }
    }
    
    private void releaseResources() {
        Enumeration e = resources.elements();
        while (e.hasMoreElements()) {
            Semaphore s = (Semaphore) e.nextElement();
            try {
                s.give();
            } catch (final SemaphoreException se) {
            }
        }
    }
    
    public boolean getCancelled() {
        return cancelled;
    }
    
    public void cancel() {
        cancelled = true;
    }
    
    public final void start() {
        thread.start();
    }
    
    protected abstract void defineResources();
    protected abstract void task() throws Exception;
    
}
