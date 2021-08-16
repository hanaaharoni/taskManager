package com.hanaah.iptiq.core;

import com.hanaah.iptiq.exception.MaximumCapacityReachedException;
import com.hanaah.iptiq.exception.ProcessAlreadyExistsException;
import com.hanaah.iptiq.model.Priority;
import com.hanaah.iptiq.model.Process;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrefixCapacityTaskManagerTest extends TaskManagerBasicTest {


    private static final int CAPACITY = 10;
    @Getter
    private TaskManager taskManager;

    @BeforeEach
    public void reset() {
        this.taskManager = new PrefixCapacityTaskManager(CAPACITY);
    }

    @Test
    public void When_capacityIsFull_Then_exceptionThrownUponAddition()
        throws MaximumCapacityReachedException, ProcessAlreadyExistsException {
        TaskManager taskManager = this.getTaskManager();
        for (int i = 0; i < CAPACITY; i++) {
            Process process = mockProcess(Priority.HIGH);
            taskManager.addProcess(process);
        }
        Process excessProcess = mockProcess(Priority.HIGH);
        Assertions.assertThrows(MaximumCapacityReachedException.class, () ->
            taskManager.addProcess(excessProcess));
    }
}