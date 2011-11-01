/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.scan.command.CommandImpl;
import org.csstudio.scan.command.WaitForDevicesCommand;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;

/** Scanner executes the {@link CommandImpl}s for one scan within a {@link ScanContext}
 *  @author Kay Kasemir
 */
public class Scan
{
    /** Provides the next available <code>id</code> */
    final private static AtomicLong ids = new AtomicLong();

    final private long id;

    final private String name;

    final private Date created = new Date();

    final private List<CommandImpl> commands;

    private volatile ScanState state = ScanState.Idle;

    private volatile Throwable error = null;

    private volatile ScanContext context = null;

    private volatile long total_work_units = 0;

    /** Initialize
     *  @param name User-provided name for this scan
     *  @param commands Commands to execute in this scan
     */
    public Scan(final String name, CommandImpl... commands)
    {
        this(name, Arrays.asList(commands));
    }

    /** Initialize
     *  @param name User-provided name for this scan
     *  @param commands Commands to execute in this scan
     */
    public Scan(final String name, final List<CommandImpl> commands)
    {
        id = ids.incrementAndGet();
        this.name = name;
        this.commands = commands;
    }

    /** @return Unique scan identifier (within JVM of the scan engine) */
    public long getId()
    {
        return id;
    }

    /** @return Scan name */
    public String getName()
    {
        return name;
    }

    /** @return Info about current state of this scan */
    public ScanInfo getScanInfo()
    {
        if (context == null)
            return new ScanInfo(id, name, created, state, error, 0, total_work_units, "");
        else
        {
            final String command;
            if (state == ScanState.Finished)
                command = "- end -";
            else
                command = context.getCurrentCommand();
            return new ScanInfo(id, name, created, state, error, context.getWorkPerformed(), total_work_units, command);
        }
    }

    /** Execute all commands on the scan,
     *  turning exceptions into a 'Failed' scan state.
     */
    public void execute(final ScanContext context)
    {
        try
        {
            execute_or_die_trying(context);
        }
        catch (InterruptedException ex)
        {
            state = ScanState.Aborted;
            error = ex;
        }
        catch (Throwable ex)
        {
            state = ScanState.Failed;
            error = ex;
        }
    }

    /** Execute all commands on the scan,
     *  passing exceptions back up.
     *  @throws Exception on error
     */
    private void execute_or_die_trying(final ScanContext context) throws Throwable
    {
        // Was scan aborted before it ever got to run?
        if (state == ScanState.Aborted)
            return;
        // Otherwise expect 'Idle'
        if (state != ScanState.Idle)
            throw new IllegalStateException("Cannot run Scan that is " + state);
        this.context = context;

        // Start Devices
        context.startDevices();

        // Determine work units
        total_work_units = 1; // WaitForDevicesCommand
        for (CommandImpl command : commands)
            total_work_units += command.getWorkUnits();

        // Execute commands
        state = ScanState.Running;
        try
        {
            context.execute(new WaitForDevicesCommand());
            context.execute(commands);
            // Successful finish
            state = ScanState.Finished;
        }
        finally
        {
            // Stop devices
            context.stopDevices();
        }
    }

    /** Pause execution of a currently executing scan */
    public void pause()
    {
        if (context != null  &&  state == ScanState.Running)
        {
            context.pause();
            state = ScanState.Paused;
        }
    }

    /** Resume execution of a paused scan */
    public void resume()
    {
        if (state == ScanState.Paused)
        {
            state = ScanState.Running;
            context.resume();
        }
    }

    /** Ask for execution to stop */
    void abort()
    {
        state = ScanState.Aborted;
        if (context != null)
            context.abort();
    }

    // Hash by ID
    @Override
    public int hashCode()
    {
        final int prime = 31;
        return prime + (int) (id ^ (id >>> 32));
    }

    // Compare by ID
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof Scan))
            return false;
        final Scan other = (Scan) obj;
        return id == other.getId();
    }
}
