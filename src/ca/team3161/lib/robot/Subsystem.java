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

/**
 * Abstracts a system which uses resources and has some task (recurring or
 * one-shot) to be performed. An example is PID control - monitor sensors
 * and periodically set motor values based on this.
 */
public abstract class Subsystem {
    
    /**
     * A list of resources which this Subsystem requires (see ResourceTracker)
     */
    protected final Vector resources;
    
    /**
     * The period length between task repeats (milliseconds)
     */
    protected final long TASK_TIMEOUT;
    
    /**
     * If this task repeats, or runs once only
     */
    protected final boolean repeating;
    
    /**
     * If this task has been requested for cancellation
     */
    protected volatile boolean cancelled;
    
    /**
     * If this task has been started
     */
    protected boolean started;
    
    /**
     * The background task for this Subsystem
     */
    private Thread thread;
    
    /**
     * The name to assign to the background task
     */
    protected final String threadName;
    
    /**
     * @param timeout update period (in milliseconds) between task repeats (if any)
     * @param repeating true iff the task is recurring
     * @param threadName the name for the background thread of this Subsystem
     */
    protected Subsystem(final long timeout, final boolean repeating, final String threadName) {
        TASK_TIMEOUT = timeout;
        this.resources = new Vector();
        this.repeating = repeating;
        this.cancelled = false;
        this.started = false;
        this.threadName = threadName;
    }
    
    private Thread getTaskThread() {
        return new Thread(new Runnable() {
            public void run() {
                if (cancelled) {
                    return;
                }
                if (repeating) {
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
        }, threadName);
    }
    
    /**
     * @param resource a sensor, speed controller, etc. that this subsystem
     * needs exclusive access to during its task
     */
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
    
    /**
     * @return if this Subsystem's background task has been canceled
     */
    public boolean getCancelled() {
        return cancelled;
    }
    
    /**
     * Cancel the background task of this Subsystem (stop it from running, if it
     * is a recurring task)
     */
    public void cancel() {
        cancelled = true;
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    /**
     * Start (or restart) this Subsystem's background task
     */
    public final void start() {
        cancelled = false;
        if (thread != null) {
            thread.interrupt();
        }
        thread = getTaskThread();
        thread.start();
    }
    
    /**
     * Use require() to define a set of required resources
     */
    protected abstract void defineResources();
    
    /**
     * The background task to run
     * @throws Exception in case the defined task throws any Exceptions
     */
    protected abstract void task() throws Exception;
    
}