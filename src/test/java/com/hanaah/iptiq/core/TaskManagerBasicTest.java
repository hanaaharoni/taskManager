package com.hanaah.iptiq.core;

import com.hanaah.iptiq.exception.MaximumCapacityReachedException;
import com.hanaah.iptiq.exception.ProcessAlreadyExistsException;
import com.hanaah.iptiq.exception.ProcessNotFoundException;
import com.hanaah.iptiq.model.Priority;
import com.hanaah.iptiq.model.Process;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

abstract class TaskManagerBasicTest {

    public abstract TaskManager getTaskManager();


    /**
     * Helper method to mock å process.
     *
     * @param priority the priority of the process.
     * @return the mocked process.
     */
    protected Process mockProcess(Priority priority) {
        Process process = Mockito.mock(Process.class);
        Mockito.when(process.getProcessId()).thenReturn(UUID.randomUUID());
        Mockito.when(process.getPriority()).thenReturn(priority);
        return process;
    }

    @Test
    public void When_processExistsAlready_Then_exceptionThrown()
        throws MaximumCapacityReachedException, ProcessAlreadyExistsException {
        Process process = mockProcess(Priority.HIGH);

        this.getTaskManager().addProcess(process);

        Assertions.assertThrows(ProcessAlreadyExistsException.class, () ->
            this.getTaskManager().addProcess(process));
    }

    @Test
    public void When_addingProcess_Then_processShowsInListing()
        throws MaximumCapacityReachedException, ProcessAlreadyExistsException {
        Process process = mockProcess(Priority.HIGH);

        this.getTaskManager().addProcess(process);

        Assertions.assertTrue(this.getTaskManager().listRunningProcess().contains(process));
    }

    @Test
    public void When_killingProcess_Then_processIsRemoved()
        throws MaximumCapacityReachedException, ProcessNotFoundException, ProcessAlreadyExistsException {

        Process process = mockProcess(Priority.HIGH);
        this.getTaskManager().addProcess(process);
        this.getTaskManager().killProcess(process.getProcessId());

        Assertions.assertFalse(this.getTaskManager().listRunningProcess().contains(process));
    }

    @Test
    public void When_killingNonexistentProcess_Then_exceptionIsThrown() {

        UUID nonexistentProcessId = UUID.randomUUID();
        
        Assertions.assertThrows(ProcessNotFoundException.class, () ->
            this.getTaskManager().killProcess(nonexistentProcessId));
    }

    @Test
    public void When_killingGroupProcesses_Then_onlyGroupProcessesRemoved()
        throws MaximumCapacityReachedException, ProcessAlreadyExistsException {

        Process process1 = mockProcess(Priority.HIGH);
        this.getTaskManager().addProcess(process1);

        Process process2 = mockProcess(Priority.HIGH);
        this.getTaskManager().addProcess(process2);

        Process process3 = mockProcess(Priority.LOW);
        this.getTaskManager().addProcess(process3);

        this.getTaskManager().killGroup(Priority.HIGH);

        Assertions.assertFalse(this.getTaskManager().listRunningProcess().contains(process1));
        Assertions.assertFalse(this.getTaskManager().listRunningProcess().contains(process2));
        Assertions.assertTrue(this.getTaskManager().listRunningProcess().contains(process3));
    }


    @Test
    public void When_killingAllProcesses_Then_noProcessesAreLeft()
        throws MaximumCapacityReachedException, ProcessAlreadyExistsException {

        Process process1 = mockProcess(Priority.HIGH);
        this.getTaskManager().addProcess(process1);

        Process process2 = mockProcess(Priority.HIGH);
        this.getTaskManager().addProcess(process2);

        Process process3 = mockProcess(Priority.LOW);
        this.getTaskManager().addProcess(process3);

        this.getTaskManager().killAll();

        Assertions.assertTrue(this.getTaskManager().listRunningProcess().isEmpty());
    }

}