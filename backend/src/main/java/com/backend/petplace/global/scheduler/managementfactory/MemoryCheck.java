package com.backend.petplace.global.scheduler.managementfactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryCheck {

  public static void printHeapMemory() {

    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

    long used = heapUsage.getUsed();
    long commited = heapUsage.getCommitted();
    long max = heapUsage.getMax();
  }
}